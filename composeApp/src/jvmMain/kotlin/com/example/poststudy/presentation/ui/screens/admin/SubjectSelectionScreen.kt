package com.example.poststudy.presentation.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.poststudy.di.AppContainer
import com.example.poststudy.domain.model.Subject
import com.example.poststudy.presentation.theme.AppDesign
import com.example.poststudy.presentation.ui.components.PostStudyDialog
import com.example.poststudy.presentation.ui.components.hoverEffect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectSelectionScreen(
    currentSubjectId: Int,
    onSubjectSelected: (Subject) -> Unit,
    onBack: () -> Unit
) {
    var subjects by remember { mutableStateOf<List<Subject>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showAddDialog by remember { mutableStateOf(false) }
    var subjectToEdit by remember { mutableStateOf<Subject?>(null) }
    var subjectToDelete by remember { mutableStateOf<Subject?>(null) }
    
    val localIp = remember { runBlocking { AppContainer.networkRepository.getLocalIpAddress().first() } }

    // Deletion steps: 0 - Closed, 1 - Confirmation, 2 - Password, 3 - Name Entry
    var deleteStep by remember { mutableStateOf(0) }

    fun refresh() {
        isLoading = true
        CoroutineScope(Dispatchers.IO).launch {
            AppContainer.localRepository.getAllSubjects().collect {
                subjects = it
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) { refresh() }

    // Add/Edit Dialog
    if (showAddDialog || subjectToEdit != null) {
        var name by remember { mutableStateOf(subjectToEdit?.name ?: "") }
        PostStudyDialog(
            onDismissRequest = { showAddDialog = false; subjectToEdit = null },
            title = if (subjectToEdit == null) "Yangi fan qo'shish" else "Fanni tahrirlash",
            text = "Fan nomini kiriting:",
            confirmText = "Saqlash",
            onConfirm = {
                if (name.isNotBlank()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        if (subjectToEdit == null) {
                            AppContainer.localRepository.addSubject(name).collect { refresh() }
                        } else {
                            AppContainer.localRepository.updateSubject(subjectToEdit!!.copy(name = name))
                            refresh()
                        }
                    }
                    showAddDialog = false
                    subjectToEdit = null
                }
            },
            content = {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Fan nomi") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = AppDesign.ComponentShape
                )
            }
        )
    }

    // 3-Step Deletion Process
    if (deleteStep > 0) {
        when (deleteStep) {
            1 -> PostStudyDialog(
                onDismissRequest = { deleteStep = 0; subjectToDelete = null },
                title = "Fanni o'chirish",
                text = "'${subjectToDelete?.name}' fanini o'chirishni xohlaysizmi? Bu fandagi barcha darslar, imtihonlar va guruhlar o'chib ketadi.",
                confirmText = "Davom etish",
                confirmColor = Color.Red,
                onConfirm = { deleteStep = 2 }
            )
            2 -> {
                var password by remember { mutableStateOf("") }
                var error by remember { mutableStateOf("") }
                PostStudyDialog(
                    onDismissRequest = { deleteStep = 0; subjectToDelete = null },
                    title = "Parolni tasdiqlang",
                    text = "Xavfsizlik uchun admin parolini kiriting:",
                    confirmText = "Tekshirish",
                    onConfirm = {
                        CoroutineScope(Dispatchers.IO).launch {
                            AppContainer.localRepository.validateUserPassword(password).collect { isValid ->
                                if (isValid) deleteStep = 3
                                else error = "Parol noto'g'ri"
                            }
                        }
                    },
                    content = {
                        Column {
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it; error = "" },
                                label = { Text("Parol") },
                                visualTransformation = PasswordVisualTransformation(),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = AppDesign.ComponentShape,
                                isError = error.isNotEmpty()
                            )
                            if (error.isNotEmpty()) {
                                Text(error, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                )
            }
            3 -> {
                var nameInput by remember { mutableStateOf("") }
                PostStudyDialog(
                    onDismissRequest = { deleteStep = 0; subjectToDelete = null },
                    title = "Yakuniy tasdiq",
                    text = "Fanni o'chirish uchun uning nomini ('${subjectToDelete?.name}') qo'lda kiriting:",
                    confirmText = "O'CHIRISH",
                    confirmColor = Color.Red,
                    onConfirm = {
                        if (nameInput == subjectToDelete?.name) {
                            CoroutineScope(Dispatchers.IO).launch {
                                AppContainer.localRepository.deleteSubject(subjectToDelete!!.id)
                                refresh()
                            }
                            deleteStep = 0
                            subjectToDelete = null
                        }
                    },
                    content = {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Fan nomini yozing") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = AppDesign.ComponentShape
                        )
                    }
                )
            }
        }
    }

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
                .background(Color(0xFF6366F1).copy(alpha = 0.05f), CircleShape)
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Fanlar boshqaruvi", fontWeight = FontWeight.Black, color = Color(0xFF065F46)) },
                    navigationIcon = {
                        IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Orqaga") }
                    },
                    actions = {
                        IconButton(onClick = { showAddDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Qo'shish", tint = Color(0xFF10B981))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 32.dp)
                    .zIndex(1f), // Ensure content is above standard background but can be beaten by high zIndex items
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Fanlarni tanlash",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF059669)
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
                    text = "Boshqarishni davom ettirish uchun fanni tanlang",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF64748B),
                    modifier = Modifier.padding(top = 8.dp, bottom = 48.dp)
                )

                if (isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(320.dp),
                        modifier = Modifier.fillMaxWidth().widthIn(max = 1200.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                        contentPadding = PaddingValues(top = 40.dp, bottom = 48.dp) // More breathing room
                    ) {
                        itemsIndexed(subjects) { index, subject ->
                            val color = AppDesign.RainbowPalette[index % AppDesign.RainbowPalette.size]
                            SubjectSelectionCard(
                                title = subject.name,
                                subtitle = if (subject.id == currentSubjectId) "Hozirgi tanlangan" else "Boshqarish uchun tanlang",
                                icon = Icons.AutoMirrored.Filled.List,
                                color = color,
                                isPrimary = subject.id == currentSubjectId,
                                onClick = { onSubjectSelected(subject) },
                                onEdit = { subjectToEdit = subject },
                                onDelete = { 
                                    subjectToDelete = subject
                                    deleteStep = 1
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
fun SubjectSelectionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    isPrimary: Boolean,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .height(240.dp)
            .hoverEffect(scale = if (isPrimary) 1.08f else 1.05f),
        shape = AppDesign.ComponentShape,
        color = Color.White,
        border = BorderStroke(if (isPrimary) 5.dp else 3.dp, color.copy(alpha = if (isPrimary) 1f else 0.6f)),
        shadowElevation = if (isPrimary) 16.dp else 8.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onEdit) { 
                    Icon(Icons.Default.Edit, contentDescription = "Tahrirlash", tint = color.copy(alpha = 0.7f)) 
                }
                if (title != "Asosiy fan") {
                    IconButton(onClick = onDelete) { 
                        Icon(Icons.Default.Delete, contentDescription = "O'chirish", tint = Color.Red.copy(alpha = 0.7f)) 
                    }
                }
            }
            
            Box(
                modifier = Modifier
                    .size(if (isPrimary) 90.dp else 80.dp)
                    .background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(if (isPrimary) 42.dp else 36.dp))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = title,
                style = if (isPrimary) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = color
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isPrimary) color else Color(0xFF64748B),
                fontWeight = if (isPrimary) FontWeight.Black else FontWeight.Bold
            )
        }
    }
}
