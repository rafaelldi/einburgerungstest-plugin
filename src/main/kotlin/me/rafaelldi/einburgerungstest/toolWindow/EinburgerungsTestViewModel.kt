package me.rafaelldi.einburgerungstest.toolWindow

import com.intellij.openapi.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

internal interface EinburgerungsTestViewModel : Disposable {
}

internal class EinburgerungsTestViewModelImpl(
    private val viewModelScope: CoroutineScope,
) : EinburgerungsTestViewModel {
    override fun dispose() {
        viewModelScope.cancel()
    }
}