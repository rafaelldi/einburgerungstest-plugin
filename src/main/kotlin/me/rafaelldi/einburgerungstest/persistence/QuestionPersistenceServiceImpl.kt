package me.rafaelldi.einburgerungstest.persistence

import com.intellij.openapi.components.SerializablePersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.annotations.Property
import me.rafaelldi.einburgerungstest.questions.QuestionCategory

internal interface QuestionPersistenceService {
    var selectedCategory: QuestionCategory
    var favorites: List<Int>
    var correctAnswers: Map<Int, Int>
    var wrongAnswers: Map<Int, Int>
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

    override var correctAnswers: Map<Int, Int>
        get() = state.correctAnswers
        set(value) {
            updateState {
                it.copy(correctAnswers = value)
            }
        }

    override var wrongAnswers: Map<Int, Int>
        get() = state.wrongAnswers
        set(value) {
            updateState {
                it.copy(wrongAnswers = value)
            }
        }

    internal data class QuestionPersistenceState(
        @JvmField @Property val selectedCategory: QuestionCategory = QuestionCategory.General,
        @JvmField val favorites: List<Int> = emptyList(),
        @JvmField val correctAnswers: Map<Int, Int> = emptyMap(),
        @JvmField val wrongAnswers: Map<Int, Int> = emptyMap(),
    )
}