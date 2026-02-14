package me.rafaelldi.einburgerungstest.questions

import kotlinx.serialization.Serializable

@Serializable
internal data class QuestionImageDTO(
    val url: String,
    val text: String? = null
)

@Serializable
internal data class QuestionDTO(
    val question: String,
    val answers: List<String>,
    val correct: Int,
    val category: String,
    val img: QuestionImageDTO? = null
)
