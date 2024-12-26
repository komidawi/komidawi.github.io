---
title: Mixed Knowledge
date: 2024-11-14 00:00:00 +0100
categories: [ mixed-tools, mixed-software, mixed-knowledge, tools, software, knowledge ]
tags: [ mixed-tools, mixed-software, mixed-knowledge, tools, software, knowledge ] # TAG names should always be lowercase
---

Content too short to constitute a separate entry.

## Git

- Push commits up to given commit:

```shell
git push origin {commitid}:{branch}
```

## Excel

- Select Column
    - `Ctrl + Space`
- Select Row
    - `Shift + Space`
- Deselect
    - `Shift + Backspace`
- Strikethrough
    - `Ctrl + 5`

## Regex

1. Non-greedy matching - `?`

## Gradle

To be able to run Kotlin Gradle project with `./gradlew run`, add into `build.gradle.kts`

```groovy
application {
    // assuming main file is Main.kt
    mainClass.set("it.komidawi.MainKt")
}
```
