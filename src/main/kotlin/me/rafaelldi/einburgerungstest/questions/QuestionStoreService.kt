package me.rafaelldi.einburgerungstest.questions

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import me.rafaelldi.einburgerungstest.JsonResourceLoader
import me.rafaelldi.einburgerungstest.persistence.QuestionPersistenceServiceImpl
import java.util.concurrent.ConcurrentHashMap
import org.jetbrains.skia.Image as SkiaImage

internal interface QuestionStoreService {
    suspend fun loadQuestions()
    fun getQuestionCount(category: QuestionCategory): Int
    fun getQuestionIds(category: QuestionCategory): List<Int>
    fun getQuestion(id: Int): Pair<Question, ImageBitmap?>?
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

    private val imageCache = ConcurrentHashMap<String, ImageBitmap>()

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
                val image = questionDTO.img?.let {
                    QuestionImage(
                        resourcePath = "/data/img/${it.url}.png",
                        attributionText = it.text
                    )
                }
                Question(index + 1, questionDTO.question, questionDTO.answers, questionDTO.correct, category, image)
            }
            questionsById = questions.associateBy { it.id }
            generalQuestions = questions.filter { it.category.group == CategoryGroup.NATIONAL }
            questionsByCategory = questions.groupBy { it.category }
        } catch (e: Exception) {
            LOG.warn("Failed to load questions", e)
            throw e
        }
    }

    override fun getQuestionCount(category: QuestionCategory): Int {
        if (category == QuestionCategory.Favorites) {
            return service<QuestionPersistenceServiceImpl>().favorites.size
        }
        return when (category) {
            QuestionCategory.All -> questionsById.size
            QuestionCategory.General -> generalQuestions.size
            else -> questionsByCategory[category]?.size ?: 0
        }
    }

    override fun getQuestionIds(category: QuestionCategory): List<Int> {
        if (category == QuestionCategory.Favorites) {
            return service<QuestionPersistenceServiceImpl>().favorites.toList()
        }
        val questions = when (category) {
            QuestionCategory.All -> questionsById.values
            QuestionCategory.General -> generalQuestions
            else -> questionsByCategory[category] ?: emptyList()
        }
        return questions.map { it.id }
    }

    override fun getQuestion(id: Int): Pair<Question, ImageBitmap?>? {
        val question = questionsById[id] ?: return null
        return withImage(question)
    }

    private fun loadImage(resourcePath: String): ImageBitmap? {
        return imageCache.getOrPut(resourcePath) {
            JsonResourceLoader::class.java.getResourceAsStream(resourcePath)
                ?.readBytes()
                ?.let { SkiaImage.makeFromEncoded(it).toComposeImageBitmap() }
                ?: return null
        }
    }

    private fun withImage(question: Question): Pair<Question, ImageBitmap?> {
        return question to question.image?.let { loadImage(it.resourcePath) }
    }
}
