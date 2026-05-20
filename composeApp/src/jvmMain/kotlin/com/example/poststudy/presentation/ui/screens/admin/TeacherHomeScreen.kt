package com.example.poststudy.presentation.ui.screens.admin

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.poststudy.data.local.DatabaseHelper
import com.example.poststudy.data.network.NetworkManager
import com.example.poststudy.domain.model.Group
import com.example.poststudy.domain.model.LessonMode
import com.example.poststudy.presentation.theme.AppDesign
import com.example.poststudy.presentation.ui.components.hoverEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherHomeScreen(
    onNavigateToLessons: () -> Unit,
    onNavigateToExam: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToMonitoring: () -> Unit,
    onNavigateToGroups: () -> Unit,
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
                    title = { Text("Admin paneli", color = Color(0xFF065F46), fontWeight = FontWeight.Black) },
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
                        text = "Xush kelibsiz, admin",
                        style = MaterialTheme.typography.displaySmall,
                        color = Color(0xFF1E293B),
                        fontWeight = FontWeight.Black
                    )
                    
                    Surface(
                        color = Color(0xFF6366F1).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(top = 12.dp),
                        border = BorderStroke(1.dp, Color(0xFF6366F1).copy(alpha = 0.3f))
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
                        title = "Guruhlar",
                        subtitle = "Tinglovchilar va guruhlar",
                        icon = Icons.Default.Groups,
                        color = Color(0xFF8B5CF6), // Violet 500
                        onClick = onNavigateToGroups
                    )
                    HomeCard(
                        modifier = Modifier.weight(1f),
                        title = "Monitoring",
                        subtitle = "Ta'lim holati",
                        icon = Icons.Default.Analytics,
                        color = Color(0xFF0EA5E9), // Sky 500
                        onClick = onNavigateToMonitoring
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

                Spacer(modifier = Modifier.height(32.dp))

                Surface(
                    modifier = Modifier
                        .width(700.dp),
                    shape = AppDesign.CardShape,
                    color = Color.White,
                    border = BorderStroke(3.dp, Color(0xFF10B981).copy(alpha = 0.3f)),
                    shadowElevation = 12.dp
                ) {
                    Column(
                        modifier = Modifier.padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Hozirgi tanlangan sessiya",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF065F46)
                        )
                        
                        Spacer(Modifier.height(24.dp))

                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = AppDesign.ComponentShape,
                            modifier = Modifier.fillMaxWidth(),
                            border = BorderStroke(2.dp, Color(0xFFE2E8F0))
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                if (settings.activeSessionTitle.isNotEmpty()) {
                                    val isExam = settings.activeSessionTitle.contains("Imtihon")
                                    val sessionType = if (isExam) "Imtihon" else "Dars"
                                    val sessionColor = if (isExam) Color(0xFF6366F1) else Color(0xFFF59E0B)

                                    Surface(
                                        color = sessionColor.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.dp, sessionColor.copy(alpha = 0.5f))
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
                                text = "O'rganish + test",
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
                                text = "Faqat test",
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
            .hoverEffect()
            .clickable { onClick() },
        shape = AppDesign.ComponentShape,
        color = Color.White,
        border = BorderStroke(3.dp, color),
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
            .hoverEffect(scale = 1.05f)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonitoringScreen(onBack: () -> Unit) {
    var selectedGroup by remember { mutableStateOf<Group?>(null) }
    var groups by remember { mutableStateOf(DatabaseHelper.getAllGroupsWithStats()) }
    
    // Auto refresh or initial fetch
    LaunchedEffect(Unit) {
        groups = DatabaseHelper.getAllGroupsWithStats()
    }

    val stats = if (selectedGroup == null) {
        groups.map { it.first.name to it.second.toFloat() }
    } else {
        val groupRecords = DatabaseHelper.getGroupRecords(selectedGroup!!.id)
        groupRecords.map { it.studentName to (it.correctAnswers.toFloat() / it.totalQuestions * 100) }
    }

    Box(modifier = Modifier.fillMaxSize().background(AppDesign.BackgroundGradient)) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Monitoring markazi", fontWeight = FontWeight.Black, color = Color(0xFF065F46)) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Orqaga", tint = Color(0xFF065F46))
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
                    .verticalScroll(rememberScrollState())
                    .padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.widthIn(max = 1000.dp).fillMaxWidth(),
                    shape = AppDesign.CardShape,
                    color = Color.White,
                    border = BorderStroke(3.dp, Color(0xFF6366F1).copy(alpha = 0.3f)),
                    shadowElevation = 12.dp
                ) {
                    Column(modifier = Modifier.padding(40.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Ta'lim tahlili",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF1E293B)
                                )
                                Text(
                                    text = if (selectedGroup == null) "Barcha guruhlar holati" else "${selectedGroup?.name} natijalari",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF64748B),
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Saralash: ", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                                Spacer(Modifier.width(8.dp))
                                var showDropdown by remember { mutableStateOf(false) }
                                Box {
                                    Surface(
                                        onClick = { showDropdown = true },
                                        color = Color(0xFFF1F5F9),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.width(240.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = selectedGroup?.name ?: "Barcha guruhlar",
                                                fontWeight = FontWeight.Black,
                                                color = Color(0xFF1E293B)
                                            )
                                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                        }
                                    }

                                    DropdownMenu(
                                        expanded = showDropdown,
                                        onDismissRequest = { showDropdown = false },
                                        modifier = Modifier.width(240.dp).background(Color.White)
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Barcha guruhlar", fontWeight = FontWeight.Bold) },
                                            onClick = { selectedGroup = null; showDropdown = false }
                                        )
                                        groups.forEach { (group, _) ->
                                            DropdownMenuItem(
                                                text = { Text(group.name) },
                                                onClick = { selectedGroup = group; showDropdown = false }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(48.dp))

                        PerformanceGraph(data = stats)
                        
                        Spacer(Modifier.height(40.dp))
                        
                        val avg = if (stats.isNotEmpty()) stats.map { it.second }.average().toInt() else 0
                        val statusColor = when {
                            avg >= 80 -> Color(0xFF10B981)
                            avg >= 60 -> Color(0xFFF59E0B)
                            else -> Color(0xFFEF4444)
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            MetricCard(
                                modifier = Modifier.weight(1f),
                                label = "O'rtacha o'zlashtirish",
                                value = "$avg%",
                                color = statusColor,
                                icon = Icons.Default.Assessment
                            )
                            MetricCard(
                                modifier = Modifier.weight(1f),
                                label = "Faollik darajasi",
                                value = if (stats.size > 10) "Yuqori" else if (stats.isNotEmpty()) "O'rtacha" else "Past",
                                color = Color(0xFF6366F1),
                                icon = Icons.Default.Timeline
                            )
                            MetricCard(
                                modifier = Modifier.weight(1f),
                                label = "Reyting",
                                value = when {
                                    avg >= 85 -> "A'lo"
                                    avg >= 60 -> "Yaxshi"
                                    else -> "Past"
                                },
                                color = Color(0xFF8B5CF6),
                                icon = Icons.Default.Stars
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun MetricCard(modifier: Modifier, label: String, value: String, color: Color, icon: ImageVector) {
    Surface(
        modifier = modifier,
        color = Color(0xFFF8FAFC),
        shape = AppDesign.ComponentShape,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp).background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = Color(0xFF1E293B))
            }
        }
    }
}

@Composable
fun PerformanceGraph(data: List<Pair<String, Float>>) {
    val animatedData = data.map { 
        animateFloatAsState(targetValue = it.second, animationSpec = tween(1000, easing = FastOutSlowInEasing))
    }

    val textMeasurer = rememberTextMeasurer()
    val textStyle = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF64748B)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(450.dp) // Increased height to accommodate vertical labels
            .background(Color(0xFFF8FAFC), AppDesign.ComponentShape)
            .padding(top = 24.dp, end = 24.dp, start = 8.dp, bottom = 8.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val paddingLeft = 60.dp.toPx()
            val paddingBottom = 120.dp.toPx()
            val width = size.width - paddingLeft
            val height = size.height - paddingBottom
            val spacing = if (data.size > 1) width / (data.size - 1) else width
            
            // Draw Y-axis labels and grid lines
            val gridColor = Color(0xFFE2E8F0)
            for (i in 0..5) {
                val percentage = i * 20
                val y = height - (i * height / 5)
                
                // Grid line
                drawLine(
                    color = gridColor,
                    start = Offset(paddingLeft, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1f
                )
                
                // Y-axis label
                drawText(
                    textMeasurer = textMeasurer,
                    text = "$percentage%",
                    style = textStyle,
                    topLeft = Offset(paddingLeft - 45.dp.toPx(), y - 10.dp.toPx())
                )
            }

            if (data.isNotEmpty()) {
                val path = Path()
                val points = animatedData.mapIndexed { index, anim ->
                    val x = paddingLeft + (index * spacing)
                    val y = height - (anim.value / 100f * height)
                    Offset(x, y)
                }

                // Draw X-axis labels (Vertical)
                data.forEachIndexed { index, pair ->
                    val x = paddingLeft + (index * spacing)
                    val label = if (pair.first.length > 15) pair.first.take(12) + "..." else pair.first
                    rotate(degrees = 90f, pivot = Offset(x, height + 10.dp.toPx())) {
                        drawText(
                            textMeasurer = textMeasurer,
                            text = label,
                            style = textStyle,
                            topLeft = Offset(x, height + 10.dp.toPx())
                        )
                    }
                }

                // Smooth path
                path.moveTo(points[0].x, points[0].y)
                if (points.size > 1) {
                    for (i in 0 until points.size - 1) {
                        val p1 = points[i]
                        val p2 = points[i + 1]
                        val controlPoint1 = Offset(p1.x + (p2.x - p1.x) / 2f, p1.y)
                        val controlPoint2 = Offset(p1.x + (p2.x - p1.x) / 2f, p2.y)
                        path.cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, p2.x, p2.y)
                    }
                    
                    drawPath(
                        path = path,
                        color = Color(0xFF6366F1),
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )
                    
                    // Draw fill area
                    val fillPath = Path().apply {
                        addPath(path)
                        lineTo(points.last().x, height)
                        lineTo(points.first().x, height)
                        close()
                    }
                    drawPath(
                        path = fillPath,
                        color = Color(0xFF6366F1).copy(alpha = 0.1f)
                    )
                }

                // Draw points and percentage labels
                points.forEachIndexed { index, point ->
                    drawCircle(
                        color = Color.White,
                        radius = 6.dp.toPx(),
                        center = point
                    )
                    drawCircle(
                        color = Color(0xFF6366F1),
                        radius = 4.dp.toPx(),
                        center = point,
                        style = Stroke(width = 2.dp.toPx())
                    )
                    
                    // Show percentage above point if it's the target value or just always
                    drawText(
                        textMeasurer = textMeasurer,
                        text = "${data[index].second.toInt()}%",
                        style = textStyle.copy(color = Color(0xFF6366F1)),
                        topLeft = Offset(point.x - 15.dp.toPx(), point.y - 25.dp.toPx())
                    )
                }
            }
        }
        
        if (data.isEmpty()) {
            Text(
                text = "Ma'lumotlar mavjud emas",
                modifier = Modifier.align(Alignment.Center),
                color = Color(0xFF64748B),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

