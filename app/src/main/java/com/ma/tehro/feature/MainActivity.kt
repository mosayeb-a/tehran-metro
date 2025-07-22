package com.ma.tehro.feature


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.toArgb
import androidx.navigation.compose.rememberNavController
import com.ma.tehro.common.ui.AppSnackbar
import com.ma.tehro.common.LocationPermissionHandler
import com.ma.tehro.common.ObserveAsEvents
import com.ma.tehro.common.ui.UiMessageManager
import com.ma.tehro.common.ui.theme.DarkGray
import com.ma.tehro.common.ui.theme.TehroTheme
import com.ma.tehro.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var locationPermissionHandler: LocationPermissionHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationPermissionHandler = LocationPermissionHandler(this)

        enableEdgeToEdge(
            SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            SystemBarStyle.dark(DarkGray.toArgb())
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
                    }
                ) { innerPadding ->
                    // This is just to silence linter complain about not use of inner padding
                    // as each screen has it's own Scaffold also (which should be suboptimal)
                    // thus the one here doesn't need to apply the paddings.
                    innerPadding.let {}
                    AppNavigation(
                        navController = navController,
                        locationPermissionHandler = locationPermissionHandler,
                    )
                }
            }
        }
    }
}