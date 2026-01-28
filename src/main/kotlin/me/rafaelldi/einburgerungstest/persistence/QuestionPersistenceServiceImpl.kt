package me.rafaelldi.einburgerungstest.persistence

import com.intellij.openapi.components.SerializablePersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import me.rafaelldi.einburgerungstest.questions.QuestionCategory

internal interface QuestionPersistenceService {
    var selectedCategory: QuestionCategory
    var favorites: List<Int>
}

@Service
@State(
    name = "QuestionPersistenceService",
    storages = [(Storage("einburgerungstest.xml"))]
)
internal class QuestionPersistenceServiceImpl :
    SerializablePersistentStateComponent<QuestionPersistenceServiceImpl.QuestionPersistenceState>(
        QuestionPersistenceState()
    ), QuestionPersistenceService {

    override var selectedCategory: QuestionCategory
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
        @JvmField val selectedCategory: QuestionCategory = QuestionCategory.General,
        @JvmField val favorites: List<Int> = emptyList(),
    )
}