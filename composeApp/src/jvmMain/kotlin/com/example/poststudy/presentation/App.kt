package com.example.poststudy.presentation

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.poststudy.data.local.DatabaseHelper
import com.example.poststudy.data.network.NetworkManager
import com.example.poststudy.data.network.SessionData
import com.example.poststudy.data.util.PptConverter
import com.example.poststudy.data.util.TestParser
import com.example.poststudy.domain.model.*
import com.example.poststudy.presentation.theme.PostStudyTheme
import com.example.poststudy.presentation.ui.screens.admin.ExamSettingsScreen
import com.example.poststudy.presentation.ui.components.HelpIcon
import com.example.poststudy.presentation.ui.components.PostStudyDialog
import com.example.poststudy.presentation.ui.screens.admin.ExamSelectionScreen
import com.example.poststudy.presentation.ui.screens.admin.HistoryScreen
import com.example.poststudy.presentation.ui.screens.admin.LessonSelectionScreen
import com.example.poststudy.presentation.ui.screens.admin.LoginScreen
import com.example.poststudy.presentation.ui.screens.admin.ReadmeScreen
import com.example.poststudy.presentation.ui.screens.admin.SettingsScreen
import com.example.poststudy.presentation.ui.screens.admin.TeacherHomeScreen
import com.example.poststudy.presentation.ui.screens.admin.TeacherIntroScreen
import com.example.poststudy.presentation.ui.screens.admin.MonitoringScreen
import com.example.poststudy.presentation.ui.screens.intro.InfoScreen
import com.example.poststudy.presentation.ui.screens.intro.RoleSelectionScreen
import com.example.poststudy.presentation.ui.screens.intro.SplashScreen
import com.example.poststudy.presentation.ui.screens.groups.*
import com.example.poststudy.presentation.ui.screens.network.NetworkConnectScreen
import com.example.poststudy.presentation.ui.screens.student.ResultScreen
import com.example.poststudy.presentation.ui.screens.student.SlideShowScreen
import com.example.poststudy.presentation.ui.screens.student.StudentHomeScreen
import com.example.poststudy.presentation.ui.screens.student.StudentIntroScreen
import com.example.poststudy.presentation.ui.screens.student.TestScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*
import javax.imageio.ImageIO

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Info) }
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
    var currentStudentId by remember { mutableStateOf<Int?>(null) }
    var currentGroupId by remember { mutableStateOf<Int?>(null) }
    
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
                    is Screen.Info -> InfoScreen(
                        onContinue = { currentScreen = Screen.Splash }
                    )
                    is Screen.Splash -> SplashScreen(
                        onContinue = { currentScreen = Screen.RoleSelection }
                    )
                    is Screen.RoleSelection -> Box {
                        RoleSelectionScreen(
                            onRoleSelected = { role ->
                                selectedRole = role
                                isNetworkMode = false
                                if (role == UserRole.Teacher) {
                                    currentScreen = Screen.Login
                                } else {
                                    studentName = "" // Reset student name when choosing role
                                    currentScreen = Screen.StudentHome
                                }
                            },
                            onJoinNetwork = {
                                selectedRole = UserRole.Student
                                isNetworkMode = true
                                studentName = "" // Reset student name
                                currentScreen = Screen.NetworkConnect
                            }
                        )
                        HelpIcon(
                            title = "Rolni tanlash",
                            helpText = "BreakPointga xush kelibsiz! Darslarni boshqarish uchun 'Admin' yoki test topshirish uchun 'Tinglovchi' rolini tanlang.",
                            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
                        )
                    }
                    is Screen.NetworkConnect -> Box {
                        NetworkConnectScreen(
                            onConnected = { ip, session ->
                                serverIp = ip
                                currentLessonTitle = session.title
                                questions = session.questions
                                slideTimer = session.slideTimerSeconds
                                testTimer = session.testTimerSeconds
                                currentSessionMode = session.mode
                                currentScreen = Screen.GroupSelection
                            },
                            onBack = { currentScreen = Screen.RoleSelection }
                        )
                        HelpIcon(
                            title = "Tarmoqqa ulanish",
                            helpText = "Admin kompyuteridagi IP manzilni kiriting va 'Ulanish' tugmasini bosing.",
                            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
                        )
                    }
                    is Screen.GroupSelection -> GroupSelectionScreen(
                        serverIp = serverIp,
                        onGroupSelected = { group ->
                            currentGroupId = group.id
                            currentScreen = Screen.StudentSelection(group)
                        },
                        onBack = { currentScreen = Screen.NetworkConnect }
                    )
                    is Screen.StudentSelection -> StudentSelectionScreen(
                        serverIp = serverIp,
                        group = screen.group,
                        onStudentSelected = { student ->
                            studentName = student.name
                            currentStudentId = student.id
                            currentScreen = Screen.StudentIntro
                        },
                        onBack = { currentScreen = Screen.GroupSelection }
                    )
                    is Screen.StudentHome -> Box {
                        StudentHomeScreen(
                            onNavigateToPreparation = {
                                currentScreen = Screen.PreparationLessonSelection
                            },
                            onNavigateToTest = {
                                isLoading = true
                                isPreparationMode = false
                                currentScreen = Screen.StudentIntro
                            },
                            onBack = { currentScreen = Screen.RoleSelection }
                        )
                        HelpIcon(
                            title = "Tinglovchi asosiysi",
                            helpText = "Mavjud darslarni o'rganish uchun 'Tayyorgarlik' yoki pedagog tomonidan belgilangan testni topshirish uchun 'Test sessiyasi'ni tanlang.",
                            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
                        )
                    }
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
                                    val qList = try { TestParser.parseTest(s.testPath).questions } catch(e: Exception) { emptyList() }
                                    val count = if (s.questionsPerStudent > 0) minOf(
                                        s.questionsPerStudent,
                                        qList.size
                                    ) else qList.size

                                    val slidesEncoded =
                                        if (s.mode == LessonMode.ReAppropriation && s.presentationPath.isNotBlank()) {
                                            try {
                                                val imgs = PptConverter.convertSlidesToImages(s.presentationPath)
                                                imgs.map { img ->
                                                    val baos = ByteArrayOutputStream()
                                                    // Use JPG with quality to reduce size significantly
                                                    ImageIO.write(img, "jpg", baos)
                                                    Base64.getEncoder().encodeToString(baos.toByteArray())
                                                }
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                                emptyList()
                                            }
                                        } else emptyList()

                                    SessionData(
                                        title = s.activeSessionTitle.ifBlank { "Dars" },
                                        questions = qList.shuffled().take(count).map { q ->
                                            val indexedOptions = q.options.withIndex().shuffled()
                                            val newCorrectIndex =
                                                indexedOptions.indexOfFirst { it.index == q.correctIndex }
                                            q.copy(
                                                options = indexedOptions.map { it.value },
                                                correctIndex = newCorrectIndex
                                            )
                                        },
                                        slideTimerSeconds = s.slideTimerSeconds,
                                        testTimerSeconds = s.testTimerSeconds,
                                        mode = s.mode,
                                        encodedSlides = slidesEncoded
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
                    is Screen.TeacherHome -> Box {
                        TeacherHomeScreen(
                            onNavigateToLessons = { currentScreen = Screen.LessonSelection },
                            onNavigateToExam = { currentScreen = Screen.ExamSelection },
                            onNavigateToHistory = { currentScreen = Screen.History },
                            onNavigateToMonitoring = { currentScreen = Screen.Monitoring },
                            onNavigateToGroups = { currentScreen = Screen.Groups },
                            onBack = { currentScreen = Screen.RoleSelection }
                        )
                        HelpIcon(
                            title = "Admin asosiysi",
                            helpText = "Bu yerda siz darslarni boshqarishingiz, yangi imtihonlar yaratishingiz va tinglovchilar natijalari tarixini ko'rishingiz mumkin.",
                            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
                        )
                    }
                    is Screen.Groups -> GroupManagementScreen(
                        onGroupSelected = { group -> currentScreen = Screen.GroupDetails(group) },
                        onBack = { currentScreen = Screen.TeacherHome }
                    )
                    is Screen.GroupDetails -> StudentListScreen(
                        group = screen.group,
                        onBack = { currentScreen = Screen.Groups }
                    )
                    is Screen.ExamSelection -> Box {
                        ExamSelectionScreen(
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
                        HelpIcon(
                            title = "Imtihonlar",
                            helpText = "Bu bo'limda siz faqat testdan iborat bo'lgan imtihon sessiyalarini boshqarishingiz mumkin.",
                            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
                        )
                    }
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
                    is Screen.LessonSelection -> Box {
                        LessonSelectionScreen(
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
                        HelpIcon(
                            title = "Darslarni tanlash",
                            helpText = "Mavjud darslardan birini tanlang yoki yangisini qo'shing. Har bir dars taqdimot va testdan iborat bo'ladi.",
                            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
                        )
                    }
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
                                            
                                            if (currentSessionMode == LessonMode.ReAppropriation && session.encodedSlides.isNotEmpty()) {
                                                slides = session.encodedSlides.mapNotNull { base64 ->
                                                    try {
                                                        val bytes = Base64.getDecoder().decode(base64)
                                                        ImageIO.read(ByteArrayInputStream(bytes))
                                                    } catch (e: Exception) {
                                                        null
                                                    }
                                                }
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
                                        val shuffledQuestions = rawQuestions.shuffled()
                                        val count = if (settings.questionsPerStudent > 0) {
                                            minOf(settings.questionsPerStudent, shuffledQuestions.size)
                                        } else {
                                            shuffledQuestions.size
                                        }
                                        
                                        questions = shuffledQuestions.take(count).map { q ->
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
                                    if (currentSessionMode == LessonMode.ReAppropriation && slides.isNotEmpty()) {
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
                                val correctCount = questions.zip(answers)
                                    .count { it.first.correctIndex == it.second }
                                val wrongDetails = questions.zip(answers)
                                    .filter { it.first.correctIndex != it.second }
                                    .joinToString("\n\n") { (q, a) ->
                                        "S: ${q.text}\nTo'g'ri javob: ${q.options[q.correctIndex]}\nTanlangan javob: ${a?.let { q.options[it] } ?: "O'tkazib yuborildi"}"
                                    }

                                val record = ExamRecord(
                                    studentName = if (studentName.isBlank()) "Anonim" else studentName,
                                    lessonTitle = currentLessonTitle,
                                    totalQuestions = questions.size,
                                    correctAnswers = correctCount,
                                    wrongAnswers = questions.size - correctCount,
                                    wrongDetails = wrongDetails,
                                    timeSpentSeconds = spent,
                                    timestamp = System.currentTimeMillis(),
                                    groupId = currentGroupId,
                                    studentId = currentStudentId
                                )
                                DatabaseHelper.saveExamRecord(record)

                                if (isNetworkMode) {
                                    Thread {
                                        NetworkManager.submitResult(serverIp, record = record)
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
                    is Screen.Monitoring -> {
                        MonitoringScreen(onBack = { currentScreen = Screen.TeacherHome })
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
        title = "Tinglovchi ismi",
        text = "Iltimos, sessiyani boshlash uchun to'liq ismingizni kiriting. Bu natijalaringizni saqlash uchun ishlatiladi.",
        confirmText = "Sessiyani boshlash",
        onConfirm = { if (name.isNotBlank()) onNameEntered(name) },
        content = {
            Column(horizontalAlignment = Alignment.End) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { if (it.length <= 22) name = it },
                    label = { Text("To'liq ism") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF6366F1),
                        focusedLabelColor = Color(0xFF6366F1)
                    )
                )
                Text(
                    text = "${name.length} / 22",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (name.length == 22) Color.Red else Color.Gray,
                    modifier = Modifier.padding(top = 4.dp, end = 8.dp)
                )
            }
        }
    )
}
