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

# Cassandra

1. Start Cassandra
   - Use `cassandra` to start

# Nodetool

1. Check status
   - `nodetool status`
