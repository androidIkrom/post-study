package com.example.poststudy.domain.model

data class Settings(
    val id: Int,
    val presentationPath: String,
    val testPath: String,
    val slideTimerSeconds: Int,
    val testTimerSeconds: Int,
    val mode: LessonMode = LessonMode.ReAppropriation
)
