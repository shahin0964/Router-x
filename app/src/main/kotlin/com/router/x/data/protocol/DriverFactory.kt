package com.router.x.data.protocol

import com.router.x.data.model.RouterItem
import com.router.x.data.model.RouterProtocol

object DriverFactory {
    private val realUniversalDriver = RealUniversalDriver()
    private val tpLinkDriver = TPLinkDriver()
    private val mikrotikDriver = MikrotikDriver()
    private val openWrtDriver = OpenWrtDriver()
    private val uniFiDriver = UniFiDriver()
    private val upnpDriver = UpnpDriver()

    fun getDriver(router: RouterItem): RouterDriver {
        return when (router.protocol) {
            RouterProtocol.TPLINK_HTTP -> tpLinkDriver
            RouterProtocol.MIKROTIK_REST -> mikrotikDriver
            RouterProtocol.OPENWRT_UBUS -> openWrtDriver
            RouterProtocol.UNIFI_API -> uniFiDriver
            RouterProtocol.UPNP_IGD -> upnpDriver
            RouterProtocol.TENDA_HTTP,
            RouterProtocol.ASUS_WRT,
            RouterProtocol.BDCOM_GPON,
            RouterProtocol.NETGEAR_GENIE,
            RouterProtocol.DLINK_HNAP,
            RouterProtocol.HUAWEI_ZTE,
            RouterProtocol.AUTO_DETECT,
            RouterProtocol.SNMP,
            RouterProtocol.SSH_CLI -> realUniversalDriver
        }
    }
}
