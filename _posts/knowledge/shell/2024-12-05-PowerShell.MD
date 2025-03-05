---
title: PowerShell
date: 2024-12-05 00:00:00 +0100
categories: [ shell ]
tags: [ windows, powershell ] # TAG names should always be lowercase
---

Knowledge, commands, configurations, guides and everything helpful when working with PowerShell in Windows.

## Basics

**Set Output**

`Out-File` (bash `>`)

```powershell
dir | Out-File -FilePath .\output.txt
```

**Copy file**

`Copy-Item` (bash `cp`)

- `Path` - specify source
- `Destination` - specify target
- `Include` - specify file filter
- `Recurse` - invoke recursively (bash `-r`)
- `Force` - always overwrite (bash `-f`)
- `Confirm` - prompts before running the cmdlet
- `PassThru` - return an object (and by the way e.g. its path)

```powershell
Copy-Item `
  -Path "./source/directory/*" `
  -Destination "./target/directory" `
  -Include "*.jar" `
  -Recurse `
  -Confirm `
  -PassThru
```

**Remove file**

`Remove-Item` (bash `rm`)

```powershell
Remove-Item ./file.txt
```

**String interpolation**

```powershell
$VERSION = "2.1.1-SNAPSHOT"
$jarFile = "myapp-$VERSION.jar"
```

**Download File**

`Invoke-WebRequest ` (bash `curl`)

- `$ProgressPreference = 'SilentlyContinue'` - Disable progress bar

```powershell
Invoke-WebRequest localhost:8080/archive.zip -OutFile ./logs.zip
```

Alternative

```powershell
iwr localhost:8080/archive.zip -o logs.zip
```

## Various

**Check if file exists**

```powershell
if (Test-Path -Path $TARGET_FILE -PathType Leaf)
{
    # ...
}
```

## Shell behavior config

**Enable debugging**

`Set-PSDebug -Trace 1` (bash `set -x`)

**Stop execution on first error**

`$ErrorActionPreference` (bash `set -e`)

```powershell
$ErrorActionPreference = "Stop"
```

## PowerShell Profile

**Get location of PowerShell Profile**

- Note that this location actually may not contain any file - in such case it will be needed to create it manually

```powershell
$PROFILE
```

**Create PowerShell Profile if not exist**

- Creates `Microsoft.PowerShell_profile.ps1` file

```powershell
New-Item -path $PROFILE -type File -force
```

