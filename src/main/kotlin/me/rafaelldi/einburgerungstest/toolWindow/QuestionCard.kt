package me.rafaelldi.einburgerungstest.toolWindow

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.intellij.ui.JBColor
import com.intellij.util.ui.UIUtil
import me.rafaelldi.einburgerungstest.questions.Question
import org.jetbrains.jewel.bridge.toComposeColor
import org.jetbrains.jewel.ui.component.Text

@Composable
internal fun QuestionCard(
    question: Question,
    selectedAnswerIndex: Int?,
    onAnswerSelected: (Int) -> Unit
) {
    val backgroundColor = UIUtil.getPanelBackground().toComposeColor()
    val borderColor = JBColor.border().toComposeColor()
    val cardShape = RoundedCornerShape(8.dp)
    val answerShape = RoundedCornerShape(4.dp)

    val correctColor = Color(0xFF4CAF50)
    val incorrectColor = Color(0xFFF44336)

    Column(
        Modifier
            .fillMaxWidth()
            .background(backgroundColor, cardShape)
            .border(1.dp, borderColor, cardShape)
            .padding(16.dp)
    ) {
        Text(
            text = question.question,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        question.answers.forEachIndexed { index, answer ->
            val isSelected = selectedAnswerIndex == index
            val isCorrect = index == question.correct
            val isAnswered = selectedAnswerIndex != null

            val answerBackgroundColor = when {
                !isAnswered -> backgroundColor
                isSelected && isCorrect -> correctColor.copy(alpha = 0.3f)
                isSelected && !isCorrect -> incorrectColor.copy(alpha = 0.3f)
                isCorrect -> correctColor.copy(alpha = 0.3f)
                else -> backgroundColor
            }

            val answerBorderColor = when {
                !isAnswered -> borderColor
                isSelected && isCorrect -> correctColor
                isSelected && !isCorrect -> incorrectColor
                isCorrect -> correctColor
                else -> borderColor
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .background(answerBackgroundColor, answerShape)
                    .border(1.dp, answerBorderColor, answerShape)
                    .clickable(enabled = !isAnswered) { onAnswerSelected(index) }
                    .padding(12.dp)
            ) {
                Text(text = answer)
            }
        }

        Text(
            text = "Question #${question.id} • ${question.category}",
            fontWeight = FontWeight.Light,
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 16.dp),
        )
    }
}