package com.ma.tehro.feature

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.ma.tehro.common.AppSnackbar
import com.ma.tehro.common.LinesScreen
import com.ma.tehro.common.MapScreen
import com.ma.tehro.common.ObserveAsEvents
import com.ma.tehro.common.PathDescriptionScreen
import com.ma.tehro.common.PathFinderScreen
import com.ma.tehro.common.StationDetailScreen
import com.ma.tehro.common.StationSelectorScreen
import com.ma.tehro.common.StationsScreen
import com.ma.tehro.common.SubmitFeedbackScreen
import com.ma.tehro.common.SubmitStationInfoScreen
import com.ma.tehro.common.TrainScheduleScreen
import com.ma.tehro.common.messenger.UiMessageManager
import com.ma.tehro.common.navTypeOf
import com.ma.tehro.data.Station
import com.ma.tehro.feature.detail.StationDetail
import com.ma.tehro.feature.line.LineViewModel
import com.ma.tehro.feature.line.Lines
import com.ma.tehro.feature.line.stations.Stations
import com.ma.tehro.feature.map.StationsMap
import com.ma.tehro.feature.map.StationsMapViewModel
import com.ma.tehro.feature.shortestpath.guide.PathDescription
import com.ma.tehro.feature.shortestpath.guide.PathDescriptionViewModel
import com.ma.tehro.feature.shortestpath.pathfinder.PathFinder
import com.ma.tehro.feature.shortestpath.pathfinder.PathViewModel
import com.ma.tehro.feature.shortestpath.selection.StationSelectionViewModel
import com.ma.tehro.feature.shortestpath.selection.StationSelector
import com.ma.tehro.feature.submit_suggestion.SubmitSuggestionViewModel
import com.ma.tehro.feature.submit_suggestion.feedback.SubmitFeedback
import com.ma.tehro.feature.submit_suggestion.station.SubmitStationInfo
import com.ma.tehro.feature.theme.Gray
import com.ma.tehro.feature.theme.TehroTheme
import com.ma.tehro.feature.train_schedule.TrainSchedule
import com.ma.tehro.feature.train_schedule.TrainScheduleViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.reflect.KType
import kotlin.reflect.typeOf

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var pendingGpsCallback: (() -> Unit)? = null

    private val requestLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pendingGpsCallback?.let { it() }
        } else {
            Toast.makeText(this, "دسترسی به موقعیت مکانی رد شد.", Toast.LENGTH_SHORT).show()
        }
        pendingGpsCallback = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            SystemBarStyle.dark(Color.TRANSPARENT),
            SystemBarStyle.dark(Gray.toArgb())
        )

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
                    // This is just to silence linter complain about not use of inner padding
                    // as each screen has it's own Scaffold also (which should be suboptimal)
                    // thus the one here doesn't need to apply the paddings.
                    innerPadding.let {}
                    NavHost(
                        navController = navController,
                        startDestination = LinesScreen,
                    ) {
                        baseComposable<LinesScreen> {
                            val metroViewModel: LineViewModel = hiltViewModel(it)
                            Lines(
                                onlineClick = { line, isBranch ->
                                    navController.navigate(StationsScreen(line, isBranch))
                                },
                                lines = metroViewModel.getLines(),
                                onFindPathClicked = { navController.navigate(StationSelectorScreen) },
                                onMapClick = { navController.navigate(MapScreen) },
                                onSubmitFeedbackClick = {
                                    navController.navigate(
                                        SubmitFeedbackScreen
                                    )
                                }
                            )
                        }
                        baseComposable<MapScreen> {
                            val viewModel: StationsMapViewModel = hiltViewModel(it)
                            StationsMap(
                                onFindCurrentLocationClick = {
                                    if (hasLocationPermission()) {
                                        ensureGpsEnabled { viewModel.getCurrentLocation() }
                                    } else {
                                        pendingGpsCallback =
                                            { ensureGpsEnabled { viewModel.getCurrentLocation() } }
                                        requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
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
                                useBranch = args.useBranch,
                                orderedStations = metroViewModel.getOrderedStationsInLineByPosition(
                                    args.lineNumber,
                                    args.useBranch
                                ),
                                onBackClick = { navController.popBackStack() },
                                onStationClick = { station, line ->
                                    navController.navigate(
                                        StationDetailScreen(
                                            station = station,
                                            lineNumber = line,
                                            useBranch = args.useBranch
                                        )
                                    )
                                },
                            )
                        }
                        baseComposable<StationSelectorScreen> { backStackEntry ->
                            val viewModel: StationSelectionViewModel = hiltViewModel(backStackEntry)
                            val state by viewModel.uiState.collectAsStateWithLifecycle()
                            StationSelector(
                                onBack = { navController.popBackStack() },
                                viewState = state,
                                onSelectedChange = { isFrom, query, fa ->
                                    viewModel.onSelectedChange(isFrom, query, fa)
                                },
                                onFindPathClick = { startEn, destEn, startFa, destFa ->
                                    navController.navigate(
                                        PathFinderScreen(
                                            startEnStation = startEn,
                                            startFaStation = startFa,
                                            enDestination = destEn,
                                            faDestination = destFa
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
                                        ensureGpsEnabled { viewModel.findNearestStation() }
                                    } else {

                                        pendingGpsCallback =
                                            { ensureGpsEnabled { viewModel.findNearestStation() } }
                                        requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                    }
                                },
                            )
                        }
                        baseComposable<PathFinderScreen> { backStackEntry ->
                            val viewModel: PathViewModel = hiltViewModel(backStackEntry)
                            val state by viewModel.state.collectAsStateWithLifecycle()
                            val args: PathFinderScreen = backStackEntry.toRoute()
                            PathFinder(
                                state = state,
                                onBack = { navController.popBackStack() },
                                fromEn = args.startEnStation,
                                toEn = args.enDestination,
                                onStationClick = { station, line ->
                                    navController.navigate(
                                        StationDetailScreen(
                                            station = station,
                                            lineNumber = line,
                                            useBranch = false
                                        )
                                    )
                                },
                                fromFa = args.startFaStation,
                                toFa = args.faDestination,
                                onInfoClick = {
                                    navController.navigate(PathDescriptionScreen(viewModel.generateGuidSteps()))
                                }
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
                                useBranch = args.useBranch,
                                onSubmitInfoStationClicked = { station, line ->
                                    navController.navigate(SubmitStationInfoScreen(station, line))
                                },
                                onTrainScheduleClick = { name, fa, line, useBranch ->
                                    navController.navigate(
                                        TrainScheduleScreen(
                                            enStationName = name,
                                            lineNumber = line,
                                            useBranch = useBranch,
                                            faStationName = fa
                                        )
                                    )
                                }
                            )
                        }
                        baseComposable<TrainScheduleScreen> {
                            val viewModel: TrainScheduleViewModel = hiltViewModel(it)
                            val state by viewModel.state.collectAsStateWithLifecycle()
                            val args = it.toRoute<TrainScheduleScreen>()
                            TrainSchedule(
                                state = state,
                                faStationName = args.faStationName,
                                lineNumber = args.lineNumber,
                                onBack = { navController.popBackStack() },
                                onScheduleTypeSelected = { destination, scheduleType ->
                                    viewModel.onScheduleTypeSelected(destination, scheduleType)
                                }
                            )
                        }
                        baseComposable<PathDescriptionScreen> {
                            val viewModel: PathDescriptionViewModel = hiltViewModel(it)
                            val state by viewModel.uiState.collectAsStateWithLifecycle()
                            PathDescription(
                                viewState = state,
                                onBackClick = { navController.popBackStack() })
                        }
                        baseComposable<SubmitFeedbackScreen> {
                            val viewModel: SubmitSuggestionViewModel = hiltViewModel(it)
                            val state by viewModel.state.collectAsStateWithLifecycle()
                            SubmitFeedback(
                                onSendMessageClicked = { message ->
                                    viewModel.sendSimpleFeedback(
                                        message
                                    )
                                },
                                viewState = state,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        baseComposable<SubmitStationInfoScreen>(
                            typeMap = mapOf(typeOf<Station>() to navTypeOf<Station>()),
                        ) { backStackEntry ->
                            val viewModel: SubmitSuggestionViewModel = hiltViewModel(backStackEntry)
                            val args: SubmitStationInfoScreen = backStackEntry.toRoute()
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
        ActivityResultContracts.StartActivityForResult()
    ) {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            pendingGpsCallback?.invoke()
        } else {
            Toast.makeText(
                this,
                "برای یافتن نزدیک ترین ایستگاه، روشن بودن GPS ضروری است.",
                Toast.LENGTH_SHORT
            ).show()
        }
        pendingGpsCallback = null
    }

    private fun ensureGpsEnabled(onSuccess: () -> Unit) {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            onSuccess()
        } else {
            pendingGpsCallback = onSuccess
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            enableLocationLauncher.launch(intent)
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
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