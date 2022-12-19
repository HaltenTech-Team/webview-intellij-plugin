package ua.haltentech.plugin.webview

import com.intellij.openapi.components.Service
import com.intellij.ui.jcef.JBCefBrowser

@Service
class JBCefBrowserService {
    val browser = JBCefBrowser()

    fun executeClickedOnLineFunction(ideEventObject: Any, lineContent: String, lineNumber: Int, filePath: String, fileContent: String) {
        browser.cefBrowser.executeJavaScript("clickedOnLine('$ideEventObject', '$lineContent', '${lineNumber + 1}', '$filePath', '$fileContent')", "", 0)
    }

    fun executeClickedOnFileFunction(ideEventObject: Any, fileOrFolderPath: String, fileContent: String, filesInFolder: Any) {
        browser.cefBrowser
            .executeJavaScript("clickedOnFile('$ideEventObject', '$fileOrFolderPath', '$fileContent', '$filesInFolder')", "", 0)
    }
}