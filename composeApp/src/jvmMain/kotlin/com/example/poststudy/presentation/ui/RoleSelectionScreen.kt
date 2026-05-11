package com.example.poststudy.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun RoleSelectionScreen(onRoleSelected: (UserRole) -> Unit) {
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
            .background(backgroundGradient),
        contentAlignment = Alignment.Center
    ) {
        // Subtle decorative circles
        Box(
            modifier = Modifier
                .size(400.dp)
                .offset(x = (-100).dp, y = (-100).dp)
                .background(Color(0xFF6366F1).copy(alpha = 0.05f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 100.dp, y = 100.dp)
                .background(Color(0xFF818CF8).copy(alpha = 0.05f), CircleShape)
        )

        Card(
            modifier = Modifier.width(600.dp).padding(16.dp),
            shape = RoundedCornerShape(32.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "PostStudy-ga xush kelibsiz",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1E293B)
                )
                
                Text(
                    text = "Boshlash uchun rolingizni tanlang",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF64748B),
                    modifier = Modifier.padding(bottom = 48.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    RoleSelectionCard(
                        title = "O'qituvchi",
                        subtitle = "Yaratish va boshqarish",
                        icon = Icons.Default.Person,
                        color = Color(0xFF6366F1),
                        modifier = Modifier.weight(1f),
                        onClick = { onRoleSelected(UserRole.Teacher) }
                    )
                    
                    RoleSelectionCard(
                        title = "Talaba",
                        subtitle = "O'rganish va test",
                        icon = Icons.Default.School,
                        color = Color(0xFFFACC15),
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
        modifier = modifier.height(220.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFF8FAFC),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF64748B)
            )
        }
    }
}
