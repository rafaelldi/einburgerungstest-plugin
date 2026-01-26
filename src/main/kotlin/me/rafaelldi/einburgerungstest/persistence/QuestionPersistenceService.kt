package me.rafaelldi.einburgerungstest.persistence

import com.intellij.openapi.components.SerializablePersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.State
import com.intellij.openapi.components.service

@Service
@State(
    name = "me.rafaelldi.einburgerungstest.persistence.QuestionPersistenceService",
    storages = [(Storage("einburgerungstest.xml"))]
)
internal class QuestionPersistenceService :
    SerializablePersistentStateComponent<QuestionPersistenceService.QuestionPersistenceState>(QuestionPersistenceState()) {
    companion object {
        fun getInstance(): QuestionPersistenceService = service()
    }

    var favorites: List<Int>
        get() = state.favorites
        set(value) {
            updateState {
                it.copy(favorites = value)
            }
        }

    internal data class QuestionPersistenceState(
        val favorites: List<Int> = emptyList(),
    )
}