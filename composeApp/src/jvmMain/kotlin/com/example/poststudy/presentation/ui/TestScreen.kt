package com.example.poststudy.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.poststudy.domain.model.Question
import com.example.poststudy.presentation.ui.components.PostStudyDialog
import kotlinx.coroutines.delay
import java.awt.image.BufferedImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestScreen(
    questions: List<Question>,
    testTimerSeconds: Int,
    studentName: String,
    onFinished: (List<Int?>, Int) -> Unit,
    onBack: () -> Unit
) {
    var currentQuestionIndex by remember { mutableStateOf(0) }
    val selectedAnswers = remember { mutableStateListOf<Int?>(*arrayOfNulls<Int>(questions.size)) }
    var timeLeftSeconds by remember { mutableStateOf(testTimerSeconds) }
    val timeSpentSeconds = testTimerSeconds - timeLeftSeconds
    var showSubmitDialog by remember { mutableStateOf(false) }
    var showBackDialog by remember { mutableStateOf(false) }
    var showCheatHint by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0F172A),
            Color(0xFF1E293B),
            Color(0xFF334155)
        )
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        while (timeLeftSeconds > 0) {
            delay(1000)
            timeLeftSeconds--
        }
        onFinished(selectedAnswers.toList(), testTimerSeconds - timeLeftSeconds)
    }

    LaunchedEffect(showCheatHint) {
        if (showCheatHint) {
            delay(5000)
            showCheatHint = false
        }
    }

    val handleNextSubmit: () -> Unit = {
        if (currentQuestionIndex < questions.size - 1) {
            currentQuestionIndex++
        } else {
            showSubmitDialog = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier
                .focusRequester(focusRequester)
                .onPreviewKeyEvent {
                    if (it.type == KeyEventType.KeyDown) {
                        if (it.key == Key.K && it.isCtrlPressed && it.isShiftPressed) {
                            showCheatHint = true
                            true
                        } else {
                            when (it.key) {
                                Key.DirectionRight -> {
                                    if (currentQuestionIndex < questions.size - 1) currentQuestionIndex++
                                    true
                                }
                                Key.DirectionLeft -> {
                                    if (currentQuestionIndex > 0) currentQuestionIndex--
                                    true
                                }
                                Key.Enter -> {
                                    handleNextSubmit()
                                    true
                                }
                                Key.Escape -> {
                                    showBackDialog = true
                                    true
                                }
                                Key.One -> { selectedAnswers[currentQuestionIndex] = 0; true }
                                Key.Two -> { selectedAnswers[currentQuestionIndex] = 1; true }
                                Key.Three -> { selectedAnswers[currentQuestionIndex] = 2; true }
                                Key.Four -> { selectedAnswers[currentQuestionIndex] = 3; true }
                                else -> false
                            }
                        }
                    } else false
                },
            topBar = {
                TopAppBar(
                    title = { 
                        Column {
                            Text("Test mashg'uloti", fontWeight = FontWeight.ExtraBold, color = Color.White)
                            Text(studentName, style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.7f))
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { showBackDialog = true }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Orqaga", tint = Color.White)
                        }
                    },
                    actions = {
                        Surface(
                            color = Color(0xFFEF4444),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.padding(end = 16.dp)
                        ) {
                            val minutes = timeLeftSeconds / 60
                            val seconds = timeLeftSeconds % 60
                            Text(
                                text = "%d:%02d".format(minutes, seconds),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { paddingValues ->
            if (questions.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Test faylida savollar topilmadi.", color = Color.White)
                }
                return@Scaffold
            }

            val currentQuestion = questions[currentQuestionIndex]

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 32.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LinearProgressIndicator(
                    progress = { (currentQuestionIndex + 1).toFloat() / questions.size },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = Color(0xFF6366F1),
                    trackColor = Color.White.copy(alpha = 0.1f)
                )

                Card(
                    modifier = Modifier.fillMaxWidth().widthIn(max = 800.dp),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(40.dp)) {
                        Text(
                            text = "${currentQuestionIndex + 1} / ${questions.size}-savol",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color(0xFF6366F1),
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = currentQuestion.text,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E293B)
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        currentQuestion.options.forEachIndexed { index, option ->
                            val isCorrectHint = showCheatHint && index == currentQuestion.correctIndex
                            
                            OptionCard(
                                text = option,
                                isSelected = selectedAnswers[currentQuestionIndex] == index,
                                isHint = isCorrectHint,
                                onClick = { selectedAnswers[currentQuestionIndex] = index }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { if (currentQuestionIndex > 0) currentQuestionIndex-- },
                        enabled = currentQuestionIndex > 0,
                        modifier = Modifier.height(60.dp).width(180.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                    ) {
                        Text("Oldingi", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = handleNextSubmit,
                        modifier = Modifier.height(60.dp).width(180.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
                    ) {
                        Text(
                            text = if (currentQuestionIndex < questions.size - 1) "Keyingisi" else "Yakunlash",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showSubmitDialog) {
        PostStudyDialog(
            onDismissRequest = { showSubmitDialog = false },
            title = "Testni yakunlash?",
            text = "Javoblarni yuborish va testni yakunlashga ishonchingiz komilmi?",
            confirmText = "Ha, yuborish",
            onConfirm = { 
                showSubmitDialog = false
                onFinished(selectedAnswers.toList(), testTimerSeconds - timeLeftSeconds)
            }
        )
    }

    if (showBackDialog) {
        PostStudyDialog(
            onDismissRequest = { showBackDialog = false },
            title = "Sessiyani tugatish?",
            text = "Sessiyani tugatishga ishonchingiz komilmi? Joriy test natijalari yo'qoladi.",
            confirmText = "Ha, tugatish",
            confirmColor = Color(0xFFEF4444),
            onConfirm = { 
                showBackDialog = false
                onBack() 
            }
        )
    }
}

@Composable
fun OptionCard(text: String, isSelected: Boolean, isHint: Boolean = false, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = when {
            isHint -> Color(0xFFECFDF5)
            isSelected -> Color(0xFFEEF2FF)
            else -> Color.Transparent
        },
        border = androidx.compose.foundation.BorderStroke(
            2.dp,
            when {
                isHint -> Color(0xFF10B981)
                isSelected -> Color(0xFF6366F1)
                else -> Color(0xFFF1F5F9)
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = if (isHint) Color(0xFF10B981) else Color(0xFF6366F1)
                )
            )
            Text(
                text = text,
                modifier = Modifier.padding(start = 12.dp),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = when {
                    isHint -> Color(0xFF065F46)
                    isSelected -> Color(0xFF4338CA)
                    else -> Color(0xFF475569)
                }
            )
        }
    }
}
