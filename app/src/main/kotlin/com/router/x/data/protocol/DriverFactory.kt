package com.router.x.data.protocol

import com.router.x.data.model.RouterItem
import com.router.x.data.model.RouterProtocol

object DriverFactory {
    private val simulatorDriver = SimulatorDriver()
    private val tpLinkDriver = TPLinkDriver()
    private val mikrotikDriver = MikrotikDriver()
    private val openWrtDriver = OpenWrtDriver()
    private val uniFiDriver = UniFiDriver()
    private val upnpDriver = UpnpDriver()

    fun getDriver(router: RouterItem): RouterDriver {
        return when (router.protocol) {
            RouterProtocol.SIMULATOR -> simulatorDriver
            RouterProtocol.TPLINK_HTTP -> tpLinkDriver
            RouterProtocol.MIKROTIK_REST -> mikrotikDriver
            RouterProtocol.OPENWRT_UBUS -> openWrtDriver
            RouterProtocol.UNIFI_API -> uniFiDriver
            RouterProtocol.UPNP_IGD -> upnpDriver
            RouterProtocol.AUTO_DETECT,
            RouterProtocol.SNMP,
            RouterProtocol.SSH_CLI -> {
                // If simulator / preview model, use simulator or auto fallback
                if (router.ipAddress.startsWith("192.168.0.1")) tpLinkDriver
                else if (router.ipAddress.startsWith("10.0.0.1")) mikrotikDriver
                else if (router.ipAddress.startsWith("192.168.10.1")) openWrtDriver
                else if (router.ipAddress.startsWith("203.0.113.1")) uniFiDriver
                else simulatorDriver
            }
        }
    }
}
