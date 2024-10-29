---
title: Cassandra
date: 2024-03-30 00:00:00 +0100
categories: [databases]
tags: [cassandra] # TAG names should always be lowercase
---

## Table of Contents

- [Table of Contents](#table-of-contents)
- [Theory](#theory)
- [Cassandra](#cassandra)
- [Keyspaces](#keyspaces)
- [Tables](#tables)
- [Partitions](#partitions)
- [Node / Nodetool](#node--nodetool)
- [Token Ring](#token-ring)
- [Peer-to-Peer](#peer-to-peer)
- [Virtual Nodes (VNodes)](#virtual-nodes-vnodes)
- [Gossip](#gossip)
- [Snitches](#snitches)
- [Replication](#replication)
- [Consistency](#consistency)
- [Hinted Handoff](#hinted-handoff)
- [Read Repair](#read-repair)
- [Write Path](#write-path)
- [Read Path](#read-path)
- [Compaction](#compaction)

## Theory

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

## Cassandra

1. Start Cassandra
    - Use `cassandra` to start
2. Logs are located in
    - `node/logs/`
    - e.g. `node/logs/system.log`

## Keyspaces

1. Keyspaces
    - Keyspaces are similar to relational "schemas"
    - Serve as a namespace (or wrapper) around DB objects
2. Create Keyspace
    - When creating Keyspace you must provide replication parameters
    -
   `CREATE KEYSPACE <keyspace> WITH REPLICATION = { 'class': '<replication>', 'replication_factor': <replication_factor> }`
   - e.g. `CREATE KEYSPACE killrvideo WITH REPLICATION = { 'class': 'SimpleStrategy', 'replication_factor': 1 }`
3. Set Keyspace
    - `USE <keyspace>;`
4. Show Keyspaces
    - `DESCRIBE KEYSPACES;`
    - `SELECT * FROM system_schema.keyspaces;`

## Tables

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
   -
   `INSERT INTO videos (video_id, added_date, title) VALUES (1645ea59-14bd-11e5-a993-8138354b7e31, '2014-01-29', 'Cassandra History');`
3. Insert data from file
    - `COPY videos (video_id, added_date, title) FROM 'videos.csv' WITH HEADER = TRUE;`
4. Show info about table
    - `DESCRIBE TABLE my_table;`
5. Remove everything
    - `TRUNCATE videos`

## Partitions

1. Partition Key
    - It makes data grouped together
    - It's responsible for how the data is placed on the Ring
        - So basing on Partition Key you know where data is located on the Ring
    - It's a part of `PRIMARY KEY`
    - Partitioner uses Consistent Hashing algorithm
        - So with remapping hash table, only `number_of_keys / number_of_slots` keys have to be remapped
            - (in contrast to almost all keys in standard hashing)
        - Querying for data is `O(1)`
2. `PRIMARY KEY`
    - **The first value in `PRIMARY KEY` is always Partition Key**
    - To make `PRIMARY KEY` unique, additional ID is added (like ordinal ID or UUID)
    - You can't change `PRIMARY KEY` in existing data model
        - There's no `ALTER TABLE` and add column to `PRIMARY KEY`
3. Clustering Columns
    - **The first column in `PRIMARY KEY` is Partition Key, remaining ones are Clustering Columns**
        - `PRIMARY KEY((state), city, name, manual_unique_id)`
    - Data is **sorted** according to these Clustering Columns
        - (And they are sorted during the **insert**)
    - You can inverse sorting using `PRIMARY KEY (...) WITH CLUSTERING ORDER BY (city DESC, name ASC)`
        - 🔨 Descending sorting can be helpful for Time Series data, as we're gonna have the latest data right on the top
4. 🔨 Querying
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
6. Show token for a given column
    - `SELECT column, tag(column), other_column FROM table`

## Node / Nodetool

1. Node performance
    - Single Node can typically handle
        - `6000 - 12000 TRX/s/core`
        - `2 - 4 TB` of data
    - Warning: Run Cassandra on local storage or direct attached storage, **never run on SAN**.
        - If your Disc has an Ethernet cable, it's probably a wrong choice.
2. Show wide basic info
    - `nodetool info`
3. Check DC and Clusters status
    - `nodetool status`
4. Get stats for table (e.g. `SSTables info)
    - `nodetool tablestats keyspace1.standard1`
    - obsolete version: `nodetool cfstats keyspace1.standard1`
5. Show Cluster information
    - Settings common across all nodes, current schema used by each of them
    - `nodetool describecluster`
6. Show ring info
    - e.g. which Nodes own what Token Ranges
    - `nodetool ring`
7. Show Logging Levels
    - `nodetool getlogginglevels`
8. Set Logging Level
    - `nodetool setlogginglevel <logger> <level>`
        - e.g. `nodetool setlogginglevel org.apache.cassandra TRACE`
9. Set trace probability
    - It's percentage of queries being saved
    - `nodetool settraceprobability 0.1`
10. Show trace
    - Look at tables in `system_traces` keyspace
11. Map tags to endpoints/nodes
    - `nodetool getendpoints <keyspace> <table> <value>`
    - e.g. `nodetool getendpoints killrvideo videos_by_tag 'datastax'`
12. Gossip information
    - `nodetool gossipinfo`
13. Drain
    - Stops writes from occurring on the node and flushes all the data to disk.
    - Typically it may be run before stopping a Node
    - `nodetool drain`
14. Stop node execution
    - `nodetool stopdaemon`
15. Stress test
    - Located in `cassandra/tools/bin` directory
    - Example stress test:
        - `cassandra-stress write n=50000 no-warmup -rate threads=1`
16. Flush
    - Use this to all data written to MemTable be committed into the disk
    - `nodetool flush`
17. A way to rebuild `SSTables`
    - `nodetool upgradesstables (...)`

## Token Ring

1. Token Ring
    - Key concept for performance
    - Token Ring keeps track of tokens
    - Each Node is responsible for a range of data (Token Range)
    - Allows to know exactly which Nodes contain which Partitions
    - Helps to avoid Single Points of Failure
2. Token
    - `partition_key => token`
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
    - Other Nodes calculate (manually or automatically) where New Node will fit in the Ring
    - After joining, other Nodes stream data to the New Node (Joining status)
    - There's no any downtime during joining

## Peer-to-Peer

1. What are downsides of Leader-Follower design?
    - When leader goes down, you have downtime until a new Leader gets elected
    - When internode communication goes down, system may become highly inconsistent, as Leaders can't see Followers and
      vice versa
        - Then probably new Leaders will be elected, and then after connections goes back up, you may end up with
          multiple competing Leaders
    - Leader-Follower probably involves sharding, in which at least:
        - You lose advantages of `JOIN`, `GROUP BY` and other aggregations
        - You have to introduce some way of routing to/from shards
2. What makes Peer-to-Peer a great approach?
    - There are no Leaders, so there are no election issues
    - There is a Coordinator, which takes data and performs writes asynchronously to Replicas
    - You don't have to target any specific Node, you can reach any of them (as all are equal), and that Node will
      handle the rest
3. Split "Failure" Scenario
    - Each Node which can be seen by the client is still online
    - As long as it's possible to write to right Replicas, system is operational
    - Consistency Level determines if a split is a problem for the system or not especially

## Virtual Nodes (VNodes)

1. Adding or removing Nodes from/to the system causes it to be unbalanced for certain amount of time
    - It also requires existing Nodes to stream data from/to other Nodes
2. VNode
    - It's a Virtual Node - an artificial SubNode within a real Node
    - It helps to distribute data more evenly
    - Real Node is responsible for multiple VNodes areas instead of just one big Node area
    - VNodes automate Token Range assignment
    - Having `num_tokens` > `1` in `cassandra.yaml` makes VNodes turned on
3. Consistent Hashing
    - Idea: Let almost all Objects stay assigned to the same Servers even when number of Servers change
    - Old Way (Simple Hashing): Having direct reference from Object to the Server. When Server goes down, all references
      have to be reassigned to another Server
    - Implementation: Here both Objects and Servers have their Hashes. Servers cover the full Hash Space - each Server
      has some Range of Hashes. After applying Hash Function on Object we compare it with provided Ranges and therefore
      find target Server. When new Server is added, it takes over only some subset of Ranges, thus only subset of
      Objects needs to be rearranged. Similarly is in the case of removal of a Server.
    - Improvement: To mitigate case when removal or adding a Server causes (too) many Objects to be moved (as the *
      *whole** Range inhabitants have to be reassigned), the actual distribution of Node Ranges is "stripped" - Nodes
      have not one long continuous space, but they have many small spaces interlaced by other Nodes' spaces. Therefore a
      continuous space contains Ranges of multiple Nodes, not just one or even two.
4. Consistent Hashing, continuation
    - It makes distribution of Objects much more balanced
    - Adding/removing Node will require synchronization of less data per Node
        - Nodes will have smaller overhead
        - Synchronization will take less time as it will be more parallelized
5. Show ring info
    - e.g. which nodes own which token ranges
    - `nodetool ring`

## Gossip

1. Gossip
    - Broadcast Protocol for disseminating (spreading) information
    - In Cassandra there's no central cluster information holder, peers spread such info by themselves (automatically)
    - Info is spread out in a polynomial fashion
        - so as the more Nodes know that info, the more Nodes are sharing such info to another ones
    - Gossip round is initiated every second
    - There are 1-3 Nodes randomly\* chosen to Gossip
    - Nodes can Gossip to any other Node in the Cluster
        - \* Actually there are some Node choosing criteria
            - e.g. Seed and Downed Nodes are slightly more favored
    - Nodes don't save data about which Nodes they Gossiped to
    - Only metadata are Gossiped, not any client data
2. **Endpoint State**
    - It's a data structure each Node has, containing:
        - **Heartbeat State**
            - `generation` - a timestamp of when Node bootstrapped
            - `version` - a number incremented by Node every second
        - **Application State**
            - Stores metadata of the Node, like:
            - `STATUS=NORMAL` - the one we see when executing `nodetool status`
            - `DC=west` - data center
            - `RACK=rack1` - rack
            - `SCHEMA=c2a2b...` - changes as schema mutates over time
            - `LOAD=100.0` - disk space used
            - ...and some other metadata
3. Gossip Message flow
    - Node sends info it possesses about Nodes
    - Each digest contains `IP address`, `generation`, `version` (Complete Heartbeat)
    - The flow is:
        1. `SYN`
            - Sender initiates Gossip sending `SYN` to Recipient
            - Recipient examines `SYN` and checks if values of `generation`/`versions` are newer that current ones
        2. `ACK`
            - Recipient sends `ACK` back to Sender
            - For data outdated from Recipient perspective, Recipient sends related Heartbeats
            - For data newer from Recipient perspective, Recipient sends fresh Endpoint States
        3. `ACK2`
            - Sender sends `ACK2` to Recipient with fresh Endpoint States for endpoints Recipient had stale data
4. Gossip Message Flow, continuation
    - Constant rate of network usage (trickle)
        - Doesn't case network spikes
    - Minimal message size in comparison with "real" table data
    - Actually just a lightweight background process
5. Gossip information
    - Nodetool
        - `nodetool gossipinfo`
    - You can also get Gossip data with query like
        - `SELECT peer, data_center, host_id, rack, release_version, rpc_address, schema_version FROM system.peers;`
        - (And for current Gossip info look at `system.local` table)

## Snitches

1. Snitches
    - Report Node's Rack and DC
    - Determine "topology" of a cluster (which Nodes belong where)
2. Snitches, continuation
    - Configured in `endpoint_snitch` in `cassandra.yaml`
    - 🔨 Be sure all Nodes in a Cluster use the same Snitch!
        - Also: changing Cluster network topology (and/or Snitches) requires restarting all Nodes
    - 🔨 Be aware that Racks and DCs are **logical** assignments to Cassandra. Please ensure that your logical racks and
      data centers align with your physical failure zones.
3. Types of Snitches
    - Regular
        - e.g. `SimpleSnitch`, `PropertyFileSnitch`, `GossipingPropertyFileSnitch`, `DynamicSnitch`
    - Cloud Based
        - Snitches for particular cloud environments like Ec2, Ec2 Multi Region, Cloudstack, Google Cloud
4. Snitches Comparison
    1. `SimpleSnitch`
        - Default
        - Places all Nodes in the same DC and Rack
        - Appropriate only for single DC deployments
    2. `PropertyFileSnitch`
        - Reads DC and Rack info from `cassandra-topology.properties` file
        - File consists of rows in shape like `IP=DC:RAC`
        - When using this Snitch you have to keep files in sync across all Nodes in the Cluster!
    3. 🔨 `GossipingPropertyFileSnitch`
        - This should be your go-to Snitch for use in production
        - Declare each Node's info in `cassandra-rackdc.properties` files separately
        - You don't have to cope with manual sync like in regular `PropertyFileSnitch`
        - Gossip automatically spreads info through the Cluster
            - ```
          dc=DC1
          rack=RACK1
          ```
        - Note: Racks can have same names in different DCs, but these will be still **different** Racks (see it like
          namespace, e.g. `DC1.RACK1` and `DC2.RACK1` are separate ones)
    4. `RackInferringSnitch`
        - Infers DC and Rack from IP
        - Be sure to read docs about it as it's kinda simple yet complex solution!
    5. 🔨 `DynamicSnitch`
        - Layered on top of current Snitch (wrapper)
        - Turned on by default for all Snitches
        - Monitors Cluster health and performance
        - Determines which Node to query so it will be the most efficient choice
            - Thanks to that e.g. sluggish Nodes aren't overwhelmed and traffic goes to most performant ones at a given
              time

## Replication

1. Replication
    - With `RF=1` there's only one "instance" of data
        - So if Node is unavailable, this data availability is equal to 0%
    - With `RF=2` Nodes keep their own data and their neighbors' data as well
    - 🔨 `RF=3` seems to be a really decent (even preferable) factor in general.
        - It's just a sweet spot.
        - Any Node has data from both his neighbors.
        - Scenario when three Servers get down together has a pretty low probability
2. Multi DC Replication
    - In Multi DC, Coordinator replicates data just like in a single DC, but of course according to DC's RF setting
        - In another DC there becomes a Local Coordinator who does it
    - RF can be different for different DCs
        - 🔨 You can e.g. disable replication when due to law data must not leave the country

## Consistency

1. Consistency Levels - CL
    - `ONE` - just one available Node is sufficient for data to be processed successfully
    - `QUORUM` (MAJORITY) - it's a reasonable sweet spot
    - `ALL` - here you just remove Partition Tolerance from your system (Total Consistency)
    - Any many, many others.. (But actually only for sophisticated purposes)
    - And don't use `ANY`
2. Strong Consistency
    - It can be described as
        - _"When I try to read data that I've just wrote, I'll always receive exactly such data
          and **never anything stale**"_
    - You can have e.g. `Write CL = ALL; Read CL = ONE` and have Strong Consistency (but this example it's rather not a
      great choice..)
    - The reasonable way is to have both `Write CL = Read CL = QUORUM`.  
      You have Strong Consistency here without having to Write/Read on all Nodes!
3. Consistency Levels - Comparison
    1. `ONE`
        - It can be used for eventually consistent data
        - For example: log data, IoT data
        - And it's just a tradeoff of having faster system but with slower consistency
            - However "slower" can be just even in range of millis (within DC)
    2. `LOCAL_QUORUM`
        - In Multi DC, standard `QUORUM` would mean that confirmations would have come from all Nodes across all DCs. Do
          expect high latency (at least).
        - Local Quorum handles this making Quorum limited only to given DC
4. Check current CL
    - `CONSISTENCY;`
5. Set CL
    - `CONSISTENCY <level>;`

## Hinted Handoff

1. Hinted Handoff
    - Hinted Handoff allows to be able to handle writes, even when Nodes are down
    - In short, Coordinator holds data and waits for Node to become available
2. Check the scenario:
    1. Write request arrives into a Cluster with `RF=3`
    2. Coordinator sends data into Replicas, however one Replica is down
    3. So then Coordinator stores data to send it when Node eventually goes up
3. Hints
    - Hints are stored in directory specified in `cassandra.yaml`
    - Hints can be disabled
    - Hints do not fulfill RF/CL settings
        - e.g. when you execute Read Query when all relevant Nodes are down (and Write Data is stored merely as Hints),
          that Query will fail
4. Default time Hint is stored is `3 hours`
    - After that time a given write is just lost
    - Also if Coordinator dies during this time, data will be lost as well
    - (That's why `CL=ANY` is quite not reasonable)
5. Show saved Hints
    - `ls -l node/data/hints`

## Read Repair

1. Read Data Flow for `CL=ALL`
    1. Coordinator gets a single instance of actual data and checksums
        - The most performant Node is asked for data, other Nodes are asked just for checksums
    2. Checksums are compared
    3. When checksums don't match, Read Repair takes place
        1. Coordinator asks Nodes for real data
        2. Coordinator compares the real data and chooses data instance with highest timestamp value
        3. Coordinator sends this data instance for all Nodes having outdated data  
           At the same time result is returned to the client
2. Read Repair
    - Proactive activity improving Consistency
    - Read Repair happens only when `Read CL` < `ALL`
        - (In other words: if there's a possibility of getting out of sync, Cassandra can proactively try to fix
          potentially existing inconsistencies)
    - Process happens asynchronously in the background
        - Note: Even if Read Repair repairs a given portion of data, client will receive stale data as process happens
          at the same time when data are returned
    - By default `dclocal_read_repair_chance` (Read Repair taking place in the same DC as Coordinator) is set to `0.1` (
      10%)
    - By default `read_repair_chance` (Read Repair taking place across all DCs) is set to `0`
3. Full Repair
    - You can use `nodetool` to manually execute Full Repair
        - But beware! It's extremely resource intensive operation and can even get down your entire Cluster!
        - Treat it as a last line of defense
    - Full Repair synchronizes all data in Cluster
    - If to do so, then only in Clusters servicing high writes/deletes
        - Or to synchronize a failed Node coming back online
        - Or run on Nodes that aren't read very often
4. Data is stored in
    - `/data/data/<keyspace>/`

## Write Path

1. When data arrives to a Node it goes into two places
    1. `MemTable` in RAM
        - `MemTable` is always (so at insert time) ordered by Partition Key and then by Clustering Columns
        - `MemTable` is for fast serving data to the user
    2. `CommitLog` on HDD
        - Located in `node/data/commitlog`
        - `CommitLog` is stored sequentially - every new record is appended at the very end
        - `CommitLog` is for safety purposes - in case of Node failure not to lost data present in memory
            - Node can perform event sourcing using `CommitLog` (e.g. after a sudden crash)
2. After saving data in both `MemTable` and `CommitLog`, Cassandra responds to Client that Write was successful
    - When flush time comes (e.g. when `MemTable` gets full), data is persisted in disk
        - Also `CommitLog` is erased, because it's no longer needed
    - Data is persisted in `SSTable` (Sorted String Table)
        - **`SSTable` is an immutable data structure**
        - Data in `SSTable` is always sorted by Partition Key and Clustering Columns
        - Partitions in `SSTable` are likely to have different sizes
    - As the time goes by, the more data is being added, more `SSTables` appear
3. 🔨 It is recommended to store `CommitLog` in a different physical disk than `SSTables`
    - `CommitLog` is just sequential write - append, append, append..
    - `SSTable` is random
    - So it is a good idea to split it - and then even on HDD seq-read will be surprisingly fast
4. Watching `CommitLog`
    - To see contents of `CommitLog` live, you can use such a bash command:
    - `watch -n 1 -d "ls -lh /data/commitlog`

## Read Path

1. Read requires to take data from both `MemTable` and `SSTables`
    1. Reading from `MemTable`
        - Very fast, as it lies in Memory
        - Also `MemTables` are organized by Partition Key (hash value / token), so finding data has complexity just of
          binary search comparing operations
    2. Reading from `SSTable`
        - Somehow slower, as it's on disk, not RAM
        - Notice that Partitions don't have the same size/length
        - Therefore we have file offsets for each given Partition
        - Such offsets lie in Partition Index file
2. Partition Index
    - It's an offset index
    - Thanks to it, Cassandra doesn't have to perform any search to reach data, as it has clear localization pointed in
      Partition Index
    - You can look at it like:
        - `token => partition_byte_offset`
    - `SSTables` can contain many Partitions in them what can lead to significant size of Partition Index
    - Also it's stored in Disk, so it's not as fast as RAM
3. Partition Summary
    - As Partition Index can grow large, there's another index - Partition Summary
    - Partition Summary is located in RAM
    - It samples every N keys
    - Provides an estimate of offset of Partition Index
    - `token => index_offset`
    - It means: for a given token, jump to specific area of Partition Index, where you will likely find that token and
      therefore you will be able to jump at Data Partition offset (where it really lies on disk)
4. Key Cache
    - Located in RAM
    - It can be manually enabled
    - `token => partition_byte_offset`
5. Bloom Filter
    - It's about existence, not localization of data
    - `token => yes / no / maybe`
    - Sometimes a False Read can happen - when Bloom says token/data is there, but it isn't (false positive)
    - Bloom Filter can be tuned for better results
        - `ALTER TABLE <table> WITH bloom_filter_fp_chance = <value>;`
            - The lower the `value`, the lower fp change, the bigger disk usage
6. The whole flow
    - input
    - -> Bloom Filter
    - -> Key Cache
    - -> Partition Summary
    - -> Partition Index
    - -> `SSTable`
    - data

## Compaction

1. Compaction is
    1. Removing stale data from persisted `SSTables`
    2. Combining multiple `SSTables` into one `SSTable`
        - Remember `SSTables` are immutable - nothing is modified, a brand new `SSTable` is created
2. Compaction is based on timestamps
    - the latest record wins
3. `gc_grace_seconds`
    - time tombstone entry is kept before it gets evicted
    - prevents resurrection of data in case of repair
    - defaults to `10 days`
4. Compaction Strategies
    - Performing one big ultimate all-round Compaction would be an overkill
    - Therefore there are particular strategies design for optimization of this process
5. Compaction Strategies, continued
    1. `SizeTiered` Compaction
        - Default
        - Triggers when there are multiple `SSTables` of a similar size
        - Fine for write-heavy workloads
    2. `Leveled` Compaction
        - Groups `SSTables` into Levels
            - each having fixed size limit being 10x larger than previous Level
        - Fine for read-heavy workloads
    3. `TimeWindow` Compaction
        - Creates time windowed buckets of `SSTables`
            - which are compacted (with each other) using `SizeTiered` Compaction
        - Fine for data coming in sequentially by time
