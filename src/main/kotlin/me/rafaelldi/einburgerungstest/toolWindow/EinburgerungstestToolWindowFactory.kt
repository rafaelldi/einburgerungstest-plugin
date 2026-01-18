package me.rafaelldi.einburgerungstest.toolWindow

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import me.rafaelldi.einburgerungstest.EinburgerungstestService
import me.rafaelldi.einburgerungstest.questions.QuestionServiceImpl
import org.jetbrains.jewel.bridge.addComposeTab


class EinburgerungstestToolWindowFactory : ToolWindowFactory, DumbAware {
    override fun shouldBeAvailable(project: Project) = true

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val questionService = QuestionServiceImpl.getInstance(project)

        val viewModel = EinburgerungstestViewModelImpl(
            EinburgerungstestService.getInstance(project).createScope(::EinburgerungstestViewModelImpl.name),
            questionService
        )
        Disposer.register(toolWindow.disposable, viewModel)

        toolWindow.addComposeTab("", focusOnClickInside = true) {
            EinburgerungstestTab(viewModel)
        }
    }
}
