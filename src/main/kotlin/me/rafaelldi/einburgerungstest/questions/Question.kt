package me.rafaelldi.einburgerungstest.questions

internal data class Question(
    val id: Int,
    val question: String,
    val answers: List<String>,
    val correctAnswer: Int,
    val category: String
)
