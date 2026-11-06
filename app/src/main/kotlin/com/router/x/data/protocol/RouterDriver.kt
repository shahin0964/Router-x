package com.router.x.data.protocol

import com.router.x.data.model.DeviceItem
import com.router.x.data.model.RouterItem
import com.router.x.data.model.TelemetryData
import com.router.x.data.model.WifiConfig

interface RouterDriver {
    suspend fun testConnection(router: RouterItem): Result<Boolean>
    suspend fun fetchTelemetry(router: RouterItem): Result<TelemetryData>
    suspend fun fetchConnectedDevices(router: RouterItem): Result<List<DeviceItem>>
    suspend fun fetchWifiConfig(router: RouterItem): Result<WifiConfig>
    suspend fun applyWifiConfig(router: RouterItem, config: WifiConfig): Result<Boolean>
    suspend fun rebootRouter(router: RouterItem): Result<Boolean>
    suspend fun blockDevice(router: RouterItem, macAddress: String): Result<Boolean>
    suspend fun unblockDevice(router: RouterItem, macAddress: String): Result<Boolean>
}
