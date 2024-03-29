package ua.haltentech.plugin.webview.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.*
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiManager
import ua.haltentech.plugin.webview.JBCefBrowserService

class SendToWebviewFromEditorAction : AnAction() {
    override fun actionPerformed(actionEvent: AnActionEvent) {
        val project = actionEvent.getData(CommonDataKeys.PROJECT) ?: return
        val virtualFile = actionEvent.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
        val editor: Editor = actionEvent.getData(CommonDataKeys.EDITOR) ?: return

        val caretModel: CaretModel = editor.caretModel
        val primaryCaret: Caret = caretModel.primaryCaret
        val visualPos: VisualPosition = primaryCaret.visualPosition

        val psiFile = PsiManager.getInstance(project).findFile(virtualFile) ?: return

        project.service<JBCefBrowserService>().executeClickedOnLineFunction(
            "SendToWebviewFromEditorAction",
            getCurrentLineContent(editor),
            visualPos.line,
            virtualFile.path,
            "psiFile.text")
    }

    private fun getCurrentLineContent(editor: Editor): String {
        val offset = editor.caretModel.offset
        val document = editor.document
        val line = document.getLineNumber(offset)
        val textRange = TextRange(document.getLineStartOffset(line), document.getLineEndOffset(line))

        return document.getText(textRange)
    }
}