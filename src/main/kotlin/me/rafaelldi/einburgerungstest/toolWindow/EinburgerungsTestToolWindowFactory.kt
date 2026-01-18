package me.rafaelldi.einburgerungstest.toolWindow

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import me.rafaelldi.einburgerungstest.EinburgerungsTestService
import org.jetbrains.jewel.bridge.addComposeTab


class EinburgerungsTestToolWindowFactory : ToolWindowFactory, DumbAware {
    override fun shouldBeAvailable(project: Project) = true

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val viewModel = EinburgerungsTestViewModelImpl(
            EinburgerungsTestService.getInstance(project).createScope(::EinburgerungsTestViewModelImpl.name),
        )
        Disposer.register(toolWindow.disposable, viewModel)

        toolWindow.addComposeTab("Test", focusOnClickInside = true) {
            EinburgerungsTestTab(viewModel)
        }
    }
}
