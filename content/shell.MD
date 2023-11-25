---
layout: page
title: shell
permalink: /shell/
---

# Zsh + Oh-my-zsh + Powerlevel10k

1. You can shorthand autocomplete paths inside commands, like:
   - `cd /h/k/.s/`\
   ->  `cd /home/komidawi/.ssh`
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
3. Expand envs
   - `$USER<TAB>`\
   -> `komidawi`
4. Print parameters help
   `cat -<TAB>`\
    ```
    --help                  -- display help and exit
    --number            -n  -- number all output lines
    --number-nonblank   -b  -- number nonempty output lines, overrides -n
    --show-all          -A  -- equivalent to -vET
    --show-ends         -E  -- display $ at end of each line
    ```
5. Use `take` to `mkdir` and `cd` into it
   - `take folder/subfolder/finish`
   - it also works with zip archives, git repos and url downloads
6. Command history
   - `Ctrl + R`
   - Press again for next occurrence
7. Quick `cd`
   - You can move into folders by name of the folder without `cd`
8. Command parking
   - Press `Ctrl + Q` to hide current command
   - After invoking another command, the first one comes back
9. Open command in editor
   - `Ctrl + X`, `Ctrl + E`
   - Opens in default editor (`$EDITOR`)
   - Useful for long commands
10. cd shortcuts
    - `cd ....` \
    -> moves 4 levels up
    - `cd -`\
    -> jumps to previous path
11. Extensive search
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
    