package com.example.poststudy.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.poststudy.data.local.DatabaseHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onBack: () -> Unit
) {
    var isRegistered by remember { mutableStateOf(DatabaseHelper.isUserRegistered()) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val handleAction = {
        if (username.isBlank() || password.isBlank()) {
            errorMessage = "Maydonlarni to'ldirish shart"
        } else if (!isRegistered && password != confirmPassword) {
            errorMessage = "Parollar mos kelmadi"
        } else {
            if (!isRegistered) {
                DatabaseHelper.registerUser(username, password)
                onLoginSuccess()
            } else {
                if (DatabaseHelper.validateUser(username, password)) {
                    onLoginSuccess()
                } else {
                    errorMessage = "Login yoki parol noto'g'ri"
                }
            }
        }
    }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0F172A),
            Color(0xFF1E293B),
            Color(0xFF334155)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        // Subtle decorative circles
        Box(
            modifier = Modifier
                .size(500.dp)
                .offset(x = (-150).dp, y = (-150).dp)
                .background(Color(0xFF6366F1).copy(alpha = 0.05f), CircleShape)
        )

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .onPreviewKeyEvent {
                    if (it.key == Key.Enter && it.type == KeyEventType.KeyDown) {
                        handleAction()
                        true
                    } else false
                },
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Orqaga", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.width(450.dp).padding(16.dp),
                    shape = RoundedCornerShape(28.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(40.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isRegistered) "Xush kelibsiz" else "Hisob yaratish",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E293B)
                        )
                        
                        Text(
                            text = if (isRegistered) "Davom etish uchun tizimga kiring" else "O'qituvchi profilini sozlang",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF64748B),
                            modifier = Modifier.padding(bottom = 32.dp)
                        )

                        if (errorMessage.isNotEmpty()) {
                            Surface(
                                color = Color(0xFFFEE2E2),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
                            ) {
                                Text(
                                    text = errorMessage,
                                    color = Color(0xFF991B1B),
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }

                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it; errorMessage = "" },
                            label = { Text("Foydalanuvchi nomi") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF6366F1),
                                focusedLabelColor = Color(0xFF6366F1)
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it; errorMessage = "" },
                            label = { Text("Parol") },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF6366F1),
                                focusedLabelColor = Color(0xFF6366F1)
                            )
                        )

                        if (!isRegistered) {
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it; errorMessage = "" },
                                label = { Text("Parolni tasdiqlang") },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF6366F1),
                                    focusedLabelColor = Color(0xFF6366F1)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = handleAction,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
                        ) {
                            Text(
                                text = if (isRegistered) "Kirish" else "Ro'yxatdan o'tish",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
