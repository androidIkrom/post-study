package com.example.poststudy.domain.model

sealed class Screen {
    data object RoleSelection : Screen()
    data object Login : Screen()
    data object Readme : Screen()
    data object TeacherIntro : Screen()
    data object StudentHome : Screen()
    data object PreparationLessonSelection : Screen()
    data class PreparationSlideShow(val lesson: Lesson) : Screen()
    data object StudentIntro : Screen()
    data object TeacherHome : Screen()
    data object Settings : Screen()
    data object ExamSelection : Screen()
    data object ExamSettings : Screen()
    data class EditExam(val exam: Exam) : Screen()
    data class EditLesson(val lesson: Lesson) : Screen()
    data object LessonSelection : Screen()
    data object SlideShow : Screen()
    data object Test : Screen()
    data object Result : Screen()
    data object History : Screen()
    data object NetworkConnect : Screen()
}

data class ExamRecord(
    val id: Int = 0,
    val studentName: String,
    val lessonTitle: String,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val wrongAnswers: Int,
    val wrongDetails: String,
    val timeSpentSeconds: Int,
    val timestamp: Long
)

enum class UserRole {
    Teacher,
    Student
}
