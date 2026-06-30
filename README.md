# NnmBot

Warframe QQ Bot - Modular Architecture

## Modules

| Module | Description |
|--------|-------------|
| nnm-common | Common utilities, exceptions, HTTP client, API response |
| nnm-core | Bot core: plugin SPI, message dispatch, events, executor config |
| nnm-render | Image rendering engine (SPI pluggable: openhtmltopdf / Playwright) |
| nnm-warframe | Warframe domain: world state, market, change detection |
| nnm-scheduler | Scheduled tasks: polling, cleanup, notifications |
| nnm-admin | Admin REST API: authentication, bot management |
| nnm-app | Application entry point, assembles all modules |

## Requirements

- Java 21+
- Maven 3.9+
- PostgreSQL 16+ (production) / H2 (development)

## Quick Start

```bash
# Development (H2, auto DDL)
mvn clean install -DskipTests
cd nnm-app
mvn spring-boot:run

# Production
export DB_HOST=localhost DB_PORT=5432 DB_NAME=nnmbot
export DB_USERNAME=nnmbot DB_PASSWORD=your-password
export JWT_SECRET=your-secret-key
java -jar nnm-app/target/NnmBot.jar --spring.profiles.active=prod
```

## Render Engine

The render engine is auto-selected based on environment:
- **openhtmltopdf** (default): Lightweight, CSS3 support, zero external dependencies
- **Playwright**: Full Chrome rendering, set `render.engine=playwright` in config

Override via `application.yaml`:
```yaml
render:
  engine: auto  # auto | openhtmltopdf | playwright
```

## Architecture

```
Spring Boot 3.4 + Spring Modulith
├── Event-driven inter-module communication
├── Plugin SPI for bot commands
├── Strategy pattern for change detection
├── Virtual threads (Java 21) for async processing
└── Resilience4j for external API fault tolerance
```
