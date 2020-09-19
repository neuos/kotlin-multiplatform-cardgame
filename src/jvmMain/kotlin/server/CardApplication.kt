package server

import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    val server = embeddedServer(Netty, 8080, "127.0.0.1") {
        routingModule()
        websocketModule()
        staticModule()
        sessionModule()
        corsModule()
    }
    server.start(wait = true)
}
