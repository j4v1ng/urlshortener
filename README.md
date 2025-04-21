# URL Shortener

A simple URL shortener application built with Kotlin and Ktor.

## Features

- Shorten long URLs to easy-to-share short links
- Redirect from short links to original URLs
- Simple and clean user interface
- In-memory H2 database for URL storage

## Technologies Used

- Kotlin
- Ktor framework
- Thymeleaf for templates
- H2 in-memory database
- Exposed ORM
- Docker for containerization

## How to Run

1. Clone the repository:

```bash
git clone https://github.com/j4v1ng/urlshortener.git
cd urlshortener
```


2. Make sure you have Java 11+ installed
3. Run the application using Gradle:

```bash
./gradlew run
```

4. Open your browser and navigate to `http://localhost:8080`

## Running with Docker

1. Build the Docker image:

```bash
docker build -t urlshortener .
```

2. Run the container:

```bash
docker run -p 8080:8080 urlshortener
```

3. Open your browser and navigate to `http://localhost:8080`

## How to Use

1. Enter a URL in the input field
2. Click "Shorten URL"
3. Copy the shortened URL and share it
4. When someone visits the shortened URL, they will be redirected to the original URL

## Project Structure

- `src/main/kotlin/Application.kt` - Main application setup
- `src/main/kotlin/Routing.kt` - HTTP routes definition
- `src/main/kotlin/Database.kt` - Database models and service
- `src/main/resources/templates/index.html` - Thymeleaf template for the UI

## License

MIT
