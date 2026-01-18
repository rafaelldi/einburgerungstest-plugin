package me.rafaelldi.einburgerungstest.questions

import kotlinx.serialization.Serializable

@Serializable
data class Question(
    val question: String,
    val answers: List<String>,
    val correct: Int,
    val category: String
)
