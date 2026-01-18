package me.rafaelldi.einburgerungstest.toolWindow

import com.intellij.openapi.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import me.rafaelldi.einburgerungstest.questions.QuestionService

internal interface EinburgerungsTestViewModel : Disposable {
    fun onLoadQuestions()
}

internal class EinburgerungsTestViewModelImpl(
    private val viewModelScope: CoroutineScope,
    private val questionService: QuestionService
) : EinburgerungsTestViewModel {

    private var currentLoadingQuestionJob: Job? = null

    override fun onLoadQuestions() {
        currentLoadingQuestionJob?.cancel()

        currentLoadingQuestionJob = viewModelScope.launch {
            questionService.loadQuestions()
        }
    }

    override fun dispose() {
        viewModelScope.cancel()
    }
}