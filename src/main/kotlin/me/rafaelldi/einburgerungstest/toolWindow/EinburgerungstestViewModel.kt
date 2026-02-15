package me.rafaelldi.einburgerungstest.toolWindow

import androidx.compose.ui.graphics.ImageBitmap
import com.intellij.openapi.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
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
    val canGoPrevious: StateFlow<Boolean>
    val favorites: StateFlow<List<Int>>

    suspend fun loadQuestions()
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
    private val quizService: QuestionQuizService,
    private val persistence: QuestionPersistenceService
) : EinburgerungstestViewModel {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    override val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _questionCategories = MutableStateFlow(QuestionCategories())
    override val questionCategories: StateFlow<QuestionCategories> = _questionCategories.asStateFlow()

    private val _selectedCategory = MutableStateFlow(persistence.selectedCategory)
    override val selectedCategory: StateFlow<QuestionCategory> = _selectedCategory.asStateFlow()

    private val _currentQuestion = MutableStateFlow<Pair<Question, ImageBitmap?>?>(null)
    override val currentQuestion: StateFlow<Pair<Question, ImageBitmap?>?> = _currentQuestion.asStateFlow()

    private val _selectedAnswerIndex = MutableStateFlow<Int?>(null)
    override val selectedAnswerIndex: StateFlow<Int?> = _selectedAnswerIndex.asStateFlow()

    private val _canGoPrevious = MutableStateFlow(false)
    override val canGoPrevious: StateFlow<Boolean> = _canGoPrevious.asStateFlow()

    private val _favorites = MutableStateFlow(persistence.favorites)
    override val favorites: StateFlow<List<Int>> = _favorites.asStateFlow()

    override suspend fun loadQuestions() {
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
        quizService.startQuiz(_selectedCategory.value)
        val firstQuestion = quizService.nextQuestion()
        _currentQuestion.value = firstQuestion

        _uiState.value = UiState.QuestionShowing
    }

    override fun onCategoryChanged(category: QuestionCategory) {
        _selectedCategory.value = category
        persistence.selectedCategory = category
    }

    override fun onAnswerSelected(answerIndex: Int) {
        _selectedAnswerIndex.value = answerIndex
        quizService.saveAnswer(answerIndex)
    }

    override fun onNextQuestion() {
        val nextQuestion = quizService.nextQuestion()

        _currentQuestion.value = nextQuestion
        _selectedAnswerIndex.value = quizService.getSavedAnswer()
        _canGoPrevious.value = quizService.hasPrevious()
    }

    override fun onPreviousQuestion() {
        val previousQuestion = quizService.previousQuestion() ?: return

        _currentQuestion.value = previousQuestion
        _selectedAnswerIndex.value = quizService.getSavedAnswer()
        _canGoPrevious.value = quizService.hasPrevious()
    }

    override fun onResetQuiz() {
        _uiState.value = UiState.NotStarted
        _currentQuestion.value = null
        _selectedAnswerIndex.value = null
        _canGoPrevious.value = false
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
