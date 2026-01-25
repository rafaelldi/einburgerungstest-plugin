package me.rafaelldi.einburgerungstest.questions

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

internal interface QuestionService {
    fun startQuiz(category: QuestionCategory)
    fun nextQuestion(): Question
    fun previousQuestion(): Question?
    fun hasPrevious(): Boolean
    fun getSavedAnswer(): Int?
    fun saveAnswer(answerIndex: Int)
}

@Service(Service.Level.PROJECT)
internal class QuestionServiceImpl : QuestionService {
    companion object {
        fun getInstance(project: Project): QuestionServiceImpl = project.service()
    }

    private var currentCategory: QuestionCategory = QuestionCategory.All
    private val questionHistory: MutableList<Question> = mutableListOf()
    private var currentIndex: Int = -1
    private val answerHistory: MutableMap<Int, Int> = mutableMapOf()

    override fun startQuiz(category: QuestionCategory) {
        currentCategory = category
        questionHistory.clear()
        currentIndex = -1
        answerHistory.clear()
        QuestionStoreServiceImpl.getInstance().loadQuestions()
    }

    override fun nextQuestion(): Question {
        if (currentIndex < questionHistory.size - 1) {
            currentIndex++
        } else {
            val newQuestion = QuestionStoreServiceImpl.getInstance().getRandomQuestion(currentCategory)
            questionHistory.add(newQuestion)
            currentIndex++
        }
        return questionHistory[currentIndex]
    }

    override fun previousQuestion(): Question? {
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
