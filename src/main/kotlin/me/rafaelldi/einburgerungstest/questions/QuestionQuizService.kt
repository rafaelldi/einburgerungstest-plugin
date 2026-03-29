package me.rafaelldi.einburgerungstest.questions

import androidx.compose.ui.graphics.ImageBitmap
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

internal interface QuestionQuizService {
    suspend fun loadQuestions()
    fun getQuestionCount(category: QuestionCategory): Int
    fun startQuiz(category: QuestionCategory, randomOrder: Boolean)
    fun hasNext(): Boolean
    fun nextQuestion(): Pair<Question, ImageBitmap?>?
    fun hasPrevious(): Boolean
    fun previousQuestion(): Pair<Question, ImageBitmap?>?
    fun getSavedAnswer(): Int?
    fun saveAnswer(answerIndex: Int)
}

@Service(Service.Level.PROJECT)
internal class QuestionQuizServiceImpl : QuestionQuizService {
    companion object {
        fun getInstance(project: Project): QuestionQuizServiceImpl = project.service()
    }

    private var questionOrder: List<Int> = emptyList()
    private var currentIndex: Int = -1
    private val answerHistory: MutableMap<Int, Int> = mutableMapOf()

    override suspend fun loadQuestions() {
        QuestionStoreServiceImpl.getInstance().loadQuestions()
    }

    override fun getQuestionCount(category: QuestionCategory): Int {
        return QuestionStoreServiceImpl.getInstance().getQuestionCount(category)
    }

    override fun startQuiz(category: QuestionCategory, randomOrder: Boolean) {
        val ids = QuestionStoreServiceImpl.getInstance().getQuestionIds(category)
        questionOrder = if (randomOrder) ids.shuffled() else ids
        currentIndex = -1
        answerHistory.clear()
    }

    override fun hasNext(): Boolean {
        return currentIndex < questionOrder.size - 1
    }

    override fun nextQuestion(): Pair<Question, ImageBitmap?>? {
        if (!hasNext()) return null
        currentIndex++
        val questionId = questionOrder[currentIndex]
        return QuestionStoreServiceImpl.getInstance().getQuestion(questionId)
    }

    override fun hasPrevious(): Boolean {
        return currentIndex > 0
    }

    override fun previousQuestion(): Pair<Question, ImageBitmap?>? {
        if (!hasPrevious()) return null
        currentIndex--
        val questionId = questionOrder[currentIndex]
        return QuestionStoreServiceImpl.getInstance().getQuestion(questionId)
    }

    override fun getSavedAnswer(): Int? {
        return answerHistory[currentIndex]
    }

    override fun saveAnswer(answerIndex: Int) {
        answerHistory[currentIndex] = answerIndex
    }
}
