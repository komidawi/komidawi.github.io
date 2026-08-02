---
title: "[Interview] Microservices and distributed systems"
description: Interview questions and answers about microservices and distributed systems.
date: 2026-08-02 20:00:00 +0100
categories: [ interview ]
tags: [ microservices, integration, api ] # TAG names should always be lowercase
---

<!-- @formatter:off -->

> Note: This article contains mainly LLM generated content, but only such that has been human reviewed. 
> Nonetheless, it is still possible that something wasn't caught or simply I made a mistake. 
> If you see anything wrong or missing the point, please let me know in the comments.
{: .prompt-info }

> This article may contain some simplifications (maybe even oversimplifications).
> Treat it like a cheat sheet with important information, not an exhaustive research paper
> with all nuances and edge cases.
{: .prompt-warning }


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

