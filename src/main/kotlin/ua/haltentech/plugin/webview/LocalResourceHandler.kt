package ua.haltentech.plugin.webview

import org.cef.handler.CefLoadHandler
import org.cef.callback.CefCallback
import org.cef.handler.CefResourceHandler
import org.cef.misc.IntRef
import org.cef.misc.StringRef
import org.cef.network.CefRequest
import org.cef.network.CefResponse
import java.io.IOException
import java.io.InputStream
import java.net.URLConnection

class LocalResourceHandler(private val httpPluginPath: String) : CefResourceHandler {

    private var state: ResourceHandlerState = ClosedConnection()

    override fun processRequest(cefRequest: CefRequest?, cefCallback: CefCallback?): Boolean {
        if (cefCallback == null) {
            return false
        }

        if (cefRequest == null || cefRequest.url == "") {
            return false
        }

        val pathToResource = cefRequest.url.replace(httpPluginPath, "/webview")
        val newUrl = LocalResourceHandler::class.java.getResource(pathToResource) ?: return false

        state = OpenedConnection(newUrl.openConnection())

        cefCallback.Continue()

        return true
    }

    override fun getResponseHeaders(cefResponse: CefResponse?, responseLength: IntRef?, redirectUrl: StringRef?) {
        state.getResponseHeaders(cefResponse!!, responseLength!!, redirectUrl!!)
    }

    override fun readResponse(dataOut: ByteArray?, bytesToRead: Int, bytesRead: IntRef?, callback: CefCallback?): Boolean {
        return state.readResponse(dataOut!!, bytesToRead, bytesRead!!, callback!!)
    }

    override fun cancel() {
        state.close()

        state = ClosedConnection()
    }
}

interface ResourceHandlerState {
    fun getResponseHeaders(cefResponse: CefResponse, responseLength: IntRef, redirectUrl: StringRef)

    fun readResponse(dataOut: ByteArray, designedBytesToRead: Int, bytesRead: IntRef, callback: CefCallback): Boolean

    fun close()
}

class ClosedConnection : ResourceHandlerState {
    override fun getResponseHeaders(cefResponse: CefResponse, responseLength: IntRef, redirectUrl: StringRef) {
        cefResponse.status = 404
    }

    override fun readResponse(dataOut: ByteArray, designedBytesToRead: Int, bytesRead: IntRef, callback: CefCallback): Boolean {
        return false
    }

    override fun close() {}
}

class OpenedConnection(val connection: URLConnection) : ResourceHandlerState {

    private val inputStream: InputStream = connection.getInputStream()

    override fun getResponseHeaders(cefResponse: CefResponse, responseLength: IntRef, redirectUrl: StringRef) {
        try {
            val url = connection.url.toString()

            when {
                url.contains("css") -> cefResponse.mimeType = "text/css"
                url.contains("js") -> cefResponse.mimeType = "text/javascript"
                url.contains("html") -> cefResponse.mimeType = "text/html"
                else -> cefResponse.mimeType = connection.contentType
            }

            responseLength.set(inputStream.available())
            cefResponse.status = 200
        } catch (e: IOException) {
            cefResponse.error = CefLoadHandler.ErrorCode.ERR_FILE_NOT_FOUND
            cefResponse.statusText = e.localizedMessage
            cefResponse.status = 404
        }
    }

    override fun readResponse(dataOut: ByteArray, designedBytesToRead: Int, bytesRead: IntRef, callback: CefCallback): Boolean {
        val availableSize = inputStream.available()

        return if (availableSize > 0) {
            val maxBytesToRead = Math.min(availableSize, designedBytesToRead)
            val realNumberOfReadBytes = inputStream.read(dataOut, 0, maxBytesToRead)

            bytesRead.set(realNumberOfReadBytes)

            true
        } else {
            inputStream.close()
            false
        }
    }

    override fun close() {
        inputStream.close()
    }
}