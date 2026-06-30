# CLAUDE.md

Guidance for working in this repository.

## What this is

komidawi's personal tech blog — a **Jekyll** site built on the
[Chirpy](https://github.com/cotes2020/chirpy-starter) theme (`jekyll-theme-chirpy` gem,
`~> 7.2`). It is published via GitHub Pages to **https://komidawi.it** (custom domain in
`CNAME`). The blog is a knowledge base of technical notes; content matters more than code
here, since the theme is consumed as a gem rather than vendored.

## Commands

```shell
bundle exec jekyll serve   # local dev server with live reload (alias: jekyll s)
bundle exec jekyll build   # build to _site/ (alias: jekyll b)
```

`run.ps1` just wraps `bundle exec jekyll serve`. Always prefix Jekyll commands with
`bundle exec`. First-time setup: `gem install jekyll bundler` then `bundle install`.

On Windows use RubyInstaller (not `rbenv-for-windows`, which is buggy) — see `README.md`.

## Content structure

Posts live under `_posts/`, organized into subfolders **purely for authoring convenience**
(`collections/`, `configs/`, `guides/`, `knowledge/<topic>/`). Jekyll flattens these; the
folder is *not* the URL or the category. Permalink is `/posts/:title/` (set in `_config.yml`).

- `_drafts/` — work-in-progress posts (not built in production; `.MD` files).
- `_tabs/` — sidebar nav pages (about, categories, tags, posts, legend).
- `_data/` — site data (`contact.yml`, `share.yml`, `locales/`).
- `assets/img/posts/<date-slug>/` — per-post images, grouped by post.
- `assets/lib` — git **submodule** (chirpy-static-assets). Run `git submodule update --init`
  if it's empty; the deploy workflow currently has submodule checkout commented out.

### Post front matter convention

```yaml
---
title: Google Cloud Storage
date: 2024-03-20 00:00:00 +0100
categories: [ devops ]
tags: [ gcp, gcloud ]   # tags must always be lowercase
---
```

Filenames follow Jekyll's `YYYY-MM-DD-Title.MD` format (note: `.MD` uppercase is common
here). `categories`/`tags` drive the archive pages (`jekyll-archives`), independent of the
folder a post sits in.

### Emoji legend (see `_tabs/legend.md`)

Posts use a consistent symbol vocabulary: ❗ critical, ⚠️ important, 📝 useful, 🔨 hands-on,
💡 tip/good practice, 🛠️ troubleshooting. Reuse these rather than inventing new ones.

## Conventions

- **Formatting:** `.editorconfig` governs — 2-space indent everywhere except Markdown
  (4-space, no trailing-whitespace trim, max line 120). `.prettierrc` disables embedded
  language formatting. SCSS/JS use single quotes; YAML uses double quotes.
- `last_modified_at` is auto-derived from git history by `_plugins/posts-lastmod-hook.rb`
  (a post counts as modified once it has >1 commit) — don't set it manually.

## Customization vs. theme

`_sass/` overrides exist locally but most layout/logic comes from the gem. To inspect or
override theme files, copy them from the installed gem (`bundle show jekyll-theme-chirpy`)
into the matching local path. `_config.yml` is the main knob for site-wide behavior.

## Deployment

Two GitHub Actions workflows in `.github/workflows/` build and deploy to GitHub Pages on
push to **any branch** (`branches: ['**']`). `pages-deploy.yml` also runs `htmlproofer`
against the built `_site` (external links disabled). Production builds use
`JEKYLL_ENV=production`.

> Note: both `jekyll.yml` and `pages-deploy.yml` deploy to the same `pages` concurrency
> group and trigger on all branches — they overlap, so be mindful when pushing.
