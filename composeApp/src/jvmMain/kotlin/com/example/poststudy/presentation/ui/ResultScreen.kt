package com.example.poststudy.presentation.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.poststudy.domain.model.Question
import com.example.poststudy.presentation.theme.AppDesign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    questions: List<Question>,
    userAnswers: List<Int?>,
    studentName: String,
    timeSpentSeconds: Int,
    onFinish: () -> Unit
) {
    val correctCount = questions.zip(userAnswers).count { (question, answer) ->
        question.correctIndex == answer
    }
    val percentage = if (questions.isNotEmpty()) (correctCount.toDouble() / questions.size * 100).toInt() else 0
    val focusRequester = remember { FocusRequester() }

    val scoreColor = when {
        percentage >= 80 -> Color(0xFF4ADE80)
        percentage >= 60 -> Color(0xFFFACC15)
        else -> Color(0xFFEF4444)
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
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
                    if (it.key == Key.Enter && it.type == KeyEventType.KeyDown) {
                        onFinish()
                        true
                    } else false
                },
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Natijalar Tahlili", fontWeight = FontWeight.ExtraBold, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = onFinish) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Orqaga", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth().widthIn(max = 650.dp),
                    shape = AppDesign.CardShape,
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
                    border = androidx.compose.foundation.BorderStroke(2.dp, scoreColor.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Sizning yakuniy natijangiz",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = studentName,
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1E293B),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))

                        Box(
                            modifier = Modifier
                                .size(180.dp)
                                .background(scoreColor.copy(alpha = 0.1f), CircleShape)
                                .border(4.dp, scoreColor.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$percentage%",
                                    style = MaterialTheme.typography.displayMedium,
                                    fontWeight = FontWeight.Black,
                                    color = scoreColor
                                )
                                Text(
                                    text = "$correctCount / ${questions.size}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = scoreColor.copy(alpha = 0.7f)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(32.dp))

                        val timeMinutes = timeSpentSeconds / 60
                        val timeSeconds = timeSpentSeconds % 60
                        Surface(
                            color = Color(0xFFF1F5F9),
                            shape = CircleShape
                        ) {
                            Text(
                                text = "Sarflangan vaqt: %d:%02d".format(timeMinutes, timeSeconds),
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF475569)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth().widthIn(max = 850.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    itemsIndexed(questions) { index, question ->
                        val userAnswerIndex = userAnswers.getOrNull(index)
                        val isCorrect = userAnswerIndex == question.correctIndex

                        Card(
                            modifier = Modifier.fillMaxWidth().animateContentSize(),
                            shape = AppDesign.CardShape,
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                            border = androidx.compose.foundation.BorderStroke(
                                2.dp, 
                                if (isCorrect) Color(0xFF4ADE80).copy(alpha = 0.3f) else Color(0xFFEF4444).copy(alpha = 0.3f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                Row(verticalAlignment = Alignment.Top) {
                                    Surface(
                                        color = if (isCorrect) Color(0xFF4ADE80).copy(alpha = 0.1f) else Color(0xFFEF4444).copy(alpha = 0.1f),
                                        shape = CircleShape,
                                        modifier = Modifier.size(40.dp),
                                        border = androidx.compose.foundation.BorderStroke(2.dp, if (isCorrect) Color(0xFF4ADE80) else Color(0xFFEF4444))
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "${index + 1}",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Black,
                                                color = if (isCorrect) Color(0xFF4ADE80) else Color(0xFFEF4444)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(20.dp))
                                    Text(
                                        text = question.text,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF1E293B)
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(24.dp))

                                val userAnswerText = userAnswerIndex?.let { question.options.getOrNull(it) } ?: "O'tkazib yuborildi"
                                val correctAnswerText = question.options[question.correctIndex]

                                ResultDetailBox(
                                    label = "Sizning javobingiz",
                                    text = userAnswerText,
                                    color = if (isCorrect) Color(0xFF059669) else Color(0xFFEF4444),
                                    backgroundColor = if (isCorrect) Color(0xFFECFDF5) else Color(0xFFFFF1F2)
                                )

                                if (!isCorrect) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    ResultDetailBox(
                                        label = "To'g'ri javob",
                                        text = correctAnswerText,
                                        color = Color(0xFF059669),
                                        backgroundColor = Color(0xFFECFDF5)
                                    )
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = onFinish,
                    modifier = Modifier.padding(vertical = 32.dp).fillMaxWidth().widthIn(max = 400.dp).height(64.dp),
                    shape = AppDesign.ComponentShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF4F46E5)),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 12.dp)
                ) {
                    Text("TAYYOR", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
fun ResultDetailBox(label: String, text: String, color: Color, backgroundColor: Color) {
    Surface(
        color = backgroundColor,
        shape = AppDesign.ComponentShape,
        modifier = Modifier.fillMaxWidth(),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "$label: ",
                style = MaterialTheme.typography.labelLarge,
                color = Color(0xFF64748B),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
        }
    }
}
