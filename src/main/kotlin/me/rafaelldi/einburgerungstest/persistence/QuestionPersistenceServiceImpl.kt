package me.rafaelldi.einburgerungstest.persistence

import com.intellij.openapi.components.SerializablePersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.State

internal interface QuestionPersistenceService {
    var selectedCategory: String?
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

    override var selectedCategory: String?
        get() = state.selectedCategory
        set(value) {
            updateState {
                it.copy(selectedCategory = value)
            }
        }

    override var favorites: List<Int>
        get() = state.favorites
        set(value) {
            updateState {
                it.copy(favorites = value)
            }
        }

    internal data class QuestionPersistenceState(
        @JvmField val selectedCategory: String? = null,
        @JvmField val favorites: List<Int> = emptyList(),
    )
}