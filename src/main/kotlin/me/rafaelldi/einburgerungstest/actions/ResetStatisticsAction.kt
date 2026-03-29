package me.rafaelldi.einburgerungstest.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.MessageDialogBuilder
import me.rafaelldi.einburgerungstest.MyBundle
import me.rafaelldi.einburgerungstest.persistence.QuestionPersistenceServiceImpl

internal class ResetStatisticsAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        val confirmed = MessageDialogBuilder.yesNo(
            MyBundle.message("action.resetStatistics.dialog.title"),
            MyBundle.message("action.resetStatistics.dialog.message")
        ).ask(project)

        if (confirmed) {
            val persistenceService = service<QuestionPersistenceServiceImpl>()
            persistenceService.correctAnswers = emptyMap()
            persistenceService.wrongAnswers = emptyMap()
        }
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = e.project != null
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }
}
