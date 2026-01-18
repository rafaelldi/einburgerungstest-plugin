package me.rafaelldi.einburgerungstest.toolWindow

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.intellij.ui.JBColor
import com.intellij.util.ui.UIUtil
import me.rafaelldi.einburgerungstest.questions.Question
import org.jetbrains.jewel.bridge.toComposeColor
import org.jetbrains.jewel.ui.component.Text

@Composable
internal fun QuestionCard(
    question: Question
) {
    val backgroundColor = UIUtil.getPanelBackground().toComposeColor()
    val borderColor = JBColor.border().toComposeColor()
    val cardShape = RoundedCornerShape(8.dp)

    Row(

    ) {
        Column(
            Modifier
                .background(backgroundColor, cardShape)
                .border(1.dp, borderColor, cardShape)
        ) {
            Text(
                text = question.question,
            )
        }
    }
}