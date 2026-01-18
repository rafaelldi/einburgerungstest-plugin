package me.rafaelldi.einburgerungstest.toolWindow

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Text

@Composable
internal fun EinburgerungsTestTab(viewModel: EinburgerungsTestViewModel) {
    val currentQuestion by viewModel.currentQuestion.collectAsState()
    val selectedAnswerIndex by viewModel.selectedAnswerIndex.collectAsState()
    val isAnswered by viewModel.isAnswered.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onLoadQuestions()
    }

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
                onAnswerSelected = { viewModel.onAnswerSelected(it) }
            )

            if (isAnswered) {
                DefaultButton(
                    onClick = { viewModel.onNextQuestion() },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Next")
                }
            }
        }
    }
}