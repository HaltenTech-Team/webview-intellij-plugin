package ua.haltentech.plugin.webview.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.service
import ua.haltentech.plugin.webview.JBCefBrowserService

class SendToWebviewFromProjectsAction : AnAction() {
    override fun actionPerformed(actionEvent: AnActionEvent) {
        val project = actionEvent.getData(CommonDataKeys.PROJECT) ?: return
        val virtualFile = actionEvent.getData(CommonDataKeys.VIRTUAL_FILE) ?: return

        project.service<JBCefBrowserService>().executeClickedOnFileFunction(
            "SendToWebviewFromProjectsAction",
            virtualFile.path,
            "",
            "")
    }
}