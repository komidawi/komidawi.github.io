---
title: PowerShell (and cmd by the way)
date: 2024-12-05 00:00:00 +0100
categories: [ shell ]
tags: [ windows, powershell, cmd, ps ] # TAG names should always be lowercase
---

Knowledge, commands, configurations, guides and everything helpful when working with PowerShell in Windows.

## Basics

**Comments**

* `#`
    * Use hashtag for comments in PowerShell

**`Get-Item`**

Primarily used to retrieve a specific item at a given location like: file, folder, registry key (or other objects
supported by PowerShell providers).

* `Get-Item .`
    * Get object of current directory
* `gi .`
    * Shortcut for `Get-Item`

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

### Environmental Variables (envs)

**Show Env**

```powershell
# Short way
$env:EnvName

# Full way
[System.Environment]::GetEnvironmentVariable("VariableName", "User")
```

**Show Envs**

```powershell
# Available scopes: User, Machine, Process
[System.Environment]::GetEnvironmentVariables([System.EnvironmentVariableTarget]::User)
```

**Show Envs by name**

```powershell
# Normal way
Get-Item env:EnvName*

# Shortcut way
gi env:EnvName*
```

**Set Env (temporarily)**

```powershell
# Replace variable
$env:VariableName = "VariableValue"

# Append variable
$env:VariableName += ";VariableValue"

# Add to the beginning
$env:VariableName = 'VariableValue;' + $env:VariableName 
```

**Set Env (permanently)**

```powershell
[Environment]::SetEnvironmentVariable('VariableName', 'VariableValue')
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

### Customize Profile

Open profile e.g. by invoking `code $PROFILE`

#### Add aliases

**`git status` shortcut**

<!-- @formatter:off -->
```powershell
function Get-GitStatus { git status }
Set-Alias -Name gs -Value Get-GitStatus
```
<!-- @formatter:on -->

Note: Function name starts with `Get-`, because cmdlets have to use "approved verbs" (`UseApprovedVerbs` rule).  
You can list all approved verbs by `Get-Verb`
([source](https://learn.microsoft.com/en-us/powershell/utility-modules/psscriptanalyzer/rules/useapprovedverbs?view=ps-modules)).


---

## Command Prompt (cmd)

**Set variable**

```
set VariableName = VariableValue
```

**Set env**

```
setx VariableName VariableValue /m
```
