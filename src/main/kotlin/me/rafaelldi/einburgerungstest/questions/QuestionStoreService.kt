package me.rafaelldi.einburgerungstest.questions

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import me.rafaelldi.einburgerungstest.JsonResourceLoader
import me.rafaelldi.einburgerungstest.persistence.QuestionPersistenceServiceImpl

internal interface QuestionStoreService {
    suspend fun loadQuestions()
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

    private var questionsById: Map<Int, Question> = emptyMap()
    private var generalQuestions: List<Question> = emptyList()
    private var questionsByCategory: Map<QuestionCategory, List<Question>> = emptyMap()

    override suspend fun loadQuestions() {
        if (questionsByCategory.isNotEmpty()) return

        try {
            val jsonContent = withContext(Dispatchers.IO) {
                JsonResourceLoader.loadJson("/data/questions.json")
            } ?: return
            val loadedQuestions = json.decodeFromString<List<QuestionDTO>>(jsonContent)
            val questions = loadedQuestions.mapIndexed { index, questionDTO ->
                val category = QuestionCategory.entries.first { it.displayName == questionDTO.category }
                Question(index + 1, questionDTO.question, questionDTO.answers, questionDTO.correct, category)
            }
            questionsById = questions.associateBy { it.id }
            generalQuestions = questions.filter { it.category.group == CategoryGroup.NATIONAL }
            questionsByCategory = questions.groupBy { it.category }
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

        if (category == QuestionCategory.All) {
            return questionsById.values.random()
        }

        if (category == QuestionCategory.General) {
            return generalQuestions.random()
        }

        return requireNotNull(questionsByCategory[category]).random()
    }
}
