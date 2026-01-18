package me.rafaelldi.einburgerungstest

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.components.service
import com.intellij.platform.util.coroutines.childScope
import kotlinx.coroutines.CoroutineScope

@Service(Service.Level.PROJECT)
internal class EinburgerungstestService(private val scope: CoroutineScope) {
    companion object {
        fun getInstance(project: Project): EinburgerungstestService = project.service()
    }

    @Suppress("UnstableApiUsage")
    fun createScope(name: String): CoroutineScope = scope.childScope(name)
}