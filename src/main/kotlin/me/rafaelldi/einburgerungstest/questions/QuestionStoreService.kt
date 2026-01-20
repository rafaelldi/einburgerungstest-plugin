package me.rafaelldi.einburgerungstest.questions

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.logger
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
        private val LOG = logger<QuestionStoreServiceImpl>()
    }

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private var questionsByCategory: Map<QuestionCategory, List<Question>> = emptyMap()

    override fun loadQuestions() {
        if (questionsByCategory.isNotEmpty()) return

        try {
            val jsonContent = JsonResourceLoader.loadJson("/data/questions.json") ?: return
            val loadedQuestions = json.decodeFromString<List<QuestionDTO>>(jsonContent)
            questionsByCategory = loadedQuestions.mapIndexed { index, questionDTO ->
                val category = QuestionCategory.entries.first { it.displayName == questionDTO.category }
                Question(index, questionDTO.question, questionDTO.answers, questionDTO.correct, category)
            }.groupBy { it.category }
        } catch (e: Exception) {
            LOG.warn("Failed to load questions", e)
            throw e
        }
    }

    override fun getRandomQuestion(category: QuestionCategory?): Question {
        val selectedCategory = category ?: questionsByCategory.keys.random()
        return requireNotNull(questionsByCategory[selectedCategory]).random()
    }
}
