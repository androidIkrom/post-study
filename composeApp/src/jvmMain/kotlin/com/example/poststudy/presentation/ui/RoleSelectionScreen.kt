package com.example.poststudy.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Person
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
fun RoleSelectionScreen(onRoleSelected: (UserRole) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppDesign.BackgroundGradient),
        contentAlignment = Alignment.Center
    ) {
        // Subtle decorative circles
        Box(
            modifier = Modifier
                .size(600.dp)
                .offset(x = (-200).dp, y = (-200).dp)
                .background(Color.White.copy(alpha = 0.07f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(400.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 150.dp, y = 150.dp)
                .background(Color.White.copy(alpha = 0.05f), CircleShape)
        )

        Card(
            modifier = Modifier.width(650.dp).padding(16.dp),
            shape = AppDesign.CardShape,
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
        ) {
            Column(
                modifier = Modifier.padding(48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "PostStudy",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF4F46E5)
                )
                
                Text(
                    text = "Boshlash uchun rolingizni tanlang",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF64748B),
                    modifier = Modifier.padding(top = 8.dp, bottom = 48.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    RoleSelectionCard(
                        title = "O'qituvchi",
                        subtitle = "Yaratish va boshqarish",
                        icon = Icons.Default.Person,
                        color = Color(0xFF4F46E5),
                        modifier = Modifier.weight(1f),
                        onClick = { onRoleSelected(UserRole.Teacher) }
                    )
                    
                    RoleSelectionCard(
                        title = "Talaba",
                        subtitle = "O'rganish va test",
                        icon = Icons.Default.School,
                        color = Color(0xFFC026D3),
                        modifier = Modifier.weight(1f),
                        onClick = { onRoleSelected(UserRole.Student) }
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
        modifier = modifier.height(240.dp),
        shape = AppDesign.ComponentShape,
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(2.dp, color.copy(alpha = 0.5f)),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(40.dp))
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF64748B),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
