package me.rafaelldi.einburgerungstest.toolWindow

import com.intellij.openapi.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.rafaelldi.einburgerungstest.questions.Question
import me.rafaelldi.einburgerungstest.questions.QuestionCategory
import me.rafaelldi.einburgerungstest.questions.QuestionService

internal interface EinburgerungstestViewModel : Disposable {
    val uiState: StateFlow<UiState>
    val currentQuestion: StateFlow<Question?>
    val selectedAnswerIndex: StateFlow<Int?>
    val canGoPrevious: StateFlow<Boolean>
    val selectedCategory: StateFlow<QuestionCategory?>

    fun onLoadQuestions()
    fun onCategoryChanged(category: QuestionCategory?)
    fun onAnswerSelected(index: Int)
    fun onNextQuestion()
    fun onPreviousQuestion()
}

internal class EinburgerungstestViewModelImpl(
    private val viewModelScope: CoroutineScope,
    private val questionService: QuestionService
) : EinburgerungstestViewModel {

    private val _uiState = MutableStateFlow<UiState>(UiState.NotStarted)
    override val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _currentQuestion = MutableStateFlow<Question?>(null)
    override val currentQuestion: StateFlow<Question?> = _currentQuestion.asStateFlow()

    private val _selectedAnswerIndex = MutableStateFlow<Int?>(null)
    override val selectedAnswerIndex: StateFlow<Int?> = _selectedAnswerIndex.asStateFlow()

    private val _canGoPrevious = MutableStateFlow(false)
    override val canGoPrevious: StateFlow<Boolean> = _canGoPrevious.asStateFlow()

    private val _selectedCategory = MutableStateFlow<QuestionCategory?>(null)
    override val selectedCategory: StateFlow<QuestionCategory?> = _selectedCategory.asStateFlow()

    override fun onLoadQuestions() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            questionService.loadQuestions()
            questionService.startQuiz(_selectedCategory.value)
            val firstQuestion = questionService.nextQuestion()
            _currentQuestion.value = firstQuestion

            _uiState.value = UiState.QuestionShowing
        }
    }

    override fun onCategoryChanged(category: QuestionCategory?) {
        _selectedCategory.value = category
    }

    override fun onAnswerSelected(index: Int) {
        _selectedAnswerIndex.value = index
        questionService.saveAnswer(index)
    }

    override fun onNextQuestion() {
        val nextQuestion = questionService.nextQuestion()

        _currentQuestion.value = nextQuestion
        _canGoPrevious.value = questionService.hasPrevious()
        _selectedAnswerIndex.value = questionService.getSavedAnswer()
    }

    override fun onPreviousQuestion() {
        val previousQuestion = questionService.previousQuestion() ?: return

        _currentQuestion.value = previousQuestion
        _canGoPrevious.value = questionService.hasPrevious()
        _selectedAnswerIndex.value = questionService.getSavedAnswer()
    }

    override fun dispose() {
        viewModelScope.cancel()
    }
}
