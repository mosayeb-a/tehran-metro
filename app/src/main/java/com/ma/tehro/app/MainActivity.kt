package com.ma.tehro.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.ma.tehro.common.LocationPermissionHandler
import com.ma.tehro.common.ObserveAsEvents
import com.ma.tehro.common.ui.AppSnackbar
import com.ma.tehro.common.ui.UiMessageManager
import com.ma.tehro.common.ui.theme.DarkGray
import com.ma.tehro.common.ui.theme.TehroTheme
import com.ma.tehro.feature.more.PreferencesViewModel
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
            SystemBarStyle.Companion.dark(Color.TRANSPARENT),
            SystemBarStyle.Companion.dark(DarkGray.copy(alpha = .5f).toArgb())
        )

        setContent {
            val preferencesViewModel: PreferencesViewModel = hiltViewModel()
            val currentTheme by preferencesViewModel.currentTheme.collectAsStateWithLifecycle()
            if (currentTheme == null) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DarkGray)
                ) {
                    CircularProgressIndicator(color = White)
                }
            } else {
                TehroTheme(colorScheme = currentTheme!!.colorScheme) {
                    val snackbarHostState = remember { SnackbarHostState() }
                    val scope = rememberCoroutineScope()

                    val keyboardController = LocalSoftwareKeyboardController.current
                    val focusManager = LocalFocusManager.current
                    ObserveAsEvents(
                        flow = UiMessageManager.events,
                        snackbarHostState
                    ) { event ->
                        scope.launch {
                            focusManager.clearFocus(force = true)
                            keyboardController?.hide()

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
                        innerPadding.let {}
                        AppNavigation(
                            navController = navController,
                            locationPermissionHandler = locationPermissionHandler,
                            preferencesViewModel = preferencesViewModel
                        )
                    }
                }
            }
        }
    }
}