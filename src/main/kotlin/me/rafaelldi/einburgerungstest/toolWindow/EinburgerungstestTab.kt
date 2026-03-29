package me.rafaelldi.einburgerungstest.toolWindow

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import me.rafaelldi.einburgerungstest.MyBundle
import me.rafaelldi.einburgerungstest.questions.QuestionCategories
import me.rafaelldi.einburgerungstest.questions.QuestionCategory
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.ui.component.*

@Composable
internal fun EinburgerungstestTab(viewModel: EinburgerungstestViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val questionCategories by viewModel.questionCategories.collectAsState()
    val currentQuestionPair by viewModel.currentQuestion.collectAsState()
    val selectedAnswerIndex by viewModel.selectedAnswerIndex.collectAsState()
    val canGoNext by viewModel.canGoNext.collectAsState()
    val canGoPrevious by viewModel.canGoPrevious.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val randomOrder by viewModel.randomOrder.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val currentQuestionIndex by viewModel.currentQuestionIndex.collectAsState()
    val totalQuestionCount by viewModel.totalQuestionCount.collectAsState()
    val correctAnswers by viewModel.correctAnswers.collectAsState()
    val wrongAnswers by viewModel.wrongAnswers.collectAsState()

    when (uiState) {
        UiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(MyBundle.message("einburgerungstest.tab.loading"))
            }
        }

        UiState.NotStarted -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CategoryDropdown(
                        modifier = Modifier.width(250.dp),
                        selectedCategory = selectedCategory,
                        questionCategories = questionCategories,
                        onCategoryChanged = { viewModel.onCategoryChanged(it) }
                    )
                    DefaultButton(onClick = { viewModel.onStartQuiz() }) {
                        Text(MyBundle.message("einburgerungstest.tab.start.button"))
                    }
                    CheckboxRow(
                        checked = randomOrder,
                        onCheckedChange = { viewModel.onRandomOrderChanged(it) }
                    ) {
                        Text(MyBundle.message("einburgerungstest.tab.randomOrder.checkbox"))
                    }
                }
            }
        }

        UiState.QuestionShowing -> {
            val focusRequester = FocusRequester()
            LaunchedEffect(currentQuestionPair) {
                focusRequester.requestFocus()
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .onKeyEvent { event ->
                        if (event.type == KeyEventType.KeyDown) {
                            val currentQuestion = currentQuestionPair?.first
                            when (event.key) {
                                Key.One -> if (selectedAnswerIndex == null && currentQuestion != null) {
                                    viewModel.onAnswerSelected(currentQuestion, 0); true
                                } else false

                                Key.Two -> if (selectedAnswerIndex == null && currentQuestion != null) {
                                    viewModel.onAnswerSelected(currentQuestion, 1); true
                                } else false

                                Key.Three -> if (selectedAnswerIndex == null && currentQuestion != null) {
                                    viewModel.onAnswerSelected(currentQuestion, 2); true
                                } else false

                                Key.Four -> if (selectedAnswerIndex == null && currentQuestion != null) {
                                    viewModel.onAnswerSelected(currentQuestion, 3); true
                                } else false

                                Key.DirectionRight -> if (canGoNext) {
                                    viewModel.onNextQuestion(); true
                                } else false

                                Key.Enter -> if (canGoNext) {
                                    viewModel.onNextQuestion(); true
                                } else false

                                Key.Spacebar -> if (canGoNext) {
                                    viewModel.onNextQuestion(); true
                                } else false

                                Key.DirectionLeft -> if (canGoPrevious) {
                                    viewModel.onPreviousQuestion(); true
                                } else false

                                Key.Backspace -> if (canGoPrevious) {
                                    viewModel.onPreviousQuestion(); true
                                } else false

                                else -> false
                            }
                        } else {
                            false
                        }
                    }
                    .focusRequester(focusRequester)
                    .focusable(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                currentQuestionPair?.let { (question, imageBitmap) ->
                    QuestionCard(
                        question = question,
                        imageBitmap = imageBitmap,
                        selectedAnswerIndex = selectedAnswerIndex,
                        correctAnswerIndex = question.correctAnswer,
                        correctAnswerCount = correctAnswers[question.id] ?: 0,
                        wrongAnswerCount = wrongAnswers[question.id] ?: 0,
                        isFavorite = question.id in favorites,
                        onAnswerSelected = { viewModel.onAnswerSelected(question, it) },
                        onFavoriteToggled = { viewModel.onToggleFavorite(it) }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.onPreviousQuestion() },
                            enabled = canGoPrevious
                        ) {
                            Text(MyBundle.message("einburgerungstest.tab.previous.button"))
                        }
                        OutlinedButton(onClick = { viewModel.onResetQuiz() }) {
                            Text(MyBundle.message("einburgerungstest.tab.startOver.button"))
                        }
                        DefaultButton(
                            onClick = { viewModel.onNextQuestion() },
                            enabled = canGoNext
                        ) {
                            Text(MyBundle.message("einburgerungstest.tab.next.button"))
                        }
                    }

                    if (totalQuestionCount > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            HorizontalProgressBar(
                                progress = (currentQuestionIndex + 1).toFloat() / totalQuestionCount.toFloat(),
                                modifier = Modifier.weight(1f)
                            )
                            Text("${currentQuestionIndex + 1}/$totalQuestionCount")
                        }
                    }
                }
            }
        }
    }
}

@Suppress("UnstableApiUsage")
@OptIn(ExperimentalJewelApi::class)
@Composable
private fun CategoryDropdown(
    modifier: Modifier = Modifier,
    selectedCategory: QuestionCategory,
    questionCategories: QuestionCategories,
    onCategoryChanged: (QuestionCategory) -> Unit
) {
    Dropdown(
        modifier = modifier,
        menuContent = {
            questionCategories.groupCategories.forEach { (category, count) ->
                selectableItem(
                    selected = selectedCategory == category,
                    onClick = {
                        onCategoryChanged(category)
                    }
                ) {
                    Text("${category.displayName} ($count)")
                }
            }

            separator()

            questionCategories.nationalCategories.forEach { (category, count) ->
                selectableItem(
                    selected = selectedCategory == category,
                    onClick = {
                        onCategoryChanged(category)
                    }
                ) {
                    Text("${category.displayName} ($count)")
                }
            }

            separator()

            questionCategories.regionalCategories.forEach { (category, count) ->
                selectableItem(
                    selected = selectedCategory == category,
                    onClick = {
                        onCategoryChanged(category)
                    }
                ) {
                    Text("${category.displayName} ($count)")
                }
            }
        }
    ) {
        Text(selectedCategory.displayName)
    }
}
