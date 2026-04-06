# NewsBit API and Admin Dashboard

NewsBit is a Spring Boot application that provides:

- A secured REST API for reading news articles
- A secured admin web dashboard for managing articles

## Tech Stack

- Java 21
- Spring Boot 4.0.5
- Spring Web, Spring Data JPA, Spring Security, Thymeleaf
- PostgreSQL (default)
- H2 (optional profile for local development)
- Docker and Docker Compose

## Project Features

- News feed with pagination and filtering
- Full-text style search on article title and content
- Trending endpoint based on views
- Category listing endpoint
- Admin dashboard for create, edit, delete article workflows
- HTTP Basic auth for API and form login for web UI

## Quick Start (Docker)

Prerequisite: Docker Desktop (or Docker daemon) must be running.

1. From the project root, run:

   ```bash
   docker compose up --build
   ```

2. Open:
   - Login page: http://localhost:8080/login
   - API base: http://localhost:8080/v1

3. Stop services:

   ```bash
   docker compose down
   ```

## Run Locally (Without Docker)

1. Ensure PostgreSQL is running and update environment values in `.env`.
2. Start the app:

   ```bash
   ./mvnw spring-boot:run
   ```

3. Run tests:

   ```bash
   ./mvnw test
   ```

## Environment Variables

The app loads variables from `.env` via `spring.config.import`.

Required app variables:

- `DB_URL` (example: `jdbc:postgresql://localhost:5432/postgres`)
- `DB_DRIVER_CLASS_NAME` (default: `org.postgresql.Driver`)
- `DB_USERNAME`
- `DB_PASSWORD`
- `ADMIN_USERNAME`
- `ADMIN_PASSWORD`
- `PUBLIC_USERNAME`
- `PUBLIC_PASSWORD`

Optional:

- `APP_LOG_LEVEL` (default `INFO`)

For Docker Compose, database container values are aligned with `DB_USERNAME` and `DB_PASSWORD` from `.env`.

## Authentication and Access Rules

Security behavior:

- `/login` is public
- `/v1/**` requires role `PUBLIC` or `ADMIN`
- `/admin/**` requires role `ADMIN`
- API supports HTTP Basic auth
- Web UI uses form login

Default credentials come from `.env`.

Example API auth usage:

```bash
curl -u public:public123 "http://localhost:8080/v1/categories"
```

## API Guide

Base URL:

- `http://localhost:8080/v1`

All API endpoints below require Basic Auth.

### 1) Get Feed

`GET /feed`

Query parameters:

- `page` (default `0`, minimum `0`)
- `limit` (default `10`, min `1`, max `100`)
- `category` (optional enum: `TOP`, `WORLD`, `TECHNOLOGY`, `SPORTS`, `BUSINESS`)
- `country` (optional string)
- `language` (optional string)

Example:

```bash
curl -u public:public123 "http://localhost:8080/v1/feed?page=0&limit=10&category=TECHNOLOGY&country=us&language=en"
```

Returns: `PagedResponse<ArticleSummaryResponse>`

### 2) Get Article by ID

`GET /articles/{id}`

Notes:

- Returns full article payload
- Increments article `views` each time it is fetched

Example:

```bash
curl -u public:public123 "http://localhost:8080/v1/articles/1"
```

Returns: `ArticleResponse`

### 3) Get Categories

`GET /categories`

Example:

```bash
curl -u public:public123 "http://localhost:8080/v1/categories"
```

Returns: `List<CategoryResponse>`

Example item:

```json
{
  "code": "TECHNOLOGY",
  "label": "Technology"
}
```

### 4) Search Articles

`GET /search`

Query parameters:

- `q` (required, non-blank)
- `page` (default `0`, minimum `0`)
- `limit` (default `10`, min `1`, max `100`)

Example:

```bash
curl -u public:public123 "http://localhost:8080/v1/search?q=ai&page=0&limit=10"
```

Returns: `PagedResponse<ArticleSummaryResponse>`

### 5) Get Trending

`GET /trending`

Query parameters:

- `limit` (default `5`, min `1`, max `20`)

Example:

```bash
curl -u public:public123 "http://localhost:8080/v1/trending?limit=5"
```

Returns: `List<ArticleSummaryResponse>`

## Response Shapes

### PagedResponse<T>

```json
{
  "content": [],
  "page": 0,
  "limit": 10,
  "totalElements": 0,
  "totalPages": 0,
  "last": true
}
```

### ArticleSummaryResponse

```json
{
  "id": 1,
  "title": "Sample title",
  "summary": "Short summary",
  "imageUrl": "https://example.com/image.jpg",
  "category": "TECHNOLOGY",
  "categoryLabel": "Technology",
  "country": "US",
  "language": "EN",
  "createdAt": "2026-04-06T10:00:00",
  "views": 42
}
```

### ArticleResponse

Same as `ArticleSummaryResponse`, plus:

- `content` (full article body)

## Error Handling

API controller errors return this shape:

```json
{
  "timestamp": "2026-04-06T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed.",
  "path": "/v1/search",
  "validationErrors": {
    "search.q": "Search query is required."
  }
}
```

Typical statuses:

- `400` invalid parameters, validation, type mismatch
- `401` missing/invalid authentication
- `403` authenticated but not authorized
- `404` article not found

## Admin Dashboard (Web)

After login as admin user, go to:

- `GET /admin/articles` dashboard
- `POST /admin/articles` create article
- `GET /admin/articles/{id}/edit` edit page
- `POST /admin/articles/{id}` update article
- `POST /admin/articles/{id}/delete` delete article

Note: Create/update/delete are currently available through the admin web interface, not public REST write endpoints.

## H2 Profile (Optional)

An H2 config exists in `src/main/resources/application-h2.properties`.

If you want to use H2 locally, run with profile `h2` and adjust datasource settings as needed.