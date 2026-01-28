package me.rafaelldi.einburgerungstest.questions

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.logger
import kotlinx.serialization.json.Json
import me.rafaelldi.einburgerungstest.JsonResourceLoader
import me.rafaelldi.einburgerungstest.persistence.QuestionPersistenceServiceImpl

internal interface QuestionStoreService {
    fun loadQuestions()
    fun getRandomQuestion(category: QuestionCategory): Question
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
    private var questionsById: Map<Int, Question> = emptyMap()

    override fun loadQuestions() {
        if (questionsByCategory.isNotEmpty()) return

        try {
            val jsonContent = JsonResourceLoader.loadJson("/data/questions.json") ?: return
            val loadedQuestions = json.decodeFromString<List<QuestionDTO>>(jsonContent)
            val questions = loadedQuestions.mapIndexed { index, questionDTO ->
                val category = QuestionCategory.entries.first { it.displayName == questionDTO.category }
                Question(index + 1, questionDTO.question, questionDTO.answers, questionDTO.correct, category)
            }
            questionsByCategory = questions.groupBy { it.category }
            questionsById = questions.associateBy { it.id }
        } catch (e: Exception) {
            LOG.warn("Failed to load questions", e)
            throw e
        }
    }

    override fun getRandomQuestion(category: QuestionCategory): Question {
        if (category == QuestionCategory.Favorites) {
            val favoriteQuestionId = service<QuestionPersistenceServiceImpl>().favorites.randomOrNull()
            return favoriteQuestionId?.let { questionsById[it] } ?: questionsById.values.random()
        }

        val selectedCategory = when (category) {
            QuestionCategory.All -> {
                QuestionCategory.nonGroupCategories.random()
            }

            QuestionCategory.General -> {
                QuestionCategory.nationalCategories.random()
            }

            else -> category
        }
        return requireNotNull(questionsByCategory[selectedCategory]).random()
    }
}
