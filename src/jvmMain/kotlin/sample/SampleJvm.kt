package sample

import Game
import GameState
import Player
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.http.content.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.mapNotNull
import kotlinx.html.*

fun main() {
    val server = embeddedServer(Netty, 8080, "127.0.0.1") {
        module()
        websocketModule()
        staticModule()
    }
    server.start(wait = true)
}


private fun Application.module() {
    install(ContentNegotiation) {
        jackson {
        }
    }

    routing {
        get("/") {
            call.respondHtml {
                head {
                    title("Hello from Ktor!")
                }
                body {
                    button {
                        id = "sendMessageButton"
                        +"Send Message"
                    }
                    script(src = "/static/uno.js") {}
                }
            }
        }
        get("/lobby") {
            call.respondHtml {
                head {
                    title("Uno Lobby")
                }
                body {
                    postForm {
                        p {
                            label { +"Name: " }
                            input { name = "name" }
                        }
                        p {
                            label { +"Game ID: " }
                            numberInput { name = "id" }
                        }
                        p {
                            submitInput { value = "Join" }
                        }
                    }
                }
            }
        }

        get("/game/{id}") {
            val id = call.parameters["id"]?.toInt()!!
            val game = Lobby.games[id]
            if (game == null) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                call.respondHtml {
                    body {
                        h1 { +"Game $id" }
                        h2 { +"Players:" }
                        ul {
                            game.gamePerspective().enemies.map {
                                li { +it.name }
                            }
                        }
                        postForm("/game/$id/start") { submitInput { value = "Start" } }
                    }
                }
            }
        }

        post("/game/{id}/start") {
            val id = call.parameters["id"]?.toInt()!!
            val game = Lobby.games[id]
            when (game?.state) {
                GameState.LOBBY -> {
                    game.start()
                    call.respondRedirect("/game/$id/running")
                }
                null -> call.respond(HttpStatusCode.NotFound)
                else -> call.respond(HttpStatusCode.MethodNotAllowed)
            }
        }

        get("/game/{id}/running") {
            val id = call.parameters["id"]?.toInt()!!
            val game = Lobby.games[id]
            if (game?.state == GameState.RUNNING) {
                call.respond(game)
            } else call.respond(HttpStatusCode.BadRequest)
        }



        post("/lobby") {
            val post = call.receiveParameters()
            val name = post["name"]!!
            val id = post["id"]!!.toInt()
            val game = Lobby.games.computeIfAbsent(id) { Game() }
            game.addPlayer(Player(name))

            call.respondRedirect("/game/$id")
        }

    }
}

private fun Application.staticModule(){
    routing {
        static("/static") {
            resource("uno.js")
        }
    }
}

private fun Application.websocketModule() {
    install(WebSockets)
    routing {
        webSocket("/ws") {
            for (frame in incoming) {
                when(frame){
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

object Lobby {
    var games = mutableMapOf<Int, Game>()
}
