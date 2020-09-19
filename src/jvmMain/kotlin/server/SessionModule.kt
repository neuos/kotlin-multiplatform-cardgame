package server

import io.ktor.application.*
import io.ktor.sessions.*
import io.ktor.util.*

fun Application.sessionModule() {
    install(Sessions) {
        cookie<MySession>("KTOR_SESSION", storage = SessionStorageMemory())
    }
    intercept(ApplicationCallPipeline.Features) {
        if (call.currentSession == null) {
            call.currentSession = MySession(generateNonce())
        }
    }
}

inline var ApplicationCall.currentSession
    get() = sessions.get<MySession>()
    set(session) = sessions.set(session)

class MySession(val nonce: String) {
    var name: String? = null
}
