---
title: How to use two separate git profiles at once
date: 2024-10-08 00:00:00 +0100
categories: [ git, guide ]
tags: [ git, guide ] # TAG names should always be lowercase
---

## Two separate git profiles simultaneously (e.g. Work and Home)

### Summary

How use two separate git profiles:

- without risk of forgetting to perform a switch
- without even needing to make switches
- having company profile automatically being used in company repositories
- having private profile being used in any other scenarios
- configure it once, forget it forever

### Solution

This solution assumes company has its own domain.

It binds work `.gitconfig` with company's git remote URIs, and uses private`.gitconfig` elsewhere.

```bash
[user]
	name = Name Surname
	email = private@mail.com
```
{: file=".gitconfig-private" }

```bash
[user]
	name = Name Surname
	email = work@company.com
```
{: file=".gitconfig-company" }

```bash
## Default: Private
[include]
    path = .gitconfig-private

## Work: Company
[includeIf "hasconfig:remote.*.url:git@company.com*/**"]
    path = .gitconfig-company
```
{: file=".gitconfig" }

And that's all.

No more private commits in company repo :)

#### Credits

_Solution is based on https://stackoverflow.com/a/77728201/10699128._

---

### Other considered solution

Probably worse than this one above, but still may be helpful in some scenarios.

```shell
# Company
# D:/
[includeIf "gitdir/i:d:/"]
     path = .gitconfig-company

# Private
# E:/
[includeIf "gitdir/i:e:/"]
     path = .gitconfig-private

# github
[includeIf "hasconfig:remote.*.url:git@github.com*/**"]
     path = .gitconfig-private
```
