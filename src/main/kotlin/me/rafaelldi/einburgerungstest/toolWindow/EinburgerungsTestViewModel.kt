package me.rafaelldi.einburgerungstest.toolWindow

import com.intellij.openapi.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.rafaelldi.einburgerungstest.questions.Question
import me.rafaelldi.einburgerungstest.questions.QuestionService

internal interface EinburgerungsTestViewModel : Disposable {
    val uiState: StateFlow<UiState>
    val currentQuestion: StateFlow<Question?>
    val selectedAnswerIndex: StateFlow<Int?>
    val isAnswered: StateFlow<Boolean>

    fun onLoadQuestions()
    fun onAnswerSelected(index: Int)
    fun onNextQuestion()
}

internal class EinburgerungsTestViewModelImpl(
    private val viewModelScope: CoroutineScope,
    private val questionService: QuestionService
) : EinburgerungsTestViewModel {

    private val _uiState = MutableStateFlow<UiState>(UiState.NotStarted)
    override val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _currentQuestion = MutableStateFlow<Question?>(null)
    override val currentQuestion: StateFlow<Question?> = _currentQuestion.asStateFlow()

    private val _selectedAnswerIndex = MutableStateFlow<Int?>(null)
    override val selectedAnswerIndex: StateFlow<Int?> = _selectedAnswerIndex.asStateFlow()

    private val _isAnswered = MutableStateFlow(false)
    override val isAnswered: StateFlow<Boolean> = _isAnswered.asStateFlow()

    override fun onLoadQuestions() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            questionService.loadQuestions()

            _currentQuestion.value = questionService.nextQuestion()
            _uiState.value = UiState.QuestionShowing
        }
    }

    override fun onAnswerSelected(index: Int) {
        if (_isAnswered.value) return
        _selectedAnswerIndex.value = index
        _isAnswered.value = true
    }

    override fun onNextQuestion() {
        _selectedAnswerIndex.value = null
        _isAnswered.value = false
        _currentQuestion.value = questionService.nextQuestion()
    }

    override fun dispose() {
        viewModelScope.cancel()
    }
}