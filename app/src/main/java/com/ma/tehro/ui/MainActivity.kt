package com.ma.tehro.ui

import android.Manifest
import android.content.Context
import android.content.IntentSender
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.ma.tehro.common.AppSnackbar
import com.ma.tehro.common.LinesScreen
import com.ma.tehro.common.MapScreen
import com.ma.tehro.common.ObserveAsEvents
import com.ma.tehro.common.PathFinderScreen
import com.ma.tehro.common.StationDetailScreen
import com.ma.tehro.common.StationSelectorScreen
import com.ma.tehro.common.StationsScreen
import com.ma.tehro.common.SubmitStationInfoScreen
import com.ma.tehro.common.hasLocationPermission
import com.ma.tehro.common.messenger.UiMessageManager
import com.ma.tehro.common.navTypeOf
import com.ma.tehro.data.Station
import com.ma.tehro.data.Translations
import com.ma.tehro.ui.detail.StationDetail
import com.ma.tehro.ui.line.LineViewModel
import com.ma.tehro.ui.line.Lines
import com.ma.tehro.ui.line.stations.Stations
import com.ma.tehro.ui.map.StationsMap
import com.ma.tehro.ui.map.StationsMapViewModel
import com.ma.tehro.ui.shortestpath.PathViewModel
import com.ma.tehro.ui.shortestpath.StationSelector
import com.ma.tehro.ui.shortestpath.pathfinder.PathFinder
import com.ma.tehro.ui.submit_info.SubmitInfoViewModel
import com.ma.tehro.ui.submit_info.SubmitStationInfo
import com.ma.tehro.ui.theme.Gray
import com.ma.tehro.ui.theme.TehroTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.reflect.KType
import kotlin.reflect.typeOf

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var pendingGpsCallback: (() -> Unit)? = null
    private val locationRequest: LocationRequest by lazy {
        LocationRequest.Builder(1000L)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setMinUpdateIntervalMillis(500L)
            .build()
    }

    private lateinit var locationSettingsRequest: LocationSettingsRequest
    private lateinit var settingsClient: SettingsClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //todo refactor this
        window.statusBarColor = Gray.toArgb()
        WindowCompat.getInsetsController(window, window.decorView)
            .isAppearanceLightStatusBars = false

        settingsClient = LocationServices.getSettingsClient(this)
        locationSettingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)
            .build()

        setContent {
            TehroTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()
                ObserveAsEvents(
                    flow = UiMessageManager.events,
                    snackbarHostState
                ) { event ->
                    scope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        val result = snackbarHostState.showSnackbar(
                            message = event.message,
                            actionLabel = event.action?.name,
                            duration = SnackbarDuration.Short
                        )

                        if (result == SnackbarResult.ActionPerformed) {
                            event.action?.action?.invoke()
                        }
                        if (event.action?.name == "Dismiss") {
                            snackbarHostState.currentSnackbarData?.dismiss()
                        }
                    }
                }
                val navController = rememberNavController()
                Scaffold(
                    snackbarHost = {
                        SnackbarHost(
                            hostState = snackbarHostState,
                            snackbar = { snackbarData ->
                                AppSnackbar(data = snackbarData)
                            }
                        )
                    },
                ) { innerPadding ->
                    NavHost(
                        modifier = Modifier.padding(innerPadding),
                        navController = navController,
                        startDestination = LinesScreen,
                    ) {
                        baseComposable<LinesScreen> {
                            val metroViewModel: LineViewModel = hiltViewModel(it)
                            Lines(
                                navController = navController,
                                lines = metroViewModel.getLines(),
                                onFindPathClicked = {
                                    navController.navigate(StationSelectorScreen)
                                },
                                onMapClick = {
                                    navController.navigate(MapScreen)
                                },
                                onNewSubmitInfoStationClicked = {
                                    navController.navigate(
                                        SubmitStationInfoScreen(
                                            station = Station(
                                                name = "",
                                                translations = Translations(fa = ""),
                                                lines = listOf(),
                                                longitude = null,
                                                latitude = null,
                                                address = null,
                                                colors = listOf(),
                                                disabled = false,
                                                wc = null,
                                                coffeeShop = null,
                                                groceryStore = null,
                                                fastFood = null,
                                                atm = null,
                                                relations = listOf(),
                                                positionsInLine = listOf()
                                            ),
                                            lineNumber = 0
                                        )
                                    )
                                }
                            )
                        }
                        baseComposable<MapScreen> {
                            val viewModel: StationsMapViewModel = hiltViewModel(it)
                            StationsMap(
                                onFindCurrentLocationClick = {
                                    if (hasLocationPermission()) {
                                        checkAndPromptEnableGPS {
                                            viewModel.getCurrentLocation()
                                        }
                                    } else {
                                        ActivityCompat.requestPermissions(
                                            this@MainActivity,
                                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                            100
                                        )
                                    }
                                },
                                viewState = viewModel.uiState.collectAsStateWithLifecycle().value,
                            )
                        }
                        baseComposable<StationsScreen> { backStackEntry ->
                            val metroViewModel: LineViewModel = hiltViewModel(backStackEntry)
                            val args = backStackEntry.toRoute<StationsScreen>()
                            Stations(
                                lineNumber = args.lineNumber,
                                orderedStations = metroViewModel.getOrderedStationsInLineByPosition(
                                    args.lineNumber
                                ),
                                onBackClick = { navController.popBackStack() },
                                onStationClick = { station, line ->
                                    navController.navigate(StationDetailScreen(station, line))
                                },
                            )
                        }
                        baseComposable<StationSelectorScreen> { backStackEntry ->
                            val viewModel: PathViewModel = hiltViewModel(backStackEntry)
                            StationSelector(
                                onBack = { navController.popBackStack() },
                                viewState = viewModel.uiState.collectAsStateWithLifecycle().value,
                                onSelectedChange = { isFrom, query, fa ->
                                    viewModel.onSelectedChange(isFrom, query, fa)
                                },
                                onFindPathClick = { startEn, destEn, startFa, destFa ->
                                    navController.navigate(
                                        PathFinderScreen(
                                            startEn,
                                            startFa,
                                            destEn,
                                            destFa
                                        )
                                    )
                                },
                                onNearestStationChanged = {
                                    if (it != null) {
                                        viewModel.onSelectedChange(
                                            isFrom = true,
                                            enStation = it.station.name,
                                            faStation = it.station.translations.fa
                                        )
                                    }
                                },
                                findNearestStationAsStart = {
                                    if (hasLocationPermission()) {
                                        checkAndPromptEnableGPS {
                                            viewModel.findNearestStation()
                                        }


                                    } else {
                                        ActivityCompat.requestPermissions(
                                            this@MainActivity,
                                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                            100
                                        )
                                    }
                                },
                            )
                        }
                        baseComposable<PathFinderScreen> { backStackEntry ->
                            val viewModel: PathViewModel = hiltViewModel(backStackEntry)
                            val args: PathFinderScreen = backStackEntry.toRoute()
                            PathFinder(
                                findShortestPath = {
                                    viewModel.findShortestPathWithDirection(
                                        from = args.startEnStation,
                                        to = args.enDestination
                                    )
                                },
                                onBack = { navController.popBackStack() },
                                fromEn = args.startEnStation,
                                toEn = args.enDestination,
                                onStationClick = { station, line ->
                                    navController.navigate(StationDetailScreen(station, line))
                                },
                                fromFa = args.startFaStation,
                                toFa = args.faDestination,
                            )
                        }
                        baseComposable<StationDetailScreen>(
                            typeMap = mapOf(typeOf<Station>() to navTypeOf<Station>()),
                        ) { backStackEntry ->
                            val args = backStackEntry.toRoute<StationDetailScreen>()
                            StationDetail(
                                station = args.station,
                                onBack = { navController.popBackStack() },
                                lineNumber = args.lineNumber,
                                onSubmitInfoStationClicked = { station, line ->
                                    navController.navigate(SubmitStationInfoScreen(station, line))
                                }
                            )
                        }
                        baseComposable<SubmitStationInfoScreen>(
                            typeMap = mapOf(typeOf<Station>() to navTypeOf<Station>()),
                        ) { backStackEntry ->
                            val viewModel: SubmitInfoViewModel = hiltViewModel(backStackEntry)
                            val args: SubmitStationInfoScreen = backStackEntry.toRoute()
                            println(args.station)
                            SubmitStationInfo(
                                onBack = { navController.popBackStack() },
                                onSubmitInfo = { viewModel.submitStationCorrection(it) },
                                state = viewModel.state.collectAsStateWithLifecycle().value,
                                station = args.station,
                                lineNumber = args.lineNumber
                            )
                        }
                    }
                }
            }
        }
    }

    private val enableLocationLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        when (result.resultCode) {
            RESULT_OK -> {
                val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                if (isGpsEnabled) {
                    pendingGpsCallback?.invoke()
                }
            }

            else -> {
                Toast.makeText(
                    this,
                    "GPS is required to find the nearest station",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        pendingGpsCallback = null
    }

    private fun checkAndPromptEnableGPS(onSuccess: () -> Unit) {
        pendingGpsCallback = onSuccess

        settingsClient.checkLocationSettings(locationSettingsRequest)
            .addOnSuccessListener {

                onSuccess()
                pendingGpsCallback = null
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {

                        enableLocationLauncher.launch(
                            IntentSenderRequest.Builder(exception.resolution).build()
                        )
                    } catch (sendEx: IntentSender.SendIntentException) {
                        sendEx.printStackTrace()
                        pendingGpsCallback = null
                    }
                } else {
                    pendingGpsCallback = null
                    Toast.makeText(
                        this,
                        "Unable to enable GPS",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}

inline fun <reified T : Any> NavGraphBuilder.baseComposable(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
    noinline content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    this.composable<T>(
        typeMap = typeMap,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
    ) {
        content(it)
    }
}