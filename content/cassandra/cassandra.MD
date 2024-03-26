---
layout: page
title: Cassandra
permalink: /cassandra/
---

# Table of Contents

- [Table of Contents](#table-of-contents)
- [Theory](#theory)
- [Keyspaces](#keyspaces)
- [Tables](#tables)
- [Partitions](#partitions)
- [Node](#node)
- [Token Ring](#token-ring)
- [Peer-to-Peer](#peer-to-peer)
- [Cassandra](#cassandra)
- [Nodetool](#nodetool)

# Theory

1. Cassandra Strengths
   1. **Scalability**
      - Linear scale performance
   2. **Reliability**
      - No single point of failure  
        (Peer-to-Peer leaderless architecture)
   3. **Availability**
2. Cassandra characteristics
   - IDs are created in an independent manner using UUIDs  
     (Also for time ordered data you can use TIMEUUID)

# Keyspaces

1. Keyspaces
   - Keyspaces are similar to relational "schemas"
   - Serve as a namespace (or wrapper) around DB objects
2. Create Keyspace
   - When creating Keyspace you must provide replication parameters
   - `CREATE KEYSPACE <keyspace> WITH REPLICATION = { 'class': '<replication>', 'replication_factor': <replication_factor> }`
     - e.g. `CREATE KEYSPACE killrvideo WITH REPLICATION = { 'class': 'SimpleStrategy', 'replication_factor': 1 }`
3. Set Keyspace
   - `USE <keyspace>;`
4. Show Keyspaces
   - `DESCRIBE KEYSPACES;`
   - `SELECT * FROM system_schema.keyspaces;`

# Tables

1. Create table
   - ```sql
        CREATE TABLE videos (
            video_id TIMEUUID,
            added_date TIMESTAMP,
            title TEXT,
            PRIMARY KEY (video_id)
        );
     ```
2. Insert data
   - `INSERT INTO videos (video_id, added_date, title) VALUES (1645ea59-14bd-11e5-a993-8138354b7e31, '2014-01-29', 'Cassandra History');`
3. Insert data from file
   - `COPY videos (video_id, added_date, title) FROM 'videos.csv' WITH HEADER = TRUE;`
4. Show info about table
   - `DESCRIBE TABLE my_table;`
5. Remove everything
   - `TRUNCATE videos`

# Partitions

1. Partition Key
   - It makes data grouped together
   - It's responsible for how the data is placed on the Ring
     - So basing on Partition Key you know where data is located on the Ring
   - It's a part of `PRIMARY KEY`
   - Partitioner uses consistent hashing algorithm
     - Querying for data is O(1)
2. `PRIMARY KEY`
   - The first value in `PRIMARY KEY` is always Partition Key
   - To make `PRIMARY KEY` unique, additional ID is added (like ordinal ID or UUID)
   - You can't change `PRIMARY KEY` in existing data model
     - There's no `ALTER TABLE` and add column to `PRIMARY KEY`
3. Clustering Columns
   - The first column in `PRIMARY KEY` is Partition Key, remaining ones are Clustering Columns
     - `PRIMARY KEY((state), city, name, manual_unique_id)`
   - Data is **sorted** according to these Clustering Columns
     - And it's sorted during the **insert**
   - You can inverse sorting using `PRIMARY KEY (...) WITH CLUSTERING ORDER BY (city DESC, name ASC)`
     - 🔨 Descending sorting can be helpful for Time Series data, as we're gonna have the latest data right on the top
4. Querying
   - **Always provide a Partition Key** - you ought to use it for the system to know where data lies.  
     Otherwise you'd end up with a full scan.
   - You can use `=`, `<`, `>` on Clustering Columns
   - **All equality comparisons must come before inequality operators**
   - Specify comparisons in exact order as in the `PRIMARY KEY`
   - As data is sorted on disk, range searches are a binary search followed by a linear read
5. `ALLOW FILTERING`
   - Makes Cassandra scan all partitions in the table
   - **Don't use it**
     - Unless you really, really have to do it
     - Avoid larger data sets
     - But still, pls, don't use it
   - **If you need such a query, there's probably something wrong with your data model**

# Node

1. Node performance
   - Single Node can typically handle
     - `6000 - 12000 TRX/s/core`
     - `2 - 4 TB` of data
   - Warning: Run Cassandra on local storage or direct attached storage, **never run on SAN**.
     - If your Disc has an Ethernet cable, it's probably a wrong choice.
2. Show info
   - `nodetool info`
3. Show Cluster information
   - Settings common across all nodes, current schema used by each of them
   - `nodetool describecluster`
4. Show Logging Levels
   - `nodetool getlogginglevels`
5. Set Logging Level
   - `nodetool setlogginglevel <logger> <level>`
     - e.g. `nodetool setlogginglevel org.apache.cassandra TRACE`
6. Set trace probability
   - It's percentage of queries being saved
   - `nodetool settraceprobability 0.1`
7. Show trace
   - Look at tables in `system_traces` keyspace
8. Drain
   - Stops writes from occurring on the node and flushes all the data to disk.
   - Typically it may be run before stopping a Node
   - `nodetool drain`
9. Stop node execution
   - `nodetool stopdaemon`
10. Stress test
    - Located in `cassandra/tools/bin` directory
    - Example stress test:
      - `cassandra-stress write n=50000 no-warmup -rate threads=1`
11. Flush
    - Use this to all data written to MemTable be committed into the disk
    - `nodetool flush`

# Token Ring

1. Token Ring
   - Key concept for performance
   - Token Ring keeps track of tokens
   - Each Node is responsible for a range of data (Token Range)
   - Allows to know exactly which nodes contain which partitions
   - Helps to avoid Single Points of Failure
2. Token
   - `hash_function(partition_key) => token`
   - Token is 64bit integer
     - So Token Range is `(-2^63 - 2^63-1)`
3. Partitioner
   - Determines how data will be distributed across the ring
   - Murmur3 or MD5 do fine distribution in a random fashion
4. Coordinator
   - Any Node can be a Coordinator
   - Thanks to Token Ring / Token Range, Coordinator knows where to relay data
5. Node joining a Cluster
   - New Node tells any existing Node about a will to join
   - Other Nodes calculate where he will fit in the Ring (manually or automatically)
   - After joining, other Nodes stream data to the New Node (Joining status)
   - There's no any downtime during joining

# Peer-to-Peer

1. What are downsides of Leader-Follower design?
   - When leader goes down, you have downtime until a new Leader gets elected
   - When internode communication goes down, system may become highly inconsistent, as Leaders can't see Followers and vice versa
     - Then probably new Leaders will be elected, and then after connections goes back up, you may end up with multiple competing Leaders
   - Leader-Follower probably involves sharding, in which at least:
     - You lose advantages of `JOIN`, `GROUP BY` and other aggregations
     - You have to introduce some way of routing to/from shards
2. What makes Peer-to-Peer a great approach?
   - There are no Leaders, so there are no election issues
   - There is a Coordinator, which takes data and performs writes asynchronously to Replicas
   - You don't have to target any specific Node, you can reach any of them (as all are equal), and that Node will handle the rest
3. Split "Failure" Scenario
   - Each Node which can be seen by the client is still online
   - As long as it's possible to write to right Replicas, system is operational
   - Consistency Level determines if a split is a problem for the system or not especially

# Cassandra

1. Start Cassandra
   - Use `cassandra` to start

# Nodetool

1. Check status
   - `nodetool status`
