package com.example.poststudy.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.poststudy.data.local.DatabaseHelper
import com.example.poststudy.domain.model.Lesson
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
    var lessons by remember { mutableStateOf(DatabaseHelper.getAllLessons()) }
    var lessonToDelete by remember { mutableStateOf<Lesson?>(null) }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0F172A),
            Color(0xFF1E293B),
            Color(0xFF334155)
        )
    )

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
                    lessons = DatabaseHelper.getAllLessons()
                }
                lessonToDelete = null
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Box(
            modifier = Modifier
                .size(400.dp)
                .offset(x = (-100).dp, y = (-100).dp)
                .background(Color(0xFF6366F1).copy(alpha = 0.05f), CircleShape)
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Saqlangan Darslar", fontWeight = FontWeight.ExtraBold, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Orqaga", tint = Color.White)
                        }
                    },
                    actions = {
                        if (isTeacher) {
                            IconButton(onClick = onViewHistory) {
                                Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Tarixni ko'rish", tint = Color.White)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            floatingActionButton = {
                if (isTeacher) {
                    ExtendedFloatingActionButton(
                        onClick = onAddNewLesson,
                        icon = { Icon(Icons.Default.Add, contentDescription = null) },
                        text = { Text("Yangi dars yaratish", fontWeight = FontWeight.Bold) },
                        containerColor = Color(0xFF6366F1),
                        contentColor = Color.White,
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }
        ) { paddingValues ->
            if (lessons.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Hozircha saqlangan darslar yo'q.\nBoshlash uchun yangi dars yarating!",
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentPadding = PaddingValues(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(lessons) { lesson ->
                        LessonItem(
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
fun LessonItem(
    lesson: Lesson,
    isTeacher: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "O'rganish: ${lesson.slideTimerSeconds / 60}daq | Test: ${lesson.testTimerSeconds / 60}daq",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B)
                )
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (isTeacher) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.background(Color(0xFF6366F1).copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Tahrirlash", tint = Color(0xFF6366F1))
                    }
                    
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.background(Color(0xFFEF4444).copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "O'chirish", tint = Color(0xFFEF4444))
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))
                
                Button(
                    onClick = onSelect,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                    modifier = Modifier.height(48.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Boshlash", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
