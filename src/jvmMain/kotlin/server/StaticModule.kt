package server

import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.routing.*

fun Application.staticModule() {
    routing {
        static("/static") {
            resource("kotlin-multiplatform-cardgame.js")
        }
        static("/") {
            resource("favicon.ico")
            resource("style.css")
        }
    }
}
