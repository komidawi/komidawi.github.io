---
title: How to use Postman without need to register
date: 2024-11-13 00:00:00 +0100
categories: [ tools, guides ]
tags: [ postman ] # TAG names should always be lowercase
---

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
