---
title: Jekyll & Liquid
date: 2024-12-20 00:00:00 +0100
categories: [ frontend ]
tags: [ jekyll, liquid ] # TAG names should always be lowercase
---

## Liquid 

### Include file content relative to current file

```liquid
{% include_relative configs/configuration-file.yaml %}
```

### Include file content from `_include` directory

```liquid
{% include _include_cir_file %}
```

Note: Files in `_include` directory are not directly accessible via URL, 
as this resource is meant for reusable components (like partials) 
to be dynamically included in templates or posts. 
