---
layout: page
title: k8s
permalink: /k8s/
---

1. View logs since given timestamp
   - `k logs --since-time='2023-05-14T00:00:00Z' <POD-NAME>`
2. Get password from deployment secrets
   - ```shell
      k get deployments -> k describe deployment my-deployment
      k get secret db-password -o yaml
      echo <thepassword> | base64 -d
     ```
   - `k describe secret <secret>`
3. Switch namespace
   - `k config set-context --current --namespace=dev`
4. Show config (namespace etc.)
   - `k config view`
5. Print only namespace
   - `k config view --minify --output 'jsonpath={..namespace}'; echo`
