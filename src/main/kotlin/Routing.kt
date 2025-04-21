package com.javing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.thymeleaf.*

fun Application.configureRouting(urlService: UrlService) {
    routing {
        // Home page - show the form
        get("/") {
            call.respond(ThymeleafContent("index", mapOf<String, Any>()))
        }

        // Handle URL shortening
        post("/shorten") {
            val formParameters = call.receiveParameters()
            val originalUrl = formParameters["url"] ?: ""
            val customShortCode = formParameters["customCode"]?.trim()

            if (originalUrl.isBlank()) {
                call.respond(ThymeleafContent("index", mapOf(
                    "error" to "Please enter a valid URL",
                    "url" to originalUrl,
                    "customCode" to (customShortCode ?: "")
                )))
                return@post
            }

            // Validate custom short code if provided
            if (!customShortCode.isNullOrBlank() && !urlService.isValidShortCode(customShortCode)) {
                call.respond(ThymeleafContent("index", mapOf(
                    "error" to "Custom code must be 3-10 characters long and contain only letters, numbers, and hyphens",
                    "url" to originalUrl,
                    "customCode" to customShortCode
                )))
                return@post
            }

            try {
                val urlEntity = urlService.createShortUrl(originalUrl, customShortCode)
                val host = call.request.host()
                val port = if (call.request.port() == 80) "" else ":${call.request.port()}"
                val shortUrl = "http://$host$port/${urlEntity.shortCode}"

                call.respond(ThymeleafContent("index", mapOf(
                    "shortUrl" to shortUrl,
                    "url" to originalUrl
                )))
            } catch (e: Exception) {
                call.respond(ThymeleafContent("index", mapOf(
                    "error" to "Error creating short URL: ${e.message}",
                    "url" to originalUrl,
                    "customCode" to (customShortCode ?: "")
                )))
            }
        }

        // Handle redirection from short URL to original URL
        get("/{shortCode}") {
            val shortCode = call.parameters["shortCode"] ?: return@get call.respond(HttpStatusCode.BadRequest)

            val urlEntity = urlService.findByShortCode(shortCode)
            if (urlEntity != null) {
                call.respondRedirect(urlEntity.originalUrl)
            } else {
                call.respond(HttpStatusCode.NotFound, "Short URL not found")
            }
        }
    }
}
