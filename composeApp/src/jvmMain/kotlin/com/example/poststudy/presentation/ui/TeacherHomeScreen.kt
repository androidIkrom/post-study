package com.example.poststudy.presentation.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.poststudy.data.local.DatabaseHelper
import com.example.poststudy.domain.model.LessonMode
import com.example.poststudy.presentation.theme.AppDesign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherHomeScreen(
    onNavigateToLessons: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onBack: () -> Unit
) {
    val settings = remember { DatabaseHelper.getSettings() }
    var currentMode by remember { mutableStateOf(settings.mode) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppDesign.BackgroundGradient)
    ) {
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

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("O'qituvchi Paneli", color = Color.White, fontWeight = FontWeight.ExtraBold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Orqaga", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Xush kelibsiz, Professor",
                    style = MaterialTheme.typography.displaySmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Bugun nima qilmoqchisiz?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 48.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth().widthIn(max = 800.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    HomeCard(
                        modifier = Modifier.weight(1f),
                        title = "Darslar",
                        subtitle = "O'quv materiallarini boshqarish",
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        color = Color(0xFFFACC15),
                        onClick = onNavigateToLessons
                    )
                    HomeCard(
                        modifier = Modifier.weight(1f),
                        title = "Tahlil",
                        subtitle = "Talabalar natijalarini ko'rish",
                        icon = Icons.AutoMirrored.Filled.List,
                        color = Color(0xFF4ADE80),
                        onClick = onNavigateToHistory
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                Surface(
                    modifier = Modifier
                        .width(500.dp)
                        .shadow(12.dp, AppDesign.CardShape),
                    shape = AppDesign.CardShape,
                    color = Color.White.copy(alpha = 0.95f),
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF4F46E5).copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Aktiv Sessiya Rejimi",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF4F46E5)
                        )
                        Text(
                            text = "Talabalar sessiyalari uchun umumiy rejimni o'rnating",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B),
                            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .clip(AppDesign.ComponentShape)
                                .background(Color(0xFFF1F5F9))
                                .padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ModeToggleButton(
                                modifier = Modifier.weight(1f),
                                text = "O'rganish",
                                isSelected = currentMode == LessonMode.ReAppropriation,
                                onClick = {
                                    currentMode = LessonMode.ReAppropriation
                                    DatabaseHelper.saveSettings(
                                        settings.presentationPath,
                                        settings.testPath,
                                        settings.slideTimerSeconds / 60,
                                        settings.testTimerSeconds / 60,
                                        LessonMode.ReAppropriation
                                    )
                                }
                            )
                            ModeToggleButton(
                                modifier = Modifier.weight(1f),
                                text = "Faqat Test",
                                isSelected = currentMode == LessonMode.TestOnly,
                                onClick = {
                                    currentMode = LessonMode.TestOnly
                                    DatabaseHelper.saveSettings(
                                        settings.presentationPath,
                                        settings.testPath,
                                        settings.slideTimerSeconds / 60,
                                        settings.testTimerSeconds / 60,
                                        LessonMode.TestOnly
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeCard(
    modifier: Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(180.dp)
            .clickable { onClick() }
            .shadow(8.dp, AppDesign.ComponentShape),
        shape = AppDesign.ComponentShape,
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(2.dp, color.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = color
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

@Composable
fun ModeToggleButton(
    modifier: Modifier,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        if (isSelected) Color.White else Color.Transparent,
        animationSpec = tween(300)
    )
    val textColor by animateColorAsState(
        if (isSelected) Color(0xFF4F46E5) else Color(0xFF64748B),
        animationSpec = tween(300)
    )
    val shadowAlpha by animateFloatAsState(if (isSelected) 0.1f else 0f)

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .then(if (isSelected) Modifier.shadow(2.dp, RoundedCornerShape(12.dp)) else Modifier)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
            color = textColor
        )
    }
}
