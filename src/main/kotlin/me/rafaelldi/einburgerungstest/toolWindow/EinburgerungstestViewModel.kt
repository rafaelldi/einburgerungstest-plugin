package me.rafaelldi.einburgerungstest.toolWindow

import com.intellij.openapi.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.rafaelldi.einburgerungstest.persistence.QuestionPersistenceService
import me.rafaelldi.einburgerungstest.questions.Question
import me.rafaelldi.einburgerungstest.questions.QuestionCategory
import me.rafaelldi.einburgerungstest.questions.QuestionQuizService

internal interface EinburgerungstestViewModel : Disposable {
    val uiState: StateFlow<UiState>
    val selectedCategory: StateFlow<QuestionCategory>
    val currentQuestion: StateFlow<Question?>
    val selectedAnswerIndex: StateFlow<Int?>
    val canGoPrevious: StateFlow<Boolean>
    val favorites: StateFlow<List<Int>>

    fun onStartQuiz()
    fun onCategoryChanged(category: QuestionCategory)
    fun onAnswerSelected(answerIndex: Int)
    fun onNextQuestion()
    fun onPreviousQuestion()
    fun onResetQuiz()

    fun onToggleFavorite(questionId: Int)
}

internal class EinburgerungstestViewModelImpl(
    private val viewModelScope: CoroutineScope,
    private val questionQuizService: QuestionQuizService,
    private val questionPersistenceService: QuestionPersistenceService
) : EinburgerungstestViewModel {

    private val _uiState = MutableStateFlow<UiState>(UiState.NotStarted)
    override val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _selectedCategory = MutableStateFlow(QuestionCategory.General)
    override val selectedCategory: StateFlow<QuestionCategory> = _selectedCategory.asStateFlow()

    private val _currentQuestion = MutableStateFlow<Question?>(null)
    override val currentQuestion: StateFlow<Question?> = _currentQuestion.asStateFlow()

    private val _selectedAnswerIndex = MutableStateFlow<Int?>(null)
    override val selectedAnswerIndex: StateFlow<Int?> = _selectedAnswerIndex.asStateFlow()

    private val _canGoPrevious = MutableStateFlow(false)
    override val canGoPrevious: StateFlow<Boolean> = _canGoPrevious.asStateFlow()

    private val _favorites = MutableStateFlow(questionPersistenceService.favorites)
    override val favorites: StateFlow<List<Int>> = _favorites.asStateFlow()

    override fun onStartQuiz() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            questionQuizService.startQuiz(_selectedCategory.value)
            val firstQuestion = questionQuizService.nextQuestion()
            _currentQuestion.value = firstQuestion

            _uiState.value = UiState.QuestionShowing
        }
    }

    override fun onCategoryChanged(category: QuestionCategory) {
        _selectedCategory.value = category
    }

    override fun onAnswerSelected(answerIndex: Int) {
        _selectedAnswerIndex.update {
            questionQuizService.saveAnswer(answerIndex)
            answerIndex
        }
    }

    override fun onNextQuestion() {
        val nextQuestion = questionQuizService.nextQuestion()

        _currentQuestion.value = nextQuestion
        _selectedAnswerIndex.value = questionQuizService.getSavedAnswer()
        _canGoPrevious.value = questionQuizService.hasPrevious()
    }

    override fun onPreviousQuestion() {
        val previousQuestion = questionQuizService.previousQuestion() ?: return

        _currentQuestion.value = previousQuestion
        _selectedAnswerIndex.value = questionQuizService.getSavedAnswer()
        _canGoPrevious.value = questionQuizService.hasPrevious()
    }

    override fun onResetQuiz() {
        _uiState.value = UiState.NotStarted
        _currentQuestion.value = null
        _selectedAnswerIndex.value = null
        _canGoPrevious.value = false
    }

    override fun onToggleFavorite(questionId: Int) {
        _favorites.update { list ->
            val updatedList =  if (questionId in list) {
                list - questionId
            } else {
                list + questionId
            }
            questionPersistenceService.favorites = updatedList
            updatedList
        }
    }

    override fun dispose() {
        viewModelScope.cancel()
    }
}
