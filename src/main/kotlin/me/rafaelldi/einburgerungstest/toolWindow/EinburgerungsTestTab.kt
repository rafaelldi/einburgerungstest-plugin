package me.rafaelldi.einburgerungstest.toolWindow

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.jewel.ui.component.Text

@Composable
internal fun EinburgerungsTestTab(viewModel: EinburgerungsTestViewModel) {
    LaunchedEffect(Unit) {
        viewModel.onLoadQuestions()
    }

    Column(
        Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Start",
            )
        }
    }
}