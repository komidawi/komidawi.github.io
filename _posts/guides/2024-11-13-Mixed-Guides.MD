---
title: Mixed Guides
date: 2024-11-13 00:00:00 +0100
categories: [ guides, mixed-guides ]
tags: [ mixed-guides ] # TAG names should always be lowercase
---

Guides too short to be separated.

## How to use Postman without need to register

Install Postman `10.13.6` - the last version before account requirement.

Add this to `hosts` file:

```
# Disable Postman auto-update
0.0.0.0         dl.pstmn.io
0.0.0.0         sync-v3.getpostman.com
0.0.0.0         getpostman.com
0.0.0.0         go.pstmn.io
```

Now app won't be able to update anymore.

## How to fix Docker problems

### Problem: failed to resolve source metadata

```
failed to solve: <registry_url>/eap:7.1: failed to resolve source metadata for <registry_url>/eap:7.1: failed to do request:
Head "https://<registry_url>/eap/manifests/7.1": dialing <registry_url>:443 container via 
direct connection because static system has no HTTPS proxy: connecting to <ipv4>:443: dial tcp <ipv4>:443: 
connectex: No connection could be made because the target machine actively refused it.</code>
```

### Workaround

Try to pull directly from command line

`docker pull <registry_url>/eap:7.1`
