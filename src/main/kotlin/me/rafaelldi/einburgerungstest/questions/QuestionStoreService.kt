package me.rafaelldi.einburgerungstest.questions

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import kotlinx.serialization.json.Json
import me.rafaelldi.einburgerungstest.JsonResourceLoader

internal interface QuestionStoreService {
    fun loadQuestions()
    fun getRandomQuestion(category: QuestionCategory? = null): Question
}

@Service(Service.Level.APP)
internal class QuestionStoreServiceImpl : QuestionStoreService {
    companion object {
        fun getInstance(): QuestionStoreServiceImpl = service()
    }

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private var questionsByCategory: Map<QuestionCategory, List<Question>> = emptyMap()

    override fun loadQuestions() {
        val jsonContent = JsonResourceLoader.loadJson("/data/questions.json") ?: return
        val loadedQuestions = json.decodeFromString<List<QuestionDTO>>(jsonContent)
        questionsByCategory = loadedQuestions.mapIndexed { index, questionDTO ->
            val category = QuestionCategory.entries.first { it.displayName == questionDTO.category }
            Question(index, questionDTO.question, questionDTO.answers, questionDTO.correct, category)
        }.groupBy { it.category }
    }

    override fun getRandomQuestion(category: QuestionCategory?): Question {
        val selectedCategory = category ?: questionsByCategory.keys.random()
        return requireNotNull(questionsByCategory[selectedCategory]).random()
    }
}
