package me.rafaelldi.einburgerungstest.questions

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import kotlinx.serialization.json.Json
import me.rafaelldi.einburgerungstest.JsonResourceLoader

internal interface QuestionStoreService {
    fun loadQuestions()
    fun getRandomQuestion(): Question
}

@Service(Service.Level.APP)
internal class QuestionStoreServiceImpl : QuestionStoreService {
    companion object {
        fun getInstance(): QuestionStoreServiceImpl = service()
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

    override fun getRandomQuestion(): Question {
        return allQuestions.random()
    }
}
