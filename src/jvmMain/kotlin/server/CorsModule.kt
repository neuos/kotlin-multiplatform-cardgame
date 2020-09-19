package server

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import java.time.Duration

fun Application.corsModule() {
    install(CORS)
    {
        anyHost()
        allowCredentials = true
    }
}
