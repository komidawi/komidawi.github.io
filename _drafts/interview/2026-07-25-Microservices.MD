---
title: "Draft: [Interview] Microservices and distributed systems"
description: Interview questions and answers about microservices and distributed systems.
date: 2026-07-25 20:00:00 +0100
categories: [ interview, engineering ]
tags: [ microservices, integration, api, engineering, interview ] # TAG names should always be lowercase
---

<!-- @formatter:off -->

> This article is an unreviewed draft and may contain incorrect information.
{: .prompt-danger }

### Resilience Design Patterns

#### Basic

Resilience patterns make a distributed system degrade predictably instead of failing catastrophically. 

In a Spring Boot ecosystem, the practical baseline is: 
- explicit timeouts
- bounded retries with backoff
- circuit breakers
- bulkheads
- deliberate fallbacks

#### Standard

A remote call can **fail, slow down, return partial data**, or **recover after a moment**.\
Resilience help to make sure that a one unhealthy dependency 
won't exhaust your threads, connections, and request capacity or in worst scenario cause a cascade.

| Pattern           | Use it for                                               | Main pitfall                                                |
|-------------------|----------------------------------------------------------|-------------------------------------------------------------|
| `Timeout`         | Bound how long a remote call may consume a resource      | HTTP client defaults may be missing or much too high        |
| `Retry`           | Transient failures such as brief network errors or `503` | Retrying non-idempotent writes creates duplicates           |
| `Circuit breaker` | Stop calling a dependency that is repeatedly failing     | Treating it as a substitute for a timeout                   |
| `Bulkhead`        | Isolate capacity between dependencies or workloads       | One shared executor/connection pool for everything          |
| `Fallback`        | Return a safe degraded result                            | Hiding an error or returning misleading/stale critical data |
| `Rate limiting`   | Protect the service under overload                       | Letting queues grow without bound                           |
| `Cache`           | Reduce dependency load and retain some read availability | Serving data whose freshness requirements are strict        |

