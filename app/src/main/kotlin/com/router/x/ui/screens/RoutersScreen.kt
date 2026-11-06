package com.router.x.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.router.x.data.model.RouterItem
import com.router.x.data.model.RouterProtocol
import com.router.x.navigation.Screen
import com.router.x.theme.CyberBackground
import com.router.x.theme.CyberCardBorder
import com.router.x.theme.CyberSurface
import com.router.x.theme.CyberSurfaceVariant
import com.router.x.theme.DeepNeonBlue
import com.router.x.theme.ElectricBlue
import com.router.x.theme.NeonCyan
import com.router.x.theme.NeonGreen
import com.router.x.theme.NeonGreenGlow
import com.router.x.theme.TextMuted
import com.router.x.theme.TextPrimary
import com.router.x.theme.TextSecondary
import androidx.compose.runtime.rememberCoroutineScope
import com.router.x.RouterXApp
import com.router.x.data.network.NetworkScanner
import com.router.x.data.network.RouterProber
import com.router.x.ui.components.CyberBackground
import com.router.x.ui.components.CyberBottomBar
import com.router.x.ui.components.CyberOutlinedButton
import com.router.x.ui.components.CyberPrimaryButton
import com.router.x.ui.components.NeonCard
import com.router.x.viewmodel.RouterViewModel
import kotlinx.coroutines.launch

@Composable
fun RoutersScreen(
    viewModel: RouterViewModel,
    onNavigate: (Screen) -> Unit
) {
    val routers by viewModel.filteredRouters.collectAsState()
    val selectedRouter by viewModel.selectedRouter.collectAsState()
    val searchQuery by viewModel.routerSearchQuery.collectAsState()
    val currentTab by viewModel.routerTabFilter.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddRouterDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, model, ip, protocol, isLocal ->
                viewModel.addRouter(name, model, ip, protocol, isLocal)
                showAddDialog = false
            }
        )
    }

    CyberBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onNavigate(Screen.Home) }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }

                    Text(
                        text = "My Routers",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = TextPrimary
                        )
                    }
                }

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.routerSearchQuery.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("Search router...", color = TextMuted, fontSize = 14.sp)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = TextMuted
                        )
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CyberSurface,
                        unfocusedContainerColor = CyberSurface,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = CyberCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Filter Tabs (All, Local, Remote)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf("All", "Local", "Remote").forEach { tab ->
                        val isSelected = currentTab == tab
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) DeepNeonBlue else CyberSurface,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) NeonCyan else CyberCardBorder
                            ),
                            modifier = Modifier.clickable { viewModel.routerTabFilter.value = tab }
                        ) {
                            Text(
                                text = tab,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) NeonCyan else TextSecondary,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 7.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Routers List
                if (routers.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No Routers Configured.\nTap + Add Router or connect to Wi-Fi to auto-detect.",
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(routers) { router ->
                            val isCurrent = router.id == selectedRouter.id
                            RouterListItemCard(
                                router = router,
                                isSelected = isCurrent,
                                onClick = {
                                    viewModel.selectRouter(router)
                                    if (router.isAuthenticated) {
                                        onNavigate(Screen.ControlCenter)
                                    } else {
                                        onNavigate(Screen.RouterLogin)
                                    }
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // + Add Router Button
                CyberOutlinedButton(
                    text = "+ Add Router",
                    onClick = { showAddDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = NeonCyan
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Bottom Navigation Bar
            CyberBottomBar(
                currentScreen = Screen.Routers,
                onNavigate = onNavigate
            )
        }
    }
}

@Composable
fun RouterListItemCard(
    router: RouterItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    NeonCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (isSelected) NeonCyan else CyberCardBorder,
        borderWidth = if (isSelected) 1.5.dp else 1.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(46.dp),
                shape = RoundedCornerShape(12.dp),
                color = CyberSurfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Router,
                        contentDescription = null,
                        tint = if (isSelected) NeonCyan else TextSecondary,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = router.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${router.modelName} • ${router.ipAddress}",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }

            // Badge
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (router.isAuthenticated) NeonGreenGlow else Color(0x33FF9E00),
                border = BorderStroke(1.dp, if (router.isAuthenticated) NeonGreen else Color(0xFFFF9E00))
            ) {
                Text(
                    text = if (router.isAuthenticated) "✓ Master" else "🔒 Locked",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (router.isAuthenticated) NeonGreen else Color(0xFFFF9E00),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }

            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                    tint = TextMuted
                )
            }
        }
    }
}

@Composable
fun AddRouterDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, model: String, ip: String, protocol: RouterProtocol, isLocal: Boolean) -> Unit
) {
    var name by remember { mutableStateOf("TP-Link Router") }
    var model by remember { mutableStateOf("TP-Link Archer Series") }
    var ip by remember { mutableStateOf("192.168.0.1") }
    var selectedProtocol by remember { mutableStateOf(RouterProtocol.TPLINK_HTTP) }
    var isLocal by remember { mutableStateOf(true) }
    var expandedProtocolDropdown by remember { mutableStateOf(false) }
    var isAutoDetecting by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val popularBrands = listOf(
        Triple("TP-Link", "Archer Series", RouterProtocol.TPLINK_HTTP),
        Triple("Tenda", "AC / F Series", RouterProtocol.TENDA_HTTP),
        Triple("ASUS", "RT / TUF AsusWRT", RouterProtocol.ASUS_WRT),
        Triple("BDCOM", "GPON / EPON ONU", RouterProtocol.BDCOM_GPON),
        Triple("Netgear", "Nighthawk Series", RouterProtocol.NETGEAR_GENIE),
        Triple("D-Link", "DIR Series", RouterProtocol.DLINK_HNAP),
        Triple("Huawei/ZTE", "Optical ONU", RouterProtocol.HUAWEI_ZTE),
        Triple("MikroTik", "RouterOS", RouterProtocol.MIKROTIK_REST),
        Triple("OpenWrt", "LuCI", RouterProtocol.OPENWRT_UBUS)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CyberSurface,
        title = {
            Text(
                text = "Add Managed Router",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = NeonCyan
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Quick Auto-Detect Button
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            scope.launch {
                                isAutoDetecting = true
                                val wifi = NetworkScanner.getWifiInfo(RouterXApp.instance)
                                val (probedModel, probedProto) = RouterProber.probeRouter(wifi.gatewayIp)
                                ip = wifi.gatewayIp
                                name = if (wifi.ssid != "Not Connected") "${wifi.ssid} Router" else "Gateway Router"
                                model = probedModel
                                selectedProtocol = probedProto
                                isAutoDetecting = false
                            }
                        },
                    shape = RoundedCornerShape(10.dp),
                    color = CyberSurfaceVariant,
                    border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(imageVector = Icons.Default.Router, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAutoDetecting) "Scanning Network..." else "⚡ Auto-Detect Connected Router",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan
                        )
                    }
                }

                // Brand Selector Chips
                Text(text = "Quick Brand Presets:", fontSize = 11.sp, color = TextSecondary)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    popularBrands.take(3).forEach { (bName, bModel, bProto) ->
                        val isSelected = selectedProtocol == bProto
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    selectedProtocol = bProto
                                    name = "$bName Router"
                                    model = "$bName $bModel"
                                    ip = bProto.defaultIp
                                },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) NeonCyan.copy(alpha = 0.2f) else CyberSurfaceVariant,
                            border = BorderStroke(1.dp, if (isSelected) NeonCyan else CyberCardBorder)
                        ) {
                            Text(
                                text = bName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) NeonCyan else TextPrimary,
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    popularBrands.drop(3).take(3).forEach { (bName, bModel, bProto) ->
                        val isSelected = selectedProtocol == bProto
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    selectedProtocol = bProto
                                    name = "$bName Router"
                                    model = "$bName $bModel"
                                    ip = bProto.defaultIp
                                },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) NeonCyan.copy(alpha = 0.2f) else CyberSurfaceVariant,
                            border = BorderStroke(1.dp, if (isSelected) NeonCyan else CyberCardBorder)
                        ) {
                            Text(
                                text = bName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) NeonCyan else TextPrimary,
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    popularBrands.drop(6).take(3).forEach { (bName, bModel, bProto) ->
                        val isSelected = selectedProtocol == bProto
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    selectedProtocol = bProto
                                    name = "$bName Router"
                                    model = "$bName $bModel"
                                    ip = bProto.defaultIp
                                },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) NeonCyan.copy(alpha = 0.2f) else CyberSurfaceVariant,
                            border = BorderStroke(1.dp, if (isSelected) NeonCyan else CyberCardBorder)
                        ) {
                            Text(
                                text = bName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) NeonCyan else TextPrimary,
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Router Name", color = TextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = CyberCardBorder
                    )
                )

                OutlinedTextField(
                    value = ip,
                    onValueChange = { ip = it },
                    label = { Text("IP Address / Hostname", color = TextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = CyberCardBorder
                    )
                )

                OutlinedTextField(
                    value = model,
                    onValueChange = { model = it },
                    label = { Text("Router Brand & Model", color = TextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = CyberCardBorder
                    )
                )

                // Protocol Selector
                Box {
                    OutlinedTextField(
                        value = selectedProtocol.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Management Protocol", color = TextSecondary) },
                        trailingIcon = {
                            TextButton(onClick = { expandedProtocolDropdown = true }) {
                                Text("Select", color = NeonCyan)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = CyberCardBorder
                        )
                    )

                    DropdownMenu(
                        expanded = expandedProtocolDropdown,
                        onDismissRequest = { expandedProtocolDropdown = false },
                        modifier = Modifier.background(CyberSurfaceVariant)
                    ) {
                        RouterProtocol.entries.forEach { proto ->
                            DropdownMenuItem(
                                text = { Text(proto.displayName, color = TextPrimary) },
                                onClick = {
                                    selectedProtocol = proto
                                    ip = proto.defaultIp
                                    expandedProtocolDropdown = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onAdd(name, model, ip, selectedProtocol, isLocal) }
            ) {
                Text("Add Router", color = NeonCyan, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
