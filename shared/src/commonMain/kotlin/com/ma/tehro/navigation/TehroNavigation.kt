package com.ma.tehro.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.ma.tehro.common.LocationPermissionHandler
import com.ma.tehro.common.navTypeOf
import com.ma.tehro.common.ui.LinesScreen
import com.ma.tehro.common.ui.MapScreen
import com.ma.tehro.common.ui.MapViewerScreen
import com.ma.tehro.common.ui.MoreScreen
import com.ma.tehro.common.ui.PathDescriptionScreen
import com.ma.tehro.common.ui.PathFinderScreen
import com.ma.tehro.common.ui.PodcastListScreen
import com.ma.tehro.common.ui.StationDetailScreen
import com.ma.tehro.common.ui.StationSelectorScreen
import com.ma.tehro.common.ui.StationsScreen
import com.ma.tehro.common.ui.SubmitFeedbackScreen
import com.ma.tehro.common.ui.TrainScheduleScreen
import com.ma.tehro.domain.common.BilingualName
import com.ma.tehro.domain.line.Station
import com.ma.tehro.domain.path.Step
import com.ma.tehro.feature.detail.StationDetail
import com.ma.tehro.feature.feedback.Feedback
import com.ma.tehro.feature.feedback.FeedbackViewModel
import com.ma.tehro.feature.line.LineViewModel
import com.ma.tehro.feature.line.Lines
import com.ma.tehro.feature.line.stations.Stations
import com.ma.tehro.feature.line.stations.StationsViewModel
import com.ma.tehro.feature.map.city.StationsMapViewModel
import com.ma.tehro.feature.map.city.StationsOnCityMap
import com.ma.tehro.feature.map.viewer.MapViewer
import com.ma.tehro.feature.more.More
import com.ma.tehro.feature.more.PreferencesViewModel
import com.ma.tehro.feature.podcast.PodcastList
import com.ma.tehro.feature.podcast.PodcastViewModel
import com.ma.tehro.feature.schedule.TrainSchedule
import com.ma.tehro.feature.schedule.TrainScheduleViewModel
import com.ma.tehro.feature.shortestpath.guide.PathDescription
import com.ma.tehro.feature.shortestpath.pathfinder.PathFinder
import com.ma.tehro.feature.shortestpath.pathfinder.PathViewModel
import com.ma.tehro.feature.shortestpath.selection.StationSelector
import com.ma.tehro.feature.shortestpath.selection.StationSelectorViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KType
import kotlin.reflect.typeOf

@Composable
fun TehroNavigation(
    navController: NavHostController,
    locationPermissionHandler: LocationPermissionHandler,
    modifier: Modifier = Modifier,
    preferencesViewModel: PreferencesViewModel
) {
    NavHost(
        navController = navController,
        startDestination = LinesScreen,
        modifier = modifier
    ) {
        baseComposable<LinesScreen> {
            val lineViewModel: LineViewModel = koinViewModel()
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
                    navController.navigate(MapViewerScreen(null))
                },
                onMoreClick = { navController.navigate(MoreScreen) },
                onPodcastClick = { navController.navigate(PodcastListScreen) }
            )
        }
        baseComposable<MapScreen> {
            val viewModel: StationsMapViewModel = koinViewModel()
            StationsOnCityMap(
                onFindCurrentLocationClick = {
                    locationPermissionHandler.checkLocationPermission {
                        viewModel.locateMe()
                    }
                },
                viewState = viewModel.uiState.collectAsStateWithLifecycle().value,
            )
        }
        baseComposable<StationsScreen> { backStackEntry ->
            val stationsViewModel: StationsViewModel = koinViewModel()
            val state by stationsViewModel.uiState.collectAsStateWithLifecycle()
            val args = backStackEntry.toRoute<StationsScreen>()
            Stations(
                lineNumber = args.lineNumber,
                useBranch = args.useBranch,
                orderedStations = state.stations,
                onBackClick = navController::navigateUp,
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
            val viewModel: StationSelectorViewModel = koinViewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
            val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()

            StationSelector(
                onBack = navController::navigateUp,
                viewState = state,
                stations = searchResults.stations,
                places = searchResults.places,
                searchQuery = searchQuery,
                onSearchQueryChanged = viewModel::setSearchQuery,
                onSelectStation = { isFrom, station ->
                    if (isFrom) viewModel.setFromStation(station) else viewModel.setToStation(
                        station
                    )
                },
                onFindPath = { from, to, delay, dayOfWeek, time ->
                    navController.navigate(
                        PathFinderScreen(
                            from = from,
                            to = to,
                            dayOfWeek = dayOfWeek,
                            departureTime = time,
                            transferDelayMinutes = delay
                        )
                    )
                },
                onFindNearbyStations = { onError -> viewModel.findNearbyStations(onError = onError) },
                onFindStationsNearPlace = { place ->
                    viewModel.findStationsNear(place.latitude, place.longitude)
                },
                onDelayChange = viewModel::setTransferDelay,
                onTimeChanged = viewModel::setDepartureTime,
                onDayOfWeekChanged = viewModel::setDayOfWeek,
                onCheckPermission = locationPermissionHandler::checkLocationPermission
            )
        }

        baseComposable<PathFinderScreen>(
            typeMap = mapOf(typeOf<BilingualName>() to navTypeOf<BilingualName>()),
        ) { backStackEntry ->
            val args: PathFinderScreen = backStackEntry.toRoute()
            val viewModel: PathViewModel = koinViewModel {
                parametersOf(
                    args.from,
                    args.to,
                    args.dayOfWeek,
                    args.departureTime,
                    args.transferDelayMinutes
                )
            }
            val state by viewModel.state.collectAsStateWithLifecycle()

            PathFinder(
                state = state,
                onBack = navController::navigateUp,
                from = args.from,
                to = args.to,
                onStationClick = { station, line ->
                    navController.navigate(
                        StationDetailScreen(
                            station = station,
                            lineNumber = line,
                            useBranch = false
                        )
                    )
                },
                onRouteGuideClick = {
                    navController.navigate(PathDescriptionScreen(viewModel.generateGuidSteps()))
                },
                transferDelayMinutes = args.transferDelayMinutes,
                onMetroMapClick = { path ->
                    navController.navigate(MapViewerScreen(shortestPath = path))
                }
            )
        }
        baseComposable<StationDetailScreen>(
            typeMap = mapOf(typeOf<Station>() to navTypeOf<Station>()),
        ) { backStackEntry ->
            val args = backStackEntry.toRoute<StationDetailScreen>()
            StationDetail(
                station = args.station,
                onBack = navController::navigateUp,
                lineNumber = args.lineNumber,
                useBranch = args.useBranch,
                onTrainScheduleClick = { station, line, useBranch ->
                    navController.navigate(
                        TrainScheduleScreen(
                            station = station,
                            lineNumber = line,
                            useBranch = useBranch,
                        )
                    )
                }
            )
        }
        baseComposable<TrainScheduleScreen>(
            typeMap = mapOf(typeOf<BilingualName>() to navTypeOf<BilingualName>())
        ) {
            val args = it.toRoute<TrainScheduleScreen>()
            val viewModel: TrainScheduleViewModel = koinViewModel {
                parametersOf(args.station.en, args.lineNumber, args.useBranch)
            }
            val state by viewModel.state.collectAsStateWithLifecycle()

            TrainSchedule(
                state = state,
                faStationName = args.station.fa,
                lineNumber = args.lineNumber,
                onBack = navController::navigateUp,
                onScheduleTypeSelected = { destination, scheduleType ->
                    viewModel.onScheduleTypeSelected(destination, scheduleType)
                }
            )
        }
        baseComposable<PathDescriptionScreen>(
            typeMap = mapOf(typeOf<List<Step>>() to navTypeOf<List<Step>>())
        ) {
            val args = it.toRoute<PathDescriptionScreen>()
            PathDescription(
                steps = args.steps,
                onBackClick = navController::navigateUp
            )
        }
        baseComposable<SubmitFeedbackScreen> {
            val viewModel: FeedbackViewModel = koinViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            Feedback(
                onSendMessage = viewModel::send,
                viewState = state,
                onBack = navController::navigateUp
            )
        }
        baseComposable<MapViewerScreen> { backStackEntry ->
            val args = backStackEntry.toRoute<MapViewerScreen>()
            MapViewer(
                onBack = navController::navigateUp,
                stations = args.shortestPath
            )
        }

        baseComposable<PodcastListScreen> {
            val podcastViewModel: PodcastViewModel = koinViewModel()
            PodcastList(
                viewModel = podcastViewModel,
                onBack = navController::navigateUp
            )
        }


        baseComposable<MoreScreen> {
            More(viewModel = preferencesViewModel)
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