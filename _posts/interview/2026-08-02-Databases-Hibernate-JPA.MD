---
title: "[Interview] Databases and Hibernate/JPA"
description: Interview questions and answers about databases and Hibernate/JPA.
date: 2026-08-02 00:00:00 +0100
categories: [ interview ]
tags: [ databases, hibernate, jpa, java ] # TAG names should always be lowercase
---

<!-- @formatter:off -->

> Note: This article contains mainly LLM generated content, but only such that has been human reviewed. 
> Nonetheless, it is still possible that something wasn't caught or simply I made a mistake. 
> If you see anything wrong or missing the point, please let me know in the comments.
{: .prompt-info }

> This article has multiple issues. Please help improve it or discuss these issues in the comments.  
> ⚠️ WARNING — MVCC snapshot scope is stated too broadly  
> ⚠️ WARNING — “Later readers” do not always see the new version  
> ⚠️ WARNING — Application validation alone does not guarantee consistency  
> ℹ️ INFO — Isolation wording can name the specific anomalies controlled  
> ℹ️ INFO — MVCC is not the only PostgreSQL isolation mechanism  
{: .prompt-warning }

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

Durability means success reported after `COMMIT` remains recoverable after a database crash.[^durability]

PostgreSQL relies on write-ahead logging (WAL): its recovery process can redo committed changes from the log, 
while MVCC is primarily about concurrent visibility rather than durability.

[^durability]: Under a database’s fully durable commit mode, once COMMIT successfully returns, 
    the committed transaction must be recoverable after a crash or power loss. 
    Modes such as delayed/asynchronous durability may acknowledge the commit before it is durable.

<!-- @formatter:on -->
