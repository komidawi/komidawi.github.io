---
title: "Draft: [Interview] REST API, API design, integrations "
description: Interview questions and answers about REST API, API design and integrations..
date: 2026-08-04 21:00:00 +0100
categories: [ interview ]
tags: [ api, restapi, integration] # TAG names should always be lowercase
---

<!-- @formatter:off -->

> This article is an unreviewed draft and may contain incorrect information.
{: .prompt-danger }

## Basics

### HTTP Status Codes

#### 1xx Informational

- `100 Continue` → Client may continue sending the request body because the server indicated it is ready to receive it.
- `101 Switching Protocols` → Server agrees to switch to a different protocol requested by the client, such as `WebSocket`.
- `102 Processing` → Server has accepted the request and is still processing it, with no final response yet.
- `103 Early Hints` → Server sends preliminary headers so the client can start loading resources before the final response arrives.

#### 2xx Success

- `200 OK` → Request succeeded and the server returned the response.
- `201 Created` → Request succeeded and created a new resource.
- `202 Accepted` → Request was accepted for processing, but the processing has not finished yet.
- `203 Non-Authoritative Information` → Request succeeded, but returned metadata was modified or came from a non-origin source.
- `204 No Content` → Request succeeded and there is no response body to return.
- `205 Reset Content` → Request succeeded and the client should reset the current document view, such as clearing a form.
- `206 Partial Content` → Server is returning only the part of the resource requested via a range request.
- `207 Multi-Status` → Response contains separate status results for multiple resources or operations, mainly in `WebDAV`.
- `208 Already Reported` → Response indicates that members of a `DAV` binding were already listed earlier in the same response.
- `226 IM Used` → Server fulfilled the request using instance manipulations such as delta encoding.

#### 3xx Redirection

- `300 Multiple Choices` → Resource has multiple possible representations or endpoints.
- `301 Moved Permanently` → Resource has been assigned a new permanent URI, normally provided in the `Location` header; clients may redirect to it.
- `302 Found` → Resource is temporarily available at a different URI.
- `303 See Other` → Server directs the client to another URI, usually in `Location`, where it may perform `GET` or `HEAD` to obtain a representation.
- `304 Not Modified` → Cached resource is still valid, so the client should reuse its stored copy.
- `305 Use Proxy` → Requested resource must be accessed through a proxy, though this status is deprecated.
- `306 (Unused)` → Status code is reserved and no longer used.
- `307 Temporary Redirect` → Resource is temporarily at another URI, and the client must preserve the original HTTP method.
- `308 Permanent Redirect` → Resource has permanently moved to another URI, and the client must preserve the original HTTP method.

#### 4xx Client Errors

- `400 Bad Request` → Server cannot process the request because it is malformed or invalid.
- `401 Unauthorized` → Request requires authentication or the provided authentication failed.
- `402 Payment Required` → Status is reserved for future use, originally intended for payment-related flows.
- `403 Forbidden` → Server understood the request but refuses to authorize it.
- `404 Not Found` → Requested resource does not exist on the server (or server is unwilling to disclose that one exists).
- `405 Method Not Allowed` → HTTP method used is not allowed for the target resource, respond with `Allow` header listing supported methods.
- `406 Not Acceptable` → Server cannot produce a response matching the client's `Accept` headers.
- `407 Proxy Authentication Required` → Client must authenticate with the proxy before the request can proceed.
- `408 Request Timeout` → Server timed out while waiting for the client's request.
- `409 Conflict` → Request conflicts with the current state of the target resource.
- `410 Gone` → Resource was intentionally removed and is no longer available.
- `411 Length Required` → Server requires a `Content-Length` header and did not receive one.
- `412 Precondition Failed` → One or more request preconditions in headers evaluated to false.
- `413 Payload Too Large` → Request body is larger than the server is willing or able to process.
- `414 URI Too Long` → Request URI is longer than the server is willing to interpret.
- `415 Unsupported Media Type` → Server does not support the request payload format.
- `416 Range Not Satisfiable` → Requested byte range cannot be fulfilled for the target resource.
- `417 Expectation Failed` → Server cannot meet the requirements of the `Expect` header.
- `418 I'm a Teapot` → Joke status code indicating the server refuses to brew coffee because it is a teapot.
- `421 Misdirected Request` → Request was sent to a server that cannot produce a response for the target resource.
- `422 Unprocessable Entity` → Server understood the request syntax but could not process its semantic content.
- `423 Locked` → Target resource is locked, mainly in `WebDAV`.
- `424 Failed Dependency` → The method could not be performed because it depended on another action that failed (like a request).
- `425 Too Early` → Server is unwilling to process a request that might be replayed.
- `426 Upgrade Required` → Client must switch to a different protocol to complete the request.
- `428 Precondition Required` → Server requires the request to be conditional, often to prevent lost updates.
- `429 Too Many Requests` → Client sent too many requests in a given amount of time.
- `431 Request Header Fields Too Large` → Server refuses the request because header fields are too large.
- `451 Unavailable For Legal Reasons` → Resource is unavailable due to legal restrictions or takedown demands.

#### 5xx Server Errors

- `500 Internal Server Error` → Server encountered an unexpected condition and could not complete the request.
- `501 Not Implemented` → Server does not support the functionality required to fulfill the request.
- `502 Bad Gateway` → Server acting as a gateway received an invalid response from an upstream server.
- `503 Service Unavailable` → Server is temporarily unable to handle the request, often due to overload or maintenance.
- `504 Gateway Timeout` → Server acting as a gateway did not receive a timely response from an upstream server.
- `505 HTTP Version Not Supported` → Server does not support the HTTP version used in the request.
- `506 Variant Also Negotiates` → Server found a configuration problem in content negotiation that caused a circular reference.
- `507 Insufficient Storage` → Server cannot store the representation needed to complete the request.
- `508 Loop Detected` → Server detected an infinite loop while processing the request, mainly in `WebDAV`.
- `510 Not Extended` → (Obsolete) Historically indicated that the request needed a mandatory HTTP extension unsupported or not declared by the client.
- `511 Network Authentication Required` → Client must authenticate to gain network access, such as through a captive portal.

### REST API Good Practices

- Use **resource-oriented** URLs → **nouns**, not verbs
  - `/users/123/orders`, not `/getUserOrders`.
- Keep HTTP methods semantic
  - `GET` for read, `POST` for create, `PUT/PATCH` for update, `DELETE` for remove.
- Return correct status codes 
  - e.g.: `200`, `201`, `204`, `400`, `401`, `403`, `404`, `409`, `422`, `500`.
- Design stateless endpoints 
  - every request should contain everything needed to process it.
- Use consistent request/response schemas
  - with predictable field names, types, and nesting.
- Support pagination, filtering, sorting, and searching for list endpoints.
- Validate input strictly
  - reject bad data early with clear error messages.
- Make error responses machine-readable and stable.
- Version APIs carefully when breaking changes are unavoidable.
- Secure by default
  - authentication, authorization, rate limiting, HTTPS, least privilege.
- Document with OpenAPI/Swagger
  - so consumers can integrate safely.
- Make idempotency explicit for operations that may be retried.
- Think about observability
  - logs, metrics, trace IDs, and auditability.

#### Practical checklist

- **Resources** are named as nouns.
- **HTTP method** matches intent.
- **Status codes** are accurate.
- **Error format** is consistent.
- **Pagination** is defined.
- **Auth** is required where needed.
- **Input** is validated server-side.
- **Backward compatibility** is preserved.
- **Contracts** are documented.
- **Timeouts and retries** are considered.

#### Common Pitfalls

- Using `POST` for everything.
- Returning `200` for failures.
- Leaking internal stack traces to clients.
- Changing schema in a breaking manner without versioning.
- Creating endpoints that do too many things.
- Ignoring idempotency for retries.
- Missing pagination on large collections.

## Authentication and Authorization

### JWT

- `JWT` (JSON Web Token) is a compact, signed token format used to transmit claims (like user identity and roles) 
between parties, most often for stateless authentication in APIs and Web Apps.
- `JWT` is a string with three `base64url` dot-separated parts: `header.payload.signature`
- It is digitally signed (`HMAC` or `RSA`/`ECDSA`), so you can verify integrity and authenticity without
storing session state server-side.
- It typically represents "who the user is" and "what is allowed to do" and is sent on each request
  (e.g. in `Authorization: Bearer <token>`)

**Structure:**

- `header`: token type (`typ: "JWT"`) and signing algorithm (`alg: "HS256"`, `RS256`, etc.).
- `payload`: claims like `sub` (user id), `exp` (expiry), `iat` (issued-at), `iss` (issuer), custom fields.
- `signature`: `HMAC` or public-key signature over `base64url(header) + "." + base64url(payload)` using the private key

## API Design

### Idempotency

Idempotency mean that repeating the same logical request has the same intended server-side effect as executing it once.

For example, a payment request retried 3 times must create **one** charge, not three.

HTTP defines following idempotent methods:
- `GET`
- `HEAD`
- `PUT`
- `DELETE`
- `OPTIONS`
- `TRACE`

It means that `POST` and `PATCH` are not guaranteed to be idempotent.

#### Standard

Idempotency does not mean the same response for each request. 
For example `DELETE /resource/:id` may return `204 No Content` at first, then `404 Not Found` later, 
while still being idempotent because the resource remains deleted.

For risky `POST` actions like payment, bookings, provisioning, sending an external command use an `Idempotency-Key` header.
The client generates one key **once per logical operation** and reuses it only for retires of that operation.

`Idempotency-Key: 1f5e70b9-6866-4ee7-8af2-dd84e5edbe69`

Server should store request fingerprint with information like `clientId`, `endpoint`, `idempotency key` 
and verify them before taking action.

### Proxy and Reverse Proxy

#### Basic

**Forward Proxy (Client Proxy)** - sits between Client and the Internet to act on behalf of Client
(privacy, filtering, caching).  

**Reverse Proxy (Server Proxy)** - sits in front of one or more servers and handles incoming Client requests
(load-balancing, SSL termination, caching, WAF).

#### Standard

Forward Proxy is used for:
- privacy/anonimity
- corporate web filtering
- outbound caching
- bypassing geo-restrictions

Reverse Proxy is used for:
- load-balancing
- TLS termination
- caching static responses
- routing requests
- protecting origins (WAF, IP Obfuscation)
