package sample


import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.browser.document
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLButtonElement


val client = HttpClient { install(WebSockets) }

lateinit var webSocketSession: DefaultClientWebSocketSession
fun main() {
    initializeWebsocket()

    val sendButton = document.getElementById("sendMessageButton") as HTMLButtonElement
    sendButton.addEventListener("click", {
        console.log(it)
        sendMessage(it.type)
    })
}

private fun initializeWebsocket() {
    CoroutineScope(Dispatchers.Default).launch {
        webSocketSession = client.webSocketSession(HttpMethod.Get, "127.0.0.1", 8080, "/ws")
        for (frame in webSocketSession.incoming) {
            console.log("Incoming frame is $frame")
            if (frame is Frame.Text) console.log("frametext is ${frame.readText()}")
        }
    }.invokeOnCompletion { console.warn("Websocket closed") }
    webSocketSession.incoming.onReceive

}

@JsName("sendMessage")
fun sendMessage(message: String) {
    console.log("message: $message")

    CoroutineScope(Dispatchers.Default).launch {
        console.log("start sending")
        webSocketSession.send(Frame.Text(message))
        console.log("end sending")
    }
}
