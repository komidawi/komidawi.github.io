---
title: "Draft: [Interview] Java Concurrency"
description: Interview questions and answers about Java Concurrency
date: 2026-08-19 00:00:00 +0100
#math: true
categories: [ interview ]
tags: [ java, jvm, concurrency ] # TAG names should always be lowercase
---

<!-- @formatter:off -->

> This article is an unreviewed draft and may contain incorrect information.
{: .prompt-danger }

## Fundamentals

### Basic Info

- one Java `Thread` is one OS Thread

### Methods

- `join()` 
  - better name would be: `waitForCompletion()`
  - then actual invocation: `parent.waitForCompletion(threadOne)` → parent joins `threadOne` execution waiting for it to finish
- `wait()`
  - give lock back away and wait for `notify()` call
- `notify()`
  - notifies the waiting one to wake up

### Producer Consumer

- Producer `produces >= 1` && Consumer `consumes >=1` && `common_buffer`

Simple pseudocode:

```python
produce:
    if buffer < max:
        buffer.add()
        lock.notify()
    else
        lock.wait()
```

```python
consume:
    if buffer > 0:
        buffer.take()
        lock.notify()
    else
        lock.wait()
```

### `ExecutorService`

#### `SingleThreadExecutor`

```java
try(ExecutorService service = Executors.newSingleThreadExecutor()) {
    // service.execute(Runnable runnable)    
}
```
#### `FixedThreadPool`

```java
try(ExecutorService service = Executors.newFixedThreadPool(int nThreads)) {
    // service.execute(Runnable runnable)
}
```

#### `CachedThreadPool`

some kind of "autoscaling" `Pool` (60s idle → terminate Thread)

```java
try(ExecutorService service = Executors.newCachedThreadPool()) {
    // service.execute(Runnable runnable)    
}
```

#### `ScheduledExecutor`

Turn on

```java
try(ExecutorService service = Executors.newSingleThreadExecutor()) {
    service.scheduleAtFixedRate(Runnable task, long firstDelay, long period, TimeUnit timeUnit);    
}
```

Shut down

```java
try {
    // true: executor terminated / false: timeout elapsed
    (if (!service.awaitTermination(long timeout, TimeUnit timeUnit))) {
        // force shutdown
        service.shutdownNow();  // shutdown() would be graceful
    } catch (InterruptedException e) {
        service.shutdownNow();
    }
}
```

### `Callable` and `Future`

- `future.get()`
  - blocking operation, waiting until result arrives
  - `throws ExecutionException, Interrupted Exception`
- `future.get(long timeout, TimeUnit timeUnit)`
  - blocking operation, waiting until timeout
  - `throws TimeoutException` (apart from `InterruptedException, ExecutionException`)
- `future.cancel(boolean mayInterruptIfRunning)`
  - attempts to cancel task, may also try to interrupt
- `future.isCancelled()`
- `future.isDone()`

```java
Future<Response> response = service.submit(
    new Callable<Response>() {
        @Override
        public Response call() throws Exception {
            return someRequest.invoke();
        }
    }
);
```

### Synchronized Collections

#### Manual

```java
List<Integer> list = Collections.synchronizedList(new ArrayList());
```

- coarse-grained locking (single `Lock` for everything)
- limited functionality (no additional methods for locking)
- no fail-fast Iterators (e.g. not throwing `InterruptedException`)
- performance overhead (lock acquisition/release overhead)

### `CountDownLatch`
 
- `CountDownLatch` is **single use**, cannot be reused

```java
CountDownLatch latch = new CountDownLatch(int count);

// e.g. for count = 3:
Runnable runnable = (latch) -> {
    Thread.sleep(random);
    // decrease latch
    latch.countDown();
};

new Thread(runnable).start();
new Thread(runnable).start();
new Thread(runnable).start();

// blocks and awaits for all 3 `countDown()` calls
latch.await();

```

### `BlockingQueue`

- taking from empty → `wait`
- adding to full → `wait`


- `put(E e)` - put or `wait` if full
- `take(): E` - take or `wait` if empty
- `offer(E e): boolean` - `true`: element added / `false`: queue full
- `poll(): E?` - take from head / `null`: if empty
- `peek(): E?` - get item without taking / `null` if empty


- `BlockingQueue`
  - `BlockingDeque` - double-ended queue (_deck_)
  - `TransferQueue` - allows `Producer` to directly transfer to waiting `Consumer`
  - `ArrayBlockingQueue` - bounded queue, backed by `ArrayList`
  - `LinkedBlockingQueue` - (un)bounded queue, backed by `LinkedList`
  - `PriorityBlockingQueue` - orders items by `Comparator`
  - `DelayQueue` - only expired delay items can te taken
  - `SynchronousQueue` - zero capacity, only direct transfers

