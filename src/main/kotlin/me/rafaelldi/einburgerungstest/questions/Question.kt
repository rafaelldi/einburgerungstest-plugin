package me.rafaelldi.einburgerungstest.questions

internal data class QuestionImage(
    val resourcePath: String,
    val attributionText: String? = null
)

internal data class Question(
    val id: Int,
    val question: String,
    val answers: List<String>,
    val correctAnswer: Int,
    val category: QuestionCategory,
    val image: QuestionImage? = null
)
