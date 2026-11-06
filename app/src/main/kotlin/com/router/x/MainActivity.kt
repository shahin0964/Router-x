package com.router.x

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.router.x.navigation.Screen
import com.router.x.theme.RouterXTheme
import com.router.x.ui.screens.AdvancedSettingsScreen
import com.router.x.ui.screens.ConnectedDevicesScreen
import com.router.x.ui.screens.ControlCenterScreen
import com.router.x.ui.screens.HomeScreen
import com.router.x.ui.screens.MonitoringScreen
import com.router.x.ui.screens.NetworkMapScreen
import com.router.x.ui.screens.RouterWebConsoleScreen
import com.router.x.ui.screens.RoutersScreen
import com.router.x.ui.screens.SplashScreen
import com.router.x.viewmodel.RouterViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: RouterViewModel by viewModels()

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        // Permissions handled
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request real network & Wi-Fi permissions
        val permissionsToRequest = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsToRequest.add(Manifest.permission.NEARBY_WIFI_DEVICES)
        }

        val needed = permissionsToRequest.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (needed.isNotEmpty()) {
            permissionLauncher.launch(needed.toTypedArray())
        }

        setContent {
            RouterXTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Screen.Splash.route,
                    enterTransition = { fadeIn(animationSpec = tween(250)) },
                    exitTransition = { fadeOut(animationSpec = tween(250)) },
                    popEnterTransition = { fadeIn(animationSpec = tween(250)) },
                    popExitTransition = { fadeOut(animationSpec = tween(250)) }
                ) {
                    composable(Screen.Splash.route) {
                        SplashScreen(
                            onNavigateToHome = {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Splash.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(Screen.Home.route) {
                        HomeScreen(
                            viewModel = viewModel,
                            onNavigate = { screen ->
                                if (screen.route != Screen.Home.route) {
                                    navController.navigate(screen.route)
                                }
                            }
                        )
                    }

                    composable(Screen.Routers.route) {
                        RoutersScreen(
                            viewModel = viewModel,
                            onNavigate = { screen ->
                                if (screen.route == Screen.Home.route) {
                                    navController.popBackStack(Screen.Home.route, false)
                                } else {
                                    navController.navigate(screen.route)
                                }
                            }
                        )
                    }

                    composable(Screen.ControlCenter.route) {
                        ControlCenterScreen(
                            viewModel = viewModel,
                            onNavigate = { screen ->
                                if (screen.route == Screen.Home.route) {
                                    navController.popBackStack(Screen.Home.route, false)
                                } else {
                                    navController.navigate(screen.route)
                                }
                            }
                        )
                    }

                    composable(Screen.Monitoring.route) {
                        MonitoringScreen(
                            viewModel = viewModel,
                            onNavigate = { screen ->
                                if (screen.route == Screen.Home.route) {
                                    navController.popBackStack(Screen.Home.route, false)
                                } else {
                                    navController.navigate(screen.route)
                                }
                            }
                        )
                    }

                    composable(Screen.ConnectedDevices.route) {
                        ConnectedDevicesScreen(
                            viewModel = viewModel,
                            onNavigate = { screen ->
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(Screen.NetworkMap.route) {
                        NetworkMapScreen(
                            viewModel = viewModel,
                            onNavigate = { screen ->
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(Screen.AdvancedSettings.route) {
                        AdvancedSettingsScreen(
                            viewModel = viewModel,
                            onNavigate = { screen ->
                                if (screen == Screen.WebConsole) {
                                    navController.navigate(Screen.WebConsole.route)
                                } else {
                                    navController.popBackStack()
                                }
                            }
                        )
                    }

                    composable(Screen.WebConsole.route) {
                        RouterWebConsoleScreen(
                            viewModel = viewModel,
                            onNavigate = { screen ->
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
