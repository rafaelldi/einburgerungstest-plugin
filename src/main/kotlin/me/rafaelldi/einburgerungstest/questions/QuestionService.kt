package me.rafaelldi.einburgerungstest.questions

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.components.service
import kotlinx.serialization.json.Json
import me.rafaelldi.einburgerungstest.JsonResourceLoader

internal interface QuestionService {
    fun loadQuestions()
    fun getRandomQuestion(): Question
}

@Service(Service.Level.PROJECT)
internal class QuestionServiceImpl(private val project: Project): QuestionService {
    companion object {
        fun getInstance(project: Project): QuestionServiceImpl = project.service()
    }

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private var questionList: List<Question> = emptyList()

    override fun loadQuestions() {
        val jsonContent = JsonResourceLoader.loadJson("/data/questions.json") ?: return
        val questions = json.decodeFromString<List<Question>>(jsonContent)
        questionList = questions
    }

    override fun getRandomQuestion(): Question {
        return questionList.random()
    }
}