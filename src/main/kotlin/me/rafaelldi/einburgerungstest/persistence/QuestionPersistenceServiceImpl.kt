package me.rafaelldi.einburgerungstest.persistence

import com.intellij.openapi.components.SerializablePersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.State
import com.intellij.openapi.components.service

internal interface QuestionPersistenceService {
    var favorites: List<Int>
}

@Service
@State(
    name = "me.rafaelldi.einburgerungstest.persistence.QuestionPersistenceService",
    storages = [(Storage("einburgerungstest.xml"))]
)
internal class QuestionPersistenceServiceImpl :
    SerializablePersistentStateComponent<QuestionPersistenceServiceImpl.QuestionPersistenceState>(
        QuestionPersistenceState()
    ), QuestionPersistenceService {
    companion object {
        fun getInstance(): QuestionPersistenceServiceImpl = service()
    }

    override var favorites: List<Int>
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