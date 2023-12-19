---
layout: page
title: Microservices
permalink: /microservices/
---

# Table of Contents

- [Table of Contents](#table-of-contents)
- [Chapter 1: What Are Microservices?](#chapter-1-what-are-microservices)
  - [Key concepts of Microservices](#key-concepts-of-microservices)
  - [Monolith](#monolith)
    - [Single-process monolith](#single-process-monolith)
    - [Modular monolith](#modular-monolith)
    - [Distributed monolith](#distributed-monolith)
    - [Monoliths are not bad by it's nature](#monoliths-are-not-bad-by-its-nature)
  - [Advantages of microservices](#advantages-of-microservices)
  - [Microservice pain points](#microservice-pain-points)
  - [Should I use Microservices?](#should-i-use-microservices)
    - [When rather not](#when-rather-not)
  - [When rather yes](#when-rather-yes)
- [Chapter 2: How to Model Microservices](#chapter-2-how-to-model-microservices)
  - [What makes a good Microservice boundary?](#what-makes-a-good-microservice-boundary)
  - [Types of coupling](#types-of-coupling)
    - [Domain Coupling](#domain-coupling)
    - [Temporal Coupling](#temporal-coupling)
    - [Pass-through coupling](#pass-through-coupling)
    - [Common coupling](#common-coupling)
    - [Content coupling](#content-coupling)
  - [DDD](#ddd)
    - [Aggregate](#aggregate)
    - [Bounded Context](#bounded-context)
- [Source](#source)

# Chapter 1: What Are Microservices?

## Key concepts of Microservices

1. **Independent deployability**
   - You should be able to make changes in a given microservice and deploy it to the users without having to make changes and deployments in other services.
2. **Modeled around a Business Domain**
   - Microservice architecture enables us to work within a given domain context and its boundaries.
3. **Owning their own state**
   - Microservices should make us avoid using shared databases.
   - Each of them should have its data encapsulated and interacted with by a defined API instead, similarly like we do in OOP.
4. **Size**
   - Projects can be kept within a size that allows to understand them sufficiently.
   - It's related to knowledge complexity, rather than sheer lines of code.
5. **Flexibility**
   - Microservices buy you options.
   - As an example it's easier to change a small project, than an enormous one.

## Monolith

We can look at monolith from the unit-of-deployment point of view.  
It will be than a single unit of deployment - **everything** must be deployed together.

### Single-process monolith

- The most common example
- All the code is packed and deployed as a single process
- In reality we see even more than one monolith tightly coupled which effectively makes it even bigger
- It's not inherently bad architecture, it's something great for the start

### Modular monolith

- Single process that consists of separate modules
- Work can be done of each of modules independently, but it still needs to be deployed together
- It can be a great choice - with decently defined boundaries, it can allow to work in parallel without high overhead of microservices
- Be aware in terms of database - it can be a point of heavy coupling which may be thwarting endeavors to split it into microservices in the future

### Distributed monolith

- Please don't.
- Highly coupled set of monoliths that require all of them to be deployed at once.

### Monoliths are not bad by its nature

- Simple deployment topology
- Simple DevOps
- Easy testing
- Convenient code reuse

## Advantages of microservices

1. **Technology heterogeneity**
   - We can use different technologies in each of them
   - It enables us to pick the right tools for given jobs
   - We can test new technologies without too much risk of breaking the whole system
2. **Robustness**
   - In monolith - when it's dead, we are dead
   - In microservices - a single failure doesn't break the whole system.  
     We can operate even when some parts are malfunctioning
3. **Scaling**
   - In monolith, when we scale, we scale everything at once
   - In microservices - we can scale more granularly, e.g. some features can be scaled more than others or put in different sets of hardware
4. **Ease of deployment**
   - Big projects deployment are big risks, we don't want to deploy them often
     - And it makes deployment even more risky, as more features pile up
   - Also one small change in a big project requires the whole big thing to be deployed
   - In microservices we can make many small changes very often, so deliver fast and in a safer manner
5. **Organizational alignment**
   - Instead of big teams with a lot of synchronization required, teams can be split into small units
   - Small units are more manageable and can work more independently
6. **Composability**
   - Microservices can be units for composing systems, e.g. one service can be part both for mobile and web app

## Microservice pain points

1. **Developer experience**
   - In big systems it may be even impossible to run the whole architecture on a single dev machine
2. **Technology overhead**
   - Overwhelming amount of tools is required to be configured, used and maintained in order to run microservices
   - Also things like e.g. data consistency, latency, service modeling (DDD) start to matter significantly
3. **Cost**
   - Much more resources like networks, load balancers, sidecars, paid software
   - A lot of DevOps and Dev time
4. **Reporting**
   - Data is scattered around many systems, so it's not so easy to e.g. present them to the stakeholders
5. **Monitoring and troubleshooting**
   - Monitoring a single system is easy.  
     Microservices require to monitor both each one of them, and also the system as a whole
   - Debugging distributed systems is much harder than a single process
6. **Security**
   - In monolith, data flows within a single unit.  
     In microservices - a lot of data fly all over the networks
7. **Testing**
   - In monolith it's easy, especially in terms of e2e testing
   - In microservices integration is hard, let alone e2e
8. **Latency**
   - Things previously done in a single processor now is scattered around many, many machines. Interoperation involves latencies, as well as other overhead costs like (de)serialization
   - Operations taking millis can now take many seconds
9. **Data consistency**
   - We don't have single database now
   - Distributed transactions mostly won't work
   - We need to change our way of thinking into different direction and forget about the well-known world

## Should I use Microservices?

### When rather not

1. Brand-new products or startups
   - Domain is constantly changing and most likely will be changing for some time
   - It will require to e.g. reshape boundaries all the time
   - You may end up with a totally different product than initially assumed  
      Microservices would a unnecessary cost and premature optimization (in this case even: anti-optimization)
   - Startups don't usually have much people to have to additionally cope with infrastructure
   - It's better to migrate to microservices when you understand the constraints of the current architecture and possible tradeoffs

### When rather yes

1. When you want to allow more developers work on the same system without conflicts
2. SaaS products
   - Expected to operate 24/7, so rolling out changes should be safe and smooth
   - Traffic can vary, so it should scale well
3. Products delivered via many delivery channels

# Chapter 2: How to Model Microservices

## What makes a good Microservice boundary?

1. Information hiding
2. Cohesion (strong)
   - _The code that changes together, stays together_
3. Coupling (loose)

## Types of coupling

### Domain Coupling

One microservice needs to interact with another one.

- For example: service #1 uses functionality that is provided by service #2
- Although it's unavoidable, we still want to make it as minimal as possible
  - When you see a service that is coupled with many, many others - it may mean it does too much
  - It may also mean that logic became centralized

### Temporal Coupling

One service needs another service be operational exactly at the same time.

- You can avoid it by asynchronous calls

### Pass-through coupling

Service #1 passes data to service #2, only because service #3, further in the chain, will need it.

- Example: a class defined in service #3 API
- One of the most problematic couplings
- It may require the calling service to know not only that the second one calls the third one, but also know what data is needed for this call
- Problem is that change to the latter microservice will require multiple changes up the stream

Ways to fix it:

- Bypass the intermediary
  - Just call the service #3 directly
  - However be aware that you increase domain coupling now
  - Sometimes it may not be great, as it may increase logic in caller service, e.g. when it will have to make more calls
- Hide the fact it's a transitive dependency
  - Instead of sending `com.third.microservice.Data`,  
    send this data in a flat structure as a part of contract with the service #2
    - Now if anything changes internally, it may require just changes in service #2, without changes in service #1
    - But the downside is it may still require all of them to change.
      - However not necessarily at the same time, so it's still a value
- Make the service #2 treat this dependency data as a blob and don't process it, just pass along
  - This way we may avoid changes at least in the service #2

### Common coupling

More than one service makes use of the same data

- Example: shared database
- The main problem is change in data structure may impact many, if not all, clients at once
- Shared data, by its nature, is hard to change due to quantity of clients
- It's somehow less problematic when data
  - is read-only
  - is static
- Big problems happen when multiple services access data often in rw fashion
- As example, when many services update a status of an order, they may break each other
  - Imagine case, when statuses can change only in a particular order (finite state machine)
  - In this scenario either we have logic leaked into many places, have logic in a database or don't have any validation at all
- Another problem can be locking and synchronization issues
- Common coupling may be sometimes somehow ok, but pay attention that we will be limited in terms of changing shared data
  - It also may speak about a low cohesion level in our system

Possible fixes

- Make only one service manage state and become the source of truth
  - Others will need to call this one in order to make changes to db
    - By the way, treat requests as requests, not orders - validation should or not allow to make given change

### Content coupling

The worst of the worst:  
When one microservice reaches another one and changes its internal details

- Example: when one service makes changes in other service's database
- It's different from Common coupling as there you are aware that data is shared, but here you just tinker with one's private internals
- If we have a fine logic in calling microservice (duplicated logic, by the way), you may somehow survive
- But when there are no checks, nothing prevents caller to just make data totally broken
- And conversely, changes in the internal schema may break the caller
- It's a severe leak

## DDD

### Aggregate

- Think about it as of a representation of a real domain concept, like Order, Invoice, Stock item.
- Aggregate typically have its own lifecycle
- Treat them as self-contained units
- Code that handles state transitions should be grouped together (along with the state)
- Aggregate can be perceived as something that has: state, identity, lifecycle

### Bounded Context

- A collection of associated Aggregates, with an explicit interface to the outer world

---

# Source

[Building Microservices: Designing Fine-Grained Systems (2nd Edition)](https://www.oreilly.com/library/view/building-microservices-2nd/9781492034018/)  
by Sam Newman  
August 2021  
O'Reilly Media, Inc.
