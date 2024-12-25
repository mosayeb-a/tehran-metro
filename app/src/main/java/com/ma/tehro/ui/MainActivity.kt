package com.ma.tehro.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.ma.tehro.common.LinesScreen
import com.ma.tehro.common.PathFinderScreen
import com.ma.tehro.common.StationSelectorScreen
import com.ma.tehro.common.StationsScreen
import com.ma.tehro.ui.line.LineViewModel
import com.ma.tehro.ui.line.Lines
import com.ma.tehro.ui.line.stations.Stations
import com.ma.tehro.ui.shortestpath.ShortestPathViewModel
import com.ma.tehro.ui.shortestpath.StationSelector
import com.ma.tehro.ui.shortestpath.pathfinder.PathFinder
import com.ma.tehro.ui.theme.Gray
import com.ma.tehro.ui.theme.TehroTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Gray.toArgb()
        WindowCompat.getInsetsController(window, window.decorView)
            .isAppearanceLightStatusBars = false

        setContent {
            TehroTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = LinesScreen) {
                    composable<LinesScreen>(
                        enterTransition = { defaultEnterTransition() },
                        exitTransition = { defaultExitTransition() },
                        popEnterTransition = { defaultEnterTransition() },
                        popExitTransition = { defaultExitTransition() }
                    ) {
                        val metroViewModel: LineViewModel = hiltViewModel(it)
                        Lines(
                            navController = navController,
                            lines = metroViewModel.getLines(),
                            onFindPathClicked = {
                                navController.navigate(StationSelectorScreen)
                            }
                        )
                    }
                    composable<StationsScreen>(
                        enterTransition = { defaultEnterTransition() },
                        exitTransition = { defaultExitTransition() },
                        popEnterTransition = { defaultEnterTransition() },
                        popExitTransition = { defaultExitTransition() }
                    ) { backStackEntry ->
                        val metroViewModel: LineViewModel = hiltViewModel(backStackEntry)
                        val args = backStackEntry.toRoute<StationsScreen>()
                        Stations(
                            lineNumber = args.lineNumber,
                            orderedStations = metroViewModel.getOrderedStationsByLine(args.lineNumber),
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                    composable<StationSelectorScreen>(
                        enterTransition = { defaultEnterTransition() },
                        exitTransition = { defaultExitTransition() },
                        popEnterTransition = { defaultEnterTransition() },
                        popExitTransition = { defaultExitTransition() }
                    ) { backStackEntry ->
                        val viewModel: ShortestPathViewModel = hiltViewModel(backStackEntry)
                        StationSelector(
                            stations = viewModel.stations.map { it.value.property.name },
                            onBack = { navController.popBackStack() },
                            viewState = viewModel.uiState.collectAsStateWithLifecycle().value,
                            onSelectedChange = { isFrom, query ->
                                viewModel.onSelectedChange(isFrom, query)
                            },
                            onFindPathClick = { start, dest ->
                                navController.navigate(PathFinderScreen(start, dest))
                            }
                        )
                    }
                    composable<PathFinderScreen>(
                        enterTransition = { defaultEnterTransition() },
                        exitTransition = { defaultExitTransition() },
                        popEnterTransition = { defaultEnterTransition() },
                        popExitTransition = { defaultExitTransition() }
                    ) { backStackEntry ->
                        val viewModel: ShortestPathViewModel = hiltViewModel(backStackEntry)
                        val args: PathFinderScreen = backStackEntry.toRoute()
                        PathFinder(
                            findShortestPath = {
                                viewModel.findShortestPathWithDirectionCache(
                                    from = args.startStation,
                                    to = args.destination
                                )
                            },
                            onBack = {
                                navController.popBackStack()
                            },
                            from = args.startStation,
                            to = args.destination,
                            getLineByPath = { path -> viewModel.getCachedLineByPath(path) },
                            isIndexInTheRange = { index, pairs ->
                                viewModel.isIndexInTheRange(
                                    index,
                                    pairs
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

fun defaultEnterTransition(): EnterTransition = slideInHorizontally(
    initialOffsetX = { it },
    animationSpec = tween(durationMillis = 300)
)

fun defaultExitTransition(): ExitTransition = slideOutHorizontally(
    targetOffsetX = { -it },
    animationSpec = tween(durationMillis = 300)
)

