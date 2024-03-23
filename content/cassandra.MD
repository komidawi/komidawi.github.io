---
layout: page
title: Cassandra
permalink: /cassandra/
---

# Cassandra (and Cassandra Open-Source)

## Commands

### Cassandra

1. Start Cassandra
   - Use `cassandra` to start
2. Show trace
   - Look at tables in `system_traces` keyspace
3. Set keyspace
   - `USE keyspace_name;`
4. Show keyspaces
   - `DESCRIBE KEYSPACES`
   - `SELECT * FROM system_schema.keyspaces;`
5. Show info about table
   - `DESCRIBE TABLE my_table;`
6. Clustering Columns
   - The first column is Partition Key, remaining ones are Clustering Columns
     - `PRIMARY KEY((state), city, name, manual_unique_id)`
   - Here, Clustering Columns are `city`, `name` and `manual_unique_id`
   - Data is **sorted** according to these Clustering Columns
     - And it's sorted during the **insert**
   - You can inverse sorting using `PRIMARY KEY (...) WITH CLUSTERING ORDER BY (city DESC, name ASC)`
     - 🔨 Descending sorting can be helpful for Time Series data, as we're gonna have the latest data right on the top
7. Querying
   - **Always provide a Partition Key** - you ought to use it for the system to know where data lies.  
     Otherwise you'd end up with a full scan.
   - You can use `=`, `<`, `>` on Clustering Columns
   - **All equality comparisons must come before inequality operators**
   - Specify comparisons in exact order as in the Primary Key
   - As data is sorted on disk, range searches are a binary search followed by a linear read
8. `ALLOW FILTERING`
   - Makes Cassandra scan all partitions in the table
   - **Don't use it**
     - Unless you really, really have to do it
     - Avoid larger data sets
     - But still, pls, don't use it
   - **If you need such a query, there's probably something wrong with your data model**
9. Stress test
   - Go to `tools` directory
   - Example stress test:
     - `cassandra-stress write n=50000 no-warmup -rate threads=1`

### Nodetool

1. Check status
   - `nodetool status`
2. Show info
   - `nodetool info`
3. Show ring info
   - `nodetool ring`
4. Show Cluster information
   - Settings common across all nodes, current schema used by each of them
   - `nodetool describecluster`
5. Show Logging Levels
   - `nodetool getlogginglevels`
6. Set trace probability
   - It's percentage of queries being saved
   - `nodetool settraceprobability 0.1`
7. Flush
   - `nodetool flush`
8. Drain
   - Stops writes from occurring on the node and flushes all the data to disk.
   - Typically it may be run before stopping a Node
   - `nodetool drain`
9. Map tags to endpoints/nodes
   - `nodetool getendpoints <keyspace> <table> <value>`
   - `nodetool getendpoints killrvideo videos_by_tag 'datastax'`
10. Stop node execution
    - `nodetool stopdaemon`

## Theory

1. Node performance
   - Warning: Run Cassandra on local storage or direct attached storage, but never on SAN. If your Disc has an Ethernet cable, it's probably a wrong choice.
   - Single Node can typically handle
     - `6000 - 12000 TRX/s/core`
     - `2 - 4 TB` of data
2. Token Ring
   - Key concept for performance
   - Token Ring keeps track of tokens
   - Allows to know exactly which nodes contain which partitions
   - Helps to avoid Single Points of Failure

# DataStax Enterprise (DSE)

1. Run Cassandra
   - `./dse cassandra`
2. Show status
   - `./dsetool status`
