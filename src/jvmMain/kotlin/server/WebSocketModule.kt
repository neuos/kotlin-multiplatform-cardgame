package server

import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*

fun Application.websocketModule() {
    install(WebSockets)
    routing {
        webSocket("/ws") {
            for (frame in incoming) {
                when (frame) {
                    is Frame.Text -> {
                        val text = frame.readText()
                        val response = handleSocketMessage(text)
                        response?.let {
                            outgoing.send(Frame.Text(response))
                        }
                    }
                    else -> log.warn("unknown incoming websocket frame: $frame")
                }
            }
        }
    }
}


private fun handleSocketMessage(message: String): String? {
    return "you said $message"
}
