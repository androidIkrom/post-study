package com.example.poststudy.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.poststudy.domain.model.LessonMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentIntroScreen(
    totalSlides: Int,
    totalQuestions: Int,
    slideTimerSeconds: Int,
    testTimerSeconds: Int,
    mode: LessonMode,
    onStart: () -> Unit,
    onBack: () -> Unit
) {
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
        Box(
            modifier = Modifier
                .size(400.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 100.dp, y = 100.dp)
                .background(Color(0xFF6366F1).copy(alpha = 0.05f), CircleShape)
        )

        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier
                .focusRequester(focusRequester)
                .onPreviewKeyEvent {
                    if ((it.key == Key.Enter || it.key == Key.DirectionRight) && it.type == KeyEventType.KeyDown) {
                        onStart()
                        true
                    } else false
                },
            topBar = {
                CenterAlignedTopAppBar(
                    title = { 
                        Text(
                            if (mode == LessonMode.ReAppropriation) "O'quv mashg'uloti brifingi" else "Imtihon mashg'uloti brifingi",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Orqaga", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 32.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = if (mode == LessonMode.ReAppropriation) 
                        "Mashg'ulotga tayyorlaning. Avval taqdimotni o'rganasiz, so'ngra test topshirasiz."
                    else 
                        "Imtihonga tayyorlaning. Bu sizning bilimingizni bevosita tekshirishdir.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Session Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    if (mode == LessonMode.ReAppropriation) {
                        StatCard(
                            modifier = Modifier.weight(1f),
                            title = "Taqdimot",
                            value = "$totalSlides Slaydlar",
                            subValue = "Jami ${(totalSlides * slideTimerSeconds) / 60} daqiqa"
                        )
                    }
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Bilim testi",
                        value = "$totalQuestions Savollar",
                        subValue = "Jami ${testTimerSeconds / 60} daqiqa"
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Rules Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(32.dp)) {
                        Text(
                            text = if (mode == LessonMode.ReAppropriation) "O'qish qoidalari" else "Imtihon qoidalari",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        if (mode == LessonMode.ReAppropriation) {
                            RuleItem("1. Har bir slaydni taymer tugashidan oldin ko'rib chiqing.")
                            RuleItem("2. Slaydlarni boshqarish uchun strelkalar yoki tugmalardan foydalaning.")
                            RuleItem("3. Taqdimotdan so'ng vaqtli test boshlanadi.")
                        } else {
                            RuleItem("1. Bu to'g'ridan-to'g'ri imtihon. O'quv materiallari ko'rsatilmaydi.")
                        }
                        
                        RuleItem("${if (mode == LessonMode.ReAppropriation) "4" else "2"}. Test ko'p variantli. Har bir savol uchun bitta javobni tanlang.")
                        RuleItem("${if (mode == LessonMode.ReAppropriation) "5" else "3"}. Testda savollar orasida oldinga va orqaga harakat qilishingiz mumkin.")
                    }
                }

                Spacer(modifier = Modifier.height(56.dp))

                Button(
                    onClick = onStart,
                    modifier = Modifier
                        .width(320.dp)
                        .height(64.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    Text(if (mode == LessonMode.ReAppropriation) "TAYYORMAN" else "IMTIHONNI BOSHLASH", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                }
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun StatCard(modifier: Modifier, title: String, value: String, subValue: String) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.7f))
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = subValue, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.5f))
        }
    }
}

@Composable
fun RuleItem(text: String) {
    Row(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(text = "•", modifier = Modifier.padding(end = 12.dp), color = Color(0xFF6366F1), fontWeight = FontWeight.Bold)
        Text(text = text, style = MaterialTheme.typography.bodyLarge, color = Color(0xFF64748B))
    }
}
