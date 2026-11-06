package com.router.x.data.network

import com.router.x.data.model.DeviceType

object VendorLookup {
    private val vendorPrefixes = mapOf(
        "00:1A:11" to ("Google" to DeviceType.PHONE),
        "F4:F5:D8" to ("Google Pixel" to DeviceType.PHONE),
        "3C:22:FB" to ("Dell Technologies" to DeviceType.LAPTOP),
        "B4:45:06" to ("Dell Technologies" to DeviceType.LAPTOP),
        "F8:DB:88" to ("Dell Technologies" to DeviceType.DESKTOP),
        "00:28:F8" to ("HP Inc." to DeviceType.LAPTOP),
        "E4:54:E8" to ("HP Inc." to DeviceType.DESKTOP),
        "48:2C:A0" to ("Samsung Electronics" to DeviceType.PHONE),
        "D0:03:DF" to ("Samsung Smart TV" to DeviceType.TV),
        "AC:5F:3E" to ("Samsung Electronics" to DeviceType.PHONE),
        "FC:8F:90" to ("Apple Inc." to DeviceType.PHONE),
        "F0:18:98" to ("Apple Inc." to DeviceType.LAPTOP),
        "A4:83:E7" to ("Apple Inc." to DeviceType.PHONE),
        "34:42:62" to ("Apple Inc." to DeviceType.PHONE),
        "70:3E:AC" to ("Apple Inc." to DeviceType.LAPTOP),
        "00:11:32" to ("Synology Inc." to DeviceType.DESKTOP),
        "B8:27:EB" to ("Raspberry Pi" to DeviceType.DESKTOP),
        "DC:A6:32" to ("Raspberry Pi" to DeviceType.DESKTOP),
        "50:C7:BF" to ("TP-Link Router" to DeviceType.ACCESS_POINT),
        "AC:84:C6" to ("TP-Link Corporation" to DeviceType.ACCESS_POINT),
        "00:31:92" to ("TP-Link Corporation" to DeviceType.ACCESS_POINT),
        "48:8D:36" to ("MikroTik" to DeviceType.SWITCH),
        "74:4D:28" to ("MikroTik" to DeviceType.ACCESS_POINT),
        "B8:69:F4" to ("MikroTik" to DeviceType.SWITCH),
        "74:83:C2" to ("Ubiquiti Networks" to DeviceType.ACCESS_POINT),
        "80:2A:A8" to ("Ubiquiti Networks" to DeviceType.ACCESS_POINT),
        "F0:9F:C2" to ("Ubiquiti Networks" to DeviceType.SWITCH),
        "60:01:94" to ("Espressif IoT" to DeviceType.PHONE),
        "24:0A:C4" to ("Espressif IoT" to DeviceType.PHONE),
        "78:11:DC" to ("Xiaomi" to DeviceType.PHONE),
        "64:09:80" to ("Xiaomi TV" to DeviceType.TV),
        "00:09:B0" to ("LG Electronics" to DeviceType.TV),
        "64:BC:0C" to ("LG Smart TV" to DeviceType.TV),
        "00:1A:80" to ("Sony Corporation" to DeviceType.TV),
        "00:04:4B" to ("NVIDIA Shield" to DeviceType.TV),
        "00:15:5D" to ("Microsoft" to DeviceType.DESKTOP),
        "58:41:20" to ("Microsoft Xbox" to DeviceType.DESKTOP),
        "00:0C:29" to ("VMware" to DeviceType.DESKTOP),
        "00:1C:42" to ("Parallels" to DeviceType.DESKTOP),
        "F0:D5:BF" to ("Intel Corporation" to DeviceType.LAPTOP),
        "80:32:53" to ("Huawei Technologies" to DeviceType.PHONE),
        "00:1E:10" to ("Huawei Technologies" to DeviceType.ACCESS_POINT),
        "CC:EE:D9" to ("OnePlus" to DeviceType.PHONE)
    )

    fun resolveVendor(macAddress: String): Pair<String, DeviceType> {
        val clean = macAddress.uppercase().replace("-", ":").replace(".", ":")
        if (clean.length >= 8) {
            val prefix = clean.substring(0, 8)
            val match = vendorPrefixes[prefix]
            if (match != null) return match
        }
        return "Network Device" to DeviceType.UNKNOWN
    }
}
