package com.example.poststudy.domain.model

enum class LessonMode {
    ReAppropriation,
    TestOnly
}

data class Lesson(
    val id: Int = 0,
    val title: String,
    val presentationPath: String,
    val testPath: String,
    val slideTimerSeconds: Int,
    val testTimerSeconds: Int,
    val mode: LessonMode = LessonMode.ReAppropriation
)

data class Exam(
    val id: Int = 0,
    val title: String,
    val testPath: String,
    val testTimerSeconds: Int,
    val questionsPerStudent: Int
)
