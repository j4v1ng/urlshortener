package com.javing

import io.ktor.server.application.*
import io.ktor.server.thymeleaf.*
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.jackson.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.http.*
import io.ktor.server.response.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    // Initialize URL service
    val urlService = UrlService()

    // Configure Thymeleaf
    install(Thymeleaf) {
        setTemplateResolver(ClassLoaderTemplateResolver().apply {
            prefix = "templates/"
            suffix = ".html"
            characterEncoding = "utf-8"
        })
    }

    // Configure content negotiation
    install(ContentNegotiation) {
        jackson()
    }

    // Configure status pages
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: ${cause.message}", status = HttpStatusCode.InternalServerError)
        }
        status(HttpStatusCode.NotFound) { call, _ ->
            call.respondText(text = "404: Page Not Found", status = HttpStatusCode.NotFound)
        }
    }

    // Configure routing
    configureRouting(urlService)
}
