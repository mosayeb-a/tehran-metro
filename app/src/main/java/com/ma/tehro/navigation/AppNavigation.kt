package com.ma.tehro.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.ma.tehro.common.AboutScreen
import com.ma.tehro.common.LinesScreen
import com.ma.tehro.common.LocationPermissionHandler
import com.ma.tehro.common.MapScreen
import com.ma.tehro.common.OfficialMetroMapScreen
import com.ma.tehro.common.PathDescriptionScreen
import com.ma.tehro.common.PathFinderScreen
import com.ma.tehro.common.StationDetailScreen
import com.ma.tehro.common.StationSelectorScreen
import com.ma.tehro.common.StationsScreen
import com.ma.tehro.common.SubmitFeedbackScreen
import com.ma.tehro.common.SubmitStationInfoScreen
import com.ma.tehro.common.TrainScheduleScreen
import com.ma.tehro.common.navTypeOf
import com.ma.tehro.data.Station
import com.ma.tehro.feature.about.About
import com.ma.tehro.feature.detail.StationDetail
import com.ma.tehro.feature.line.LineViewModel
import com.ma.tehro.feature.line.Lines
import com.ma.tehro.feature.line.stations.Stations
import com.ma.tehro.feature.line.stations.StationsViewModel
import com.ma.tehro.feature.map.city.StationsMap
import com.ma.tehro.feature.map.city.StationsMapViewModel
import com.ma.tehro.feature.map.official_pic.OfficialMapPicture
import com.ma.tehro.feature.shortestpath.guide.PathDescription
import com.ma.tehro.feature.shortestpath.guide.PathDescriptionViewModel
import com.ma.tehro.feature.shortestpath.pathfinder.PathFinder
import com.ma.tehro.feature.shortestpath.pathfinder.PathViewModel
import com.ma.tehro.feature.shortestpath.selection.StationSelectionViewModel
import com.ma.tehro.feature.shortestpath.selection.StationSelector
import com.ma.tehro.feature.submit_suggestion.SubmitSuggestionViewModel
import com.ma.tehro.feature.submit_suggestion.feedback.SubmitFeedback
import com.ma.tehro.feature.submit_suggestion.station.SubmitStationInfo
import com.ma.tehro.feature.train_schedule.TrainSchedule
import com.ma.tehro.feature.train_schedule.TrainScheduleViewModel
import kotlin.reflect.KType
import kotlin.reflect.typeOf

@Composable
fun AppNavigation(
    navController: NavHostController,
    locationPermissionHandler: LocationPermissionHandler,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = LinesScreen,
        modifier = modifier
    ) {
        baseComposable<LinesScreen> {
            val lineViewModel: LineViewModel = hiltViewModel(it)
            val state by lineViewModel.uiState.collectAsStateWithLifecycle()
            Lines(
                onlineClick = { line, isBranch ->
                    navController.navigate(StationsScreen(line, isBranch))
                },
                lines = state.lines,
                onFindPathClicked = { navController.navigate(StationSelectorScreen) },
                onMapClick = { navController.navigate(MapScreen) },
                onSubmitFeedbackClick = {
                    navController.navigate(
                        SubmitFeedbackScreen
                    )
                },
                onPathFinderClick = {
                    navController.navigate(StationSelectorScreen)
                },
                onMetroMapClick = {
                    navController.navigate(OfficialMetroMapScreen)
                },
                onAboutClick = { navController.navigate(AboutScreen) }
            )
        }
        baseComposable<MapScreen> {
            val viewModel: StationsMapViewModel = hiltViewModel(it)
            StationsMap(
                onFindCurrentLocationClick = {
                    locationPermissionHandler.checkLocationPermission {
                        viewModel.getCurrentLocation()
                    }
                },
                viewState = viewModel.uiState.collectAsStateWithLifecycle().value,
            )
        }
        baseComposable<StationsScreen> { backStackEntry ->
            val stationsViewModel: StationsViewModel = hiltViewModel(backStackEntry)
            val state by stationsViewModel.uiState.collectAsStateWithLifecycle()
            val args = backStackEntry.toRoute<StationsScreen>()
            Stations(
                lineNumber = args.lineNumber,
                useBranch = args.useBranch,
                orderedStations = state.stations,
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
                onFindPathClick = { startEn, destEn, startFa, destFa, lineChangeDelayMinutes, dayOfWeek, currentTime ->
                    navController.navigate(
                        PathFinderScreen(
                            startEnStation = startEn,
                            startFaStation = startFa,
                            enDestination = destEn,
                            faDestination = destFa,
                            dayOfWeek = dayOfWeek,
                            currentTime = currentTime,
                            lineChangeDelayMinutes = lineChangeDelayMinutes,

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
                    locationPermissionHandler.checkLocationPermission {
                        viewModel.findNearestStation()
                    }
                },
                onLineChangeDelayChanged = { viewModel.onLineChangeDelayChanged(it) },
                onTimeChanged = { viewModel.onTimeChanged(it) },
                onDayOfWeekChanged = { viewModel.onDayOfWeekChanged(it) },

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
                },
                lineChangeDelayMinutes = args.lineChangeDelayMinutes
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
        baseComposable<OfficialMetroMapScreen> { backStackEntry ->
            OfficialMapPicture(
                onBack = { navController.popBackStack() },
            )
        }
        baseComposable<AboutScreen> {
            About(
                onBack = { navController.popBackStack() }
            )
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