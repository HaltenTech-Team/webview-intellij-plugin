package ua.haltentech.plugin.webview.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.*
import ua.haltentech.plugin.webview.JBCefBrowserService

class SendToWebviewFromEditorAction : AnAction() {
    override fun actionPerformed(actionEvent: AnActionEvent) {
        val project = actionEvent.getData(CommonDataKeys.PROJECT) ?: return
        val virtualFile = actionEvent.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
        val editor: Editor = actionEvent.getData(CommonDataKeys.EDITOR) ?: return

        val caretModel: CaretModel = editor.caretModel
        val primaryCaret: Caret = caretModel.primaryCaret
        val visualPos: VisualPosition = primaryCaret.visualPosition

        project.service<JBCefBrowserService>().executeClickedOnLineFunction(
            "",
            "",
            visualPos.line,
            virtualFile.path,
            "")
    }
}