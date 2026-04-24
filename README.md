# Smart Campus — Sensor & Room Management API

> **Module:** 5COSC022W — Client-Server Architectures (2025/26)  
> **Student:** Madhusha  
> **Framework:** JAX-RS (Jersey 2.29.1) on **Apache Tomcat 9**  
> **Persistence:** In-memory Java collections (`ConcurrentHashMap`)  
> **Java Version:** 23 (JDK 23)
> **Context Path:** `/cw`

---

## Table of Contents

1. [Overview](#overview)
2. [Build & Run Instructions](#build--run-instructions)
3. [API Overview](#api-overview)
4. [Sample `curl` Commands](#sample-curl-commands)
5. [Report Questions](#report-questions)

---

## Overview

This project implements a RESTful API for the **Smart Campus** initiative, allowing administrators to manage **rooms**, **sensors**, and **sensor readings** across a university campus. It is built exclusively with **JAX-RS (Jersey)** and deployed as a standard WAR on **Apache Tomcat 9**.

### Architecture at a Glance

```
Client (Postman / curl / Browser)
        │
        ▼
┌─────────────────────────────────────┐
│   Apache Tomcat 9 (Port 8080)       │
├─────────────────────────────────────┤
│   Context Path: /cw                 │
│   Servlet Mapping: /api/v1/*        │
├──────────┬──────────┬───────────────┤
│ Discovery│  Room    │   Sensor      │
│ Resource │ Resource │   Resource    │
│          │          │  └─ Reading   │
│          │          │    SubResource│
├──────────┴──────────┴───────────────┤
│     DataStore (ConcurrentHashMap)   │
│     Thread-safe Singleton           │
└─────────────────────────────────────┘
```

---

## Build & Run Instructions

### Prerequisites

- **JDK 23**
- **Apache Maven 3.8+**
- **Apache Tomcat 9.x**

### Deployment (NetBeans)

1. **Open Project**: Open the `cw` folder in NetBeans.
2. **Configure Tomcat**: Ensure Apache Tomcat 9 is added to your Servers (Services tab).
3. **Set Java Platform**: Right-click project > Properties > Libraries > Java Platform: **JDK 23**.
4. **Context Path**: Ensure the Context Path is set to `/cw` in the project settings.
5. **Clean & Build**: Right-click project > **Clean and Build**.
6. **Run**: Right-click project > **Run**.

The API will be available at:  
**`http://localhost:8080/cw/api/v1/discovery`**

The professional landing page is at:  
**`http://localhost:8080/cw/`**

---

## API Overview

| Method   | Endpoint                                  | Description                                        |
|----------|-------------------------------------------|----------------------------------------------------|
| `GET`    | `/cw/api/v1/discovery`                    | Discovery — API metadata & HATEOAS links           |
| `GET`    | `/cw/api/v1/rooms`                         | List all rooms                                     |
| `POST`   | `/cw/api/v1/rooms`                         | Create a new room                                  |
| `GET`    | `/cw/api/v1/rooms/{roomId}`                  | Retrieve a specific room                           |
| `DELETE` | `/cw/api/v1/rooms/{roomId}`                  | Decommission a room (blocked if sensors assigned)  |
| `GET`    | `/cw/api/v1/sensors`                         | List all sensors (supports `?type=` filter)        |
| `POST`   | `/cw/api/v1/sensors`                         | Register a new sensor                              |
| `GET`    | `/cw/api/v1/sensors/{sensorId}`              | Retrieve a specific sensor                         |
| `PUT`    | `/cw/api/v1/sensors/{sensorId}`              | Update sensor metadata / reassign room             |
| `GET`    | `/cw/api/v1/sensors/{sensorId}/read`         | Fetch reading history for a sensor                 |
| `POST`   | `/cw/api/v1/sensors/{sensorId}/read`         | Append a new reading (updates sensor currentValue) |

---

## Sample `curl` Commands

### 1. Discovery Endpoint (HATEOAS Root)

```bash
curl -X GET http://localhost:8080/cw/api/v1/discovery
```

### 2. Create a Room

```bash
curl -X POST http://localhost:8080/cw/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id":"ROOM-101","name":"Lecture Hall A","capacity":120}'
```

### 3. List All Rooms

```bash
curl -X GET http://localhost:8080/cw/api/v1/rooms
```

---

## Report Questions

### Part 1 - Question 1: JAX-RS Resource Lifecycle and Thread-Safety
My choice of a ConcurrentHashMap for the in-memory DataStore was a deliberate decision to ensure thread-safety without the heavy overhead of global synchronization. Since JAX-RS resources are request-scoped and the server handles multiple requests concurrently, a standard HashMap would have been prone to race conditions and data corruption. By using the concurrent alternative, I guaranteed that individual read and write operations are atomic, which is essential for maintaining a consistent state across the various threads serving the API.

### Part 1 - Question 2: HATEOAS and Self-Discoverability
I implemented HATEOAS links in the discovery resource to transform the API from a static set of endpoints into a navigable, self-documenting web of resources. By providing hypermedia links at the root, I allow any client to discover the core room and sensor collections dynamically without needing to hardcode URI patterns from external documentation. This approach follows the Richardson Maturity Model by ensuring that the server guides the client’s state transitions, making the overall system more decoupled and easier to evolve over time. My implementation uses `UriInfo` to generate absolute paths dynamically, ensuring stability across different environments.

### Part 2 - Question 1: Bandwidth vs. Processing Trade-offs
In the list resources, I decided to return full room and sensor objects rather than just a list of IDs to avoid the common N+1 request problem. While returning only IDs reduces the initial payload size and saves a small amount of bandwidth, it forces the client to make multiple follow-up requests to get any meaningful data, which actually increases the total network latency and server load. Given the relatively small scale of campus data, providing complete objects in the collection responses offers a much better balance of performance and developer experience.

### Part 2 - Question 2: Idempotency of the DELETE Method
The DELETE operation for decommissioning rooms was designed to be strictly idempotent, meaning that the final state of the server remains the same whether the request is sent once or ten times. The first successful request removes the room and returns a 204 No Content status, while subsequent requests correctly return a 404 Not Found to reflect that the resource is no longer there. This behavior ensures that my API remains reliable even in unstable network conditions where a client might retry a request that actually succeeded on the server side.

### Part 2 - Question 3: Business Rule Integrity and 409 Conflict
To protect the integrity of the data model, I implemented a safety check that prevents the deletion of any room that still has active sensors assigned to it. I chose to map this business rule violation to a 409 Conflict status because it precisely signals that the request is valid in syntax but cannot be completed due to the current state of the server's data. This is a much more descriptive RESTful choice than a generic 400 error, as it tells the client exactly why the operation was blocked and implies that they must first resolve the dependency by reassigning or deleting the sensors.

### Part 3 - Question 1: Media Type Negotiation and 415 Status
I utilized the @Consumes annotation to enforce strict Media Type Negotiation within the sensor registration resources. By explicitly requiring application/json, I ensured that any client attempting to send plain text or other incompatible formats is immediately rejected with an HTTP 415 Unsupported Media Type. This protocol-level validation acts as a first line of defense, ensuring that the application logic only processes structured data that it is designed to handle, which improves the overall robustness of the system.

### Part 3 - Question 2: Filtering Strategy (Query vs. Path)
For the filtered retrieval of sensors, I chose to use query parameters because they provide a clean way to narrow down a collection without changing the primary identity of the resource path. Path variables are semantically intended for identifying unique resources, whereas query strings are the standard RESTful choice for searching, sorting, and filtering. Using query parameters makes the API more flexible and scalable, as it allows clients to optionally filter by type without needing to navigate a complex and often ambiguous nested path structure.

### Part 4 - Question 1: Complexity Management and Sub-Resource Locators
The introduction of historical sensor readings required a deeply nested URI structure, which I managed by implementing the Sub-Resource Locator pattern in the sensor resource. By delegating nested requests for readings to a specialized handler class, I avoided the massive controller anti-pattern that often occurs when a single class tries to handle too many responsibilities. This architectural choice follows the Single Responsibility Principle and allows me to manage complex, hierarchical data relationships without cluttering the main resource with unrelated logic.

### Part 4 - Question 2: Side Effects and Atomic State Updates
To ensure data consistency, I implemented a side effect that automatically updates the parent sensor's current value whenever a new reading is posted to its historical sub-resource. I integrated this logic directly into the DataStore to ensure that the update happens atomically within the thread-safe maps. This design ensures that a client fetching the sensor metadata always sees the most recent value, eliminating the need for them to manually calculate or fetch the latest state from the time-series history themselves.

### Part 5 - Question 1: Semantic Accuracy (422 vs. 404)
When a client submits a sensor registration with a roomId that doesn't exist, my API returns an HTTP 422 Unprocessable Entity instead of a 404. This choice is based on semantic accuracy, as a 404 should signify that the endpoint itself was not found. The 422 status clearly indicates that the server understands the request and the syntax is correct, but the content of the payload is logically invalid, which provides the developer with a much more helpful and precise error message.

### Part 5 - Question 2: Observability and JAX-RS Filters
I implemented JAX-RS filters to centralize the logging of every request and response, which allows me to handle observability as a cross-cutting concern. By using a centralized filter instead of adding manual logging statements to every resource method, I ensured that the entire API has consistent audit coverage with zero code duplication. This keeps the business logic in my resource classes clean and focused while providing a reliable way to monitor the HTTP lifecycle across the entire system.

### Part 5 - Question 3: Security and Hiding Stack Traces
To protect the system against information leakage, I created a global exception mapper that intercepts all unhandled errors and returns a sanitized JSON response. This implementation ensures that internal Java stack traces are never exposed to the client, as they could provide an attacker with sensitive details about the application's internal structure and library versions. By returning a generic internal server error message while logging the full trace only on the server side, I have significantly improved the cybersecurity posture of the API.
