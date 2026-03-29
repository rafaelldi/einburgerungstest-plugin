package me.rafaelldi.einburgerungstest.toolWindow

import androidx.compose.ui.graphics.ImageBitmap
import com.intellij.openapi.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.rafaelldi.einburgerungstest.persistence.QuestionPersistenceService
import me.rafaelldi.einburgerungstest.questions.Question
import me.rafaelldi.einburgerungstest.questions.QuestionCategories
import me.rafaelldi.einburgerungstest.questions.QuestionCategory
import me.rafaelldi.einburgerungstest.questions.QuestionQuizService

internal interface EinburgerungstestViewModel : Disposable {
    val uiState: StateFlow<UiState>
    val questionCategories: StateFlow<QuestionCategories>
    val selectedCategory: StateFlow<QuestionCategory>
    val currentQuestion: StateFlow<Pair<Question, ImageBitmap?>?>
    val selectedAnswerIndex: StateFlow<Int?>
    val canGoNext: StateFlow<Boolean>
    val canGoPrevious: StateFlow<Boolean>
    val randomOrder: StateFlow<Boolean>
    val favorites: StateFlow<List<Int>>
    val correctAnswers: StateFlow<Map<Int, Int>>
    val wrongAnswers: StateFlow<Map<Int, Int>>

    fun onStartQuiz()
    fun onCategoryChanged(category: QuestionCategory)
    fun onRandomOrderChanged(randomOrder: Boolean)
    fun onAnswerSelected(question: Question, selectedAnswer: Int)
    fun onNextQuestion()
    fun onPreviousQuestion()
    fun onResetQuiz()

    fun onToggleFavorite(questionId: Int)
}

internal class EinburgerungstestViewModelImpl(
    private val viewModelScope: CoroutineScope,
    private val quizService: QuestionQuizService,
    private val persistence: QuestionPersistenceService
) : EinburgerungstestViewModel {

    init {
        viewModelScope.launch {
            loadQuestions()
        }
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    override val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _questionCategories = MutableStateFlow(QuestionCategories())
    override val questionCategories: StateFlow<QuestionCategories> = _questionCategories.asStateFlow()

    private val _selectedCategory = MutableStateFlow(persistence.selectedCategory)
    override val selectedCategory: StateFlow<QuestionCategory> = _selectedCategory.asStateFlow()

    private val _randomOrder = MutableStateFlow(persistence.randomOrder)
    override val randomOrder: StateFlow<Boolean> = _randomOrder.asStateFlow()

    private val _currentQuestion = MutableStateFlow<Pair<Question, ImageBitmap?>?>(null)
    override val currentQuestion: StateFlow<Pair<Question, ImageBitmap?>?> = _currentQuestion.asStateFlow()

    private val _selectedAnswerIndex = MutableStateFlow<Int?>(null)
    override val selectedAnswerIndex: StateFlow<Int?> = _selectedAnswerIndex.asStateFlow()

    private val _canGoNext = MutableStateFlow(false)
    override val canGoNext: StateFlow<Boolean> = _canGoNext.asStateFlow()

    private val _canGoPrevious = MutableStateFlow(false)
    override val canGoPrevious: StateFlow<Boolean> = _canGoPrevious.asStateFlow()

    private val _favorites = MutableStateFlow(persistence.favorites)
    override val favorites: StateFlow<List<Int>> = _favorites.asStateFlow()

    private val _correctAnswers = MutableStateFlow(persistence.correctAnswers)
    override val correctAnswers: StateFlow<Map<Int, Int>> = _correctAnswers.asStateFlow()

    private val _wrongAnswers = MutableStateFlow(persistence.wrongAnswers)
    override val wrongAnswers: StateFlow<Map<Int, Int>> = _wrongAnswers.asStateFlow()

    private suspend fun loadQuestions() {
        quizService.loadQuestions()
        updateQuestionCategories()
        _uiState.value = UiState.NotStarted
    }

    private fun updateQuestionCategories() {
        _questionCategories.update {
            QuestionCategories(
                groupCategories = QuestionCategory.groupCategories.map { it to quizService.getQuestionCount(it) },
                nationalCategories = QuestionCategory.nationalCategories.map { it to quizService.getQuestionCount(it) },
                regionalCategories = QuestionCategory.regionalCategories.map { it to quizService.getQuestionCount(it) }
            )
        }
    }

    override fun onStartQuiz() {
        quizService.startQuiz(_selectedCategory.value, _randomOrder.value)

        val firstQuestion = quizService.nextQuestion() ?: return

        _currentQuestion.value = firstQuestion
        _canGoNext.value = quizService.hasNext()

        _uiState.value = UiState.QuestionShowing
    }

    override fun onCategoryChanged(category: QuestionCategory) {
        _selectedCategory.value = category
        persistence.selectedCategory = category
    }

    override fun onRandomOrderChanged(randomOrder: Boolean) {
        _randomOrder.value = randomOrder
        persistence.randomOrder = randomOrder
    }

    override fun onAnswerSelected(question: Question, selectedAnswer: Int) {
        _selectedAnswerIndex.value = selectedAnswer
        quizService.saveAnswer(selectedAnswer)

        if (question.correctAnswer == selectedAnswer) {
            val updated = _correctAnswers.updateAndGet {
                val currentCount = it[question.id] ?: 0
                it + (question.id to currentCount + 1)
            }
            persistence.correctAnswers = updated
        } else {
            val updated = _wrongAnswers.updateAndGet {
                val currentCount = it[question.id] ?: 0
                it + (question.id to currentCount + 1)
            }
            persistence.wrongAnswers = updated
        }
    }

    override fun onNextQuestion() {
        val nextQuestion = quizService.nextQuestion() ?: return

        _currentQuestion.value = nextQuestion
        _selectedAnswerIndex.value = quizService.getSavedAnswer()
        _canGoPrevious.value = quizService.hasPrevious()
        _canGoNext.value = quizService.hasNext()
    }

    override fun onPreviousQuestion() {
        val previousQuestion = quizService.previousQuestion() ?: return

        _currentQuestion.value = previousQuestion
        _selectedAnswerIndex.value = quizService.getSavedAnswer()
        _canGoPrevious.value = quizService.hasPrevious()
        _canGoNext.value = quizService.hasNext()
    }

    override fun onResetQuiz() {
        _uiState.value = UiState.NotStarted
        _currentQuestion.value = null
        _selectedAnswerIndex.value = null
        _canGoPrevious.value = false
        _canGoNext.value = true
    }

    override fun onToggleFavorite(questionId: Int) {
        val list = _favorites.updateAndGet { list ->
            val updatedList = if (questionId in list) {
                list - questionId
            } else {
                list + questionId
            }
            updatedList
        }
        persistence.favorites = list
        updateQuestionCategories()
    }

    override fun dispose() {
        viewModelScope.cancel()
    }
}
