package com.example.poststudy.presentation.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.poststudy.presentation.theme.AppDesign
import kotlinx.coroutines.delay

@Composable
fun InfoScreen(onContinue: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(100)
        visible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppDesign.BackgroundGradient),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + expandVertically()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "LOYIHA HAQIDA",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF064E3B)
                    )
                    
                    Text(
                        text = "PostStudy — ta'lim sifatini oshirish uchun innovatsion yechim",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF059669),
                        modifier = Modifier.padding(top = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically { it / 2 }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                    InfoRow(
                        icon = Icons.Default.Adjust,
                        title = "Loyihaning maqsadi",
                        description = "O'quv jarayonini raqamlashtirish, bilimlarni nazorat qilishni avtomatlashtirish va o'qituvchi bilan talaba o'rtasidagi aloqani soddalashtirish."
                    )
                    
                    InfoRow(
                        icon = Icons.Default.TaskAlt,
                        title = "Asosiy vazifalari",
                        description = "Dars materiallarini elektron shaklda taqdim etish, interaktiv testlar orqali baholash va natijalarni real vaqt rejimida tahlil qilish."
                    )
                    
                    InfoRow(
                        icon = Icons.Default.Computer,
                        title = "Qo'llanish sohalari",
                        description = "Oliy va o'rta maxsus ta'lim muassasalari, o'quv markazlari hamda korporativ o'qitish tizimlarida foydalanish uchun mo'ljallangan."
                    )
                }
            }

            Spacer(modifier = Modifier.height(56.dp))

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically { it }
            ) {
                Button(
                    onClick = onContinue,
                    modifier = Modifier.width(300.dp).height(64.dp),
                    shape = AppDesign.ComponentShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Text(
                        "DAVOM ETISH",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

@Composable
fun InfoRow(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.6f), AppDesign.CardShape)
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Surface(
            color = Color(0xFF10B981),
            shape = CircleShape,
            modifier = Modifier.size(56.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
            }
        }
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1E293B)
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF475569),
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2
            )
        }
    }
}
