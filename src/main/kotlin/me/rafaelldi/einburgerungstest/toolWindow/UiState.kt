package me.rafaelldi.einburgerungstest.toolWindow

internal sealed interface UiState {
    data object NotStarted : UiState
    data object Loading : UiState
    data object QuestionShowing : UiState
}
