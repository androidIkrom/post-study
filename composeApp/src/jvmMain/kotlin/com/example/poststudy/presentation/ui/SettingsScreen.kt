package com.example.poststudy.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.poststudy.data.local.DatabaseHelper
import com.example.poststudy.domain.model.*
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import io.github.vinceglb.filekit.core.PlatformFile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    lessonToEdit: Lesson? = null,
    onSaveComplete: () -> Unit,
    onBack: () -> Unit
) {
    val initialSettings = remember { DatabaseHelper.getSettings() }
    
    var title by remember { mutableStateOf(lessonToEdit?.title ?: "") }
    var presentationPath by remember { mutableStateOf(lessonToEdit?.presentationPath ?: initialSettings.presentationPath) }
    var testPath by remember { mutableStateOf(lessonToEdit?.testPath ?: initialSettings.testPath) }
    
    var slideTimerMin by remember { mutableStateOf(( (lessonToEdit?.slideTimerSeconds ?: initialSettings.slideTimerSeconds) / 60 ).toString()) }
    var testTimerMin by remember { mutableStateOf(( (lessonToEdit?.testTimerSeconds ?: initialSettings.testTimerSeconds) / 60 ).toString()) }
    
    var selectedMode by remember { mutableStateOf(lessonToEdit?.mode ?: initialSettings.mode) }
    var showSaveDialog by remember { mutableStateOf(false) }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0F172A),
            Color(0xFF1E293B),
            Color(0xFF334155)
        )
    )

    val presentationLauncher = rememberFilePickerLauncher(
        type = PickerType.File(extensions = listOf("ppt", "pptx")),
        mode = PickerMode.Single
    ) { file: PlatformFile? ->
        file?.path?.let { presentationPath = it }
    }

    val testLauncher = rememberFilePickerLauncher(
        type = PickerType.File(extensions = listOf("doc", "docx")),
        mode = PickerMode.Single
    ) { file: PlatformFile? ->
        file?.path?.let { testPath = it }
    }

    val slideTimerVal = slideTimerMin.toIntOrNull() ?: 0
    val testTimerVal = testTimerMin.toIntOrNull() ?: 0

    val isReady = title.isNotEmpty() &&
                  (if (selectedMode == LessonMode.ReAppropriation) presentationPath.isNotEmpty() else true) && 
                  testPath.isNotEmpty() && 
                  (if (selectedMode == LessonMode.ReAppropriation) slideTimerVal >= 5 else true) && 
                  testTimerVal >= 5

    val saveSettings = {
        val lesson = Lesson(
            id = lessonToEdit?.id ?: 0,
            title = title,
            presentationPath = presentationPath,
            testPath = testPath,
            slideTimerSeconds = if (selectedMode == LessonMode.ReAppropriation) slideTimerVal * 60 else 0,
            testTimerSeconds = testTimerVal * 60,
            mode = selectedMode
        )
        
        if (lessonToEdit == null) {
            DatabaseHelper.addLesson(lesson)
        } else {
            DatabaseHelper.updateLesson(lesson)
        }
        
        DatabaseHelper.saveSettings(presentationPath, testPath, slideTimerVal, testTimerVal, selectedMode)
        onSaveComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier.onPreviewKeyEvent {
                if (it.key == Key.Enter && it.type == KeyEventType.KeyDown) {
                    if (isReady) showSaveDialog = true
                    true
                } else false
            },
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(if (lessonToEdit == null) "Yangi dars" else "Darsni tahrirlash", fontWeight = FontWeight.ExtraBold, color = Color.White) },
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
                    .padding(horizontal = 32.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(32.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                        SettingsSection(title = "Dars nomi") {
                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                label = { Text("masalan, Rim tarixi") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF6366F1),
                                    focusedLabelColor = Color(0xFF6366F1)
                                )
                            )
                        }

                        SettingsSection(title = "Sessiya rejimi") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                ModeCard(
                                    modifier = Modifier.weight(1f),
                                    title = "O'rganish",
                                    description = "Dars + Test",
                                    isSelected = selectedMode == LessonMode.ReAppropriation,
                                    onClick = { selectedMode = LessonMode.ReAppropriation }
                                )
                                ModeCard(
                                    modifier = Modifier.weight(1f),
                                    title = "Faqat Test",
                                    description = "Faqat baholash",
                                    isSelected = selectedMode == LessonMode.TestOnly,
                                    onClick = { selectedMode = LessonMode.TestOnly }
                                )
                            }
                        }

                        if (selectedMode == LessonMode.ReAppropriation) {
                            SettingsSection(title = "Prezentatsiya fayli") {
                                FilePickerRow(
                                    path = presentationPath,
                                    label = "PowerPoint fayli",
                                    onSelect = { presentationLauncher.launch() }
                                )
                            }
                        }

                        SettingsSection(title = "Test fayli") {
                            FilePickerRow(
                                path = testPath,
                                label = "Word hujjati",
                                onSelect = { testLauncher.launch() }
                            )
                        }

                        SettingsSection(title = "Taymerlar (kamida 5 daqiqa)") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                if (selectedMode == LessonMode.ReAppropriation) {
                                    OutlinedTextField(
                                        value = slideTimerMin,
                                        onValueChange = { if (it.all { c -> c.isDigit() }) slideTimerMin = it },
                                        label = { Text("O'rganish (daq)") },
                                        isError = slideTimerMin.isNotEmpty() && (slideTimerMin.toIntOrNull() ?: 0) < 5,
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(16.dp),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true
                                    )
                                }

                                OutlinedTextField(
                                    value = testTimerMin,
                                    onValueChange = { if (it.all { c -> c.isDigit() }) testTimerMin = it },
                                    label = { Text("Test (daq)") },
                                    isError = testTimerMin.isNotEmpty() && (testTimerMin.toIntOrNull() ?: 0) < 5,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(16.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = { saveSettings() },
                    enabled = isReady,
                    modifier = Modifier.width(320.dp).height(64.dp).padding(bottom = 32.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    Text("DARSNI SAQLASH", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Sozlamalarni saqlash") },
            text = { Text("Ushbu o'zgarishlarni saqlab, darslar ro'yxatiga qaytishni xohlaysizmi?") },
            confirmButton = {
                TextButton(onClick = { 
                    showSaveDialog = false
                    saveSettings()
                }) {
                    Text("Ha, Saqlash", color = Color(0xFF6366F1), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Bekor qilish", color = Color(0xFF64748B))
                }
            },
            shape = RoundedCornerShape(28.dp),
            containerColor = Color.White
        )
    }
}

@Composable
fun ModeCard(
    modifier: Modifier,
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) Color(0xFFEEF2FF) else Color(0xFFF8FAFC),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF6366F1)) else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = if (isSelected) Color(0xFF4338CA) else Color(0xFF1E293B))
            Text(text = description, style = MaterialTheme.typography.bodySmall, color = if (isSelected) Color(0xFF6366F1) else Color(0xFF64748B))
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = Color(0xFF6366F1),
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
        )
        content()
    }
}

@Composable
fun FilePickerRow(path: String, label: String, onSelect: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = path.split("\\", "/").last(),
            onValueChange = {},
            label = { Text(label) },
            modifier = Modifier.weight(1f),
            readOnly = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF6366F1),
                unfocusedBorderColor = Color(0xFFE2E8F0)
            )
        )
        Spacer(modifier = Modifier.width(12.dp))
        FilledTonalButton(
            onClick = onSelect,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.height(56.dp),
            colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFFF1F5F9))
        ) {
            Text("Tanlash", color = Color(0xFF1E293B), fontWeight = FontWeight.Bold)
        }
    }
}
