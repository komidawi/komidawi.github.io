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
- [Virtual Nodes (VNodes)](#virtual-nodes-vnodes)
- [Gossip](#gossip)
- [Snitches](#snitches)
- [Replication](#replication)
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

# Virtual Nodes (VNodes)

1. Adding or removing Nodes from/to the system causes it to be unbalanced for certain amount of time
   - It also requires existing Nodes to stream data from/to other Nodes
2. VNode
   - It's a Virtual Node - an artificial SubNode within a real Node
   - It helps to distribute data more evenly
   - Real Node is responsible for multiple VNodes areas instead of just one big Node area
   - VNodes automate Token Range assignment
   - Setting number higher than `1` in `cassandra.yaml` `num_tokens` field turns on VNodes
3. Consistent Hashing
   - Idea: Let almost all Objects stay assigned to the same Servers even when number of Servers change
   - Old Way (Simple Hashing): Having direct reference from Object to the Server. When Server goes down, all references have to be reassigned to another Server
   - Implementation: Here both Objects and Servers have their Hashes. Servers overarch the full Hash Space - each Server has some Range of Hashes. After applying Hash Function on Object we compare it with provided Ranges and therefore find target Server. When new Server is added, it takes over only some subset of Ranges, thus only subset of Objects needs to be rearranged. Similarly is in case of removal of Server.
   - Improvement: To mitigate case when removal or adding a Server causes (too) many Objects to be moved (as the **whole** Range inhabitants have to be reassigned), the actual distribution of Node Ranges is "stripped" - Nodes have not one long continuous space, but they have many small spaces interlaced by other Nodes' spaces. Therefore a continuous space contains Ranges of multiple Nodes, not just one or even two.
4. Consistent Hashing, continuation
   - It makes distribution of Objects much more balanced
   - Adding/removing Node will require synchronization of less data per Node
     - Nodes will have smaller overhead
     - Synchronization will take less time as it will be more parallelized
5. Show ring info
   - e.g. which nodes own which token ranges
   - `nodetool ring`

# Gossip

1. Gossip
   - Broadcast Protocol for disseminating (spreading) data
   - In Cassandra there's no central cluster information holder, peers spread such info by themselves (automatically)
   - Info is spread out in a polynomial fashion, so as the more Nodes know that info, the more Nodes are sharing such info to another ones
   - Gossip round is initiated every second
   - There are 1-3 Nodes chosen to gossip
   - Nodes can Gossip to any other Node in the Cluster
   - There are some Node choosing criteria
     - e.g. Seed and Downed Nodes are slightly more favored
   - Nodes don't save data about which Nodes they Gossiped to
   - Only metadata are Gossiped, not any client data
2. **Endpoint State**
   - It's an overarching data structure each Node has, which contains:
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
3. Message flow
   - Node sends all info it possess about Nodes
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
4. Message Flow, continuation
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

# Snitches

6. Snitches
   - Report Node's Rack and DC
   - Determine "topology" of a cluster (which Nodes belong where)
   - Configured in `cassandra.yaml` `endpoint_snitch` field
   - 🔨 Be sure all Nodes in a Cluster use the same Snitch!
     - Also: changing Cluster network topology (and/or Snitches) requires restarting all Nodes
   - 🔨 Be aware that Racks and DCs are **logical** assignments to Cassandra. Please ensure that your logical racks and data centers align with your physical failure zones.
7. Types of Snitches
   - Regular
     - e.g. `SimpleSnitch`, `PropertyFileSnitch`, `GossipingPropertyFileSnitch`, `DynamicSnitch`
   - Cloud Based
     - Snitches for particular cloud environments like Ec2, Ec2 Multi Region, Cloudstack, Google Cloud
8. `SimpleSnitch`
   - Default
   - Places all Nodes in the same DC and Rack
   - Appropriate only for single DC deployments
9. `PropertyFileSnitch`
   - Reads DC and Rack info from `cassandra-topology.properties` file
   - File consists of rows in shape like `IP=DC:RAC`
   - When using this Snitch you have to keep files in sync across all Nodes in the Cluster!
10. `GossipingPropertyFileSnitch`
    - This should be your go-to Snitch for use in production
    - Declare each Node's info in `cassandra-rackdc.properties` files separately
    - You don't have to cope with manual sync like in regular `PropertyFileSnitch`
    - Gossip automatically spreads info through the Cluster
      - ```
        dc=DC1
        rack=RACK1
        ```
    - Note: Racks can have same names in different DCs, but these will be still **different** Racks (as they are in different DCs, indeed)
11. `RackInferringSnitch`
    - Infers DC and Rack from IP
    - e.g. `110.100.200.105`
      - `100` <- DC octet
      - `200` <- Rack octet
      - `105` <- Node octet
    - Be sure to read docs about it as it's kinda simple yet complex solution!
12. `DynamicSnitch`
    - Layered on top of current Snitch (wrapper)
    - Turned on by default for all Snitches
    - Monitors Cluster health and performance
    - Determines which Node to query so it will be the most efficient choice
      - Thanks to that e.g. sluggish Nodes aren't overwhelmed and traffic goes to most performant ones at a given time

# Replication

7. Replication
   - With `RF=1` there's only one "instance" of data
     - So if Node is unavailable, this data availability is equal to 0%
   - With `RF=2` Nodes keep their own data and their neighbors' data as well
   - `RF=3` seems to be a really decent (even preferable) factor in general.
     - It's just a sweet spot.
     - Any Node has data from both his neighbors.
     - Scenario when three Servers get down together has a pretty low probability
8. Multi DC Replication
   - In Multi DC, Coordinator replicates data just like in a single DC, but of course according to DC's RF setting
     - In another DC there becomes a Local Coordinator who does it
   - RF can be different for different DCs
     - 🔨 You can e.g. disable replication when due to law data must not leave the country

# Cassandra

1. Start Cassandra
   - Use `cassandra` to start

# Nodetool

1. Check status
   - `nodetool status`
