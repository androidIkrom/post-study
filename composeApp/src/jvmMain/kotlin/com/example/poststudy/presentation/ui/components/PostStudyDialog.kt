package com.example.poststudy.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun PostStudyDialog(
    onDismissRequest: () -> Unit,
    title: String,
    text: String,
    confirmText: String = "Tasdiqlash",
    dismissText: String = "Bekor qilish",
    confirmColor: Color = Color(0xFF6366F1), // Default Indigo
    onConfirm: () -> Unit,
    content: @Composable (() -> Unit)? = null
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier
                .width(400.dp)
                .onPreviewKeyEvent {
                    if (it.type == KeyEventType.KeyDown) {
                        when (it.key) {
                            Key.Enter -> {
                                onConfirm()
                                true
                            }
                            Key.Escape -> {
                                onDismissRequest()
                                true
                            }
                            else -> false
                        }
                    } else false
                },
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1E293B)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF64748B),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                if (content != null) {
                    Spacer(modifier = Modifier.height(24.dp))
                    content()
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Text(dismissText, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                    }
                    
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = confirmColor)
                    ) {
                        Text(confirmText, color = Color.White, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}
