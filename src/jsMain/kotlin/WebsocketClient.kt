import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.browser.window

class WsClient(private val client: HttpClient, private val path: String = "/ws") {
    var session: WebSocketSession? = null

    suspend fun connect() {
        console.log("Connecting to websocket")
        session = client.webSocketSession(
            method = HttpMethod.Get,
            host = window.location.hostname,
            port = window.location.port.toInt(),
            path = path
        )
        console.log("Connected to websocket")
    }

    suspend fun send(message: String) {
        session?.send(Frame.Text(message))?:throw RuntimeException("Websocket is not connected")
    }

    suspend fun receive(onReceive: (input: String) -> Unit) {
        while (true) {
            for (frame in session!!.incoming) {
                when (frame) {
                    is Frame.Text -> onReceive(frame.readText())
                    else -> console.warn("Unknown incoming frame $frame")
                }
            }
        }
    }
}
