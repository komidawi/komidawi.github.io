---
title: "Draft: [Interview] Databases and Hibernate/JPA"
description: Interview questions and answers about databases and Hibernate/JPA.
date: 2026-06-21 00:00:00 +0100
categories: [ interview, databases ]
tags: [ databases, hibernate, jpa, java, interview, postgresql ] # TAG names should always be lowercase
---

<!-- @formatter:off -->

> This article is an unreviewed draft and may contain incorrect information.
{: .prompt-danger }

## General

### ACID

ACID describes the **guarantees** a database **transaction** should provide: Atomicity, Consistency, Isolation, Durability.  
Think of a bank transfer: debit and credit must succeed together, preserve rules, avoid concurrent interference, and survive a crash.

| Property            | Practical meaning                                                      | Typical mechanism                             |
|---------------------|------------------------------------------------------------------------|-----------------------------------------------|
| **A — Atomicity**   | Either all statements commit, or none                                  | `COMMIT`/`ROLLBACK`, transaction log          |
| **C — Consistency** | A committed transaction obeys database and business invariants         | Constraints, triggers, application validation |
| **I — Isolation**   | Concurrent transactions should not observe unsafe intermediate effects | MVCC, locks, isolation levels                 |
| **D — Durability**  | Once committed, data remains after a crash                             | WAL, fsync                                    |

#### Atomicity

Atomicity means no partially completed transaction becomes visible as committed state.

```sql
BEGIN;

UPDATE accounts
SET balance = balance - 100
WHERE id = 1;

UPDATE accounts
SET balance = balance + 100
WHERE id = 2;

COMMIT;
```

If the second `UPDATE` fails and the application executes `ROLLBACK`, the first update is undone too: that is atomicity.

#### Consistency

Consistency means every **committed** transaction takes the database **from one valid state to another**. 
It does **not** mean "all replicas immediately show the same data" and it does not magically encode your business rules.

#### Isolation

Isolation governs what concurrent transactions may see. 
PostgreSQL implements transaction isolation with MVCC snapshots; 
it supports `Read Committed`, `Repeatable Read`, and `Serializable` (while `Read Uncommitted` behaves as `Read Committed`).

**MVCC** means **Multi-Version Concurrency Control**: instead of making readers wait for writers, 
the database can retain multiple versions of a row, while each query or transaction sees a consistent **snapshot**. 
In PostgreSQL, this is the main mechanism behind the **Isolation** part of ACID.

Mental model: a row update creates a new row version; old readers continue reading the old visible version, 
while later readers see the new committed version.

#### Durability

Durability means success reported after `COMMIT` remains recoverable after a database crash.

PostgreSQL relies on write-ahead logging (WAL): its recovery process can redo committed changes from the log, 
while MVCC is primarily about concurrent visibility rather than durability.


### Isolation Levels

#### Core anomalies

- **Dirty read**
  - Transaction `A` sees a change made by `B` **before** `B` commits. 
- **Non-repeatable read**
  - `A` reads a row **twice**; `B` commits an update **between** the reads, so `A` gets two **different** values.
- **Phantom read**
  - `A` runs the same predicate **query twice**; `B` **commits** an insert/delete that **changes which rows match**.
- **Write skew / serialization anomaly**
  - Two transactions each read a **shared condition**, **update different rows**, and jointly violate a business rule. 
  - This can happen without either transaction updating the same row.


#### ANSI SQL

| Level              | Dirty reads | Non-repeatable reads | Phantom reads  | Typical meaning                                                    |
|--------------------|-------------|----------------------|----------------|--------------------------------------------------------------------|
| `READ UNCOMMITTED` | ❌          | ❌                   | ❌             | May read another transaction’s uncommitted work                    |
| `READ COMMITTED`   | ✅          | ❌                   | ❌             | Each statement sees only data committed when that statement begins |
| `REPEATABLE READ`  | ✅          | ✅                   | ❌ by standard | Rows already read remain stable for the transaction                |
| `SERIALIZABLE`     | ✅          | ✅                   | ✅             | Outcome must be equivalent to some one-at-a-time ordering          |

#### PostgreSQL

`PostgreSQL` isolation levels control what one transaction can observe 
while other transactions are reading or writing the same data. 
PostgreSQL uses MVCC (multi-version concurrency control), 
so readers generally do not block writers and vice versa.

| Requested level    | PostgreSQL behavior                        | Snapshot scope                                       | Main anomalies still possible                                                             |
|--------------------|--------------------------------------------|------------------------------------------------------|-------------------------------------------------------------------------------------------|
| `READ UNCOMMITTED` | Treated as `READ COMMITTED`                | Per statement                                        | Non-repeatable reads, serialization anomalies                                             |
| `READ COMMITTED`   | Default                                    | Each SQL statement gets a new view of committed data | Non-repeatable reads, phantoms, write skew                                                |
| `REPEATABLE READ`  | Transaction-consistent snapshot            | First query/data-change through transaction end      | Serialization anomalies; PostgreSQL prevents phantom reads despite the SQL-standard table |
| `SERIALIZABLE`     | Strongest; as if one transaction at a time | Transaction-consistent snapshot & conflict detection | None if transactions succeed; one may be aborted and must be retried                      |


#### Examples

```sql
CREATE TABLE accounts (
  id        INTEGER PRIMARY KEY,
  balance   INTEGER NOT NULL
);

INSERT INTO accounts VALUES
  (1, 100),
  (2, 100);
```

##### Read Commited

`READ COMMITTED`: each statement sees new commits

This is PostgreSQL’s default.   
Each statement sees rows committed **before** that statement begins.

Session A read

```sql
BEGIN;  -- default: READ COMMITTED

SELECT balance FROM accounts WHERE id = 1;
-- 100
```

Session B update

```sql
UPDATE accounts
SET balance = 80
WHERE id = 1;

COMMIT;
```

Session A update

```sql
SELECT balance FROM accounts WHERE id = 1;
-- 80: a later statement sees B's committed update

COMMIT;
```

##### Repeatable Read

`REPEATABLE READ`: a stable transaction snapshot

Under `REPEATABLE READ`, every statement sees the database as it stood 
when the transaction’s **first query** or **data-modification statement** ran.

Session A read

```sql
BEGIN ISOLATION LEVEL REPEATABLE READ;

SELECT balance FROM accounts WHERE id = 1;
-- 100
```

Session B update

```sql
UPDATE accounts
SET balance = 80
WHERE id = 1;

COMMIT;
```

Session A update

```sql
SELECT balance FROM accounts WHERE id = 1;
-- Still 100

COMMIT;
```

##### Serializable

`SERIALIZABLE`: enforce business invariants

Suppose at least one doctor must remain on call:

```sql
CREATE TABLE on_call (
  doctor text PRIMARY KEY,
  on_duty boolean NOT NULL
);

INSERT INTO on_call VALUES
  ('Ava', true),
  ('Ben', true);
```

Two concurrent `REPEATABLE READ` transactions can both observe two doctors on duty, 
each turn off a different doctor, and leave nobody on duty.  
`SERIALIZABLE` detects that unsafe combination.

Session A

```sql
BEGIN ISOLATION LEVEL SERIALIZABLE;

SELECT count(*) FROM on_call WHERE on_duty;
-- 2

UPDATE on_call
SET on_duty = false
WHERE doctor = 'Ava';

COMMIT;
```

Session B

```sql
BEGIN ISOLATION LEVEL SERIALIZABLE;

SELECT count(*) FROM on_call WHERE on_duty;
-- 2

UPDATE on_call
SET on_duty = false
WHERE doctor = 'Ben';

COMMIT;
-- One transaction may instead fail:
-- ERROR: could not serialize access due to read/write dependencies among transactions
```

#### Choice

- `READ COMMITTED`
  - for most OLTP application code; it is the PostgreSQL default.
- `REPEATABLE READ`
  - when several reads must agree on one consistent snapshot
  - such as a report or multi-step calculation
- `SERIALIZABLE`
  - when correctness depends on cross-row or predicate-based invariants, 
  - such as scheduling, inventory reservations, limits, or financial rules.

At `REPEATABLE READ` or `SERIALIZABLE`, implement bounded retry handling for `SQLSTATE 40001`. 
`PostgreSQL` explicitly requires applications using those levels to be prepared for serialization-related retries.

### Optimistic Locking

`Optimistic locking` is a concurrency control strategy that assumes conflicts are rare, 
so it does **not** block other writers while you read or edit data.

In practice, it detects a conflict at save/commit time, usually with a version field like `@Version` in JPA/Hibernate, 
and then fails the transaction if someone else changed the row first.

Typical flow:

- Transaction **A** reads row `X` with version `7`.
- Transaction **B** reads the same row `X` with version `7`.
- **B** updates and commits first, version becomes `8`.
- **A** tries to update using expected version `7`.
- Update affects `0` rows, so the ORM throws `OptimisticLockException` or equivalent.

This is the usual default for business data where collisions are possible but not constant. 
It fits CRUD screens, admin panels, user profiles, shopping carts, 
and any workflow where you prefer **detect-and-retry** over blocking other requests.

#### Examples

##### SQL

```sql
CREATE TABLE product_inventory (
    id                  BIGINT PRIMARY KEY,
    available_quantity  INTEGER NOT NULL CHECK (available_quantity >= 0),
    version             BIGINT NOT NULL DEFAULT 0
);

INSERT INTO product_inventory (id, available_quantity)
VALUES (42, 10);
```

```sql
-- Read the row
SELECT id, available_quantity, version
FROM product_inventory
WHERE id = 42;
```

| id | available_quantity | version |
| -- | ------------------ | ------- |
| 42 | 10                 | 7       |

```sql
-- Update the row
UPDATE product_inventory
SET available_quantity = available_quantity - 2,
    version = version + 1
WHERE id = 42
  AND version = 7
  AND available_quantity >= 2;
```

- `1` row updated
  - reservation succeeded
- `0` rows updated
  - it may indicate that:
    - another transaction changed the row first 
    - the record no longer exists
    - inventory is insufficient 
  - re-read and decide whether to retry, return a conflict, or report insufficient stock

##### Java/Hibernate

```java
@Entity
@Table(name = "product_inventory")
public class ProductInventory {

    @Id
    private Long id;

    @Column(name = "available_quantity", nullable = false)
    private int availableQuantity;

    @Version
    private long version;

    public void reserve(int quantity) {
        if (quantity <= 0 || availableQuantity < quantity) {
            throw new IllegalArgumentException("Insufficient inventory");
        }
        availableQuantity -= quantity;
    }
}
```

```java
@Transactional
public void reserve(Long productId, int quantity) {
    ProductInventory inventory = repository.findById(productId)
        .orElseThrow(() -> new NoSuchElementException("Product not found"));

    inventory.reserve(quantity);
    // At flush/commit, Hibernate checks and increments `version`.
    // A conflicting modification results in an optimistic-lock exception.
}
```

### Pessimistic Locking

`Pessimistic locking` means you lock a row or resource **before** updating it, 
so other transactions cannot acquire conflicting locks or perform conflicting modifications until the lock is released.

This is useful for things like balances, inventory, reservations, or any workflow where two writers must not race.

#### Limitations

Pessimistic locking creates deadlock risk when transactions lock multiple rows in inconsistent orders. 
Mitigate this by locking rows in a consistent order, keeping transactions short, setting sensible timeouts, 
and treating deadlock/serialization errors as retryable only when the operation is idempotent.

#### Example

##### SQL

```sql
BEGIN;

SELECT id, available_quantity
FROM product_inventory
WHERE id = 42
    FOR UPDATE;

UPDATE product_inventory
SET available_quantity = available_quantity - 2
WHERE id = 42
  AND available_quantity >= 2;

COMMIT;
```

##### Java/Hibernate

```java
public interface ProductInventoryRepository
        extends JpaRepository<ProductInventory, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM ProductInventory p WHERE p.id = :id")
    Optional<ProductInventory> findByIdForUpdate(@Param("id") Long id);
}
```

```java
@Transactional
public void reserveWithLock(Long productId, int quantity) {
    ProductInventory inventory = repository.findByIdForUpdate(productId)
        .orElseThrow(() -> new NoSuchElementException("Product not found"));

    inventory.reserve(quantity);
}
```

#### Standard

In practice, this is a database concurrency strategy that favors **preventing** conflicts over detecting them later.

In JPA, it commonly appears as `LockModeType` of `PESSIMISTIC_READ`, `PESSIMISTIC_WRITE`, or `PESSIMISTIC_FORCE_INCREMENT`
to hold a lock until the transaction commits or rolls back.\
A common SQL equivalent is `SELECT ... FOR UPDATE` for exclusive locking.

#### Advanced

The main tradeoff is **simplicity vs throughput**: pessimistic locking reduces lost updates, but it also increases blocking, 
lock wait time, and deadlock risk. It is usually a better fit when contention is high or when a failed retry would be costly, 
while optimistic locking is often better when conflicts are rare.

#### Short Answer

_I default to an atomic conditional update or optimistic locking when conflicts are relatively rare, 
because it preserves throughput and avoids holding locks across remote calls.  
If concurrent updates to the same record are frequent and the critical section is short, 
I use pessimistic row locking with a bounded lock wait. 
I keep the transaction small, acquire resources in a stable order, 
monitor lock waits and deadlocks, and make retries idempotent.”_

#### Alternatives

Avoiding read-then-write cycle and explicit locking using single atomic statement.  
If it returns no row, the reservation did not happen. 
It is often the simplest and highest-throughput solution for a counter-like invariant.

```sql
UPDATE product_inventory
SET available_quantity = available_quantity - :quantity,
    version = version + 1
WHERE id = :id
  AND available_quantity >= :quantity
RETURNING id, available_quantity, version;
```


<!-- @formatter:on -->
