import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.util.*
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLInputElement


@KtorExperimentalAPI
fun main() {
    console.log("Main")
    val wsClient = WsClient(HttpClient { install(WebSockets) })
    console.log("websocket client created")
    GlobalScope.launch { initializeWebsocket(wsClient) }
    val sendButton = document.getElementById("sendMessageButton") as HTMLButtonElement
    val messageInput = document.getElementById("messageInput") as HTMLInputElement
    console.log(sendButton)
    console.log(messageInput)

    sendButton.addEventListener("click", {
        val message = messageInput.value
        GlobalScope.launch { wsClient.send(message) }
    })
}

private suspend fun initializeWebsocket(wsClient: WsClient) {
    wsClient.connect()
    wsClient.receive {
        console.log("received message $it")
    }
}

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
        session!!.send(Frame.Text(message))
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
