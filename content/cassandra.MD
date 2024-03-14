---
layout: page
title: Cassandra
permalink: /cassandra/
---

# Cassandra (and Cassandra Open-Source)

1. Start Cassandra
   - Use `./cassandra` to start
2. Check status
   - Use `./nodetool status` to check status
3. Set keyspace
   - `USE keyspace;`
4. Show keyspaces
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
     - Descending sorting can be helpful for Time Series data, as we're gonna have the latest data right on the top
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

# DataStax Enterprise (DSE)

1. Run Cassandra
   - `./dse cassandra`
2. Show status
   - `./dsetool status`
