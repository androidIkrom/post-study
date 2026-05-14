package com.example.poststudy.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.example.poststudy.presentation.theme.AppDesign
import com.example.poststudy.presentation.ui.components.PostStudyDialog
import kotlinx.coroutines.delay

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
            .background(AppDesign.BackgroundGradient)
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
                            shape = AppDesign.ComponentShape,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                            modifier = Modifier.padding(end = 16.dp)
                        ) {
                            val minutes = timeLeftSeconds / 60
                            val seconds = timeLeftSeconds % 60
                            Text(
                                text = "%d:%02d".format(minutes, seconds),
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
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
                        .fillMaxWidth(0.8f)
                        .padding(bottom = 32.dp)
                        .height(12.dp)
                        .clip(CircleShape),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.2f)
                )

                Card(
                    modifier = Modifier.fillMaxWidth().widthIn(max = 850.dp),
                    shape = AppDesign.CardShape,
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF4F46E5).copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(48.dp)) {
                        Text(
                            text = "${currentQuestionIndex + 1} / ${questions.size}-savol",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF4F46E5),
                            fontWeight = FontWeight.ExtraBold
                        )
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        Text(
                            text = currentQuestion.text,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1E293B)
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        currentQuestion.options.forEachIndexed { index, option ->
                            val isCorrectHint = showCheatHint && index == currentQuestion.correctIndex
                            
                            OptionCard(
                                text = option,
                                isSelected = selectedAnswers[currentQuestionIndex] == index,
                                isHint = isCorrectHint,
                                onClick = { selectedAnswers[currentQuestionIndex] = index }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().widthIn(max = 850.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { if (currentQuestionIndex > 0) currentQuestionIndex-- },
                        enabled = currentQuestionIndex > 0,
                        modifier = Modifier.height(64.dp).width(200.dp),
                        shape = AppDesign.ComponentShape,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color.White.copy(alpha = 0.5f))
                    ) {
                        Text("Oldingi", fontWeight = FontWeight.ExtraBold)
                    }

                    Button(
                        onClick = handleNextSubmit,
                        modifier = Modifier.height(64.dp).width(200.dp),
                        shape = AppDesign.ComponentShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF4F46E5)),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        Text(
                            text = if (currentQuestionIndex < questions.size - 1) "Keyingisi" else "Yakunlash",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(40.dp))
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
    val borderColor = when {
        isHint -> Color(0xFF10B981)
        isSelected -> Color(0xFF4F46E5)
        else -> Color(0xFFE2E8F0)
    }
    
    val backgroundColor = when {
        isHint -> Color(0xFFECFDF5)
        isSelected -> Color(0xFFEEF2FF)
        else -> Color.Transparent
    }

    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = AppDesign.ComponentShape,
        color = backgroundColor,
        border = androidx.compose.foundation.BorderStroke(2.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = if (isHint) Color(0xFF10B981) else Color(0xFF4F46E5),
                    unselectedColor = Color(0xFF94A3B8)
                )
            )
            Text(
                text = text,
                modifier = Modifier.padding(start = 16.dp),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                color = when {
                    isHint -> Color(0xFF065F46)
                    isSelected -> Color(0xFF4F46E5)
                    else -> Color(0xFF475569)
                }
            )
        }
    }
}
