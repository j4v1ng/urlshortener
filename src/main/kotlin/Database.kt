package com.javing

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import org.h2.Driver
import org.jetbrains.exposed.sql.Database

// Define the URL table structure
object UrlTable : IntIdTable() {
    val originalUrl = varchar("original_url", 2048)
    val shortCode = varchar("short_code", 10).uniqueIndex()
    val createdAt = long("created_at").default(System.currentTimeMillis())
}

// Data class for URL entity
data class UrlEntity(
    val id: Int? = null,
    val originalUrl: String,
    val shortCode: String,
    val createdAt: Long = System.currentTimeMillis()
)

// Database service for URL operations
class UrlService {
    // Initialize database
    init {
        Database.connect("jdbc:h2:mem:urlshortener;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
        transaction {
            SchemaUtils.create(UrlTable)
        }
    }

    // Create a new shortened URL
    fun createShortUrl(originalUrl: String, customShortCode: String? = null): UrlEntity {
        // Use custom short code if provided and valid, otherwise generate a random one
        val shortCode = if (!customShortCode.isNullOrBlank()) {
            // Check if the custom short code is already in use
            val existingUrl = findByShortCode(customShortCode)
            if (existingUrl != null) {
                throw IllegalArgumentException("This custom code is already in use. Please choose another one.")
            }
            customShortCode
        } else {
            generateShortCode()
        }

        val id = transaction {
            UrlTable.insert {
                it[this.originalUrl] = originalUrl
                it[this.shortCode] = shortCode
            } get UrlTable.id
        }

        return UrlEntity(
            id = id.value,
            originalUrl = originalUrl,
            shortCode = shortCode
        )
    }

    // Validate a custom short code
    fun isValidShortCode(shortCode: String): Boolean {
        // Short code should be between 3 and 10 characters
        if (shortCode.length < 3 || shortCode.length > 10) {
            return false
        }

        // Short code should only contain alphanumeric characters and hyphens
        val validCharsPattern = Regex("^[a-zA-Z0-9-]+$")
        if (!validCharsPattern.matches(shortCode)) {
            return false
        }

        return true
    }

    // Find URL by short code
    fun findByShortCode(shortCode: String): UrlEntity? {
        return transaction {
            UrlTable.select { UrlTable.shortCode eq shortCode }
                .map {
                    UrlEntity(
                        id = it[UrlTable.id].value,
                        originalUrl = it[UrlTable.originalUrl],
                        shortCode = it[UrlTable.shortCode],
                        createdAt = it[UrlTable.createdAt]
                    )
                }
                .singleOrNull()
        }
    }

    // Generate a random short code
    private fun generateShortCode(): String {
        val allowedChars = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..6)
            .map { allowedChars.random() }
            .joinToString("")
    }
}
