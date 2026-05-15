package com.example.poststudy.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.poststudy.domain.model.UserRole
import com.example.poststudy.presentation.theme.AppDesign

@Composable
fun RoleSelectionScreen(
    onRoleSelected: (UserRole) -> Unit,
    onJoinNetwork: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppDesign.BackgroundGradient),
        contentAlignment = Alignment.Center
    ) {
        // Decorative background elements
        Box(
            modifier = Modifier
                .size(700.dp)
                .offset(x = (-250).dp, y = (-250).dp)
                .background(Color(0xFF10B981).copy(alpha = 0.05f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(500.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 200.dp, y = 200.dp)
                .background(Color(0xFF3B82F6).copy(alpha = 0.05f), CircleShape)
        )

        Card(
            modifier = Modifier.width(780.dp).padding(16.dp),
            shape = AppDesign.CardShape,
            elevation = CardDefaults.cardElevation(defaultElevation = 20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF10B981).copy(alpha = 0.2f))
        ) {
            Column(
                modifier = Modifier.padding(64.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "PostStudy",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF059669) // Emerald 600
                )
                
                Text(
                    text = "O'rganishni boshlash uchun rolingizni tanlang",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF64748B),
                    modifier = Modifier.padding(top = 8.dp, bottom = 48.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    RoleSelectionCard(
                        title = "Admin",
                        subtitle = "Boshqaruv paneli",
                        icon = Icons.Default.Person,
                        color = Color(0xFF3B82F6), // Blue 500
                        modifier = Modifier.weight(1f),
                        onClick = { onRoleSelected(UserRole.Teacher) }
                    )
                    
                    RoleSelectionCard(
                        title = "Tinglovchi",
                        subtitle = "Lokal darslar",
                        icon = Icons.Default.School,
                        color = Color(0xFF10B981), // Emerald 500
                        modifier = Modifier.weight(1f),
                        onClick = { onRoleSelected(UserRole.Student) }
                    )

                    RoleSelectionCard(
                        title = "Tarmoq",
                        subtitle = "Adminga ulanish",
                        icon = Icons.Default.Lan,
                        color = Color(0xFF6366F1), // Indigo 500
                        modifier = Modifier.weight(1f),
                        onClick = onJoinNetwork
                    )
                }
            }
        }
    }
}

@Composable
fun RoleSelectionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(260.dp),
        shape = AppDesign.ComponentShape,
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(3.dp, color),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(48.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = color
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF64748B),
                fontWeight = FontWeight.Bold
            )
        }
    }
}
