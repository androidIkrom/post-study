package com.example.poststudy.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.poststudy.domain.model.Question

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

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0F172A),
            Color(0xFF1E293B),
            Color(0xFF334155)
        )
    )

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
                    if (it.key == Key.Enter && it.type == KeyEventType.KeyDown) {
                        onFinish()
                        true
                    } else false
                },
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Natijalar tahlili", fontWeight = FontWeight.ExtraBold, color = Color.White) },
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
                    modifier = Modifier.fillMaxWidth().widthIn(max = 600.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (percentage >= 50) Color(0xFFECFDF5) else Color(0xFFFFF1F2)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Sizning yakuniy balingiz",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (percentage >= 50) Color(0xFF065F46) else Color(0xFF9F1239),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = studentName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Text(
                            text = "$correctCount / ${questions.size}",
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (percentage >= 50) Color(0xFF059669) else Color(0xFFE11D48)
                        )
                        
                        val timeMinutes = timeSpentSeconds / 60
                        val timeSeconds = timeSpentSeconds % 60
                        Text(
                            text = "Sarflangan vaqt: %d:%02d".format(timeMinutes, timeSeconds),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF64748B),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Surface(
                            color = (if (percentage >= 50) Color(0xFF059669) else Color(0xFFE11D48)).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "$percentage% aniqlik",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (percentage >= 50) Color(0xFF059669) else Color(0xFFE11D48)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth().widthIn(max = 800.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    itemsIndexed(questions) { index, question ->
                        val userAnswerIndex = userAnswers.getOrNull(index)
                        val isCorrect = userAnswerIndex == question.correctIndex

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(
                                2.dp, 
                                if (isCorrect) Color(0xFFD1FAE5) else Color(0xFFFFE4E6)
                            )
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                Row(verticalAlignment = Alignment.Top) {
                                    Surface(
                                        color = if (isCorrect) Color(0xFFD1FAE5) else Color(0xFFFFE4E6),
                                        shape = CircleShape,
                                        modifier = Modifier.size(32.dp),
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "${index + 1}",
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isCorrect) Color(0xFF059669) else Color(0xFFE11D48)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = question.text,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B)
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(20.dp))

                                val userAnswerText = userAnswerIndex?.let { question.options.getOrNull(it) } ?: "O'tkazib yuborildi"
                                val correctAnswerText = question.options[question.correctIndex]

                                ResultDetailBox(
                                    label = "Sizning javobingiz",
                                    text = userAnswerText,
                                    color = if (isCorrect) Color(0xFF059669) else Color(0xFFE11D48),
                                    backgroundColor = if (isCorrect) Color(0xFFECFDF5) else Color(0xFFFFF1F2)
                                )

                                if (!isCorrect) {
                                    Spacer(modifier = Modifier.height(8.dp))
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
                    modifier = Modifier.padding(vertical = 24.dp).width(300.dp).height(64.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    Text("TAYYOR", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

@Composable
fun ResultDetailBox(label: String, text: String, color: Color, backgroundColor: Color) {
    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "$label: ",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF64748B)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
