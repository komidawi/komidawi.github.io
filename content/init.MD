---
layout: page
title: init
permalink: /init/
---

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