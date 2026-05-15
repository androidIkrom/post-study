package com.example.poststudy.domain.model

data class Question(
    val text: String,
    val options: List<String>,
    val correctIndex: Int
)

data class ParseResult(
    val questions: List<Question> = emptyList(),
    val warnings: List<String> = emptyList(),
    val error: String? = null
)
