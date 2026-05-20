package com.example.poststudy

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.FilterNone
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.compose.resources.painterResource
import poststudy.composeapp.generated.resources.Res
import poststudy.composeapp.generated.resources.icon
import com.example.poststudy.data.local.DatabaseHelper
import com.example.poststudy.data.network.NetworkManager
import com.example.poststudy.presentation.App

fun main() = application {
    DatabaseHelper.init()
    val windowState = rememberWindowState(placement = WindowPlacement.Maximized)
    
    Window(
        onCloseRequest = {
            NetworkManager.stopServer()
            exitApplication()
        },
        title = "BreakPoint",
        state = windowState,
        undecorated = true, // This makes the window match our UI perfectly
        icon = painterResource(Res.drawable.icon)
    ) {
        Column(Modifier.fillMaxSize()) {
            // Custom Title Bar
            val backgroundGradient = Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFF064E3B), // Darker Emerald
                    Color(0xFF10B981)  // Emerald 500
                )
            )
            
            WindowDraggableArea {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .background(backgroundGradient),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "BreakPoint",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Black
                        )
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { windowState.isMinimized = true },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Kichraytirish", tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                            Spacer(Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    windowState.placement = if (windowState.placement == WindowPlacement.Maximized) {
                                        WindowPlacement.Floating
                                    } else {
                                        WindowPlacement.Maximized
                                    }
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                val icon = if (windowState.placement == WindowPlacement.Maximized) Icons.Default.FilterNone else Icons.Default.CropSquare
                                Icon(icon, contentDescription = "Kattalashtirish", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                            Spacer(Modifier.width(8.dp))
                            IconButton(
                                onClick = { exitApplication() },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Yopish", tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
            
            // App Content
            Box(Modifier.weight(1f)) {
                App()
            }
        }
    }
}
