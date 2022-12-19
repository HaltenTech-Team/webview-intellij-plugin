package ua.haltentech.plugin.webview

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory

import org.cef.CefApp

class WebviewToolwindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val httpPluginPath = "http://plugin"

        val cefBrowserService = service<JBCefBrowserService>()

        CefApp.getInstance()
                .registerSchemeHandlerFactory("http", "plugin", LocalResourceSchemeHandlerFactory(httpPluginPath))

        cefBrowserService.browser.loadURL("$httpPluginPath/ide-plugin.html")

        toolWindow.component.parent.add(cefBrowserService.browser.component)
    }
}
