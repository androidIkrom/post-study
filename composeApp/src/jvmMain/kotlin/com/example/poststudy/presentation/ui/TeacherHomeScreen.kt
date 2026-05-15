package com.example.poststudy.presentation.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.poststudy.data.network.NetworkManager
import com.example.poststudy.domain.model.LessonMode
import com.example.poststudy.presentation.theme.AppDesign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherHomeScreen(
    onNavigateToLessons: () -> Unit,
    onNavigateToExam: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onBack: () -> Unit
) {
    var settings by remember { mutableStateOf(DatabaseHelper.getSettings()) }
    var currentMode by remember { mutableStateOf(settings.mode) }
    val localIp = remember { NetworkManager.getLocalIpAddress() }

    // Refresh settings when screen is shown
    LaunchedEffect(Unit) {
        settings = DatabaseHelper.getSettings()
        currentMode = settings.mode
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppDesign.BackgroundGradient)
    ) {
        // Decorative background elements
        Box(
            modifier = Modifier
                .size(600.dp)
                .offset(x = (-150).dp, y = (-150).dp)
                .background(Color(0xFF10B981).copy(alpha = 0.05f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(500.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 150.dp, y = 150.dp)
                .background(Color(0xFF3B82F6).copy(alpha = 0.05f), CircleShape)
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Admin Paneli", color = Color(0xFF065F46), fontWeight = FontWeight.Black) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Orqaga", tint = Color(0xFF065F46))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { paddingValues ->
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 32.dp, vertical = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(48.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Xush kelibsiz, Admin",
                        style = MaterialTheme.typography.displaySmall,
                        color = Color(0xFF1E293B),
                        fontWeight = FontWeight.Black
                    )
                    
                    Surface(
                        color = Color(0xFF6366F1).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(top = 12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF6366F1).copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "Sizning IP: $localIp",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6366F1)
                        )
                    }

                    Text(
                        text = "Bugun o'quv jarayonini qanday boshqaramiz?",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth().widthIn(max = 1200.dp),
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    HomeCard(
                        modifier = Modifier.weight(1f),
                        title = "Darslar",
                        subtitle = "Materiallarni boshqarish",
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        color = Color(0xFFF59E0B), // Amber 500
                        onClick = onNavigateToLessons
                    )
                    HomeCard(
                        modifier = Modifier.weight(1f),
                        title = "Imtihon",
                        subtitle = "Test sinovlarini o'tkazish",
                        icon = Icons.Default.Assignment,
                        color = Color(0xFF6366F1), // Indigo 500
                        onClick = onNavigateToExam
                    )
                    HomeCard(
                        modifier = Modifier.weight(1f),
                        title = "Tahlil",
                        subtitle = "Natijalarni ko'rish",
                        icon = Icons.AutoMirrored.Filled.List,
                        color = Color(0xFF10B981), // Emerald 500
                        onClick = onNavigateToHistory
                    )
                }

                Spacer(modifier = Modifier.height(64.dp))

                Surface(
                    modifier = Modifier
                        .width(700.dp),
                    shape = AppDesign.CardShape,
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(3.dp, Color(0xFF10B981).copy(alpha = 0.3f)),
                    shadowElevation = 12.dp
                ) {
                    Column(
                        modifier = Modifier.padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Hozirgi Tanlangan Sessiya",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF065F46)
                        )
                        
                        Spacer(Modifier.height(24.dp))

                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = AppDesign.ComponentShape,
                            modifier = Modifier.fillMaxWidth(),
                            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFE2E8F0))
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                if (settings.activeSessionTitle.isNotEmpty()) {
                                    val isExam = settings.activeSessionTitle.contains("Imtihon")
                                    val sessionType = if (isExam) "IMTIHON" else "DARS"
                                    val sessionColor = if (isExam) Color(0xFF6366F1) else Color(0xFFF59E0B)

                                    Surface(
                                        color = sessionColor.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(12.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, sessionColor.copy(alpha = 0.5f))
                                    ) {
                                        Text(
                                            text = sessionType,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Black,
                                            color = sessionColor
                                        )
                                    }
                                    
                                    Spacer(Modifier.height(16.dp))
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        if (settings.activeSessionTitle.contains("Imtihon")) Icons.Default.Assignment else Icons.AutoMirrored.Filled.MenuBook,
                                        contentDescription = null,
                                        tint = if (settings.activeSessionTitle.isEmpty()) Color.Gray else Color(0xFF10B981),
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        text = if (settings.activeSessionTitle.isEmpty()) "Sessiya tanlanmagan" else settings.activeSessionTitle,
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (settings.activeSessionTitle.isEmpty()) Color.Gray else Color(0xFF1E293B)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(32.dp))

                        Text(
                            text = "Tinglovchilar rejimi",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B)
                        )

                        Spacer(Modifier.height(16.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(72.dp)
                                .clip(AppDesign.ComponentShape)
                                .background(Color(0xFFF1F5F9))
                                .padding(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ModeToggleButton(
                                modifier = Modifier.weight(1f),
                                text = "O'rganish + Test",
                                isSelected = currentMode == LessonMode.ReAppropriation,
                                onClick = {
                                    currentMode = LessonMode.ReAppropriation
                                    DatabaseHelper.saveSettings(
                                        settings.presentationPath,
                                        settings.testPath,
                                        settings.slideTimerSeconds / 60,
                                        settings.testTimerSeconds / 60,
                                        LessonMode.ReAppropriation,
                                        settings.activeSessionTitle,
                                        settings.questionsPerStudent
                                    )
                                    settings = DatabaseHelper.getSettings()
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
                                        LessonMode.TestOnly,
                                        settings.activeSessionTitle,
                                        settings.questionsPerStudent
                                    )
                                    settings = DatabaseHelper.getSettings()
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
            .height(200.dp)
            .clickable { onClick() },
        shape = AppDesign.ComponentShape,
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(3.dp, color),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(color.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = color
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Bold
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
        if (isSelected) Color(0xFF10B981) else Color(0xFF64748B),
        animationSpec = tween(300)
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .then(if (isSelected) Modifier.shadow(4.dp, RoundedCornerShape(16.dp)) else Modifier)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
            color = textColor
        )
    }
}
