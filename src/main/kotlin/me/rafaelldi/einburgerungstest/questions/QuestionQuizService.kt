package me.rafaelldi.einburgerungstest.questions

import androidx.compose.ui.graphics.ImageBitmap
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import me.rafaelldi.einburgerungstest.persistence.QuestionPersistenceServiceImpl

internal interface QuestionQuizService {
    suspend fun loadQuestions()
    fun getQuestionCount(category: QuestionCategory): Int
    fun startQuiz(category: QuestionCategory)
    fun nextQuestion(): Pair<Question, ImageBitmap?>
    fun previousQuestion(): Pair<Question, ImageBitmap?>?
    fun hasPrevious(): Boolean
    fun getSavedAnswer(): Int?
    fun saveAnswer(answerIndex: Int)
}

@Service(Service.Level.PROJECT)
internal class QuestionQuizServiceImpl : QuestionQuizService {
    companion object {
        fun getInstance(project: Project): QuestionQuizServiceImpl = project.service()
    }

    private var currentCategory: QuestionCategory = QuestionCategory.General
    private val questionHistory: MutableList<Pair<Question, ImageBitmap?>> = mutableListOf()
    private var currentIndex: Int = -1
    private val answerHistory: MutableMap<Int, Int> = mutableMapOf()

    override suspend fun loadQuestions() {
        QuestionStoreServiceImpl.getInstance().loadQuestions()
    }

    override fun getQuestionCount(category: QuestionCategory): Int {
        if (category == QuestionCategory.Favorites) {
            return service<QuestionPersistenceServiceImpl>().favorites.size
        }
        return QuestionStoreServiceImpl.getInstance().getQuestionCount(category)
    }

    override fun startQuiz(category: QuestionCategory) {
        currentCategory = category
        questionHistory.clear()
        currentIndex = -1
        answerHistory.clear()
    }

    override fun nextQuestion(): Pair<Question, ImageBitmap?> {
        if (currentIndex < questionHistory.size - 1) {
            currentIndex++
        } else {
            val newQuestion = QuestionStoreServiceImpl.getInstance().getRandomQuestion(currentCategory)
            questionHistory.add(newQuestion)
            currentIndex++
        }
        return questionHistory[currentIndex]
    }

    override fun previousQuestion(): Pair<Question, ImageBitmap?>? {
        if (currentIndex <= 0) return null
        currentIndex--
        return questionHistory[currentIndex]
    }

    override fun hasPrevious(): Boolean {
        return currentIndex > 0
    }

    override fun getSavedAnswer(): Int? {
        return answerHistory[currentIndex]
    }

    override fun saveAnswer(answerIndex: Int) {
        answerHistory[currentIndex] = answerIndex
    }
}
