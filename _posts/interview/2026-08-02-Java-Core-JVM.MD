---
title: "[Interview] Java Core and JVM"
description: Interview questions and answers about Java language and JVM details
date: 2026-08-02 00:00:00 +0100
categories: [ interview ]
tags: [ java, jvm ] # TAG names should always be lowercase
---

<!-- @formatter:off -->

> Note: This article contains mainly LLM generated content, but only such that has been human reviewed. Nonetheless, it is still possible that something wasn't caught or simply I made a mistake. If you see anything wrong or missing the point, please let me know in the comments.
{: .prompt-info }

## Fundamentals

### ⭐ `equals()` and `hashCode()`

- `equals()` defines **logical** equality
- `hashCode()` returns an `int` used by hash-based collections


- IF `x.equals(y)` → THEN MUST `x.hashCode() == y.hashCode()`

- Collections first use `hashCode()` to choose a bucket and then `equals()` to find exact match
- Equal objects must have the same `hashCode()` and both methods should use fields that remain unchanged when using hash-related collections - good idea is to use the same **immutable identity fields**[^hashcode]
- If you `@Override` `equals()` then you should also `@Override` `hashCode()` (and vice versa)

#### `hashCode()`

- used in hash-based collections like `HashMap`, `HashSet`
- may collide
  - especially if bad implementation → decreased performance
- if not overridden along with `equals()` 
  - → then `HashMap`, `HashSet` and related structured can behave as if "equal" objects have different keys or be unable to find inserted Objects

#### `equals()`

- returns `true`/`false`
- bad implementation can break correctness
  - → by allowing duplicates, failed lookups, failed removals, inconsistent key replacement

#### Wrong implementation

##### Collections 

- `HashSet`
  - **duplicate logical objects** may both be stored if `equals()` and `hashCode()` disagree
- `HashMap`
  - `map.get(key)` may return `null` even if you inserted the same logical key
- `remove()`
  - you may fail to delete an object because it hashed into a different bucket after mutation

##### Common Pitfalls

- Mutable fields in `hashCode()` or `equals()`. 
  - If you change those fields after putting the object into a `HashSet` or as a key in `HashMap`, the object may become “lost” in the collection: `contains()`, `get()`, and `remove()` can stop working because the object’s hash position no longer matches its current state.
- Some subtle issue is violating symmetry or transitivity, often with inheritance.
  - Example: a base class `A` compares only an `id`, while a subclass `B` adds more fields, and then `a.equals(b)` differs from `b.equals(a)`
- In JPA/Hibernate entities, using a generated database ID in `hashCode()` before persistence can be dangerous
  - if the ID is `null` when the entity enters a hash-based collection and becomes non-null later - this way the hash code changes after insertion

## Concurrency

### ⭐ `volatile`

#### Basic

`volatile` ensures that a write to the field is visible to another thread that subsequently reads that same field.   
It provides a happens-before relationship, not a real-time “immediate” delivery guarantee.

- `volatile` **read** → prevents later reads/writes from being reordered **before** that read. 
- `volatile` **write** → prevents earlier reads/writes from being reordered **after** that write.
- 
- **Before** a `volatile` **write** → **earlier writes** in that thread are ordered **before** it.
- **After** a `volatile` **read** → **later reads/writes** in that thread are ordered **after** it.

If you do:

```java
data = 42;
ready = true;   // `ready` is volatile
```

another thread that does:

```java
if (ready) {
    System.out.println(data);
}
```

is guaranteed to see `data == 42` after it sees `ready == true`.

#### Standard

Use `volatile` when one thread writes, other threads read, and you only need a simple signal or state flag. 

Typical examples are `initialized`, or a shutdown flag in a worker loop. A volatile write by one thread happens before a later volatile read of the same variable by another thread, so the reader sees the value and the memory effects before that write.

Be aware that `volatile` is weaker than `synchronized`, it's not an alternative.  
It gives **visibility** and **ordering**, but **not mutual exclusion** and **not atomicity**.  
So it is good for a boolean flag, but bad for counters, lazy initialization with multiple steps (but valid for double-checked locking), or any `read-modify-write` sequence.

Think of `volatile` as a **memory-visibility tool**, **not a lock**. The JVM and CPU may cache values or reorder instructions for performance, but a write to a `volatile` variable happens before subsequent reads of that same variable.


#### Example

```java
class Class {
    private volatile Object object;

    void setObject(Object newObject) {
        object = newObject;
    }

    Object getObject() {
        return object;
    }
}
```

#### Common pitfalls

`volatile int count; count++;` is still broken, because `count++` is multiple steps: read, add, write. Two threads can both read the same value and overwrite each other’s updates.

If you need atomic increments, use `AtomicInteger`, `LongAdder`, or locking depending on the use case.

## OOP and Code Design

### Coupling, cohesion, separation of concerns, and modularity

- **Coupling** → how much one module/class knows about or depends on another.
- **Cohesion** → how closely related the responsibilities inside a single module/class are.
- **Separation of concerns (SoC)** → splitting the system that each part handles one kind of responsibility.
- **Modularity** → structuring the system as distinct, replaceable modules with clear boundaries and interfaces.


#### Cohesion

- **Cohesion** is about how focused a single module/class is.
- **Low cohesion**: a class does many unrelated things (logging, DB access, HTTP calls, business rules, formatting).

#### Separation of Concerns

Different kinds of responsibilities should live in different parts of the system:

- **Business logic** (rules, calculations, invariants)
- **Persistence** (DB schema, queries, ORM mappings)
- **Presentation / API** (HTTP endpoints, request/response mapping)
- **Cross-cutting** (logging, metrics, security, transactions)

In a typical Spring Boot app:

- `@Service` – business rules
- `@Repository` / JPA entities – persistence details
- `@RestController` – handles HTTP, status codes, mapping
- Aspects/filters – logging, auth, metric

## Footnotes

[^hashcode]: Actually, `hashCode()` may use a subset of equality fields. It must not use a field that can differ for objects considered equal by `equals()`, but it may use the same fields as `equals()` or a subset of them.

<!-- @formatter:on -->
