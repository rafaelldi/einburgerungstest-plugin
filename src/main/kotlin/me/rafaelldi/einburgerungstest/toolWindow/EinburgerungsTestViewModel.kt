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
    val correctAnswerIndex: StateFlow<Int?>
    val canGoPrevious: StateFlow<Boolean>

    fun onLoadQuestions()
    fun onAnswerSelected(index: Int)
    fun onNextQuestion()
    fun onPreviousQuestion()
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

    private val _correctAnswerIndex = MutableStateFlow<Int?>(null)
    override val correctAnswerIndex: StateFlow<Int?> = _correctAnswerIndex.asStateFlow()

    private val _canGoPrevious = MutableStateFlow(false)
    override val canGoPrevious: StateFlow<Boolean> = _canGoPrevious.asStateFlow()

    override fun onLoadQuestions() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            questionService.loadQuestions()
            val firstQuestion = questionService.nextQuestion()
            _currentQuestion.value = firstQuestion
            _correctAnswerIndex.value = questionService.getCorrectAnswer(firstQuestion.id)

            _uiState.value = UiState.QuestionShowing
        }
    }

    override fun onAnswerSelected(index: Int) {
        _selectedAnswerIndex.value = index
        questionService.saveAnswer(index)
    }

    override fun onNextQuestion() {
        val nextQuestion = questionService.nextQuestion()

        _currentQuestion.value = nextQuestion
        _correctAnswerIndex.value = questionService.getCorrectAnswer(nextQuestion.id)
        _canGoPrevious.value = questionService.hasPrevious()
        _selectedAnswerIndex.value = questionService.getSavedAnswer()
    }

    override fun onPreviousQuestion() {
        val previousQuestion = questionService.previousQuestion() ?: return

        _currentQuestion.value = previousQuestion
        _correctAnswerIndex.value = questionService.getCorrectAnswer(previousQuestion.id)
        _canGoPrevious.value = questionService.hasPrevious()
        _selectedAnswerIndex.value = questionService.getSavedAnswer()
    }

    override fun dispose() {
        viewModelScope.cancel()
    }
}