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
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement


@KtorExperimentalAPI
fun main() {
    val httpClient = HttpClient { install(WebSockets) }
    val wsClient = WsClient(httpClient)
    val apiClient = ApiClient(httpClient, "http://localhost:8080")

    GlobalScope.launch { initializeWebsocket(wsClient) }

    val sendButton = document.getElementById("sendMessageButton") as HTMLButtonElement
    val messageInput = document.getElementById("messageInput") as HTMLInputElement
    val nameButton = document.getElementById("nameButton") as HTMLButtonElement
    val nameInput = document.getElementById("nameInput") as HTMLInputElement
    val handCards = document.getElementById("handcards") as HTMLDivElement

    sendButton.addEventListener("click", {
        val message = messageInput.value
        sendMessage(wsClient, message)
    })

    nameButton.addEventListener("click", {
        val name = nameInput.value
        GlobalScope.launch {
            apiClient.post("name", ApiClient.Parameter("name", name))
        }
    })
    createDeck().forEach {
        handCards.appendChild(it.html())
    }

}

private fun sendMessage(wsClient: WsClient, message: String) {
    GlobalScope.launch { wsClient.send(message) }
}

private suspend fun initializeWebsocket(wsClient: WsClient) {
    wsClient.connect()
    wsClient.receive {
        console.log("received message $it")
    }
}
