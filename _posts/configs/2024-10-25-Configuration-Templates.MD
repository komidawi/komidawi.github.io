---
title: Configuration Templates
date: 2024-10-25 00:00:00 +0100
categories: [ config ]
tags: [ templates, repository, git, gitignore, editorconfig, ai, gitattributes ] # TAG names should always be lowercase
---

## AI

### Custom AI

```text
Response as a Senior Software Engineer.
Unless otherwise instructed, answer concisely.
Prefer to use CLI tools to solve the asked problems. Use long-form command arguments.
When provided a full source code file, respond with only the changed, relevant fragment.
When asked to generate code, create simple, neat and correct solution.
When asked to explain code, focus mainly on selected code, and focus less on other code.
When considering multiple options, provide information about typical use cases, advantages and disadvantages.
At the end add a brief summary.
```

## Git

### ⚠️ Auto CRLF ️⚠️

Auto CRLF is **EVIL**. Absolutely set it to FALSE!

```shell
git config --global core.autocrlf false
git config --global core.eol lf
```

Or directly in `.gitconfig`:

```text
[core]
  autocrlf = false
  eol = lf
```
{: file=".gitconfig" }

For curious ones - some background behind CRLF:  
[https://markentier.tech/posts/2021/10/autocrlf-true-considered-harmful/](https://markentier.tech/posts/2021/10/autocrlf-true-considered-harmful/)

## Repository

### Configuration

- Linear history
- Squashing commits

### `.gitattributes`

```
# Force bash scripts to always use LF line endings so that if a repo is accessed
# in Unix via a file share from Windows, the scripts will work.
*.sh text eol=lf

# Force batch scripts to always use CRLF line endings so that if a repo is accessed
# in Windows via a file share from Linux, the scripts will work.
*.{cmd,[cC][mM][dD]} text eol=crlf
*.{bat,[bB][aA][tT]} text eol=crlf
```

### .gitignore

[Download file](/assets/configs/gitignore.txt)

### .editorconfig

[Download file](/assets/configs/editorconfig.txt)

### agnoster.zsh-theme

[Download file](/assets/configs/agnoster.zsh-theme.txt)

### Windows Terminal settings

[Download file](/assets/configs/settings.json)
