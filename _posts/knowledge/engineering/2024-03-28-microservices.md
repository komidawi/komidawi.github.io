---
title: Microservices
date: 2024-03-28 00:00:00 +0100
categories: [ engineering ]
tags: [ microservices ] # TAG names should always be lowercase
---

## Table of Contents

- [Table of Contents](#table-of-contents)
- [Chapter 1: What Are Microservices?](#chapter-1-what-are-microservices)
    - [Key concepts of Microservices](#key-concepts-of-microservices)
    - [Monolith](#monolith)
        - [Single-process monolith](#single-process-monolith)
        - [Modular monolith](#modular-monolith)
        - [Distributed monolith](#distributed-monolith)
        - [Monoliths are not bad by its nature](#monoliths-are-not-bad-by-its-nature)
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
- [Chapter 3: Splitting the Monolith](#chapter-3-splitting-the-monolith)
    - [What to split first?](#what-to-split-first)
    - [Decomposition by layer](#decomposition-by-layer)
        - [Code First Approach](#code-first-approach)
        - [Data First Approach](#data-first-approach)
    - [Useful Decompositional Patterns](#useful-decompositional-patterns)
        - [Strangler Fig](#strangler-fig)
        - [Parallel Run](#parallel-run)
        - [Feature Toggle](#feature-toggle)
    - [Data Decomposition Concerns](#data-decomposition-concerns)
        - [Performance](#performance)
        - [Data Integrity](#data-integrity)
        - [Transactions](#transactions)
        - [Tooling](#tooling)
        - [Appendix: Reporting DB](#appendix-reporting-db)
    - [More information](#more-information)
- [Chapter 4: Microservice Communication Styles](#chapter-4-microservice-communication-styles)
    - [Synchronous Blocking](#synchronous-blocking)
        - [Pros](#pros)
        - [Cons](#cons)
        - [Where to use?](#where-to-use)
    - [Asynchronous Non-blocking](#asynchronous-non-blocking)
        - [Pros](#pros-1)
        - [Cons](#cons-1)
        - [Where to use?](#where-to-use-1)
    - [Pattern: Communication Through Common Data](#pattern-communication-through-common-data)
        - [Pros](#pros-2)
        - [Cons](#cons-2)
        - [Where to use?](#where-to-use-2)
    - [Pattern: Request-Response Communication](#pattern-request-response-communication)
        - [Where to use?](#where-to-use-3)
    - [Pattern: Event-Driven Communication](#pattern-event-driven-communication)
        - [Cons](#cons-3)
    - [Event design](#event-design)
        - [Just ID](#just-id)
            - [Cons](#cons-4)
        - [Fully detailed event](#fully-detailed-event)
            - [Pros](#pros-3)
            - [Cons](#cons-5)
        - [Hybrid](#hybrid)
- [Part II: Implementation](#part-ii-implementation)
- [Chapter 5: Implementing Microservice Communication](#chapter-5-implementing-microservice-communication)
    - [Technology choices](#technology-choices)
        - [RPC](#rpc)
            - [Pros](#pros-4)
            - [Cons](#cons-6)
            - [When to use it](#when-to-use-it)
            - [Tips](#tips)
        - [REST](#rest)
            - [Pros](#pros-5)
            - [Cons](#cons-7)
            - [When to use it](#when-to-use-it-1)
        - [GraphQL](#graphql)
            - [Pros](#pros-6)
            - [Cons](#cons-8)
            - [When to use it](#when-to-use-it-2)
        - [Message Brokers](#message-brokers)
    - [Schemas](#schemas)
        - [Why to use schemas](#why-to-use-schemas)
    - [Handling Change Between Microservices](#handling-change-between-microservices)
        - [Avoiding Breaking Changes 🔨](#avoiding-breaking-changes-)
        - [Managing Breaking Changes 🔨](#managing-breaking-changes-)
        - [Social Contract 🔨](#social-contract-)
        - [Tracking usage 🔨](#tracking-usage-)
        - [Extreme measures 🔨](#extreme-measures-)
    - [DRY and the Perils of Code Reuse in a Microservice World](#dry-and-the-perils-of-code-reuse-in-a-microservice-world)
        - [Sharing code via libraries](#sharing-code-via-libraries)
            - [In general](#in-general)
            - [Client libraries](#client-libraries)
    - [Service discovery](#service-discovery)
        - [DNS](#dns)
        - [Dynamic Service Registers](#dynamic-service-registers)
            - [Zookeper](#zookeper)
            - [Consul](#consul)
            - [etcd and Kubernetes](#etcd-and-kubernetes)
    - [Service Meshes and API Gateways](#service-meshes-and-api-gateways)
        - [API Gateways](#api-gateways)
            - [Where to use](#where-to-use-4)
            - [What to avoid](#what-to-avoid)
        - [Service Meshes](#service-meshes)
            - [How do they work](#how-do-they-work)
            - [Aren’t service meshes smart pipes?](#arent-service-meshes-smart-pipes)
            - [Do you need one?](#do-you-need-one)
    - [Documenting Services](#documenting-services)
        - [Explicit Schemas](#explicit-schemas)
        - [The Self-Describing System](#the-self-describing-system)
- [Chapter 6: Workflow](#chapter-6-workflow)
    - [Transactions](#transactions-1)
        - [Database Transactions](#database-transactions)
        - [Distributed Transactions](#distributed-transactions)
    - [Sagas](#sagas)
        - [Saga Failure Modes](#saga-failure-modes)
            - [Backward Recovery](#backward-recovery)
            - [Forward Recovery](#forward-recovery)
        - [Saga Implementations](#saga-implementations)
            - [Orchestrated Sagas](#orchestrated-sagas)
                - [Pros](#pros-7)
                - [Cons](#cons-9)
            - [Choreographed Sagas](#choreographed-sagas)
                - [Pros](#pros-8)
                - [Cons](#cons-10)
            - [Which one to use?](#which-one-to-use)
- [Chapter 7: Build](#chapter-7-build)
    - [Are you really doing CI?](#are-you-really-doing-ci)
    - [Branching models](#branching-models)
    - [Continuous Delivery vs Continuous Deployment](#continuous-delivery-vs-continuous-deployment)
    - [Artifact creation](#artifact-creation)
    - [Mapping Source Code and Builds to Microservices](#mapping-source-code-and-builds-to-microservices)
        - [Pattern: Multirepo](#pattern-multirepo)
            - [Case: When features require changes in multiple repositories](#case-when-features-require-changes-in-multiple-repositories)
        - [Pattern: Monorepo](#pattern-monorepo)
            - [Pros](#pros-9)
            - [Cons](#cons-11)
            - [Semi-monorepo](#semi-monorepo)
            - [Which approach should I use?](#which-approach-should-i-use)
- [Chapter 8: Deployment](#chapter-8-deployment)
    - [Principles of Microservice Deployment](#principles-of-microservice-deployment)
        - [_Financial Times_ case of zero-downtime deployment](#financial-times-case-of-zero-downtime-deployment)
    - [Deployment options](#deployment-options)
    - [Kubernetes and Container Orchestration](#kubernetes-and-container-orchestration)
        - [Knative](#knative)
    - [Progressive Delivery](#progressive-delivery)
        - [Deploy vs Release](#deploy-vs-release)
        - [Progressive Delivery techniques 🔨](#progressive-delivery-techniques-)
            - [Blue/Green Deployment](#bluegreen-deployment)
            - [Feature Toggles (Feature Flags)](#feature-toggles-feature-flags)
            - [Canary Release](#canary-release)
            - [Parallel Run](#parallel-run-1)
- [Chapter 9: Testing](#chapter-9-testing)
    - [In short](#in-short)
    - [Contract Tests and CDCs (Consumer-Driven Contracts) 🔨](#contract-tests-and-cdcs-consumer-driven-contracts-)
        - [Pact 🔨](#pact-)
        - [Spring Cloud Contract 🔨](#spring-cloud-contract-)
    - [Cross-functional testing](#cross-functional-testing)
        - [Performance tests 🔨](#performance-tests-)
- [Chapter 10: From Monitoring to Observability](#chapter-10-from-monitoring-to-observability)
    - [Observability](#observability)
        - [Log Aggregation](#log-aggregation)
            - [First and foremost 🔨](#first-and-foremost-)
            - [Common format 🔨](#common-format-)
            - [Correlation IDs 🔨](#correlation-ids-)
            - [Timing](#timing)
        - [Metrics aggregation](#metrics-aggregation)
            - [Cardinality 🔨](#cardinality-)
        - [Distributed Tracing](#distributed-tracing)
        - [Are you doing OK?](#are-you-doing-ok)
            - [SLA](#sla)
            - [SLO](#slo)
            - [SLI](#sli)
            - [Error Budgets 🔨](#error-budgets-)
        - [Alerting](#alerting)
            - [Good alerts characteristics 📝](#good-alerts-characteristics-)
        - [Semantic Monitoring 📝](#semantic-monitoring-)
            - [Real user monitoring](#real-user-monitoring)
        - [Testing in Production 🔨](#testing-in-production-)
            - [Synthetic Transactions 🔨](#synthetic-transactions-)
            - [A/B Testing 🔨](#ab-testing-)
            - [Canary release 🔨](#canary-release-)
            - [Parallel run 🔨](#parallel-run-)
            - [Smoke tests 🔨](#smoke-tests-)
            - [Synthetic transaction 🔨](#synthetic-transaction-)
            - [Chaos engineering 🔨](#chaos-engineering-)
    - [Standardization 📝](#standardization-)
    - [Selecting Tools](#selecting-tools)
        - [Democratic](#democratic)
        - [Easy to integrate](#easy-to-integrate)
        - [Provide Context 📝](#provide-context-)
        - [Able to scale](#able-to-scale)
    - [Getting Started 🔨](#getting-started-)
- [Chapter 11: Security](#chapter-11-security)
    - [The 5 functions of Cybersecurity](#the-5-functions-of-cybersecurity)
        - [Identify](#identify)
        - [Protect](#protect)
        - [Detect](#detect)
        - [Respond](#respond)
        - [Recover](#recover)
    - [Foundations of Application Security](#foundations-of-application-security)
        - [Credentials](#credentials)
            - [User Credentials](#user-credentials)
            - [Secrets](#secrets)
                - [Secret management](#secret-management)
                - [Secret rotation](#secret-rotation)
                - [Key leaking 🔨](#key-leaking-)
                - [Limiting scope - Principle of Least Privilege](#limiting-scope---principle-of-least-privilege)
                - [Secret management 🔨](#secret-management-)
        - [Patching 🔨](#patching-)
        - [Backups 📝](#backups-)
        - [Rebuild](#rebuild)
    - [Implicit Trust vs Zero Trust](#implicit-trust-vs-zero-trust)
        - [Implicit Trust](#implicit-trust)
        - [Zero Trust](#zero-trust)
        - [Spectrum Trust](#spectrum-trust)
    - [Securing Data](#securing-data)
        - [Data in transit](#data-in-transit)
            - [Concerns about data in transit](#concerns-about-data-in-transit)
        - [Data at rest](#data-at-rest)
            - [Be frugal](#be-frugal)
    - [Confused Deputy Problem](#confused-deputy-problem)
        - [Centralized, Upstream Authorization](#centralized-upstream-authorization)
        - [Decentralizing Authorization](#decentralizing-authorization)
            - [JWT](#jwt)
                - [Using Tokens 📝](#using-tokens-)
                - [Challenges 🔨](#challenges-)
- [Chapter 12: Resiliency](#chapter-12-resiliency)
    - [What is Resiliency?](#what-is-resiliency)
        - [Robustness](#robustness)
        - [Rebound](#rebound)
        - [Graceful Extensibility](#graceful-extensibility)
        - [Sustained Adaptability](#sustained-adaptability)
    - [Degrading Functionality](#degrading-functionality)
    - [Stability Patterns](#stability-patterns)
        - [Time-Outs 🔨](#time-outs-)
        - [Retries 🔨](#retries-)
        - [Bulkheads 🔨](#bulkheads-)
        - [Circuit Breakers](#circuit-breakers)
        - [Isolation](#isolation)
        - [Redundancy](#redundancy)
        - [Middleware](#middleware)
        - [Idempotency](#idempotency)
    - [CAP Theorem](#cap-theorem)
        - [Sacrificing Consistency (AP)](#sacrificing-consistency-ap)
        - [Sacrificing Availability (CP)](#sacrificing-availability-cp)
        - [Sacrificing Partition Tolerance (CA)](#sacrificing-partition-tolerance-ca)
        - [It's not All or Nothing 📝](#its-not-all-or-nothing-)
    - [Chaos Engineering 📝](#chaos-engineering--1)
        - [Game Days 📝](#game-days-)
            - [Anecdote of Bob's Team 📝](#anecdote-of-bobs-team-)
        - [Production Experiments 📝](#production-experiments-)
            - [Netflix and its Simian Army 🔨](#netflix-and-its-simian-army-)
            - [Tools 🔨](#tools-)
- [Source](#source)

## Chapter 1: What Are Microservices?

### Key concepts of Microservices

1. **Independent deployability**
    - You should be able to make changes in a given microservice and deploy it to the users without having to make
      changes and deployments in other services.
2. **Modeled around a Business Domain**
    - Microservice architecture enables us to work within a given domain context and its boundaries.
3. **Owning their own state**
    - Microservices should make us avoid using shared databases.
    - Each of them should have its data encapsulated and interacted with by a defined API instead, similarly like we do
      in OOP.
4. **Size**
    - Projects can be kept within a size that allows to understand them sufficiently.
    - It's related to knowledge complexity, rather than sheer lines of code.
5. **Flexibility**
    - Microservices buy you options.
    - As an example it's easier to change a small project, than an enormous one.

### Monolith

We can look at monolith from the unit-of-deployment point of view.  
It will be than a single unit of deployment - **everything** must be deployed together.

#### Single-process monolith

- The most common example
- All the code is packed and deployed as a single process
- In reality we see even more than one monolith tightly coupled which effectively makes it even bigger
- It's not inherently bad architecture, it's something great for the start

#### Modular monolith

- Single process that consists of separate modules
- Work can be done of each of modules independently, but it still needs to be deployed together
- It can be a great choice - with decently defined boundaries, it can allow to work in parallel without high overhead of
  microservices
- Be aware in terms of database - it can be a point of heavy coupling which may be thwarting endeavors to split it into
  microservices in the future

#### Distributed monolith

- Please don't.
- Highly coupled set of monoliths that require all of them to be deployed at once.

#### Monoliths are not bad by its nature

- Simple deployment topology
- Simple DevOps
- Easy testing
- Convenient code reuse

### Advantages of microservices

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
    - In microservices - we can scale more granularly, e.g. some features can be scaled more than others or put in
      different sets of hardware
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

### Microservice pain points

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
    - Things previously done in a single processor now is scattered around many, many machines. Interoperation involves
      latencies, as well as other overhead costs like (de)serialization
    - Operations taking millis can now take many seconds
9. **Data consistency**
    - We don't have single database now
    - Distributed transactions mostly won't work
    - We need to change our way of thinking into different direction and forget about the well-known world

### Should I use Microservices?

#### When rather not

1. Brand-new products or startups
    - Domain is constantly changing and most likely will be changing for some time
    - It will require to e.g. reshape boundaries all the time
    - You may end up with a totally different product than initially assumed  
      Microservices would a unnecessary cost and premature optimization (in this case even: anti-optimization)
    - Startups don't usually have much people to have to additionally cope with infrastructure
    - It's better to migrate to microservices when you understand the constraints of the current architecture and
      possible tradeoffs

#### When rather yes

1. When you want to allow more developers work on the same system without conflicts
2. SaaS products
    - Expected to operate 24/7, so rolling out changes should be safe and smooth
    - Traffic can vary, so it should scale well
3. Products delivered via many delivery channels

## Chapter 2: How to Model Microservices

### What makes a good Microservice boundary?

1. Information hiding
2. Cohesion (strong)
    - _The code that changes together, stays together_
3. Coupling (loose)

### Types of coupling

#### Domain Coupling

One microservice needs to interact with another one.

- For example: service #1 uses functionality that is provided by service #2
- Although it's unavoidable, we still want to make it as minimal as possible
    - When you see a service that is coupled with many, many others - it may mean it does too much
    - It may also mean that logic became centralized

#### Temporal Coupling

One service needs another service be operational exactly at the same time.

- You can avoid it by asynchronous calls

#### Pass-through coupling

Service #1 passes data to service #2, only because service #3, further in the chain, will need it.

- Example: a class defined in service #3 API
- One of the most problematic couplings
- It may require the calling service to know not only that the second one calls the third one, but also know what data
  is needed for this call
- Problem is that change to the latter microservice will require multiple changes up the stream

Ways to fix it:

- Bypass the intermediary
    - Just call the service #3 directly
    - However be aware that you increase domain coupling now
    - Sometimes it may not be great, as it may increase logic in caller service, e.g. when it will have to make more
      calls
- Hide the fact it's a transitive dependency
    - Instead of sending `com.third.microservice.Data`,  
      send this data in a flat structure as a part of contract with the service #2
        - Now if anything changes internally, it may require just changes in service #2, without changes in service #1
        - But the downside is it may still require all of them to change.
            - However not necessarily at the same time, so it's still a value
- Make the service #2 treat this dependency data as a blob and don't process it, just pass along
    - This way we may avoid changes at least in the service #2

#### Common coupling

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
    - In this scenario either we have logic leaked into many places, have logic in a database or don't have any
      validation at all
- Another problem can be locking and synchronization issues
- Common coupling may be sometimes somehow ok, but pay attention that we will be limited in terms of changing shared
  data
    - It also may speak about a low cohesion level in our system

Possible fixes

- Make only one service manage state and become the source of truth
    - Others will need to call this one in order to make changes to db
        - By the way, treat requests as requests, not orders - validation should or not allow to make given change

#### Content coupling

The worst of the worst:  
When one microservice reaches another one and changes its internal details

- Example: when one service makes changes in other service's database
- It's different from Common coupling as there you are aware that data is shared, but here you just tinker with one's
  private internals
- If we have a fine logic in calling microservice (duplicated logic, by the way), you may somehow survive
- But when there are no checks, nothing prevents caller to just make data totally broken
- And conversely, changes in the internal schema may break the caller
- It's a severe leak

### DDD

#### Aggregate

- Think about it as of a representation of a real domain concept, like Order, Invoice, Stock item.
- Aggregate typically have its own lifecycle
- Treat them as self-contained units
- Code that handles state transitions should be grouped together (along with the state)
- Aggregate can be perceived as something that has: state, identity, lifecycle

#### Bounded Context

- A collection of associated Aggregates, with an explicit interface to the outer world

## Chapter 3: Splitting the Monolith

### What to split first?

It depends on the main driver of this change.

App needs to be scaled?

- Look at features that limits maximum load a project can handle

Improve time-to-market?

- Detect areas which change most often and determine if they can be extracted as microservices (you can use CodeScene as
  a helpful tool)

In general, it's a good idea to start splitting from the easiest areas, to gain knowledge, experience and work in an
iterative way.  
By the way when it turns out to be too hard, it may imply splitting would be much harder than previously thought.

### Decomposition by layer

Don't neglect decomposition of user interface.

- Although it may not be perceived as something relevant for microservices, it wouldn't be smart to ignore it.
- Sometimes the biggest gains would come from, indeed, UI.

#### Code First Approach

- It seems to be easier
- It may give some short-term benefit
- If this approach fails, it's not too late
    - (in contradiction to DB first approach)
- However, you must be aware, that you will eventually have to move database
    - So make some feasibility analysis of such extraction upfront not to waste resources on mission impossible

#### Data First Approach

- It's helpful when it's uncertain if data would be possible to extract
    - So you will be certain after doing it
- The advantage is you reduce risk by doing the harder part at first

### Useful Decompositional Patterns

#### Strangler Fig

- Add layer in front of service
- Make this layer forward traffic into old and new service(s)
- Extract features and setup traffic
- Do this step by step until everything is migrated

#### Parallel Run

- Run two apps - the old one and the new one
- Make one working for real, and the second one in dry-run mode
- Compare results between them

#### Feature Toggle

- Mind this patten, as it can be helpful

### Data Decomposition Concerns

#### Performance

- Relational DBs are perfect in joins between tables
    - It's done in a rapid manner
    - But microservices with their distributed nature will require _joins_ across DTOs transported through the network,
      what will be much, much slower (and probably somehow less functional)

#### Data Integrity

- With one DB, it's easy do make data valid as everything happens inside just a one box
- When data lives in different DBs, nothing may stop us from making them invalid across the entire system
- You will have to cope with these _inconveniences_

#### Transactions

- With Relational DBs we have full transaction support
- Now we will have to deal with distributed environment with eventual consistency, at most
- Also shift your mindset from ACID into CAP, welcome to a totally new world

#### Tooling

- When refactoring code, we have very powerful IDEs
- When refactoring DB, we are highly limited in these terms
- In general, Flyway/Liquibase can be helpful

#### Appendix: Reporting DB

- There are some cases, when it's totally fine to have Shared DB or when using DB is better suited than REST API calls (
  like analytic SQL queries)
- In such case, we can create a dedicated extra DB designed for free access and make service push data from internal
  storage to this "reporting DB"
    - It would be fine to add it this gives an opportunity to create DB tailored especially for client's needs, instead
      of stretching main DB to these purposes
- Still, treat this DB as any other endpoint and don't break the contract in backward-incompatible way

### More information

- See "Monolith to Microservices" book for much more information about this topic

## Chapter 4: Microservice Communication Styles

### Synchronous Blocking

#### Pros

- Simple and well-known

#### Cons

- Temporal coupling
    - This coupling is not only between two services - it's indeed between two **instances** of microservices
    - Failure either of them may cause operation to fail (e.g. caller fails before getting response)
- Being blocked until response arrives, doing nothing all that time
- Prone to cascading failures

#### Where to use?

- Simple architectures
- (Very) short chains of calls

### Asynchronous Non-blocking

#### Pros

- Temporal decoupling
- Not blocking on long operations
    - (e.g. sending parcel can take days)

#### Cons

- High complexity

#### Where to use?

- Long-running processes
- Long call chains

### Pattern: Communication Through Common Data

- It can be e.g. a file placed on a given location or Shared Database
- Two common examples are Data Lakes and Data Warehouses (where files go in one direction)
    - Data Lake
        - Sources upload raw data in any convenient format, consumers have to know how to process it
    - Data Warehouse
        - Warehouse is structured data store, so uploader must adhere to contract

#### Pros

- Simple
- High interoperability between different systems
- Works fine for high volumes of data

#### Cons

- Bad for low-latency needs as you have to provide mechanism for consumers to know about new data like polling or cron
  job
- Common Coupling
- It's worth to make sure that e.g. filesystem is reliable enough

#### Where to use?

- Where high interoperability needed, especially with systems which can't be modified
- When you want to share high volumes of data

### Pattern: Request-Response Communication

- Can be blocking sync or non-blocking async

#### Where to use?

- When result of a request is needed before any further processing
- When service wants to know if call succeeded or failed to carry out proper next action

### Pattern: Event-Driven Communication

- Loose coupling - event emitter knows nothing about recipients (even if they exist or not)
- Emitter doesn't have to know what recipient can do - the recipient handles it the right way - in opposite to
  request-based communication
    - This way components are more autonomous

#### Cons

- More complex
- Overhead with maintaining message broker and related infrastructure

### Event design

#### Just ID

##### Cons

- One service has to know about other service containing data for a given ID - domain coupling
- In case of lots of subscribers, it can cause a sudden spike of all of them calling for details
- Higher latency and lesser reliability, as a separate request has to be made

#### Fully detailed event

##### Pros

- No domain coupling
- No need to invoke extra calls, also no risks associated with network
- Detailed events can act as a log with snapshots of state of the data
    - It can be even an event sourcing mechanism

##### Cons

- Higher volume of data
- Every service sees all the data - but we may not want to some of them see some info
    - The solution can be to emit two events - but this "duplication" is a tradeoff
- Data becomes a contract - so it may be hard to change or remove some field

#### Hybrid

- Just include information widely needed and skip some less interesting trivia

## Part II: Implementation

## Chapter 5: Implementing Microservice Communication

### Technology choices

#### RPC

RPC framework defines serialization and deserialization.

##### Pros

- Having explicit schema allows to really easily generate client code
    - It may reduce the need for libraries
    - Avro RPC can send schema along with the payload, so clients can interpret it dynamically
- Good performance

##### Cons

- Some RPC mechanisms are tied to a specific platform (e.g. Java RMI), so it can shrink technology choices
- As idea of RPC is to hide remote call complexity, but it can hide too much - especially even the fact, that the call
  is remote and not local one
    - Network calls are totally different from local calls and it should not be overseen!
- It may force us to regenerate clients even if it's de facto not required, e.g.:
    - Adding a new method to the interface -> you may have to regenerate clients even if they won't use this method
    - Changing data model (e.g. removing field or moving into another class) -> you may have to regenerate client even
      if they don't use this class/field
- We may end up with lockstep deployments, where changes in server will cause need for changes in (all) clients

##### When to use it

- RPC is fine for synchronous request-response models
    - But it can also work with reactive extensions
- When having high control of both client and server

##### Tips

- Don't make it invisible that call is remote - actually make it explicit
- Make sure you can change server without changing clients

#### REST

##### Pros

- Well understood
- Using verbs is helpful
- Large tooling and technology ecosystem
- Fine for high volumes
- Fine security

##### Cons

- Code generation is somehow less convenient
- Performance can be an issue (depending on payload type)
    - Overhead of HTTP for each request may be problematic for low-latency systems (and it goes with TCP)

##### When to use it

- Obvious choice for synchronous request-response interfaces
- When you need to support a high variety of clients (high interoperability)
- When you want to large-scale and effective utilization of caches

#### GraphQL

##### Pros

- It makes possible for a client to make queries to fetch only needed data and thus avoid unnecessary calls.
- In server side it may reduce the need for call aggregation.
    - Imagine scenario where mobile has to make multiple calls fetching a lot of data, while actually it needs only a
      small subset of it. Using GraphQL it is possible to declare what's needed and fetch only such info - and
      everything in one call.

##### Cons

- An expensive GraphQL call can have large impact, just as an expensive SQL query.
    - (But the difference is for SQL we have at least query planners.)
- Also caching is an issue, which is more complex.
    - For ordinary REST API, we can use headers to control cache mechanisms. In GraphQL it's not possible to be done in
      any simple way (you can try associating an ID with every requested resource and make client cache them by ID, but
      it will introduce difficulties with CDNs or caching reverse proxies).
- GraphQL doesn't handle writes well.
    - In short, it is common to use GraphQL for reads, but REST API for writes.
- The last (somehow relative) issue is it may make you think you deal with sheer wrappers over DBs, forgetting that in
  fact it may be a complex, long-tailed logic chain solution.

##### When to use it

- At the perimeter of the system to be used by external clients
    - Typically GUIs
    - Often mobile phones
- Cases when it is usually needed to make multiple calls to fetch all required information
- It can be a call aggregation and filtering mechanism
- It can be used for BFF scenarios (Backend For Frontend)

#### Message Brokers

A type of a middleware.

**Queue vs Topic**

- When a message is sent through a **queue**, the **recipient is known**
- When a message is published on a **topic**, the **recipient(s) (if any) are unknown**

Topics suit more for event-based messaging, queues suit more for request-response communication style.

### Schemas

#### Why to use schemas

- Makes life easier for both devs and consumers
- They act as a documentation
    - And help write documentation as well
- They protect from structural breaking of a contract
- Allow to have history of API in version control
- With static typing they assure the code is valid
- They help writing tests
- Explicit schemas make it easier to collaborate between teams

### Handling Change Between Microservices

#### Avoiding Breaking Changes 🔨

1. Expansion Changes: Add new things, don't remove old things
2. Tolerant Reader Pattern: When consuming microservice, be flexible what you read
    1. For example - you shouldn't fail when there's an error in fields you don't even use
    2. Or when fields you don't use were moved in different place in a structure
3. Right Technology: Pick technology that allows easy backward-compatible changes
4. Explicit Interface: Have an explicit schema/docs/interface
    1. Just be as much explicit as possible, please
5. Catch errors early: Have mechanisms for catching errors before going to prod
    1. Using schemas is helpful in such scenarios
    2. Also there's some software like Protolock, json-schema-diff-validator, openapi-diff

#### Managing Breaking Changes 🔨

1. Lockstep Deployment: Require that microservice and all its consumers to be changed (deployed) at the same time
    1. It's the opposite of independent deployability
2. Coexist Multiple Microservice Versions: You can route old consumers into old service and new consumers into new
   service
    1. Downside is you have to maintain both versions simultaneously
    2. And introduce complexity of routing
    3. In case of service having some persistent state, it may be also a source of issues to mange it properly, for both
       old and new data models and/or service logic
    4. Such technique seem to be fine for short periods of time (like canary)
    5. But for longer periods of time - consider e.g. having two endpoints instead of having two separate microservice
       versions
3. Emulate Old Interface: Just have e.g. two endpoints in one service
    1. Consumers can upgrade to new version at any time
    2. Choosing the right interface may be handled e.g. by HTTP Headers or URL paths

#### Social Contract 🔨

As you work with people, you should think about dealing with people as well.
Both service owners and service consumers should be clear about certain things:

1. How will it be raised that interface has to be changed?
2. What way it be worked out together to decide on a final shape of a contract?
3. Who should get hands on updating consumers?
4. When the change is accepted, how much time will consumer have to shift over?

Well done microservices are done with consumer-first approach. They exist to be used by their consumers. So the
consumers' needs are so important factor then.

#### Tracking usage 🔨

1. Even if you agree on a migration deadline, make sure consumers migrated indeed
2. Have monitoring of API usage with details of who uses it and in what scale
3. You don't want to suddenly disconnect an important part of the whole system

#### Extreme measures 🔨

Suppose time is out, maybe even double out, and there's still someone using old API. What to do then? You can take the
plug off, but there may be some better ways to handle it.

1. At first - really do try to speak with them and make everything you can to find a way out.
2. You can just insert a `sleep` inside the old API. And increase this delay over time. This way you won't break the
   integration, yet still make consumer rethink their plans

### DRY and the Perils of Code Reuse in a Microservice World

DRY is relevant not in terms of avoiding duplication of code, but **behavior** and **knowledge**.

#### Sharing code via libraries

##### In general

In general we want to avoid heavy coupling. However, sometimes using shared code can cause a heavy coupling. Let's
imagine case of library of common domain objects representing core entities, which is used by many, many services.
If breaking change is introduced in such a library, it will cause a hard time for the whole ecosystem.

When use of shared code leaks outside the service boundary, then a coupling appears. Using common code such as logging
frameworks is fine, as such internal concepts are invisible to the outer world.

In real-estate.com.au they handle duplication & coupling case for bootstrapping new services just by coping the code. In
such way there is no coupling and such duplication seem to be fine.

**A huge downside of shared library is that you can't reasonably update all users at once.** They usually will have to
upgrade its version within the microservice and deploy it. In scenario when you need all of them to upgrade at once you
have to welcome lockstep deployment.

If you have a code that needs to be both shared and upgraded for everyone at once, you'd better use a shared
microservice in this case.

The conclusion is if you want to reuse code by a shared library, you have to accept that different services will use
different versions of it at different times. Make sure it's fine in this scenario. If not, it will be a pain, but if
yes - it will be a huge advantage.

##### Client libraries

One may say that client libraries help avoid duplication of code. You have to be aware, however, of problem when logic
that should exist only on the server is leaking to the client - such happens when the same team creates both server and
client API. **A good practice is when such libraries are written by wider community - especially when people who use
these libraries should be different than these who write them.**

If you think of concept of client libraries, it may be a good idea to separate code handling transport protocol, that
can deal with things like e.g. service discovery and failure. Also make sure clients can upgrade their library versions
at any time they want, independently of each other.

### Service discovery

#### DNS

DNS entries have its TTL, so after we change IP of some DNS entry, it will take some time for changes to be read and
used. DNS entries can get cached in many places. The more such places, the more stale DNS entry can become.

One of the workarounds can be DNS entry pointing to the Load Balancer which points to actual instances. You can then add
and remove instances without risk of having outdated DNS entries.

Some people use DNS round-robin, where DNS entries refer to the group of machines, however this approach is highly
problematic! Client is hidden from the underlying host and it can't stop routing traffic to one of the hosts when it
start malfunctioning.

**In other words it seems that pointing to Load Balancer is fine, while having direct DNS entries is not, as it won't be
able to detect problems and auto-update healthy node list, unlike the LB.**

In general, when having only single nodes, making DNS refer them directly may by sufficient, but when you have more than
one instance of a host it would be better to point to Load Balancer.

#### Dynamic Service Registers

In highly dynamic environments, just a DNS may be not enough.

##### Zookeper

ZooKeeper is used for lots of use cases, like configuration management, synchronizing data between services, leader
election, message queues and as a naming service.

ZooKeeper runs of multiple nodes in a cluster to provide guarantees. Smarts in ZooKeeper are focused on ensuring data is
replicated safely between nodes, what should make data remain intact when nodes fail.

Inside, Zookeeper provides a hierarchical namespace for storing information. Clients can insert, change or query nodes
in this hierarchy. They can also add watches to nodes to be notified when they change. We could also store the
information about where our services are located in this structure and as a client be told when they change.

ZooKeeper is often used as a general configuration store, so you can also store service-specific configuration in it.
This way you can e.g. dynamically changing log levels or turn off features of a running system.

In reality, for sheer dynamic service registration Zookeper is less relevant as more appropriate software exists.

##### Consul

Like Zookeper, Consul supports both config management and service discovery, but provides much wider support in these
terms - for example it exposes HTTP interface for service discovery.

**Its killer feature is providing a DNS server out of the box, serving SRV records (giving both IP and port for a given
name).** Consul has also some other features like providing health checks on nodes.

Consul uses REST HTTP as an interface, so it's very easy to integrate it with other technologies. It also has a suite of
tools like consul-template, which updates text files basing on entries in Consul.

You may think it's not a rocket science, but imagine that you can update a configuration file and any node reading
config can be updated dynamically without even knowing about Consul existence. An example can be adding or removing
nodes to a load balancer pool using a software load balancer like HAProxy.

##### etcd and Kubernetes

etcd has capabilities similar to Consul. In a nutshell, when you deploy a container in a pod, service dynamically
identifies which pods should be a part of a service by pattern matching on metadata of the pod. It's a quite neat
mechanism and can be very powerful. Requests to a service will then get routed to one of the pods that make up that
service.

etcd is a fine solution for K8s only environments, however if you run a mixed environment with some workloads on K8s,
and some elsewhere, it would be a wise thing to have a dedicated service discovery tool capable of working with both
platforms.

### Service Meshes and API Gateways

In typical data center language, “east-west” traffic is inside a data center, while “north-south” traffic relates to
entering or leaving the data center from the outside world.

Generally, an API gateway sits on the perimeter of the system and deals with "north-south" traffic. Its primary concerns
are managing access from the outside world to your internal microservices. A service mesh, on the other hand, deals very
narrowly with communication between microservices inside your perimeter - "east-west" traffic.

Service Meshes and API Gateways can potentially allow to share code without need of creating new client libs or new
microservices. Using a simplification, you can think of Service Meshes and API Gateways as proxies between
microservices, where you can implement some microservice-agnostic behavior like service discovery or logging, otherwise
needed to be done in code.  
If you are using Service Mesh or API Gateway to implement shared, common behavior, it's **essential** for it to be
totally generic - so without any relation to any specific behavior of any individual microservice.

By the way - a number of API Gateways try to provide capabilities for "east-west" traffic as well (and it will be
discussed soon).

#### API Gateways

Focused on "north-south" traffic, main responsibility of API Gateway is mapping requests from external parties to
internal microservices. However they can also be used to implement mechanisms like API keys for external parties,
logging, rate limiting etc. Some products also provide developer portals, often targeted at external consumers.

Much of the time, all an API gateway is actually being used for is to manage access to organization's microservices from
its own GUI clients (web pages, native mobile applications) via the public internet. In this scenario there isn't any "
third-party", there are only "ours".  
The need for some form of an API Gateway for Kubernetes is essential, as Kubernetes natively handles networking only
within the cluster and does nothing about handling communication to and from the cluster itself.  
In such case, API Gateway designed for external third-party access would be an overkill.

If you want to have API Gateway, be sure what exactly you need from it.  
You should probably avoid having API Gateway that does too much.

##### Where to use

If you need just to expose microservices on K8s, you can run your own reverse proxies or even better look at a focused
product like Ambassador build for this purpose.  
If you need to manage large numbers of third-party uses accessing your API, there are probably ready solutions for such
scenario.
It's even possible to have multiple API Gateways (but of course keep in mind caveats like system complexity or more
hops).

##### What to avoid

Two prominent examples of API Gateway misuse is call aggregation and protocol rewriting. Another cases is extensive use
for in-perimeter (east-west) calls.

"In this chapter we’ve already briefly looked at the usefulness of a protocol like GraphQL to help us in a situation in
which we need to make a number of calls and then aggregate and filter the results, but people are often tempted to solve
this problem in API gateway layers too.  
It starts off innocently enough: you combine a couple of calls and return a single payload. Then you start making
another downstream call as part of the same aggregated flow. Then you start wanting to add conditional logic, and before
long you realize that you’ve baked core business processes into a thirdparty tool that is ill suited to the task"

**If you need to do call aggregation and filtering - look at GraphQL or BFF pattern.  
If the call aggregation is a business process (rather than technical process), look at Saga pattern.**

In terms of protocol rewriting at API Gateway level, it's a violation of rule "keep the pipes dumb, and the endpoints
smart". It's pushing so much logic into a pipe component, which should be as simple as possible. Generally the more
behavior is added to API Gateway, the less efficient it would be to maintain this - handoffs, coordination, lockstep
deployments..

The last misuse is placing API Gateway as an intermediary between all microservice calls, what at least increases number
of hops. Here the answer would be simple - just use Service Meshes for this use case.

#### Service Meshes

With a Service Mesh, common functionality associated with inter-microservice communication is moved into the mesh. This
provides consistency across how particular things are done and also allows to reduce functionality required to be
implemented by microservice.

Examples of features done by Service Mesh are mutual TLS, correlation IDs, Service Discovery, Load Balancing. These
functionalities are so generic, that they could be placed in a shared library - but then we would have to deal with all
drawbacks of sharing code this way. Service Mesh allows us to avoid all these difficulties.

With Service Mesh we can reuse common functionalities across services written by different teams in various programming
languages and technologies.

##### How do they work

Service Meshes are done in various ways, but one thing in common is their architecture focusing on limiting the impact
caused by calls to and from the proxy. This is primarily achieved by reducing number of remote calls by distributing the
proxy processes to run on the same physical machine as the microservice instances.

Service Mesh instances are managed by a Control Plane, which allows to see what's going on. It also handles
functionalities like distributing client and server certificates when using MTLS.

##### Aren’t service meshes smart pipes?

You may now wonder if pushing all this behavior into a Service Mesh isn't making pipe smart.  
**The answer is the common behavior that is put into the Service Mesh is not specific to any microservice. No business
leaks to the outside. Configuration applies only to generic things like request time-outs.**

##### Do you need one?

Service Meshes aren't for everyone. They add complexity. If you're not on Kubernetes, you have limited set of choices.
If you have 5 microservices, it's unlikely you need K8s yet alone Service Mesh.

It's a fine option for organizations having many microservices, especially written in different languages.

### Documenting Services

#### Explicit Schemas

Schemas help by showing the structure, however they don't tell you behavior of an endpoint, so good documentation is
still needed. In case of not using explicit schemas, such documentation is needed even more. And more work is required
to detect if it's up to date with real shape of endpoint.  
Stale docs is a problem, but explicit schema increases chances that at least some information will not be outdated.

OpenAPI is a standard schema format and which is also effective in providing documentation, however its functionalities
are somehow limited.  
If you use Kubernetes, Ambassador's developer portal is an interesting product. Ambassador is a popular API Gateway for
K8s and has ability to autodiscover available OpenAPI endpoints. The idea of deploying new microservice and having its
documentation provided automatically is a huge value.

In terms of Event-based Interfaces, we have some options like AsyncAPI (which started as an adaptation of OpenAPI) or
CloudEvents (a project by CNCF).

#### The Self-Describing System

It is a sensible idea to have some information board showing state of the system, especially at a scale.  
"By tracking the health of our downstream services together with correlation IDs to help us see call chains, we can get
real data in terms of how our services interrelate. Using service discovery systems like Consul, we can see where our
microservices are running. Mechanisms like OpenAPI and CloudEvents can help us see what capabilities are being hosted on
any given endpoint, while our health check pages and monitoring systems let us know the health of both the overall
system and individual services."  
One of solutions can be tool used by Spotify - Backstage.  
Kubernetes' Ambassador also has some functionalities in its Service Catalog, although limited only to K8s.

## Chapter 6: Workflow

### Transactions

When thinking about a Transaction, we think about one or more actions which we want to treat as a single unit.

#### Database Transactions

In terms of Database Transactions, we think about ACID Transactions.

- **Atomicity**
    - Everything completes or everything fails
- **Consistency**
    - After we make changes, DB is left in valid, consistent state
- **Isolation**
    - No interim changes of one Transaction are visible for other Transactions
- **Durability**
    - After Transaction completes, data won't be lost in case of e.g. system failure

#### Distributed Transactions

Things like 2PC (2 Phase Commit) don't actually work as intended.  
It's discouraged to use it, as well as distributed transactions in general.

What to do then?

First option can be just avoiding splitting the data apart at all.

If you want to have state managed in an atomic and consistent way, but you have hard times making it work without ACID
transactions, maybe just leave it in a single database and set state management in a single service (or monolith).

However if you do really need to split data, have operations carried out without locking and these operations can
actually take minutes, days or even longer - then you may consider use of Sagas.

### Sagas

Saga in an algorithm to coordinate multiple changes in state without need of locking resources. This is done by
modelling particular steps as discrete activities which can be executed independently. Using sagas also help us
explicitly model business processes, which is a nice advantage.

The core idea is to handle Long Lived Transactions (LTT) by splitting them into multiple SubTransactions.
SubTransactions will be shorter and will modify only a subset of data.  
This way we can also break a single business process into a set of calls that will be
made to collaborating services.

#### Saga Failure Modes

Note that we talk about business failures (not technical ones), such as e.g. having insufficient stock in warehouse.

##### Backward Recovery

Revert a failure and clean-up afterwards by using compensation actions (semantic rollback).  
Applies for actions that can be undone.

**Example: An email sent to customer that his order was shipped.**  
We can't unsend and email, but we can send another one clarifying the situation.  
So it's not like `git reset --hard`, it's more like another commits that fix what's wrong.

To be able to perform such compensating actions we may need to have some/many information stored in our system for such
ability.

It is advised then to model Saga in such way that in case of failure will require as less as possible compensating
actions. For example we can move operations most likely to fail closer to the beginning.

Look at this scenario:

1. Check stock of an item and reserve it
2. Take funds from a customer
3. **Award loyalty points for customer**
4. Package and send order

Just by changing order of these steps we can really optimize it:

1. Check stock of an item and reserve it
2. Take funds from a customer
3. Package and send order
4. **Award loyalty points for customer**

##### Forward Recovery

We process the Saga from the point of failure and try to continue.  
It can be e.g. retry of a step.

#### Saga Implementations

##### Orchestrated Sagas

We use a central coordinator (Orchestrator) which defines order of execution and triggers compensation actions.  
It's command-and-control approach.

###### Pros

The biggest benefit is that we have our Saga explicitly modelled inside one place - The Orchestrator. We can look there
and easily understand what's the whole flow of a business process.

###### Cons

Downside is by definition is a more coupled approach. The Orchestrator needs to know about all dependants, so we have at
least coupling on domain level (Domain Coupling).

Also, it's a (too) common tendency to have logic being pushed to the central place always, when such a place exists.
Therefore this area collects more and more responsibility and becomes more and more coupled.  
You have to take care that services are independent entities with their own state and behavior and they are in charge of
themselves. Orchestrator just requests things from services - it's all.

##### Choreographed Sagas

Choreographed Saga distributes responsibility between multiple collaborating services.
It's _trust-but-verify_ approach.

Oftentimes Choreographed Sagas are build with heavy use of Event Architecture where particular services listen and react
to events and this is the main way of communication. Many services can listen and react to the same events.

###### Pros

By using such highly decoupled approach, services usually don't know about other services. We have highly reduced Domain
Coupling.  
Also we don't have any central point, so logic is placed inside particular services and don't leaks into The
Orchestrator.

###### Cons

It's much harder to reason about a such highly decoupled system. We can't just open The Orchestrator _main_ method and
see what goes when. Logic is now scattered amongst many places.  
Moreover, in terms of running Sagas, we don't actually know at what state the Saga is - this may e.g. make compensating
actions harder.

To overcome it one may create a dashboard listening to events and showing information based on these events. Then we may
be able to conclude the state by examining details of events.

##### Which one to use?

You are free to mix styles of sagas.  
Some business processes fit more into orchestrated style, another fit more into choreographed one.  
You can use e.g. an orchestrated Saga as en step inside a choreographed Saga.

In case when one team owns implementation of an entire saga, it may be fine to use choreographed Saga. Here, inherently
more coupled architecture suits more to be inside the single team boundary.  
When saga is spread across multiple teams, less coupled and more distributed choreographed saga should allow teams to be
able to work more independently of each other.

Also choreographed saga tend to go for request-response style of communication, whereas orchestrated saga is associated
with higher use of events.

## Chapter 7: Build

### Are you really doing CI?

Jez Humble asks 3 questions to assess if people _really_ get what CI is about:

1. **Do you check in to mainline once per day?**
    - Your code has to be integrated. If your whole team doesn't sync code frequently, the future integration will
      become harder. Even when using short-lived feature branches, you should integrate as frequently as possible - at
      least once a day.
2. **Do you have a suite of tests to validate your changes?**
    - CI without tests should not be called _CI_...
3. **When the build is broken, is it the #1 priority of the team to fix it?**
    - It means you cannot let more changes to pile up waiting for a build to be passing. It's not **Continuous**
      Integration, when you wait hours, maybe days to be able to integrate.

### Branching models

O'Really says:

> _The discussion around this topic is nuanced, but my own take is that the benefits of frequent integration—and
validation of that integration—are significant enough that **trunk-based development** is my preferred style of
development. Moreover, the work to implement **feature flags** is frequently beneficial in terms
of **progressive delivery**._

Also, the 2016 State of DevOps [report](https://oreil.ly/YqEEh) by DORA and Puppet stated:

> _We found that having branches or forks with very short lifetimes (less than a day)
> before being merged into trunk, and less than three active branches in total, are important aspects of continuous
delivery, and all contribute to higher performance. So does merging code into trunk or master on a daily basis._

Although open-source projects live in a different way as a lot of unknown people perform ad-hoc development and
repository authors are not systematically developing a project 5 days for 8 hours a week, some guidelines still apply:

- **Committing code sooner is better**
    - In open source projects, it was observed that merging patches faster (preventing e.g. rebases) helps developers
      move faster.
- **Working in small batches is better**
    - Large code bombs are harder and slower to merge into a project than small, concise patches, as maintainers need
      much more time to review such changes.

### Continuous Delivery vs Continuous Deployment

**Continuous Delivery** is when every commit/PR is treated as something that potentially can be released and we can
verify its quality to make a final decision.

**Continuous Deployment** is when every commit/PR after positive automatic verification is automatically deployed to the
production.

### Artifact creation

Any given artifact should be built exactly once. Then it should be reused everywhere it's required.  
Artifact should be also environment-agnostic - if some configuration for particular envs is needed, it should be stored
elsewhere.

### Mapping Source Code and Builds to Microservices

#### Pattern: Multirepo

A common approach, where every project has its own, separate repository.

You can easily change ownership of a repository (what supports a strong ownership model).

Working with multiple repositories is harder, it's also not possible to make one atomic commit across all needed
repositories.  
Sharing code is also harder, as you have to e.g. create a library. Therefore you encounter drawbacks of this solution -
to make an upgrade you have to make changes in library repo, wait for build to complete, deploy new version of lib, make
changes in microservice repo, wait for build to complete, deploy new version of microservice.
If there are many microservices using lib, the process would have to be repeated for each of them, and it probably won't
happen at the same time.

##### Case: When features require changes in multiple repositories

If you see it happens (too) often that in order to add a new feature you have to make changes in many repositories, it
may indicate too much coupling between them.  
Maybe you are actually within one boundary, artificially (consciously or not) split into parts. In such scenario it may
be considered to actually merge microservices.  
Cross-cutting changes should be something rather unusual, rather than opposite of it.

#### Pattern: Monorepo

An uncommon approach, where all of the code is stored in one repository.

##### Pros

There are some interesting advantages of such an approach:

- Changes across multiple services can be done in atomic fashion
- Code reuse is so much easier and requires much less maintenance compared to multirepo
    - Just import a class or method you want to use - and it's just it!
    - Such an import is selective - only the needed code is being acquired (not the e.g. whole library)
- Visibility of other people's code is improved

It's worth noting, that although changes can be done across many services, they still should be able to be released
independently.

##### Cons

Build & Deployment side of this approach is however more intricate.

(Let's assume, that separate services lie within separate directories)

- More than one folder can trigger the same build
- One folder can trigger more than one build
    - Especially some "common" code may start build of a huge amount of services
- It may be necessary to use some tooling (like Bazel) to manage the graph of dependencies
- Ownership handling is more complex
- Repo can be really huge in size, so it would be needed to handle this somehow
- In overall, monorepo may/will require much more maintenance effort

##### Semi-monorepo

One variant of monorepo approach is one repository, but not for all developers - let one repo for a one team.  
In such scenario we don't get drawbacks of immense size of enormous repository, but still achieve advantages it gives.

##### Which approach should I use?

In theory, for smaller teams each approach should be fine, however the problems emerge when the scale grows above the
small one.  
If you chose monorepo, you will likely encounter sunk cost fallacy - should we continue having monorepo and getting more
problems with it, or migrate everything we have (and "lose" everything we have achieved) into multirepo? In both cases
it's painful and costly. And too long hesitation may lead to even more pain and costs.

Therefore, nonetheless, multirepo is recommended approach for most cases.

If you want to use Monorepo, you do really should know it makes sense, as it's a kind of sophisticated approach that
will shine only in exceptional circumstances.

So TL;DR - if you aren't absolutely confident you have to use multirepo to cope with given concrete problems, don't
touch it.

## Chapter 8: Deployment

### Principles of Microservice Deployment

- **Isolated Execution**
    - Microservices should have their own resources and shouldn't impact on any other software running nearby
- **Focus on automation**
    - Choose tech that allows a high degree of automation and make it a part of work culture
- **Infrastructure as Code**
    - Store infrastructure configuration in repository
- **Zero-downtime deployment**
    - Make independent deployability a step higher and allow new releases to be done in a way invisible to end users
- **Desired state management**
    - Use platform which takes care of infrastructure and deployments, it should monitor and act in real-time to ensure
      valid state

#### _Financial Times_ case of zero-downtime deployment

After implementing zero-downtime deployment, as one could have high confidence that releases won't break things to end
users, companies greatly increase release frequency. Also such a deployment can be done during working hours (and not
night maintenance ones).

Practices like rolling updates and/or blue-green deployment are worth taking into consideration.

It's also a good thing to implement zero-downtime deployment in earlier stages of a project - why wait with it until
things become more complex?

### Deployment options

- **Physical Machine**
    - No virtualization, just a real computer
- **Virtual Machine**
- **Container**
    - Microservice instance runs as a separate container on a virtual or physical machine. Such a runtime can be managed
      by container orchestration tool like Kubernetes
- **Application container**
    - Microservice instance runs in application container that manages other application instances, typically on the
      same runtime, examples are Tomcat or IIS
- **Platform as a Service (PaaS)**
    - Highly abstracted platform hiding from user infrastructural details and allowing to focus more on the actual app,
      rather than everything around it
- **Function as a Service (FaaS)**
    - You deploy just code which is invoked on demand

### Kubernetes and Container Orchestration

Fundamentally, K8s **Cluster** consists of two things:

- **Nodes** - a set of machines that run workloads
- **Control Plane** - a set of software that manages these Nodes

When we schedule a workload, we don't do it on a Container, but on a **Pod** (which can run 1 or more containers, but
usually only one).

A **Service** is actually a mapping between running Pods and Network Interface.

Pods should be considered ephemeral - they can come up and down at any time. The stable thing here is Service.  
You can say you deploy a service, however you actually deploy a Pod which maps to Service.

**Replica Set** is definition of desired state - how many Pods you would like to have running.

You don't manage Replica Set directly - it's handled via **Deployment** - this is how you apply changes to Pods and
Replica Sets.  
With a Deployment it's possible to issue things like rolling upgrades, rollbacks, scaling, etc.

To sum up - to deploy a microservice in K8s you define a Pod, which will contain container with Microservice, define a
Service which will enable access to Microservice, and you apply changes to running Pods with Deployment.

#### Knative

Knative is open-source project aiming to provide FaaS-like workflows using K8s under the hood. It's goal is to hide the
complexity of K8s from developers and make managing the full lifecycle of software much easier.

### Progressive Delivery

There's an observation that high-performing companies deploy more frequently and idea "go fast and break stuff" doesn't
seem to apply here. Actually, shipping often goes hand in hand with lower failure rate. It is because such companies
take advantage of feature toggles, canary releases, parallel runs, etc. which have positive effect on these metrics.

#### Deploy vs Release

**Deployment** is when you **install** some version of software on a given environment.  
**Release** is when you make it **available** to end users.

#### Progressive Delivery techniques 🔨

##### Blue/Green Deployment

You have one version of your software working currently (Blue),  
then you deploy a new version alongside it in production (Green).  
You perform verification to check if it works as intended - and only if it indeed works, you redirect users to the new
version.  
If you notice problems beforehand, no one will be affected.

##### Feature Toggles (Feature Flags)

You can have binary toggles, which you can enable or disable at any given moment (or at a scheduled time).  
You can have more fine-grained toggles, which e.g. will accept only certain users (Beta group).
Heavily used in Trunk Based Development.

##### Canary Release

You deploy two versions running side by side and route only a fraction of traffic to the new one.

Tools like Spinnaker may be an interesting aid here.

##### Parallel Run

You deploy two versions running side by side, but the second one works in dry mode.  
Then you compare metrics and results for both versions.

## Chapter 9: Testing

### In short

In microservice world end-to-end tests are rather tough thing to do, so it seem to be pragmatic to lean toward different
approach.  
They have high degree of confidence and they indeed verify things in-depth, but the cost - the cost is just too high.  
I mean you _can_ use e2e tests to achieve an adequate level of confidence, but the cost associated with it won't be
probably worth it.
It's discouraged to choose this method because we have other ways of providing a decent level of confidence, but with
much smaller costs and overhead.  
Using explicit schemas, Contract Tests / CDCs, in-production testing, Progressive Delivery - there are some effective
means to assure quality in such a distributed world.

### Contract Tests and CDCs (Consumer-Driven Contracts) 🔨

Here, the team which uses some other microservice, create such tests. It's a kind of specifying how you expect that
microservice to behave.

One advantage is such tests can be run on any mock server or real server or even real microservice.

They are also very useful while combined with Consumer-Driven Contracts (CDCs).  
It's in effect an explicit representation of how upstream service expect the downstream service to work.

With CDCs, the consumer team shares these tests with producer team to allow them to verify that microservice meets these
expectations. They can be e.g. added CI/CD nightly builds and run for every consumer of the microservice. Of course you
can also be both consumer and producer.

Contract Tests are much faster, require less resources and are more convenient that e2e tests they actually replace.

#### Pact 🔨

Pact is consumer-driving testing tool. You define expectations using DSL, start local Pact server and run expectations
to make this server generate specification file (it's just a JSON).

#### Spring Cloud Contract 🔨

It's a Spring way of CDC.

### Cross-functional testing

#### Performance tests 🔨

Having chain of multiple synchronous calls, if any node gets slower, everything goes slower, what can have significant
impact. Therefore Performance Testing is even more important than in monolith system.  
Performance Testing is often postponed argued that there's not much system to test. Unfortunately, (too) often this ends
with it being done just before production release, or even never.

To get reasonable results, it would be fine to run scenarios with gradually increasing number of users. This allows to
describe load characteristics.  
Such tests should also be run on environment and data as much similar to production as possible.

Performance tests can take a really long time (and resources), so it's a common to run a subset of them nightly, and all
of them on a weekend.

Take care to run them regularly without any breaks. The longer you are without perf tests, the harder it will be to find
the source of slow down.

And don't forget to check the results!

It's (too) often when a lot of effort is put into making perf tests operational just to continuously ignore their
outputs.

So, to have a solid reason to keep an eye on perf results, you should set targets. When microservice is a part of a
wider architecture, you should have defined SLOs and one of them can be related to performance.  
Another metrics can be a delta - if performance decreases in more than x%, tests should fail.

## Chapter 10: From Monitoring to Observability

### Observability

Building Blocks for Observability:

- **Log aggregation**
    - Collecting information across multiple microservices
- **Metrics aggregation**
    - Having a wide data range and being able to look at them from the distance
- **Distributed tracing**
    - Tracking a flow of calls across multiple microservice boundaries
- **Are you doing OK?**
    - Defining Error Budgets, SLAs, SLOs
- **Alerting**
- **Semantic monitoring**
- **Testing in production**

#### Log Aggregation

##### First and foremost 🔨

**Log Aggregation tools should be a \*requirement\* to even start working in microservice architecture.**

BTW: Implementing a log aggregation is easier than other microservice overheads, so if your company fails to do this, it
means they would not handle microservices at all.

##### Common format 🔨

Make sure logs end up being stored in a common format.

Some log forwarding agents allow to reformat logs before being sent to central log store.  
**It's advised to avoid it**.  
It can be both resource-consuming and more error-prone.

**It's better for the microservice to send logs in target format from the beginning.**

Log reformating tools can be helpful in cases when we have no power to change logging, like in 3rd party software. Then
it would be helpful.

##### Correlation IDs 🔨

As soon as you start having log aggregation, implement Correlation IDs.

##### Timing

Keep in mind that logs are generated on machines, which have its own Time. There's no guarantee this time will be
valid (synchronized) across them. In such cases we may see logs from the present before logs from the past.

We have 2 limitations of logs then:

1. We cannot be certain about timing of logs
2. We cannot be certain about causality (causation)

#### Metrics aggregation

##### Cardinality 🔨

Cardinality can be described as number of fields that can be easily queried in a given data point.

For every logging record I may want to capture microservice name, instance ID, customer ID, correlation ID, version
number, headers etc. The more items in record, the higher the cardinality is.

The problem here boils down not to the storage size of the record(s), but to indexing all of these tags.  
Storing too much tags for a metric may significantly impact underlying engine.

You should carefully design log records taking into consideration software limitations. E.g. Prometheus is made to store
rather simple information like CPU usage for a given instance. In its documentation you can read:

> _Remember that every unique combination of key-value label pairs represents a new time series, which can dramatically
increase the amount of data stored. Do not use labels to store dimensions with high cardinality (many different label
values), such as user IDs, email addresses, or other unbounded sets of values_

#### Distributed Tracing

Instead of looking separately on a particular microservices, we want to see the state of a whole system.

For this purpose we've got software which is usually a central collector which gathers info from all nodes and merges it
into one story.

With high volumes of traffic, we have to think about sampling resolution. It may not be possible to sample every single
one event, so we may have to choose what to reject.  
For example we may want to store every single one error event, but only 1 for 100 successful events, especially if they
are very similar to each other.

When choosing utility for distributed tracing, choose something supporting OpenTelemetry, as it's industry standard.

#### Are you doing OK?

##### SLA

SLAs are usually "bare minimum" of service not to make client upset - but the end-user still may be dissatisfied with
quality of service.

##### SLO

It's what team signs to provide to the client. It can be e.g. expected uptime or response times.

##### SLI

SLIs are metrics that allow to assess if SLOs/SLAs are satisfied.

##### Error Budgets 🔨

Growth implies change, and change imply risk. Being too cautious may be impeding, Error Budgets are to define how much
error is acceptable for the system.  
They help find how well your system does (or not).  
Error Budgets are something that gives a space to breathe and try new things without fear.

#### Alerting

One important remark is to try to take care of amount of alerts an operator gets in case of system failure. Research
about plane pilots showed when having too many alerts, especially competing with themselves, they exceed mental
resources, narrow attentional focus and have negative impact on speed and quality of taken decisions.

##### Good alerts characteristics 📝

1. **Relevant**
    - Make sure that alert is valuable
2. **Unique**
    - Ensure alert isn't a duplicate of another one
3. **Timely**
    - Alert should come up quickly enough to react on it
4. **Prioritized**
    - It should allow to decide how much importance the alert has
5. **Understandable**
    - Information should be clear and readable
6. **Diagnostic**
    - Information should tell what's wrong
7. **Advisory**
    - It should be a guidance what actions can be taken
8. **Focusing**
    - It should draw attention to the most important issues

When creating an alert (or configuring alert system), keep in mind who will be receiving it. Make it be written
especially for the recipient, not for nobody/anybody.

#### Semantic Monitoring 📝

We don't specify low-level goals like "Disk usage should be below 80%", but we focus on business processes. For example
we can describe our system as operational when:

- Users can register
- We are selling at least 10k products per hour during daytime
- We are shipping orders out of a normal rate

These statements can be also SLOs or similar to SLOs.

How to perform such semantic monitoring then?

##### Real user monitoring

We gather data in real time and compare it with expectations.

The drawback is it's about what happened in the past, a not if the next action will succeed. That's where Synthetic
Transactions come and shine.

#### Testing in Production 🔨

> _Not testing in prod is like not practicing with the full orchestra because your solo sounded fine at home_ (~Charity
> Majors)

##### Synthetic Transactions 🔨

We can e.g. create a new fake user in production system and see if it went fine.

In a complex system there are many moving parts and plain metrics like CPU usage are insufficient. Implying based just
of them isn't accurate.

But verification of the real system can be done in an empiric way - for example e.g. generating fake events (with some
tag allowing to distinguish them with real events) and putting them into the real queue. The system would consume them
and produce some output, but it wouldn't go as usual with other users' flow, let end in a special bucket. Then such
output and other factors can be analyzed and system condition can be assessed.

But low-level metrics are **not** obsolete now - they just tell us a different set of information.

PS: Of course make sure that fake transactions won't have a real effect, indeed!

##### A/B Testing 🔨

You simultaneously deploy two versions, half users see A, half user see B.  
You then analyze differences in operation. And example can be a different placing order UI with analysis of time spent
in the form and finished orders rate.

##### Canary release 🔨

You can gradually increase amount of users using a new version.

##### Parallel run 🔨

You deploy two versions, both are working (but only one has process effects) and you compare outputs.

##### Smoke tests 🔨

Smoke tests are done after microservice is deployed, but before it's released. They check if the software starts and
doesn't brake.

Usually such tests are automated and check simple activity like health check + some synthetic transaction.

##### Synthetic transaction 🔨

A full-blown, fake user interaction which is injected into the system.  
It's actually often very close to e2e tests.

##### Chaos engineering 🔨

Injecting artificial faults to the running system to ensure it can handle it or monitoring/alerting works as intended.

### Standardization 📝

Write all logs in the same format, use the same formatting, use the same field names and tags.

### Selecting Tools

#### Democratic

Having tools which are too complicated for an ordinary employee to use will decrease number of people that will be able
to help when something wrong happens. Also picking tools being too expensive to use will have similar effect.

Pick tools which are accessible for the most widest range of users.

#### Easy to integrate

Pick tools that support open standards like OpenTelemetry to make integration easier as well as changing tools in the
future.

#### Provide Context 📝

When looking at a piece of information, tool should provide as much context to aid to understand what's going on and
what to do next.

Here's an example categorization system:

1. **Temporal context**
    - How does this look compared to a minute, hour, day or month ago?
2. **Relative context**
    - How has this changed in relation to other things in the system?
3. **Relational context**
    - Is something depending on this? Is this depending on something else?
4. **Proportional context**
    - How bad is this? Is it large or small scoped? Who is impacted?

#### Able to scale

You should take tool that can scale along with you. Also - make sure, that costs of such scaling won't skyrocket at the
same time.

### Getting Started 🔨

Capture basic system info: CPU, memory, I/O.  
Also make sure you can match a microservice instance back to the host it is running on.  
For each instance capture response times for network interfaces and record (log) all calls with downstream
microservices.  
Add correlationID from the very beginning.  
Log major steps of business process.  
You should have at least a basic metric and log aggregation tooling being operational.

You may not have to start with dedicated distributed tracing tool - especially if it requires to run and host it by
yourself. However if you have something that would work with a little effort - it would be definitely worth it.

For key operations, strongly consider using synthetic transactions - it will give you high confidence that whole system
works properly. Try to design the system with this capability in mind.

This is just the beginning. Over time you will collect more information and get more confidence with assessing if the
system works well and end-users can use it successfully.

## Chapter 11: Security

### The 5 functions of Cybersecurity

1. **Identify**
    - who your potential attackers are, what can be their targets, where your are most unprotected
2. **Protect**
    - your key assets from potential hackers
3. **Detect**
    - that attack happened
4. **Respond**
    - when you find out that something happened
5. **Recover**
    - in the case of security accident

#### Identify

Threat modeling is about understanding what attackers may want from your system. It's about going into mind of a
potential attacker.  
Will different types of attackers want to take over different assets? Or maybe there's a single place valuable for many
parties?  
Outside view is very important, and having somebody from the outside perform such analysis is valuable.

#### Protect

Once we identified most valuable and most vulnerable assets, we need to protect them adequately. Microservices are wider
area of attack than simple monoliths, so there will be more things to be protected. Yet at the same time we have also
more means to defend.

#### Detect

With microservices, detecting adverse operations can be more complex task.
There's more things to have an eye on, there are more sources of information.

Log aggregation is some basic way to monitor things going on.

There is also software for intrusion detection, e.g. Aqua.

#### Respond

If something bad happens - what next? What to do?

Having an effective incident response approach may limit damage caused by breach. Basics cover analyzing scope of breach
and what data was exposed. Especially if any PII got leaked, you have to follow both security and privacy incident
response and notification processes.

Also one aspect is how things are handled internally. If the company has culture of blame, it would be hard to perform
quality post-mortem analysis and draw conclusions. Culture of openness supports learning from mistakes.

#### Recover

With Microservices we have many moving parts, so to perform recovery it will be probably somehow more complex.

### Foundations of Application Security

#### Credentials

##### User Credentials

> _There is some excellent advice out there about how to properly handle things like passwords—advice that despite being
simple and clear to follow is still not being adopted widely enough. (...) This advice includes recommendations
to **use password managers and long passwords**, to avoid the use of complex password rules, and—somewhat
surprisingly—to avoid mandated regular password changes._

##### Secrets

Examples of secrets:

- Certificates for TLS
- SSH keys
- public/private API keypairs
- DB credentials

###### Secret management

1. **Creation**
    - How the secret is created?
2. **Distribution**
    - After the secret is created, how it is delivered to the destination?
    - And **only** there?
3. **Storage**
    - Is secret stored in a way that allows access for no one other than authorized parties?
4. **Monitoring**
    - Do we see how the secret is used?
5. **Rotation**
    - Can we change secret without brakes and downtime?

###### Secret rotation

Secret rotation process can be very painful, even to degree of stopping working for the whole system. It is often caused
by secrets having too broad scope. Therefore a given secret should have only a single responsibility.

Also make sure you performed tests of secret rotation process, don't wait until they expire!

###### Key leaking 🔨

It is not rare to expose private keys publicly by committing them to the repository.

Therefore it may be useful to use tools like [gitsecrets](https://github.com/awslabs/git-secrets)
or [gitleaks](https://github.com/gitleaks/gitleaks) to scan repository for such leaks.

###### Limiting scope - Principle of Least Privilege

Limiting scope can be performed on two levels at once - limiting the scope of a given secret and limiting the scope of
who has access to such a secret.  
Therefore we limit impact of damage in case of secret leakage, we limit area of verification we have to make, also it
makes easier to track down source of leakage.  
We may also easy track down e.g. expensive query killing our DB.

###### Secret management 🔨

Secret management can be daunting, therefore it is advised to use tools aiding this - e.g. Vault from HashiCorp.

#### Patching 🔨

On a daily basis vulnerabilities are discovered across libraries used in your project. Having unpatched software may
make you susceptible to hacker attack. And such scenario can become a real thing - and you certainly don't want it to
happen.

Detect such holes in software on a daily bases in an automated manner - use code scanning tools like **Snyk** or *
*GitHub Code Scanning**.

#### Backups 📝

1. Create Backups on a regular basis
2. **Verify that Backups indeed can be restored on a regular basis as well!**

#### Rebuild

If malicious party gains access to your system, even if you recover, it's still not guaranteed you evicted all evil.  
There's a chance an attacker is still present somewhere in your system. Rootkits can help it especially (imagine
modifying `ls` command not to show suspicious files).

**Reinstalling system from scratch is something giving higher level of safety.**

The ability to wipe everything and rebuild from scratch is effective not only after a known attack, but also is
something that can remove undetected transgressors.

**It would be desired to rebuild microservices or even a whole system based on information in source code.**  
Then you need to restore backups of data.

**Having the same process for deployment and rebuilding assures that the rebuilding indeed works.** (Actually it happens
even now - with containers you shut down old containers and spin up new ones)

One harder thing can be rebuilding the whole platform - e.g. whole Kubernetes.  
If you have managed solution, creating a new cluster can be relatively easy, however if you have your own on-prem
installation, things will be much harder.

### Implicit Trust vs Zero Trust

#### Implicit Trust

Assumption that calls made from inside our perimeter are implicitly trusted.

**This is the most common approach, however it is often like that due to unconsciousness about dangers related with it!
**

#### Zero Trust

When operating in Zero Trust environment, you assume that the whole system is compromised and you can trust nobody. It's
a kind of Paranoic Mode.  
The goal is to operate in such a way that malicious parties won't be able to hurt you.

**All calls have to be verified, all secrets have to be stored securely, all sensitive data in transit have to be
encrypted.**

One thing - this approach is also called "perimeterless computing", but don't act as it was really true - for example
one may allow connections from the Internet to the whole system "because there's no perimeter"! Just remember to be
sane.

#### Spectrum Trust

Implicit vs Zero trust is not a xor - it's more like a spectrum.  
Some services can have either one, or even both approaches depending on certain factors.

You can for example group information in such way:

1. **Public**
    - Data shared freely with third-parties. It's effectively a public domain.
2. **Private**
    - Information only for authorized users. For example details of customer orders.
3. **Secret**
    - Sensitive information. Can be accessed only in particular circumstances only by allowed users, e.g. medical data.

You can make microservices run in these 3 different environments, but allow one-way connections - only from lower layers
to upper layers.

Also we can see we have here moving from Zero Trust Area, through Implicit Trust Area to end up in Public Area.

### Securing Data

#### Data in transit

##### Concerns about data in transit

1. **Is the Client actually the one it claims it is?**
    - For example use certificate to sign its message.
2. **Can anyone see the data being sent?**
    - Using HTTPS or Mutual TLS makes data in-transit encrypted.
3. **Can anyone change the data being sent?**
    - Usually data unavailable for third parties can't be manipulated
    - However when we want to send data without encryption, we can make sure they are not modified - e.g. use HMAC (
      hash-based message authentication code)
4. **Is the Server actually the one it claims it is?**
    - With HTTPS browser can look at Server Certificate and verify if it's valid.

#### Data at rest

##### Be frugal

Of course data, especially sensitive, have to be stored in a safe manner.

**But at a first place you can avoid having to deal with securing such data just by avoiding storing it.**  
Keep bare minimum required for system to work and no more.

Also - when logging a request - do we need the whole IP, or can we just save 81.45.162.x?  
Do we need name, gender, address, DoB or just age and postal code would be enough to provide product offers?

Not having data makes you not having to deal with it, no danger of leaking them and no requirement to share it to e.g.
Police.

Also remember to encrypt also backups, if they contain sensitive data.

### Confused Deputy Problem

Imagine there's a logged used who wants to check status of an order. He clicks on a link and then an
`store.com/orderStatus/12345` opens up. In behind, SSO Gateway calls WebShopService which calls both ShippingService and
OrderService.

Should ShippingService and OrderService accept calls from WebShopService? We can assume that yes - calls come from
within the perimeter from trusted service.

However what if user opens `store.com/orderStatus/55555` which is someone else's order? In such case he still will see
the details, although he shouldn't!

What happened here is that we had Authentication, but we didn't have Authorization.

#### Centralized, Upstream Authorization

One approach would be to have a central point, e.g. on the API Gateway or in some Orchestrating Microservice. Therefore
all requests that went through this Central Point would be treated as authorized. In such scenario we have some kind of
Implicit Trust.

One issue is that upstream component has to have knowledge about downstream services, functionality they provide and how
to limit access to these functionalities.  
This impairs independent deployability as we may need to deploy both microservice and central point at the same time.

It implies that it would be better to move this logic into the microservice itself.

#### Decentralizing Authorization

With decentralized authorization we need to make sure, that client sending request is indeed authenticated.

##### JWT

- Multiple claims can be stored in one JWT
- Can be signed for ensuring data integrity
- Can be encrypted to limit who can read the data
- Can be configured to expire

###### Using Tokens 📝

Customer authenticates as normal, e.g. logging on a webpage and obtaining e.g. an OAuth token. Requests he makes contain
this OAuth token which our Gateway validates. If everything is OK, the Gateway can create JWT Token for internal
communication. Such token can be valid only for duration of a request.

Downstream microservices can validate this Token and also check claims included in it, so it can both verify
authentication and authorization.

A variation of this method can be generating JWT Token and giving it for the user himself, however it's less secure and
such tokens should have limited validity time.

###### Challenges 🔨

In case of signed JWTs, to be able to validate signature, receiver will need e.g. public key for such operation.
Therefore all inconveniences related with key management come in place.

Also it is a balancing act with defining token expiration time. Some processing can take a very long time (e.g. ordering
an item is something taking days, usually). Set too short time, and you have problems. Set too long time and increase
risk.  
In such case you may generate a special long-lived token with this narrow scope of privileges just for this long-lived
operation.  
Some just stop using token at a certain point in the flow.  
In terms of this issue, you need to find the right solution for yourself.

Sometimes also there are cases, when system needs really a lot of information to be carried with the request - exceeding
max JWT length. In such scenario you would need to find another solution.  
However nothing stops from splitting the flow to be able to use JWT in some parts of it, at least.

## Chapter 12: Resiliency

### What is Resiliency?

1. **Robustness**
    - Ability to absorb an expected perturbation
2. **Rebound**
    - Ability to recover after a traumatic event
3. **Graceful extensibility**
    - How well unexpected situations are dealt with
4. **Sustained adaptability**
    - Ability to continually adapt to changing environments, stakeholders and demands

#### Robustness

Robustness by definition requires prior knowledge - we are putting measures into
place to deal with known perturbations.

It is being able to handle cases like e.g.:

- Failing host
    - auto spin up replacement host
- Connection getting timed out
    - perform a retry
- Unavailable microservice
    - handle failure in a graceful manner
- Important employee gets sick
    - have many individuals with given knowledge

#### Rebound

Rebound is how well we recover from possible disruptions.

One may put all its energy to prevent scenarios, but can't foresee everything, so it's wide to prepare to recover when
they eventually, nonetheless happen.

- Having backups
    - Testing these backups!!!
- Having Instructions how to handle outages

#### Graceful Extensibility

Graceful Extensibility is about handling unforeseen scenarios.

In short - more flat organizations handle it better as employees hands aren't so tied up, also automation can be
helpful. However, when it replaces \<too\> many people, it may actually decrease this metric.

#### Sustained Adaptability

Sustained Adaptability requires us to not be complacent.

That we haven’t yet suffered from a catastrophic outage doesn’t mean that it cannot happen. We need to challenge
ourselves to make sure we are constantly adapting what we do as an organization to ensure future resiliency. Done right,
a concept like **Chaos Engineering** can be a useful tool in helping build sustained adaptability.

### Degrading Functionality

Assuming your system consists of many parts, some of them are probably less crucial or not crucial at all. When some of
them fails, it may be fine for the system still work but without these features. This way it still would be operational,
even if some elements are broken.

### Stability Patterns

#### Time-Outs 🔨

- Set time-outs on all calls.
- Log when time-out occurs.
- Monitor these occurrences.

Also single time-outs for particular service calls may not be enough. When a call is a part of a wider set of
operations (and likely it is), add also some meta-timeout for the whole set.  
Therefore you won't wait `t1 + t2 + ... + tn` seconds (as we can see the number grows along with number of hops), but a
certain `T` time for the whole operation.

This is especially important for user-facing operations.

#### Retries 🔨

Some problems are just temporary - packets can get misplaced, gateways can face an odd load spike, etc.  
Retrying a call is a so simple yet reasonable solution.

Just don't make a retry immediately - wait some time to allow downstream service to take a breath, as it's probably
under heavy load or facing other problems.

Also before making retry make sure the timeout budget isn't exceeded - if so, there's no point of retrying as it's
already too late.

#### Bulkheads 🔨

In short these are Worker Pools that isolate Threads (load shedding). Therefore if one of downstream services hangs, it
won't halt all available threads with waiting for it.

#### Circuit Breakers

When a certain number of requests to the downstream service fail (due to service being unavailable or just sheer
time-outs), the circuit breaker opens.

**Set circuit breakers for all synchronous calls.**

For asynchronous calls, when the circuit breakers is down, you can queue up requests and retry them later.  
However for synchronous calls it would be better to fail-fast.

Having circuit breakers also allows us also to manually "enable" and "disable" microservices.

#### Isolation

The more one microservice is depending on another one being available, the bigger is the impact of the first one to do
its job. Using technologies which allow a downstream services to be offline (e.g. using middleware or call buffering
system), upstream microservices are less prone to be affected by outages of downstream ones.

Also increased isolation between services makes them require less coordination. The less coordination needed, the higher
autonomy and teams can develop their services more freely.

Isolation can be also on higher level. Consider two microservices, totally isolated of each other. However they both use
database deployed to the same db infrastructure. A failure in such infrastructure will affect both these microservices.

Therefore aim to make microservices run on independent hosts with their own ring-fenced OS and computing resources (
actually this is what happens when running them on VMs or Containers).

However take into consideration all trade-offs. Isolating by running on different machines requires more infrastructure
and related tooling. Having separate db infrastructures, like stated in paragraph above, also ..increases infrastructure
overhead. We could use middleware to decrease temporal coupling, but this in turn makes us to worry about the broker
itself.

#### Redundancy

- Having multiple instances is a way of achieving redundancy
- But remember that this also applies to people!
    - You should have multiple employees being able to perform certain tasks

#### Middleware

One of useful things a middleware as a Message Broker can provide, is guaranteed delivery.

#### Idempotency

Having idempotent operations makes cases of receiving a message multiple times not cause any harm.

For example for operation of receiving money, receiving duplicated events may make balance invalid. However, when having
an assumption that one order can be paid once, and having orderID inside event, many same events won't cause payment to
be accounted more than once.

### CAP Theorem

**Consistency** - receive the same data from all different nodes

**Availability** - every request receives a response

**Partition Tolerance** - system can work even when communication between its parts is not possible

#### Sacrificing Consistency (AP)

**Available and Partition Tolerant**

When changes made to one node can't be visible in the another one, we have a partition. And we lost consistency.

However, though another node serves stale data - it still works.

In reality, if we don't have partition situation, we still aren't actually consistent, as data flow between nodes takes
time. So here we are Eventually Consistent.

#### Sacrificing Availability (CP)

**Consistent and Partition Tolerant**

When nodes can't talk with each other to ensure Consistency, we respond with error.

Actually in Distributed Systems this approach is really hard.

Distributed Transactions are.. we've already discussed this.

So, if you do really need to have CP system, use some existing, well-tested in production, solution.

#### Sacrificing Partition Tolerance (CA)

Actually, if system can't tolerate partition, it can't be Distributed..

#### It's not All or Nothing 📝

It's not that the whole system is CP or AP.  
It's even not that the microservice is CP or AP.

Actually, you can be CP or AP per particular feature. And also within a feature you can be "both" CP and AP.

For example reading loyalty points can be AP, while using these points can be CP.

Tech also don't need to be either-or - look at Cassandra, where different trade-offs can be set for different calls. You
may want a confirmation from one, majority or all nodes.

### Chaos Engineering 📝

> _Chaos Engineering is the discipline of experimenting on a system in order to build
> confidence in the system’s capability to withstand turbulent conditions in production._

— from [Principles of Chaos Engineering](https://principlesofchaos.org/)

#### Game Days 📝

Game Day exercises are to test people's readiness for system failures. Planned in advance, but started in surprise,
allow to verify how people would behave in case of emergency.

##### Anecdote of Bob's Team 📝

In one of the companies, Game Days episode was to examine overreliance of one team member - Bob. He was closed in a
special room where he could observe how his team will handle a failure.

Interestingly, they eventually logged into production and started the process of destroying production data xd

So.. As we can see, many may learn a lot from such exercises..

#### Production Experiments 📝

One thing is planning for failures, expecting them, having some theoretical knowledge what to do, how our software will
behave, how our people will behave.

But the reality is, you ony assume, you only prophesize, you actually don't know.

**And there's one, and only one way, to validate it.**

So to prepare for real failures, you actually incur real failures - to make people respond, to make software work, and
to draw conclusions.

##### Netflix and its Simian Army 🔨

In Netflix, they have a tool called Chaos Monkey, which turns off random machines in production.  
They have also Latency Monkey, which simulates congestion in network.
And they have Chaos Gorilla, which takes out an entire availability zone (one AWS DC).

##### Tools 🔨

There's a [Chaos Toolkit](https://chaostoolkit.org/) available for everyone.

You can also take a look at [Reliably](https://reliably.com/) and [Gremlin](https://www.gremlin.com/).

## Source

[Building Microservices: Designing Fine-Grained Systems (2nd Edition)](https://www.oreilly.com/library/view/building-microservices-2nd/9781492034018/)  
by Sam Newman  
August 2021  
O'Reilly Media, Inc.
