package com.example.poststudy.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.poststudy.data.local.DatabaseHelper
import com.example.poststudy.domain.model.ExamRecord
import com.example.poststudy.presentation.ui.components.PostStudyDialog
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(onBack: () -> Unit) {
    var records by remember { mutableStateOf(DatabaseHelper.getAllExamRecords()) }
    var showClearAllDialog by remember { mutableStateOf(false) }
    var recordToDelete by remember { mutableStateOf<ExamRecord?>(null) }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0F172A),
            Color(0xFF1E293B),
            Color(0xFF334155)
        )
    )

    if (showClearAllDialog) {
        PostStudyDialog(
            onDismissRequest = { showClearAllDialog = false },
            title = "Barcha tarixlarni tozalash",
            text = "Haqiqatan ham barcha imtihon yozuvlarini o'chirib tashlamoqchimisiz? Bu amalni qaytarib bo'lmaydi.",
            confirmText = "Hammasini tozalash",
            confirmColor = Color(0xFFEF4444),
            onConfirm = {
                DatabaseHelper.clearAllExamRecords()
                records = emptyList()
                showClearAllDialog = false
            }
        )
    }

    if (recordToDelete != null) {
        PostStudyDialog(
            onDismissRequest = { recordToDelete = null },
            title = "Yozuvni o'chirish",
            text = "Haqiqatan ham '${recordToDelete?.studentName}' talabasining ushbu imtihon yozuvini o'chirib tashlamoqchimisiz?",
            confirmText = "O'chirish",
            confirmColor = Color(0xFFEF4444),
            onConfirm = {
                recordToDelete?.let {
                    DatabaseHelper.deleteExamRecord(it.id)
                    records = DatabaseHelper.getAllExamRecords()
                }
                recordToDelete = null
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
                    title = { Text("Imtihon Tahlili", fontWeight = FontWeight.ExtraBold, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Orqaga", tint = Color.White)
                        }
                    },
                    actions = {
                        if (records.isNotEmpty()) {
                            IconButton(onClick = { showClearAllDialog = true }) {
                                Icon(Icons.Default.Delete, contentDescription = "Hammasini tozalash", tint = Color(0xFFEF4444))
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { paddingValues ->
            if (records.isEmpty()) {
                Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text("Imtihon tarixi topilmadi.", style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(alpha = 0.6f))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentPadding = PaddingValues(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(records) { record ->
                        HistoryItem(
                            record = record,
                            onDelete = { recordToDelete = record }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryItem(record: ExamRecord, onDelete: () -> Unit) {
    val date = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(record.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = record.studentName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = record.lessonTitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF6366F1),
                        fontWeight = FontWeight.Medium
                    )
                }
                
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.background(Color(0xFFEF4444).copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "O'chirish", tint = Color(0xFFEF4444))
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp), thickness = 1.dp, color = Color(0xFFF1F5F9))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                InfoColumn("Ball", "${record.correctAnswers} / ${record.totalQuestions}")
                InfoColumn("Aniqlik", "${if (record.totalQuestions > 0) (record.correctAnswers.toDouble() / record.totalQuestions * 100).toInt() else 0}%")
                
                val m = record.timeSpentSeconds / 60
                val s = record.timeSpentSeconds % 60
                InfoColumn("Vaqt", "%d:%02d".format(m, s))

                InfoColumn("Sana", date)
            }

            if (record.wrongDetails.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Xatolar tahlili:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF991B1B),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                record.wrongDetails.split("\n\n").forEach { errorBlock ->
                    Surface(
                        color = Color(0xFFFFF1F2),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            val lines = errorBlock.split("\n")
                            lines.forEachIndexed { index, line ->
                                val color = when {
                                    line.startsWith("To'g'ri") -> Color(0xFF059669)
                                    line.startsWith("Tanlangan") -> Color(0xFFE11D48)
                                    else -> Color(0xFF1E293B)
                                }
                                val weight = if (index == 0) FontWeight.Bold else FontWeight.Medium
                                Text(
                                    text = line,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = color,
                                    fontWeight = weight
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoColumn(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
    }
}
