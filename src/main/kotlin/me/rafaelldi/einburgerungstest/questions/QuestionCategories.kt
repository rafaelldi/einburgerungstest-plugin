package me.rafaelldi.einburgerungstest.questions

internal data class QuestionCategories(
    val groupCategories: List<Pair<QuestionCategory, Int>> = emptyList(),
    val nationalCategories: List<Pair<QuestionCategory, Int>> = emptyList(),
    val regionalCategories: List<Pair<QuestionCategory, Int>> = emptyList()
)
