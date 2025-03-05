---
title: Jekyll & Liquid
date: 2024-12-20 00:00:00 +0100
categories: [ frontend ]
tags: [ jekyll, liquid ] # TAG names should always be lowercase
---

Knowledge, commands, configurations, guides and everything helpful while using Jekyll and Liquid.

## Liquid

### Escape Liquid

To avoid Liquid being interpreted, wrap it with between &#123;% raw %&#125; and &#123;% endraw %&#125;.

### Include file content relative to current file

{% raw %}
```liquid
{% include_relative configs/configuration-file.yaml %}
```
{% endraw %}

### Include file content from `_include` directory

{% raw %}
```liquid
{% include _include_cir_file %}
```
{% endraw %}

Note: Files in `_include` directory are not directly accessible via URL,
as this resource is meant for reusable components (like partials)
to be dynamically included in templates or posts.


## Jekyll

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
