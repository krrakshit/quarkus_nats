# NATS-Quarkus Notification System

A notification system built with Quarkus and NATS that receives messages from NATS and exposes them via REST endpoints.

## Architecture

- **NATS Server**: Message broker for publishing notifications
- **Quarkus Backend**: Consumes NATS messages and provides REST APIs
- **REST API**: HTTP endpoints for retrieving notifications

## Prerequisites

- Java 17+
- Maven 3.8+
- Docker (for NATS server)

## Quick Start

### 1. Start NATS Server

```bash
docker run -d --name nats-server -p 4222:4222 -p 8222:8222 nats:latest
```

### 2. Run the Application

```bash
mvn quarkus:dev
```

### 3. Test the REST API

```bash
# Get all notifications
curl http://localhost:8080/notifications

# Get notification count
curl http://localhost:8080/notifications/count

# Get latest notification
curl http://localhost:8080/notifications/latest

# Add test notification
curl -X POST http://localhost:8080/notifications/test \
  -H "Content-Type: application/json" \
  -d '{"title":"Test","message":"Hello World","type":"info"}'
```

### 4. Test NATS Integration

Run the NATS publisher in a separate terminal:

```bash
mvn compile exec:java -Dexec.mainClass="com.example.test.NatsPublisher"
```

Then use the interactive commands:
- `auto` - Send a test notification
- `send <title> <message> <type>` - Send custom notification
- `quit` - Exit

## Configuration

Edit `src/main/resources/application.properties`:

```properties
# NATS Configuration
nats.url=nats://localhost:4222
nats.subject=notifications
nats.connection.timeout=5000
nats.reconnect.wait=2000
nats.max.reconnect=10
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/notifications` | Get all notifications |
| GET | `/notifications/latest` | Get latest notification |
| GET | `/notifications/count` | Get notification count |
| POST | `/notifications/test` | Add test notification |

## Message Format

```json
{
  "id": "uuid",
  "title": "Notification Title",
  "message": "Notification message content",
  "type": "info|warning|error",
  "timestamp": "2024-01-01T12:00:00"
}
```

## Development

### Running Tests

```bash
mvn test
```

### Building for Production

```bash
mvn clean package
java -jar target/quarkus-app/quarkus-run.jar
```

### Development Mode

```bash
mvn quarkus:dev
```

## Features

- ✅ NATS message consumption
- ✅ REST API for notification retrieval
- ✅ In-memory storage (configurable)
- ✅ Automatic reconnection to NATS
- ✅ JSON serialization/deserialization
- ✅ Error handling and logging
- ✅ Interactive test publisher
