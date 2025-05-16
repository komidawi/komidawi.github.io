---
title: Mixed Knowledge
date: 2024-11-14 00:00:00 +0100
categories: [ mixed-tools, mixed-software, mixed-knowledge, tools, software, knowledge ]
tags: [ mixed-tools, mixed-software, mixed-knowledge, tools, software, knowledge ] # TAG names should always be lowercase
---

Everything that is too short to constitute a separate entry.

## OpenRewrite

### Usage

Add desired recipe into root `pom.xml`, e.g.:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.openrewrite.maven</groupId>
            <artifactId>rewrite-maven-plugin</artifactId>
            <version>6.2.2</version>
            <configuration>
                <exportDatatables>true</exportDatatables>
                <activeRecipes>
                    <recipe>org.openrewrite.java.spring.boot3.UpgradeSpringBoot_3_2</recipe>
                </activeRecipes>
            </configuration>
            <dependencies>
                <dependency>
                    <groupId>org.openrewrite.recipe</groupId>
                    <artifactId>rewrite-spring</artifactId>
                    <version>6.2.0</version>
                </dependency>
            </dependencies>
        </plugin>
    </plugins>
</build>
```

If `mvn rewrite:run` didn't work, open IntelliJ Maven menu and there find

PROJECT: Main -> Plugins -> rewrite -> `rewrite:run`


## Git

### Push commits up to given commit:

```shell
git push origin {commitid}:{branch}
```

### Git LFS

Draft notes of work with Git LFS

```text
https://rehansaeed.com/gitattributes-best-practices/

git lfs track "*.exe"

TODO: how multiple add one command?


but better i guess:

LFS gitattributes
*.exe filter=lfs diff=lfs merge=lfs -text
*.dll filter=lfs diff=lfs merge=lfs -text

git add .gitattributes

git rm --cached .\lib\*

git rm --cached ./\*.dll
git rm --cached ./\*.exe

git lfs status
git lfs ls-files


if encounter

Remote "origin" does not support the Git LFS locking API. Consider disabling it with:
$ git config lfs.https://github.com/komidawi/Anki.git/info/lfs.locksverify false
git@github.com: Permission denied (publickey).: exit status 255

then
start-ssh-agent
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
- Get value from last column
    - `=CHOOSEROWS(TOCOL(A:A; 1); -1)`

## Regex

1. `?`
    - Non-greedy matching

## Gradle

To be able to run Kotlin Gradle project with `./gradlew run`, add into `build.gradle.kts`

```groovy
application {
    // assuming main file is Main.kt
    mainClass.set("it.komidawi.MainKt")
}
```

## Espanso

Cursor position

Using them is very simple, just insert $|$ where you want the cursor to be positioned, in this case:

```yaml
  -   trigger: :div
      replace: <div>$|$</div>
```

