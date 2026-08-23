---
title: "Draft: [Interview] Java Concurrency"
description: Interview questions and answers about Java Concurrency
date: 2026-08-09 00:00:00 +0100
#math: true
categories: [ interview ]
tags: [ java, jvm, concurrency ] # TAG names should always be lowercase
---

<!-- @formatter:off -->

> This article is an unreviewed draft and may contain incorrect information.
{: .prompt-danger }


## Fundamentals

### Thread Lifecycle

- `NEW`
- `RUNNABLE`
- `BLOCKED`
- `WAITING`
- `TIMED_WAITING`
- `TERMINATED`

### Methods

- `join()` 
  - better name would be: `waitForCompletion()`
  - then actual invocation: `parent.waitForCompletion(threadOne)` → parent joins `threadOne` execution waiting for it to finish
- `wait()`
  - give lock back away and wait for `notify()` call
- `notify()`
  - notifies the waiting one to wake up


### Producer Consumer

- `Producer` `produces >= 1` && `Consumer` `consumes >=1` && `common_buffer`

Simple pseudocode:

```java
// produce
synchronized (this) {
    while (buffer == MAX) {
        wait()
    }
    buffer.add()
    notifyAll()
}
```

```java
// consume
synchronized (this) {
    while (buffer == 0) {
        wait()
    }
    buffer.take()
    notify()
}
```

### `volatile`

- used for main-memory visibility guarantees in JVM
- affects happens-before ordering
- makes CPU read/write from common memory e.g. L3 Cache and not its own register


## `ExecutorService`

- `execute(Runnable)` - Schedules a `Runnable`; does not return a result handle.
- `submit(Runnable)` - Schedules a `Runnable` and returns a `Future<?>` to track completion or failure.
- `submit(Callable<T>)` - Schedules a value-returning task and returns a `Future<T>`. 
- `shutdown()` - Stops accepting new tasks and lets already submitted tasks finish.
- `shutdownNow()` - Attempts to stop active tasks by interrupting workers and returns tasks that never started.
- `awaitTermination(timeout, unit)` - Blocks until termination, timeout, or interruption.

### `SingleThreadExecutor`

```java
try(ExecutorService service = 
        Executors.newSingleThreadExecutor()) {
    // service.execute(Runnable runnable)    
}
```


### `FixedThreadPool`

```java
try(ExecutorService service = 
        Executors.newFixedThreadPool(int nThreads)) {
    // service.execute(Runnable runnable)
}
```


### `CachedThreadPool`

some kind of "autoscaling" `Pool` (60s idle → terminate Thread)

```java
try(ExecutorService service = 
        Executors.newCachedThreadPool()) {
    // service.execute(Runnable runnable)    
}
```


### Scheduled execution

Turn on

```java
try(ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor()) {
    service.scheduleAtFixedRate(
        Runnable task, 
        long firstDelay, 
        long period, 
        TimeUnit timeUnit
    );
}
```

Shut down

```java
try {
    // true: executor terminated / false: timeout elapsed
    boolean succeeded = 
        service.awaitTermination(
            long timeout, 
            TimeUnit timeUnit
        );
    
    if (!succeeded) {
        // force shutdown
        service.shutdownNow();  // shutdown() would be graceful
    }
} catch (InterruptedException e) {
    service.shutdownNow();
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
Future<Response> response = 
    service.submit(
        new Callable<Response>() {
            @Override
            public Response call() throws Exception {
                return someRequest.invoke();
            }
        }
);
```

### `CompletableFuture`

- `Future<T>` answers: 
  - "Has this submitted task finished, and can I obtain its result?" 
  - Usually you call `get()`, which waits.

- `CompletableFuture<T>` answers: 
  - "When this finishes, what should happen next?" 
  - You attach continuations such as `thenApply()`, `thenCompose()`, and `handle()` instead of blocking immediately.

```java
ExecutorService ioExecutor = Executors.newFixedThreadPool(32);

CompletableFuture<OrderSummary> summaryFuture =
    CompletableFuture
        .supplyAsync(() -> customerClient.getCustomer(customerId), ioExecutor)
        .thenCompose(customer -> CompletableFuture.supplyAsync(() -> orderClient.findRecentOrders(customer.id()), ioExecutor))
        .thenApply(orders -> OrderSummary.from(orders))
        .exceptionally(ex -> { 
            log.warn("Unable to build order summary for {}", customerId, ex);
            return OrderSummary.unavailable();
        });
```

## Synchronized Collections


### Manual

- coarse-grained locking (single `Lock` for everything)
- limited functionality (no additional methods for locking)
- performance overhead (lock acquisition/release overhead)

```java
List<Integer> list = 
    Collections.synchronizedList(
        new ArrayList()
    );
```


### `BlockingQueue`

General behavior:

- taking from empty → `wait`
- adding to full → `wait`

Methods:

- `put(E e)` - put or `wait` if full
- `take(): E` - take or `wait` if empty
- `offer(E e): boolean` - `true`: element added / `false`: queue full
- `poll(): E?` - take from head / `null`: if empty
- `peek(): E?` - get item without taking / `null` if empty

Implementations:

- `BlockingDeque` - double-ended queue (_deck_)
- `TransferQueue` - allows `Producer` to directly transfer to waiting `Consumer`
- `ArrayBlockingQueue` - bounded queue, backed by fixed array
- `LinkedBlockingQueue` - (un)bounded queue, backed by linked nodes
- `PriorityBlockingQueue` - orders items by `Comparator`
- `DelayQueue` - only expired delay items can be taken
- `SynchronousQueue` - zero capacity, only direct transfers


### `ConcurrentMap`

Basic Operations

- `putIfAbsent(key, value)`
- `computeIfAbsent(key, fn)`
- `compute()`
- `merge(key, value, fn)` - inserts a value if absent; otherwise combines old and new values atomically.
- `replace(key, value)` - replaces only when the existing value equals oldValue.
- `remove(key, value)` - removes only when the current value equals the supplied value.

Implementations:

- `ConcurrentHashMap`
- `ConcurrentSkipListMap`


### `CopyOnWriteArrayList`

- writers won't interfere with readers
- some like `Git` branching

- `add(element)` - appends an element by creating and publishing a new backing-array snapshot.
- `addIfAbsent(element)` - adds the element only when it is not already present.
- `set(index, element)` - replaces an element by copying and publishing a new array.
- `remove(element)` - removes the first matching element by creating a new array.
- `remove(index)` - removes the element at the index by creating a new array.
- `get(index)` - reads an element without copying the array.
- `contains(element)` - checks whether an element exists in the current snapshot.

```java
List<Integer> list = 
    new CopyOnWriteArrayList<>();

// Thread1 can read
sout(list);
// Thread2 can write
list.set(index, value);
```


## Atomic Variables

### `read-modify-write` cycle

`count++` is actually:

1. Load count value
2. Increment loaded value
3. Set value to variable

Basic Operations

- `get()`
- `set()`
- `compareAndSet(expected, updated)` - if (expected) then → update
- `getAndIncrement()` / `incrementAndGet()`
- `getAndDecrement()` / `decrementAndGet()`

### Implementations

- `AtomicInteger`
- `AtomicBoolean`
- `AtomicLong`
- `AtomicReference`
- `LongAdder`
- `DoubleAdder`


## Locks & Synchronizers


### `CountDownLatch`
 
- `CountDownLatch` is **single use**, cannot be reused

```java
CountDownLatch latch = 
    new CountDownLatch(int count);

// e.g. for count = 3:
Runnable runnable = () -> {
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


### `CyclicBarrier`

E.g. used for multiple checkpoints during a play when we have to wait for all players to reach given point to proceed

```java
CyclicBarrier barrier = 
    new CyclicBarrier(
        int cycle, 
        Runnable action // what to do when releasing
)

barrier.await();
```


### `Exchanger`

- synchronization point at which `Threads` can pair and swap elements within concurrent environment
- e.g. used if you create pipeline for adjacent steps

```java
Exchanger<String> exchanger =
    new Exchanger<>();

// Thread1: calls
String dataFromThread2 = 
    exchanger.exchange("DataToFlow: Thread1 → Thread2"); // waiting for another `Thread`
// Thread2: calls
String dataFromThread1 = 
    exchanger.exchange("DataToFlow: Thread2 → Thread1"); // exchange takes place in this step
```


### `Condition`

- `Condition` can be e.g. `Queue` being full → `await()`
- when `Condition` is met, there goes signal → `signal()`

```java
Lock lock = new ReentrantLock();

// Conditions are not programmable, we have to use them properly
// so naming is very important!
Condition bufferNotFull = lock.newCondition();
Condition bufferNotEmpty = lock.newCondition();

// Producer:
lock.lock();
try {
    while(buffer.size() == MAX_SIZE) {
        bufferNotFull.await(); // wait for condition to be met
    }
    buffer.offer(item);
    bufferNotEmpty.signal(); // signal that condition is now met
} finally {
    lock.unlock(); // don't forget to unlock
}

// Consumer:
lock.lock();
try {
    while(buffer.size() == 0) {
        bufferNotEmpty.await(); // wait until buffer is not empty
    }
    buffer.poll();
    bufferNotFull.signal() // let them know buffer is not full anymore
} finally {
    lock.unlock(); // don't forget
}
```


### `ReentrantLock`

- `ReentrantLock` enables the same `Thread` to `lock()` multiple times without preceding `unlock()`
- It has count how many times lock has been acquired by a given `Thread`
- Release takes place when counter reaches 0
- Fairness makes `Threads` waiting most time higher in the Priority Queue
- Without Fairness result is non-deterministic

Methods

- `lock()`
- `unlock()`
- `getHoldCount(): int` - count of current `Thread`
- `tryLock(): boolean` - `true` if successfully acquired / `false` if not acquired
- `tryLock(timeout, timeUnit): boolean` - similar with timeout
- `isHeldByCurrentThread(): boolean`
- `getQueueLength()`
- `newCondition()`

Example

```java
Lock lock = 
    new ReentrantLock(boolean fair);

methodA() {
    lock.lock();
    try {
        value++;
        methodB();
    } finally {
        lock.unlock();
    }
}

methodB() {
    lock.lock();
    try {
        value--;
    } finally {
        lock.unlock();
    }
}
```


### `ReadWriteLock`

- Used when resource is **read heavy**
- **Many** `Threads` can `read`
- **Only one** `Thread` can `write`
- It contains two separate `Locks` - one for `Readers`, one for `Writer`
- Only one of these `Locks` can be active at a given time
- `Threads` are in Priority Queue and with fairness writers won't be starved

- `readLock()`
- `writeLock()`

```java
ReadWriteLock lock =
    new ReentrantReadWriteLock();

writeValue() {
    lock.writeLock().lock();
    try {
        value++;
    } finally {
        lock.writeLock().unlock();
    }
}

getValue() {
    lock.readLock().lock();
    try {
        sout(value);
    } finally {
        lock.readLock().unlock();
    }
}
```


### `Semaphore`

- `acquire() / acquire(permits)` - acquires permit(s)
- `release() / release(permits)` - release permit(s)
- `tryAcquire()`
- `tryAcquire(timeout)`
- `availablePermits()`

```java
Semaphore semaphore =
    new Semaphore(int permits, boolean fair);

semaphore.acquire();
// ...
semaphore.release();
```


### `StampedLock`

TODO
optimistic-read alternative to `ReadWriteLock`


### `Phaser`

TODO
more flexible multi-phase alternative to `CyclicBarrier`


## Deadlocks

`Deadlock` is lock release dependency cycle

- `jps -l` - list processes running Java
- `kill -3 <PID>` - kill with Thread Dump

```java
ThreadMXBean mxBean = 
    ManagementFactory.getThreadMXBean();

long[] threadIds =
    mxBean.findDeadlockedThreads();

ThreadInfo[] threadInfo =
    mxBean.getThreadInfo(threadIds);
```


### How to prevent Deadlocks

- Use Timeouts
- Take care of Global Ordering of the Locks
  - e.g. always Lock in ascending order (LockA, LockB, LockC)
- Avoid nesting Locks
- Use Thread-Safe alternatives


## `ForkJoinPool`

- similar to `ExecutorService`
- `ForkJoin` can have subtasks
- work stealing
- utilization of multi-core processors
- simplified parallelism
- efficient work stealing algorithms

- `RecursiveTask`
- `RecursiveAction`
