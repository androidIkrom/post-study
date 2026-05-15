package com.example.poststudy.presentation

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import com.example.poststudy.data.local.DatabaseHelper
import com.example.poststudy.data.network.NetworkManager
import com.example.poststudy.data.network.SessionData
import com.example.poststudy.data.util.PptConverter
import com.example.poststudy.data.util.TestParser
import com.example.poststudy.domain.model.*
import com.example.poststudy.presentation.theme.PostStudyTheme
import com.example.poststudy.presentation.ui.*
import com.example.poststudy.presentation.ui.components.PostStudyDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.image.BufferedImage

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.RoleSelection) }
    var selectedRole by remember { mutableStateOf<UserRole?>(null) }
    
    var slides by remember { mutableStateOf<List<BufferedImage>>(emptyList()) }
    var slideTimer by remember { mutableStateOf(300) } // Default 5 mins in seconds
    var isPreparationMode by remember { mutableStateOf(false) }
    
    var questions by remember { mutableStateOf<List<Question>>(emptyList()) }
    var testTimer by remember { mutableStateOf(1800) } // Default 30 mins in seconds
    var userAnswers by remember { mutableStateOf<List<Int?>>(emptyList()) }
    var timeSpent by remember { mutableStateOf(0) }
    
    var isLoading by remember { mutableStateOf(false) }
    var studentName by remember { mutableStateOf("") }
    var currentLessonTitle by remember { mutableStateOf("Tezkor sessiya") }
    var currentSessionMode by remember { mutableStateOf(LessonMode.ReAppropriation) }

    var isNetworkMode by remember { mutableStateOf(false) }
    var serverIp by remember { mutableStateOf("") }

    PostStudyTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Crossfade(targetState = currentScreen) { screen ->
                when (screen) {
                    is Screen.RoleSelection -> RoleSelectionScreen(
                        onRoleSelected = { role ->
                            selectedRole = role
                            isNetworkMode = false
                            if (role == UserRole.Teacher) {
                                currentScreen = Screen.Login
                            } else {
                                currentScreen = Screen.StudentHome
                            }
                        },
                        onJoinNetwork = {
                            selectedRole = UserRole.Student
                            isNetworkMode = true
                            currentScreen = Screen.NetworkConnect
                        }
                    )
                    is Screen.NetworkConnect -> NetworkConnectScreen(
                        onConnected = { ip, session ->
                            serverIp = ip
                            currentLessonTitle = session.title
                            questions = session.questions
                            slideTimer = session.slideTimerSeconds
                            testTimer = session.testTimerSeconds
                            currentSessionMode = session.mode
                            currentScreen = Screen.StudentIntro
                        },
                        onBack = { currentScreen = Screen.RoleSelection }
                    )
                    is Screen.StudentHome -> StudentHomeScreen(
                        onNavigateToPreparation = { currentScreen = Screen.PreparationLessonSelection },
                        onNavigateToTest = { 
                            isLoading = true
                            isPreparationMode = false
                            currentScreen = Screen.StudentIntro 
                        },
                        onBack = { currentScreen = Screen.RoleSelection }
                    )
                    is Screen.PreparationLessonSelection -> LessonSelectionScreen(
                        isTeacher = false,
                        onLessonSelected = { lesson ->
                            currentLessonTitle = lesson.title
                            currentScreen = Screen.PreparationSlideShow(lesson)
                        },
                        onAddNewLesson = {}, // Not needed for students
                        onEditLesson = {}, // Not needed for students
                        onViewHistory = {}, // Not needed for students
                        onBack = { currentScreen = Screen.StudentHome }
                    )
                    is Screen.PreparationSlideShow -> {
                        val lesson = screen.lesson
                        var isPrepLoading by remember { mutableStateOf(true) }
                        LaunchedEffect(lesson) {
                            withContext(Dispatchers.IO) {
                                slides = PptConverter.convertSlidesToImages(lesson.presentationPath)
                            }
                            isPrepLoading = false
                        }

                        if (isPrepLoading) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(color = Color(0xFF6366F1))
                                    Spacer(Modifier.height(16.dp))
                                    Text("Taqdimot yuklanmoqda...", color = Color.Gray)
                                }
                            }
                        } else {
                            SlideShowScreen(
                                slides = slides,
                                slideTimerSeconds = 0, // No timer
                                onFinished = { currentScreen = Screen.StudentHome },
                                onBack = { currentScreen = Screen.StudentHome }
                            )
                        }
                    }
                    is Screen.Login -> LoginScreen(
                        onLoginSuccess = { 
                            // Start server when teacher logs in
                            NetworkManager.startServer(
                                getSession = {
                                    val s = DatabaseHelper.getSettings()
                                    val qList = TestParser.parseTest(s.testPath).questions
                                    val count = if (s.questionsPerStudent > 0) minOf(s.questionsPerStudent, qList.size) else qList.size
                                    SessionData(
                                        title = s.activeSessionTitle.ifBlank { "Dars" },
                                        questions = qList.shuffled().take(count),
                                        slideTimerSeconds = s.slideTimerSeconds,
                                        testTimerSeconds = s.testTimerSeconds,
                                        mode = s.mode
                                    )
                                },
                                onRecordReceived = { record ->
                                    DatabaseHelper.saveExamRecord(record)
                                }
                            )
                            currentScreen = Screen.TeacherIntro 
                        },
                        onBack = { currentScreen = Screen.RoleSelection }
                    )
                    is Screen.TeacherIntro -> TeacherIntroScreen(
                        onNext = { currentScreen = Screen.TeacherHome },
                        onBack = { currentScreen = Screen.Login }
                    )
                    is Screen.TeacherHome -> TeacherHomeScreen(
                        onNavigateToLessons = { currentScreen = Screen.LessonSelection },
                        onNavigateToExam = { currentScreen = Screen.ExamSelection },
                        onNavigateToHistory = { currentScreen = Screen.History },
                        onBack = { currentScreen = Screen.RoleSelection }
                    )
                    is Screen.ExamSelection -> ExamSelectionScreen(
                        onExamSelected = { exam ->
                            DatabaseHelper.saveSettings(
                                presentationPath = "",
                                testPath = exam.testPath,
                                slideTimerMin = 0,
                                testTimerMin = exam.testTimerSeconds / 60,
                                mode = LessonMode.TestOnly,
                                sessionTitle = exam.title,
                                qCount = exam.questionsPerStudent
                            )
                            currentLessonTitle = exam.title
                            currentSessionMode = LessonMode.TestOnly
                            currentScreen = Screen.TeacherHome
                        },
                        onAddNewExam = { currentScreen = Screen.ExamSettings },
                        onEditExam = { exam -> currentScreen = Screen.EditExam(exam) },
                        onBack = { currentScreen = Screen.TeacherHome }
                    )
                    is Screen.ExamSettings -> ExamSettingsScreen(
                        onSaveComplete = { currentScreen = Screen.ExamSelection },
                        onBack = { currentScreen = Screen.ExamSelection }
                    )
                    is Screen.EditExam -> ExamSettingsScreen(
                        examToEdit = screen.exam,
                        onSaveComplete = { currentScreen = Screen.ExamSelection },
                        onBack = { currentScreen = Screen.ExamSelection }
                    )
                    is Screen.Readme -> ReadmeScreen(
                        onNext = { currentScreen = Screen.TeacherHome },
                        onBack = { currentScreen = Screen.TeacherHome }
                    )
                    is Screen.LessonSelection -> LessonSelectionScreen(
                        onLessonSelected = { lesson ->
                            DatabaseHelper.saveSettings(
                                lesson.presentationPath,
                                lesson.testPath,
                                lesson.slideTimerSeconds / 60,
                                lesson.testTimerSeconds / 60,
                                lesson.mode,
                                lesson.title,
                                0 // All questions
                            )
                            currentLessonTitle = lesson.title
                            currentSessionMode = lesson.mode
                            currentScreen = Screen.TeacherHome
                        },
                        onAddNewLesson = {
                            currentScreen = Screen.Settings
                        },
                        onEditLesson = { lesson ->
                            currentScreen = Screen.EditLesson(lesson)
                        },
                        onViewHistory = {
                            currentScreen = Screen.History
                        },
                        onBack = {
                            currentScreen = Screen.TeacherHome
                        }
                    )
                    is Screen.StudentIntro -> {
                        if (isLoading) {
                            LaunchedEffect(isLoading, isNetworkMode) {
                                if (isNetworkMode) {
                                    withContext(Dispatchers.IO) {
                                        val session = NetworkManager.fetchSession(serverIp)
                                        if (session != null) {
                                            currentLessonTitle = session.title
                                            questions = session.questions
                                            slideTimer = session.slideTimerSeconds
                                            testTimer = session.testTimerSeconds
                                            currentSessionMode = session.mode
                                            if (currentSessionMode == LessonMode.ReAppropriation) {
                                                // Note: Files aren't shared via network yet, but metadata is.
                                                // For a full fix, we'd need to serve files or assume they are shared.
                                                // Keeping it as is for now.
                                            } else {
                                                slides = emptyList()
                                            }
                                        }
                                    }
                                } else {
                                    val settings = DatabaseHelper.getSettings()
                                    slideTimer = settings.slideTimerSeconds
                                    testTimer = settings.testTimerSeconds
                                    currentSessionMode = settings.mode
                                    currentLessonTitle = settings.activeSessionTitle.ifBlank { "Tezkor sessiya" }
                                    withContext(Dispatchers.IO) {
                                        if (currentSessionMode == LessonMode.ReAppropriation) {
                                            slides = PptConverter.convertSlidesToImages(settings.presentationPath)
                                        } else {
                                            slides = emptyList()
                                        }
                                        val result = TestParser.parseTest(settings.testPath)
                                        val rawQuestions = result.questions
                                        val shuffled = rawQuestions.shuffled()
                                        val count = if (settings.questionsPerStudent > 0) {
                                            minOf(settings.questionsPerStudent, shuffled.size)
                                        } else {
                                            shuffled.size
                                        }
                                        
                                        questions = shuffled.take(count).map { q ->
                                            val indexedOptions = q.options.withIndex().shuffled()
                                            val newCorrectIndex = indexedOptions.indexOfFirst { it.index == q.correctIndex }
                                            q.copy(
                                                options = indexedOptions.map { it.value },
                                                correctIndex = newCorrectIndex
                                            )
                                        }
                                    }
                                }
                                isLoading = false
                            }
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator()
                                    Spacer(Modifier.height(16.dp))
                                    Text("Sessiya tayyorlanmoqda...", style = MaterialTheme.typography.bodyLarge)
                                }
                            }
                        } else {
                            var showNameDialog by remember { mutableStateOf(studentName.isEmpty()) }
                            if (showNameDialog) {
                                NameInputDialog(
                                    onNameEntered = { name ->
                                        studentName = name
                                        showNameDialog = false
                                    },
                                    onDismiss = {
                                        currentScreen = Screen.RoleSelection
                                    }
                                )
                            }
                            
                            StudentIntroScreen(
                                title = currentLessonTitle,
                                totalSlides = slides.size,
                                totalQuestions = questions.size,
                                slideTimerSeconds = slideTimer,
                                testTimerSeconds = testTimer,
                                mode = currentSessionMode,
                                onStart = {
                                    if (currentSessionMode == LessonMode.ReAppropriation) {
                                        currentScreen = Screen.SlideShow
                                    } else {
                                        currentScreen = Screen.Test
                                    }
                                },
                                onRefresh = {
                                    isLoading = true
                                },
                                onBack = {
                                    currentScreen = Screen.RoleSelection
                                }
                            )
                        }
                    }
                    is Screen.Settings -> SettingsScreen(
                        onSaveComplete = { currentScreen = Screen.LessonSelection },
                        onBack = { currentScreen = Screen.LessonSelection }
                    )
                    is Screen.EditLesson -> SettingsScreen(
                        lessonToEdit = screen.lesson,
                        onSaveComplete = { currentScreen = Screen.LessonSelection },
                        onBack = { currentScreen = Screen.LessonSelection }
                    )
                    is Screen.SlideShow -> {
                        SlideShowScreen(
                            slides = slides,
                            slideTimerSeconds = slideTimer,
                            onFinished = {
                                currentScreen = Screen.Test
                            },
                            onBack = {
                                currentScreen = Screen.RoleSelection
                            }
                        )
                    }
                    is Screen.Test -> {
                        TestScreen(
                            sessionTitle = currentLessonTitle,
                            questions = questions,
                            testTimerSeconds = testTimer,
                            studentName = studentName,
                            onFinished = { answers, spent ->
                                userAnswers = answers
                                timeSpent = spent
                                
                                // Save Exam Record
                                val correctCount = questions.zip(answers).count { it.first.correctIndex == it.second }
                                val wrongDetails = questions.zip(answers).filter { it.first.correctIndex != it.second }
                                    .joinToString("\n\n") { (q, a) -> 
                                        "S: ${q.text}\nTo'g'ri javob: ${q.options[q.correctIndex]}\nTanlangan javob: ${a?.let { q.options[it] } ?: "O'tkazib yuborildi"}"
                                    }
                                
                                DatabaseHelper.saveExamRecord(ExamRecord(
                                    studentName = if (studentName.isBlank()) "Anonim" else studentName,
                                    lessonTitle = currentLessonTitle,
                                    totalQuestions = questions.size,
                                    correctAnswers = correctCount,
                                    wrongAnswers = questions.size - correctCount,
                                    wrongDetails = wrongDetails,
                                    timeSpentSeconds = spent,
                                    timestamp = System.currentTimeMillis()
                                ))

                                if (isNetworkMode) {
                                    Thread {
                                        NetworkManager.submitResult(serverIp, record = ExamRecord(
                                            studentName = if (studentName.isBlank()) "Anonim" else studentName,
                                            lessonTitle = currentLessonTitle,
                                            totalQuestions = questions.size,
                                            correctAnswers = correctCount,
                                            wrongAnswers = questions.size - correctCount,
                                            wrongDetails = wrongDetails,
                                            timeSpentSeconds = spent,
                                            timestamp = System.currentTimeMillis()
                                        ))
                                    }.start()
                                }
                                
                                currentScreen = Screen.Result
                            },
                            onBack = {
                                currentScreen = Screen.RoleSelection
                            }
                        )
                    }
                    is Screen.Result -> {
                        ResultScreen(
                            questions = questions,
                            userAnswers = userAnswers,
                            studentName = studentName,
                            timeSpentSeconds = timeSpent,
                            onFinish = {
                                currentScreen = Screen.RoleSelection
                            }
                        )
                    }
                    is Screen.History -> {
                        HistoryScreen(onBack = { currentScreen = Screen.TeacherHome })
                    }
                }
            }
        }
    }
}

@Composable
fun NameInputDialog(onNameEntered: (String) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    PostStudyDialog(
        onDismissRequest = onDismiss,
        title = "Talabaning ismi",
        text = "Iltimos, sessiyani boshlash uchun to'liq ismingizni kiriting. Bu natijalaringizni saqlash uchun ishlatiladi.",
        confirmText = "Sessiyani boshlash",
        onConfirm = { if (name.isNotBlank()) onNameEntered(name) },
        content = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("To'liq ism") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6366F1),
                    focusedLabelColor = Color(0xFF6366F1)
                )
            )
        }
    )
}
