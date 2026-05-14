package com.example.poststudy.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.poststudy.data.local.DatabaseHelper
import com.example.poststudy.domain.model.Lesson
import com.example.poststudy.domain.model.LessonMode
import com.example.poststudy.presentation.theme.AppDesign
import com.example.poststudy.presentation.ui.components.PostStudyDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonSelectionScreen(
    isTeacher: Boolean = true,
    onLessonSelected: (Lesson) -> Unit,
    onAddNewLesson: () -> Unit,
    onEditLesson: (Lesson) -> Unit,
    onViewHistory: () -> Unit,
    onBack: () -> Unit
) {
    val lessons = remember { mutableStateListOf<Lesson>() }
    var isLoading by remember { mutableStateOf(true) }
    var lessonToDelete by remember { mutableStateOf<Lesson?>(null) }

    LaunchedEffect(Unit) {
        lessons.clear()
        lessons.addAll(DatabaseHelper.getAllLessons())
        isLoading = false
    }

    if (lessonToDelete != null) {
        PostStudyDialog(
            onDismissRequest = { lessonToDelete = null },
            title = "Darsni o'chirish",
            text = "'${lessonToDelete?.title}' darsini o'chirishni xohlaysizmi? Bu amalni qaytarib bo'lmaydi.",
            confirmText = "O'chirish",
            confirmColor = Color(0xFFEF4444),
            onConfirm = {
                lessonToDelete?.let {
                    DatabaseHelper.deleteLesson(it.id)
                    lessons.remove(it)
                }
                lessonToDelete = null
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppDesign.BackgroundGradient)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(if (isTeacher) "Darslar Ro'yxati" else "Dars Tanlash", color = Color.White, fontWeight = FontWeight.ExtraBold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Orqaga", tint = Color.White)
                        }
                    },
                    actions = {
                        if (isTeacher) {
                            IconButton(onClick = onViewHistory) {
                                Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Tarix", tint = Color.White)
                            }
                            IconButton(onClick = onAddNewLesson) {
                                Icon(Icons.Default.Add, contentDescription = "Qo'shish", tint = Color.White)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { paddingValues ->
            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else if (lessons.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.White.copy(alpha = 0.5f))
                        Spacer(Modifier.height(16.dp))
                        Text("Darslar hali qo'shilmagan", color = Color.White.copy(alpha = 0.7f))
                        if (isTeacher) {
                            Button(
                                onClick = onAddNewLesson,
                                modifier = Modifier.padding(top = 24.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF4F46E5)),
                                shape = AppDesign.ComponentShape
                            ) {
                                Text("Birinchi darsni yaratish", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 350.dp),
                    modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    items(lessons) { lesson ->
                        LessonCard(
                            lesson = lesson,
                            isTeacher = isTeacher,
                            onSelect = { onLessonSelected(lesson) },
                            onEdit = { onEditLesson(lesson) },
                            onDelete = { lessonToDelete = lesson }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LessonCard(
    lesson: Lesson,
    isTeacher: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        onClick = onSelect,
        modifier = Modifier.fillMaxWidth().height(180.dp),
        shape = AppDesign.CardShape,
        color = Color.White.copy(alpha = 0.95f),
        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF4F46E5).copy(alpha = 0.3f)),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = lesson.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = if (lesson.mode == LessonMode.ReAppropriation) "O'rganish va Test" else "Faqat Test",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF4F46E5),
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                
                if (isTeacher) {
                    Row {
                        IconButton(onClick = onEdit) {
                            Icon(Icons.Default.Edit, contentDescription = "Tahrirlash", tint = Color(0xFF64748B))
                        }
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, contentDescription = "O'chirish", tint = Color(0xFFEF4444))
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                InfoBadge(
                    icon = Icons.Default.Timer,
                    text = "${lesson.testTimerSeconds / 60} daq test",
                    color = Color(0xFF0EA5E9)
                )
                if (lesson.mode == LessonMode.ReAppropriation) {
                    InfoBadge(
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        text = "${lesson.slideTimerSeconds / 60} daq dars",
                        color = Color(0xFFFACC15)
                    )
                }
            }
        }
    }
}

@Composable
fun InfoBadge(icon: ImageVector, text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = CircleShape,
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text(text = text, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = color)
        }
    }
}
