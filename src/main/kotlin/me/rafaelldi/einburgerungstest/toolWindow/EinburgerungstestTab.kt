package me.rafaelldi.einburgerungstest.toolWindow

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.rafaelldi.einburgerungstest.MyBundle
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.OutlinedButton
import org.jetbrains.jewel.ui.component.Text

@Composable
internal fun EinburgerungstestTab(viewModel: EinburgerungstestViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val currentQuestion by viewModel.currentQuestion.collectAsState()
    val selectedAnswerIndex by viewModel.selectedAnswerIndex.collectAsState()
    val correctAnswerIndex by viewModel.correctAnswerIndex.collectAsState()
    val canGoPrevious by viewModel.canGoPrevious.collectAsState()

    when (uiState) {
        UiState.NotStarted -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                DefaultButton(onClick = { viewModel.onLoadQuestions() }) {
                    Text(MyBundle.message("einburgerungstest.tab.start.button"))
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
                        correctAnswerIndex = correctAnswerIndex,
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