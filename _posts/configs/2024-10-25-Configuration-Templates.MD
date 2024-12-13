---
title: Configuration Templates
date: 2024-10-25 00:00:00 +0100
categories: [ config ]
tags: [ config, config-templates, repository, git ] # TAG names should always be lowercase
---

# Git

## Config

Auto CRLF is EVIL. Absolutely set it to FALSE.

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

### `.editorconfig`

```editorconfig
root = true

# Globals
[*]
charset = utf-8
end_of_line = lf
indent_style = space

insert_final_newline = true
trim_trailing_whitespace = true

# TypeScript
[{*.ats,*.cts,*.mts,*.ts}]
quote_type = single
ij_typescript_spaces_within_imports = true
ij_typescript_spaces_within_object_literal_braces = true
ij_typescript_keep_simple_methods_in_one_line = true

# Markdown
[{*.markdown,*.md}]
max_line_length = off
trim_trailing_whitespace = false
ij_markdown_force_one_space_after_blockquote_symbol = false

# Bugged options - you have to set them in IntelliJ Options
# ij_markdown_min_lines_around_block_elements = 0
# ij_markdown_max_lines_around_block_elements = 1

# Properties (Spring)
[{*.properties,spring.handlers,spring.schemas}]
ij_properties_keep_blank_lines = true
```
{: file=".editorconfig" }
