package com.router.x.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Routers : Screen("routers")
    object ControlCenter : Screen("control_center")
    object Monitoring : Screen("monitoring")
    object ConnectedDevices : Screen("connected_devices")
    object NetworkMap : Screen("network_map")
    object AdvancedSettings : Screen("advanced_settings")
    object WebConsole : Screen("web_console")
}
