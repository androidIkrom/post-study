package com.example.poststudy.presentation.ui.screens.network

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.poststudy.data.network.NetworkManager
import com.example.poststudy.data.network.SessionData
import com.example.poststudy.presentation.theme.AppDesign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkConnectScreen(
    onConnected: (String, SessionData) -> Unit,
    onBack: () -> Unit
) {
    var ipAddress by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val connect = {
        if (ipAddress.isBlank()) {
            errorMessage = "IP manzilni kiriting"
        } else {
            isLoading = true
            errorMessage = ""
            // Use a coroutine to fetch session
            Thread {
                val session = NetworkManager.fetchSession(ipAddress)
                isLoading = false
                if (session != null) {
                    onConnected(ipAddress, session)
                } else {
                    errorMessage = "Adminga ulanib bo'lmadi. IP manzilni tekshiring."
                }
            }.start()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppDesign.BackgroundGradient),
        contentAlignment = Alignment.Center
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Tarmoqqa ulanish",
                            color = Color(0xFF065F46),
                            fontWeight = FontWeight.Black
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Orqaga",
                                tint = Color(0xFF065F46)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { paddingValues ->
            Box(
                Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.width(500.dp).padding(16.dp),
                    shape = AppDesign.CardShape,
                    elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(
                        3.dp,
                        Color(0xFF6366F1).copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Lan,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color(0xFF6366F1)
                        )

                        Spacer(Modifier.height(24.dp))

                        Text(
                            text = "Admin IP manzili",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1E293B)
                        )

                        Text(
                            text = "Admin ekranidagi IP manzilni kiriting",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF64748B),
                            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
                        )

                        if (errorMessage.isNotEmpty()) {
                            Text(
                                text = errorMessage,
                                color = Color.Red,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }

                        OutlinedTextField(
                            value = ipAddress,
                            onValueChange = { ipAddress = it; errorMessage = "" },
                            label = { Text("masalan, 197.181.1.5") },
                            modifier = Modifier.fillMaxWidth().onPreviewKeyEvent { 
                                if (it.key == Key.Enter && it.type == KeyEventType.KeyDown) {
                                    connect()
                                    true
                                } else false
                            },
                            shape = AppDesign.ComponentShape,
                            singleLine = true,
                            enabled = !isLoading,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(
                                    0xFF6366F1
                                )
                            )
                        )

                        Spacer(Modifier.height(32.dp))

                        Button(
                            onClick = connect,
                            modifier = Modifier.fillMaxWidth().height(64.dp),
                            shape = AppDesign.ComponentShape,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text("ULANISH", fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }
        }
    }
}
