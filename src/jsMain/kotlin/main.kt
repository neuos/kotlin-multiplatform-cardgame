import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
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
    val httpClient = HttpClient { install(WebSockets) }
    val wsClient = WsClient(httpClient)
    val apiCLient = Client(httpClient, "http://localhost:8080")

    GlobalScope.launch { initializeWebsocket(wsClient) }

    val sendButton = document.getElementById("sendMessageButton") as HTMLButtonElement
    val messageInput = document.getElementById("messageInput") as HTMLInputElement
    val nameButton = document.getElementById("nameButton") as HTMLButtonElement
    val nameInput = document.getElementById("nameInput") as HTMLInputElement

    sendButton.addEventListener("click", {
        val message = messageInput.value
        GlobalScope.launch { wsClient.send(message) }
    })

    nameButton.addEventListener("click", {
        val name = nameInput.value
        GlobalScope.launch {
            apiCLient.post("name", Client.Parameter("name", name))
        }
    })
}


class Client(val httpClient: HttpClient, val apiUrl: String) {
    suspend inline fun <reified T> post(endpoint: String, vararg parameters: Parameter): T {
        val response :T= httpClient.post {
            url("$apiUrl/$endpoint")
            parameters.forEach {
                parameter(it.key, it.value)
            }
        }
        console.log(response)
        return response
    }

    class Parameter(val key: String, val value: String)
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
