---
title: Linux/Shell
date: 2024-03-26 00:00:00 +0100
categories: [ linux, shell ]
tags: [ linux, zsh, bash ] # TAG names should always be lowercase
---

## Table of Contents

* [Table of Contents](#table-of-contents)
* [Bash](#bash)
* [Zsh + Oh-my-zsh + Powerlevel10k](#zsh--oh-my-zsh--powerlevel10k)
* [All Terminals](#all-terminals)
* [Nano](#nano)
* [Top](#top)
* [Man pages](#man-pages)

## Interesting threads

- https://stackoverflow.com/a/42082956/10699128
    - "Difference between single and double quotes in Bash"
    - Comprehensive explanation of variable evaluation
    - `""`, `''`, `$`, `\`


- https://unix.stackexchange.com/a/444949/379903
    - "How can we run a command stored in a variable?"
    - Comprehensive explanation of functions
    - definition, evaluation, arguments

## Bash

1. Make `.sh` script fail on error (and not continue like nothing happened)!
    - `set -euo pipefail`
        - `-e`
            - exit immediately on error
        - `-u`
            - fail on unset variables
        - `-o pipefail`
            - fail on failed pipes
        - `-x`
            - before executing it command print it with arguments to stderr (like debug mode)
    - More info:  
      https://www.gnu.org/software/bash/manual/html_node/The-Set-Builtin.html
2. Complex alias
    - In general:
         ```bash
         aliasname() {
             command --firstParam --secondParam
         }
         ```
        - Example:
            ```bash
            getaks() {
                az aks list -g $1 -o $2
            }
            ```
    - Pass all arguments to alias
        - Use `$@`
        - Example:
            - ```bash
              maven() {
                mvn -T1C "$@"
              }
              ```
        - Effect: `maven install -DskipTests` -> `mvn -T1C install -DskipTests`
3. Function arguments
    - List:
        - `$@` - stores all the arguments in a list of string
        - `$*` - stores all the arguments as a single string
        - `$#` - stores the number of arguments
        - `"$@"` is actually not a list of strings, but a list of arguments.
    - `$@` expands to the arguments passed from the caller to a function or a script.
        - Inside a function, it expands to the arguments passed to such function.
        - If used in a script (outside a function), it expands to the arguments passed to such script.

## Zsh + Oh-my-zsh + Powerlevel10k

1. You can shorthand autocomplete paths inside commands, like:
    - `cd /h/k/.s/`\
      -> `cd /home/komidawi/.ssh`
2. History search by starting fragment
    - Example:
        ```bash
        nano /home/komidawi/.ssh/known_hosts
        nano /etc/hosts

        nano /h<UP>
        ```
      will auto-complete into
        ```bash
        nano /home/komidawi/.ssh/known_hosts
        ```
3. Show `n` last history entries (`0` shows all)
    - `history n`
4. Expand envs
    - `$USER<TAB>`\
      -> `komidawi`
5. Print parameters help
   `cat -<TAB>`\
    ```
    --help                  -- display help and exit
    --number            -n  -- number all output lines
    --number-nonblank   -b  -- number nonempty output lines, overrides -n
    --show-all          -A  -- equivalent to -vET
    --show-ends         -E  -- display $ at end of each line
    ```
6. Use `take` to `mkdir` and `cd` into it
    - `take folder/subfolder/finish`
    - it also works with zip archives, git repos and url downloads
7. Command history
    - `Ctrl + R`
    - Press again for next occurrence
8. Quick `cd`
    - You can move into folders by name of the folder without `cd`
9. List last visited dirs
    1. `d` (shortcut for `dirs -v`)
10. Command parking
    - Press `Ctrl + Q` to hide current command
    - After invoking another command, the first one comes back
11. Open command in editor
    - `Ctrl + X`, `Ctrl + E`
    - Opens in default editor (`$EDITOR`)
    - Useful for long commands
12. cd shortcuts
    - `cd ....` \
      -> moves 4 levels up
    - `cd -`\
      -> jumps to previous path
13. Extensive search
    - `ls *.txt`\
      -> text files
    - `ls **/*.txt`\
      -> text files (also within subdirectories)
    - `ls **/(START)*.*`
      -> files starting with word START
    - `ls **/*(END).*`
      -> files ending with word END
    - `ls **/*(WORD)*.*`
      -> files with WORD
    - `ls **/*(.)`
      -> search only files
    - `ls **/*(/)`
      -> search only folders
14. List recently open directories
    1. `d`
15. Copy to clipboard (WSL2)
    1. `command | clip.exe`

## All Terminals

1. Pretty format JSON
    - Use JQ, e.g.:
        - `cat file.json | jq`
    - Just colorize:
        - `jc -c`

## Nano

1. Search a phrase
    - `Ctrl + W`
2. Search next occurrence
    - `Ctrl + W`
    - `F6`
3. Previous occurrence
    - `Alt + Q`
4. Show help
    - `F1`
5. Show navigation help
    - `h`

## Top

1. Show process tree
    - `Shift + V`
2. Sort by CPU usage
    - `Shift + P`
3. Sort by Memory usage
    - `Shift + M`
4. Kill process
    - `k`

## Man pages

1. Search
    - `/<phrase>`, `Enter`
2. Next occurrence
    - `n`
