---
title: "[Interview] Java Core and JVM"
date: 2026-08-02 00:00:00 +0100
categories: [ interview ]
tags: [ java, jvm ] # TAG names should always be lowercase
---

<!-- @formatter:off -->

> Note: This article contains mainly LLM generated content, but only such that has been human reviewed. Nonetheless, it is still possible that something wasn't caught or simply I made a mistake. If you see anything wrong or missing the point, please let me know in the comments.
{: .prompt-info }

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
