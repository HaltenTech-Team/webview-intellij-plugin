package ua.haltentech.plugin.webview.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.service
import ua.haltentech.plugin.webview.JBCefBrowserService

class SendToWebviewFromUsagesAction : AnAction() {
    override fun actionPerformed(actionEvent: AnActionEvent) {
        val virtualFile = actionEvent.getData(CommonDataKeys.VIRTUAL_FILE) ?: return

        service<JBCefBrowserService>().executeClickedOnFileFunction("", virtualFile.path, "", "")
    }
}