package ua.haltentech.plugin.webview

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class SendToWebviewAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        println("Test")
    }
}