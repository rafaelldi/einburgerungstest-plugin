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
import me.rafaelldi.einburgerungstest.questions.QuestionCategory
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.ui.component.*

@Composable
internal fun EinburgerungstestTab(viewModel: EinburgerungstestViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val currentQuestionPair by viewModel.currentQuestion.collectAsState()
    val selectedAnswerIndex by viewModel.selectedAnswerIndex.collectAsState()
    val canGoPrevious by viewModel.canGoPrevious.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val favorites by viewModel.favorites.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadQuestions()
    }

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
                        onCategoryChanged = { viewModel.onCategoryChanged(it) }
                    )
                    DefaultButton(onClick = { viewModel.onStartQuiz() }) {
                        Text(MyBundle.message("einburgerungstest.tab.start.button"))
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
                            when (event.key) {
                                Key.One -> if (selectedAnswerIndex == null) { viewModel.onAnswerSelected(0); true } else false
                                Key.Two -> if (selectedAnswerIndex == null) { viewModel.onAnswerSelected(1); true } else false
                                Key.Three -> if (selectedAnswerIndex == null) { viewModel.onAnswerSelected(2); true } else false
                                Key.Four -> if (selectedAnswerIndex == null) { viewModel.onAnswerSelected(3); true } else false
                                Key.DirectionRight -> { viewModel.onNextQuestion(); true }
                                Key.Enter -> { viewModel.onNextQuestion(); true }
                                Key.Spacebar -> { viewModel.onNextQuestion(); true }
                                Key.DirectionLeft -> if (canGoPrevious) { viewModel.onPreviousQuestion(); true } else false
                                Key.Backspace -> if (canGoPrevious) { viewModel.onPreviousQuestion(); true } else false
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
                        favorites = favorites,
                        onAnswerSelected = { viewModel.onAnswerSelected(it) },
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
                            onClick = { viewModel.onNextQuestion() }
                        ) {
                            Text(MyBundle.message("einburgerungstest.tab.next.button"))
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
    onCategoryChanged: (QuestionCategory) -> Unit
) {
    Dropdown(
        modifier = modifier,
        menuContent = {
            QuestionCategory.groupCategories.forEach { category ->
                selectableItem(
                    selected = selectedCategory == category,
                    onClick = {
                        onCategoryChanged(category)
                    }
                ) {
                    Text(category.displayName)
                }
            }

            separator()

            QuestionCategory.nationalCategories.forEach { category ->
                selectableItem(
                    selected = selectedCategory == category,
                    onClick = {
                        onCategoryChanged(category)
                    }
                ) {
                    Text(category.displayName)
                }
            }

            separator()

            QuestionCategory.regionalCategories.forEach { category ->
                selectableItem(
                    selected = selectedCategory == category,
                    onClick = {
                        onCategoryChanged(category)
                    }
                ) {
                    Text(category.displayName)
                }
            }
        }
    ) {
        Text(selectedCategory.displayName)
    }
}
