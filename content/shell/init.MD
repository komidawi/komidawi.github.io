---
layout: page
permalink: /init/
---

# Installing things (new)

List things you want to install and give some tips instead of having sheer file with no context.

## Windows Terminal

Go to `C:\Users\komidawi\AppData\Local\Packages\Microsoft.WindowsTerminal_8wekyb3d8bbwe\LocalState` and change JSON content according to [settings.json](init/settings.json).

## oh-my-zsh

### plugins

#### ssh-agent

##### Install

Run `sudo apt install askpass` first

Then add `ssh-agent` to plugin list

Set these properties:
`zstyle :omz:plugins:keychain agents ssh #gpg`  
`zstyle :omz:plugins:keychain identities id_ed25519`  
`zstyle :omz:plugins:ssh-agent lazy yes` (don't run it on every shell startup)  
`zstyle :omz:plugins:ssh-agent quiet yes` (non-quiet mode will make p10k show warning on startup)

##### TODO

What gives `AddKeysToAgent`? Check [docs](https://github.com/ohmyzsh/ohmyzsh/tree/master/plugins/ssh-agent#lazy)

##### Docs

https://github.com/ohmyzsh/ohmyzsh/tree/master/plugins/ssh-agent

## aliases

## sdkman

## asdf

## JS

### nvm

#### nvmrc

## direnv

---

# Installing things (old)

```bash
#!/bin/bash

### CLI API (Setup)
USERNAME=$1

### SDKMAN
sudo apt-get install zip unzip -y
curl -s "https://get.sdkman.io" | bash
source "/home/$USERNAME/.sdkman/bin/sdkman-init.sh"
sdk version
sdk install java 17.0.9-tem

### zsh
sudo apt install zsh -y
zsh --version
chsh -s $(which zsh)

## oh-my-zsh
sh -c "$(curl -fsSL https://raw.githubusercontent.com/ohmyzsh/ohmyzsh/master/tools/install.sh)"
git clone --depth=1 https://github.com/romkatv/powerlevel10k.git ${ZSH_CUSTOM:-$HOME/.oh-my-zsh/custom}/themes/powerlevel10k
sed -i.original -e 's/^ZSH_THEME=\"\(robbyrussell\)\"$/ZSH_THEME=\"powerlevel10k\/powerlevel10k\"/p' ~/.zshrc
exec zsh


## zsh-plugins

# zsh-autosuggestions
# https://github.com/zsh-users/zsh-autosuggestions/blob/master/INSTALL.md
git clone https://github.com/zsh-users/zsh-autosuggestions ${ZSH_CUSTOM:-~/.oh-my-zsh/custom}/plugins/zsh-autosuggestions

```

# `.zshrc`

```bash
# Command history
HISTFILE=$HOME/.zsh_history
HISTSIZE=999999999
SAVEHIST=$HISTSIZE

setopt hist_ignore_all_dups # remove older duplicate entries from history
setopt hist_reduce_blanks # remove superfluous blanks from history items
setopt inc_append_history # save history entries as soon as they are entered
setopt share_history # share history between different instances of the shell

# Autocorrect prompt
setopt correct_all # autocorrect commands

## Aliases
# auto move
if [[ $(pwd) == "/mnt/c/Windows/System32" ]]
        then cd ~
fi

# cd
alias cddev='cd /mnt/d/PRIV/LEARNING/DEVELOPER'
alias cdblog='cd /mnt/d/PRIV/LEARNING/DEVELOPER/#PUBLIC/docs'

# git
alias ga='git add'
alias gs='git status'
alias gcm='git commit'
alias gp='git push'

# commands
alias zshh='nano +118 ~/.zshrc'
alias zzsh='exec zsh'

# envs
export EDITOR=nano

# functions
jwt-decode() {
  jq -R 'split(".") |.[0:2] | map(@base64d) | map(fromjson)' <<< $1
}

# Ruby
export PATH="$HOME/.rbenv/bin:$PATH"
eval "$(~/.rbenv/bin/rbenv init - zsh)"
export PATH="$HOME/.rbenv/plugins/ruby-build/bin:$PATH"

# nvm
export NVM_DIR="$HOME/.nvm"
[ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh"  # This loads nvm
[ -s "$NVM_DIR/bash_completion" ] && \. "$NVM_DIR/bash_completion"  # This loads nvm bash_completion

#THIS MUST BE AT THE END OF THE FILE FOR SDKMAN TO WORK!!!
export SDKMAN_DIR="$HOME/.sdkman"
[[ -s "$HOME/.sdkman/bin/sdkman-init.sh" ]] && source "$HOME/.sdkman/bin/sdkman-init.sh"
```
