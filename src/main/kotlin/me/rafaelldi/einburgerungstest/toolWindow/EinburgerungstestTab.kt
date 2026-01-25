package me.rafaelldi.einburgerungstest.toolWindow

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.rafaelldi.einburgerungstest.MyBundle
import me.rafaelldi.einburgerungstest.questions.QuestionCategory
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.ui.component.*

@Composable
internal fun EinburgerungstestTab(viewModel: EinburgerungstestViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val currentQuestion by viewModel.currentQuestion.collectAsState()
    val selectedAnswerIndex by viewModel.selectedAnswerIndex.collectAsState()
    val canGoPrevious by viewModel.canGoPrevious.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    when (uiState) {
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

        UiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(MyBundle.message("einburgerungstest.tab.loading"))
            }
        }

        UiState.QuestionShowing -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                currentQuestion?.let { question ->
                    QuestionCard(
                        question = question,
                        selectedAnswerIndex = selectedAnswerIndex,
                        correctAnswerIndex = question.correctAnswer,
                        onAnswerSelected = { viewModel.onAnswerSelected(it) }
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
            selectableItem(
                selected = selectedCategory == QuestionCategory.All,
                onClick = {
                    onCategoryChanged(QuestionCategory.All)
                }
            ) {
                Text(QuestionCategory.All.displayName)
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
