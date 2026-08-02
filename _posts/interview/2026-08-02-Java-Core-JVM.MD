---
title: "[Interview] Java Core and JVM"
description: TODO.
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
- `hashCode()` gives bucket-friendly integer (used e.g. by hash-based collections))


- IF `x.equals(y)` → THEN MUST `x.hashCode() == y.hashCode()`
- IF `@Override` `equals()` → THEN MUST `@Override` `hashCode()`


- Collections first use `hashCode()` to choose a bucket and then `equals()` to find exact match
- Equal objects must have the same `hashCode()` and both methods should use the same **immutable identity fields**, otherwise hash-based collections will not work properly

#### `hashCode()`

- used in hash-based collections like `HashMap`, `HashSet`
- should be deterministic
- may collide
  - especially if bad implementation → decreased performance
- if not overridden along with `equals()` 
  - → then `HashMap`, `HashSet` and related structured can behave as if "equal" objects have different keys or be unable to find inserted Objects

#### `equals()`

- returns `true`/`false` or `null`
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

`volatile` means: updates to that field are **visible** to other threads **immediately**.

`volatile` creates a kind of memory barrier:
- **Before** a `volatile` **write** → **earlier writes** in that thread must stay **before** it.
- **After** a `volatile` **read** → **later reads/writes** in that thread must stay **after** it.

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

Typical examples are `initialized`, or a shutdown flag in a worker loop. A volatile write by one thread happens before a later volatile read of the same variable by another thread, so the reader sees the latest value and the memory effects before that write.

Be aware that `volatile` is weaker than `synchronized`, not it's alternative.  
It gives **visibility** and **ordering**, but **not mutual exclusion** and **not atomicity**.  
So it is good for a boolean flag, but bad for counters, lazy initialization with multiple steps, or any `read-modify-write` sequence.

Think of `volatile` as a **memory-visibility tool**, **not a lock**. The JVM and CPU may cache values or reorder instructions for performance, but a write to a `volatile` variable happens before subsequent reads of that same variable.


#### Example

```java
class ConfigHolder {
    private volatile Config config;

    void setConfig(Config newConfig) {
        config = newConfig;
    }

    Config getConfig() {
        return config;
    }
}
```

#### Common pitfalls

`volatile int count; count++;` is still broken, because `count++` is multiple steps: read, add, write. Two threads can both read the same value and overwrite each other’s updates.

If you need atomic increments, use `AtomicInteger`, `LongAdder`, or locking depending on the use case.

<!-- @formatter:on -->
