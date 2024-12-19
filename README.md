# komidawi's blog

## Install

### Prerequisites

#### Node
- \[Linux] `nvm` is fine
- \[Windows] `nvm-for-windows` is fine

```shell
nvm -v
```

#### Ruby + Jekyll
- \[Linux] `rbenv` is fine
- \[Windows] don't use `rbenv-for-windows` - it's bugged
    - Use RubyInstaller - see [Official Jekyll Guide](https://jekyllrb.com/docs/installation/windows/)

```shell
ruby -v
```

### Installation

```shell
gem install jekyll bundler
bundle exec jekyll -v
```

## Run

```shell
bundle exec jekyll serve
```

or shorter:

```shell
bundle exec jekyll s
```

Remember to add `bundle exec` before all `jekyll` commands ([docs](https://jekyllrb.com/docs/usage/)).

---

Using [chirpy](https://github.com/cotes2020/chirpy-starter) theme
by [Cotes Chung (cotes2020)](https://github.com/cotes2020/) licensed
under [MIT License](MIT.LICENSE).
