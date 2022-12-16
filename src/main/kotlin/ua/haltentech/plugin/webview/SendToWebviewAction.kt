package ua.haltentech.plugin.webview

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class SendToWebviewAction : AnAction() {
    override fun actionPerformed(actionEvent: AnActionEvent) {
        println("Test")
    }
}