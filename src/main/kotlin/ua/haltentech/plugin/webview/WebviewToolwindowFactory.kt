package ua.haltentech.plugin.webview

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.jcef.JBCefBrowser
import org.cef.CefApp

class WebviewToolwindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val browser = JBCefBrowser()
        val httpPluginPath = "http://plugin"

        CefApp.getInstance()
                .registerSchemeHandlerFactory("http", "plugin", LocalResourceSchemeHandlerFactory(httpPluginPath))

        browser.loadURL("$httpPluginPath/ide-plugin.html")

        toolWindow.component.parent.add(browser.component)
    }
}