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
try(ExecutorService service = 
        Executors.newSingleThreadExecutor()) {
    // service.execute(Runnable runnable)    
}
```
#### `FixedThreadPool`

```java
try(ExecutorService service = 
        Executors.newFixedThreadPool(int nThreads)) {
    // service.execute(Runnable runnable)
}
```

#### `CachedThreadPool`

some kind of "autoscaling" `Pool` (60s idle → terminate Thread)

```java
try(ExecutorService service = 
        Executors.newCachedThreadPool()) {
    // service.execute(Runnable runnable)    
}
```

#### `ScheduledExecutor`

Turn on

```java
try(ExecutorService service = Executors.newSingleThreadExecutor()) {
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

### Synchronized Collections

#### Manual

```java
List<Integer> list = 
    Collections.synchronizedList(
        new ArrayList()
    );
```

- coarse-grained locking (single `Lock` for everything)
- limited functionality (no additional methods for locking)
- no fail-fast Iterators (e.g. not throwing `InterruptedException`)
- performance overhead (lock acquisition/release overhead)

### `CountDownLatch`
 
- `CountDownLatch` is **single use**, cannot be reused

```java
CountDownLatch latch = 
    new CountDownLatch(int count);

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
- `ArrayBlockingQueue` - bounded queue, backed by `ArrayList`
- `LinkedBlockingQueue` - (un)bounded queue, backed by `LinkedList`
- `PriorityBlockingQueue` - orders items by `Comparator`
- `DelayQueue` - only expired delay items can te taken
- `SynchronousQueue` - zero capacity, only direct transfers


### `ConcurrentMap`

Implementations:

- `ConcurrentHashMap`
- `ConcurrentSkipListMap`
- `ConcurrentLinkedHashMap`
- `ConcurrentNavigableMap`

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

### `CopyOnWriteArray`

- writers won't interfere with readers
- some like `Git` branching

```java
List<Integer> list = 
    new CopyOnWriteArrayList<>();

// Thread1 can read
sout(list);
// Thread2 can write
list.set(index, value);
```

### Condition on Locks

- `Condition` can be e.g. `Queue` being full → `await()`
- when `Condition` is met, there goes signal → `signal()

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
- `Threads` are in Priority Queue and writers won't be starved

```java
ReadWriteLock lock =
    new ReentrantReadWriteLock();

writeValue() {
    lock.writeLock.lock();
    try {
        value++;
    } finally {
        lock.writeLock().unlock();
    }
}

getValue() {
    lock.readLock.lock();
    try {
        sout(value);
    } finally {
        lock.readLock.unlock();
    }
}
```

### Visibility Problem

- `volatile` - makes CPU read/write from common memory (like L3 Cache) and not is own registry

### Deadlocks

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

#### How to prevent Deadlocks

- Use Timeouts
- Take care of Global Ordering of the Locks
  - e.g. always Lock in ascending order (LockA, LockB, LockC)
- Avoid nesting Locks
- Use Thread-Safe alternatives

### Atomic Variables

#### `read-modify-write` cycle

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

Atomic Variables

- `AtomicInteger`
- `AtomicBoolean`
- `AtomicLong`

