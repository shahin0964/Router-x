package com.router.x.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.router.x.navigation.Screen
import com.router.x.theme.CyberSurfaceVariant
import com.router.x.theme.NeonCyan
import com.router.x.theme.TextMuted
import com.router.x.theme.TextPrimary
import com.router.x.ui.components.CyberBackground
import com.router.x.viewmodel.RouterViewModel

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun RouterWebConsoleScreen(
    viewModel: RouterViewModel,
    onNavigate: (Screen) -> Unit
) {
    val selectedRouter by viewModel.selectedRouter.collectAsState()
    val context = LocalContext.current
    var webView: WebView? by remember { mutableStateOf<WebView?>(null) }
    var progress by remember { mutableIntStateOf(0) }

    val routerUrl = "http://${selectedRouter.ipAddress}:${selectedRouter.credentials.port}"

    CyberBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onNavigate(Screen.Home) }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = selectedRouter.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = routerUrl,
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                IconButton(onClick = { webView?.reload() }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reload",
                        tint = NeonCyan
                    )
                }

                IconButton(onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(routerUrl))
                    context.startActivity(intent)
                }) {
                    Icon(
                        imageVector = Icons.Default.OpenInBrowser,
                        contentDescription = "Browser",
                        tint = TextPrimary
                    )
                }
            }

            if (progress in 1..99) {
                LinearProgressIndicator(
                    progress = { progress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = NeonCyan
                )
            }

            // Quick Brand Credential Helper Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("admin / admin", "admin / password", "telecomadmin", "user / user").forEach { cred ->
                    val clean = cred.replace(" ", "")
                    Surface(
                        modifier = Modifier
                            .clickable {
                                val parts = clean.split("/")
                                val user = parts[0]
                                val pass = if (parts.size > 1) parts[1] else "admintelecom"
                                webView?.evaluateJavascript(
                                    """
                                    (function() {
                                        var inputs = document.getElementsByTagName('input');
                                        for (var i = 0; i < inputs.length; i++) {
                                            var type = inputs[i].type ? inputs[i].type.toLowerCase() : '';
                                            var name = inputs[i].name ? inputs[i].name.toLowerCase() : '';
                                            var id = inputs[i].id ? inputs[i].id.toLowerCase() : '';
                                            if (type === 'password' || name.indexOf('pass') !== -1 || id.indexOf('pass') !== -1) {
                                                inputs[i].value = '$pass';
                                            } else if (type === 'text' && (name.indexOf('user') !== -1 || id.indexOf('user') !== -1 || name.indexOf('admin') !== -1)) {
                                                inputs[i].value = '$user';
                                            }
                                        }
                                    })();
                                    """.trimIndent(), null
                                )
                                android.widget.Toast.makeText(context, "Filled: $cred", android.widget.Toast.LENGTH_SHORT).show()
                            },
                        shape = RoundedCornerShape(8.dp),
                        color = CyberSurfaceVariant
                    ) {
                        Text(
                            text = cred,
                            fontSize = 10.sp,
                            color = NeonCyan,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Embedded Real Router Web Admin Console
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            databaseEnabled = true
                            useWideViewPort = true
                            loadWithOverviewMode = true
                            cacheMode = WebSettings.LOAD_DEFAULT
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            userAgentString = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
                        }
                        webViewClient = object : WebViewClient() {
                            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                                // Allow self-signed certificates on local router IPs
                                handler?.proceed()
                            }
                        }
                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                progress = newProgress
                            }
                        }
                        loadUrl(routerUrl)
                        webView = this
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
