package me.rafaelldi.einburgerungstest.questions

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.components.service
import kotlinx.serialization.json.Json
import me.rafaelldi.einburgerungstest.JsonResourceLoader

internal interface QuestionService {
    fun loadQuestions()
    fun nextQuestion(): Question
}

@Service(Service.Level.PROJECT)
internal class QuestionServiceImpl(private val project: Project): QuestionService {
    companion object {
        fun getInstance(project: Project): QuestionServiceImpl = project.service()
    }

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private var allQuestions: List<Question> = emptyList()

    override fun loadQuestions() {
        val jsonContent = JsonResourceLoader.loadJson("/data/questions.json") ?: return
        val loadedQuestions = json.decodeFromString<List<QuestionDTO>>(jsonContent)
        allQuestions = loadedQuestions.mapIndexed { index, questionDTO ->
            Question(index, questionDTO.question, questionDTO.answers, questionDTO.correct, questionDTO.category)
        }
    }

    override fun nextQuestion(): Question {
        return allQuestions.random()
    }
}