package com.example.poststudy.domain.model

data class Question(
    val text: String,
    val options: List<String>,
    val correctIndex: Int
)
