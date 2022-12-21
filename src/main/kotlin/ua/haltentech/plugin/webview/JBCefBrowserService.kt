package ua.haltentech.plugin.webview

import com.intellij.ide.util.PsiNavigationSupport
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.readAction
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.ui.jcef.JBCefBrowser
import com.intellij.ui.jcef.JBCefBrowserBase
import com.intellij.ui.jcef.JBCefClient
import com.intellij.ui.jcef.JBCefJSQuery


@Service
class JBCefBrowserService(private val project: Project) {
    val browser = JBCefBrowser()

    init {
        browser.jbCefClient.setProperty(JBCefClient.Properties.JS_QUERY_POOL_SIZE, 10)
    }

    fun executeClickedOnLineFunction(ideEventObject: Any, lineContent: String, lineNumber: Int, filePath: String, fileContent: String) {
        setupCallback()
        browser.cefBrowser.executeJavaScript("clickedOnLine('$ideEventObject', '$lineContent', '${lineNumber + 1}', '$filePath', '$fileContent')", "", 0)
    }

    fun executeClickedOnFileFunction(ideEventObject: Any, fileOrFolderPath: String, fileContent: String, filesInFolder: Any) {
        setupCallback()
        browser.cefBrowser.executeJavaScript("clickedOnFile('$ideEventObject', '$fileOrFolderPath', '$fileContent', '$filesInFolder')", "", 0)
    }

    private fun setupCallback() {
        val javaScriptEngineProxy: JBCefJSQuery = JBCefJSQuery.create(browser as JBCefBrowserBase)

        javaScriptEngineProxy.addHandler { result ->
            val goToDetails = result.split(":")
            val filePath = goToDetails[0]
            val lineNumber = goToDetails[1]

            val virtualFile = LocalFileSystem.getInstance().findFileByPath(filePath)
                ?: return@addHandler JBCefJSQuery.Response("Ok")

            ApplicationManager.getApplication().invokeLater {
                FileEditorManager.getInstance(project)
                    .openTextEditor(OpenFileDescriptor(project, virtualFile, lineNumber.toInt() - 1, 0), true)
            }

            null
        }

        val injectedJavaScript = """
            window.goToLine = function() {                
                var filePath = document.getElementById("go-to-filepath").value
                var lineNumber = document.getElementById("go-to-line-number").value
                var goToPath = filePath + ":" + lineNumber
                ${javaScriptEngineProxy.inject("goToPath")}
            
                document.getElementById("go-to-file-event").innerHTML = "going to file: " + filePath + ", line: " + lineNumber
            }
            
            """.trimIndent()

        browser.cefBrowser.executeJavaScript(injectedJavaScript, browser.cefBrowser.url, 0)
    }
}
