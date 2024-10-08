## Solutions

1. WSL2 can't connect to internet
    - `net stop winnat` can cause it even if you make `net start winnat`
    - Use [this](https://github.com/microsoft/WSL/issues/3438#issuecomment-410518578) workaround instead:
        - ```
           netsh winsock reset
           netsh int ip reset all
           netsh winhttp reset proxy
           ipconfig /flushdns
          ```

2. Port is used
    1. `netsh int ip show excludedportrange protocol=tcp`
    2. `netsh int ipv4 add excludedportrange protocol=tcp startport=1234 numberofports=1`

3. New, probably better solution:
    1. `restart-service nhs`

## Regex

1. Non-greedy matching - `?`
2. Trim JWS header
    - `X-JWS-SIGNATURE=(.*?),`
    - `"Code":"(.*)?"`

## Handy life

- `.editorconfig`

## Nice software

- PotPlayer

## Windows

1. You can setup four-finger Desktop switching

## Tooling

1. Code Analysis
    1. secret leak prevention
        1. gitsecrets / gitleaks
    2. Vulnerabilities in code
        1. Snyk / GitHub Code Scanning
2. Secret management
    1. Vault by HashiCorp

## Windows

https://github.com/coreybutler/nvm-windows

## Maven

IntelliJ: PPM on `pom.xml` -> Show effective POM!

##

How to deep dive into compatibility in Java / Maven / POM.

Example:

https://repo.maven.apache.org/maven2/org/apache/camel/springboot/spring-boot/3.10.0/spring-boot-3.10.0.pom

references

```xml
<!--  Spring-Boot target version  -->
<spring-boot-version>2.4.5</spring-boot-version>
```

## Trivy

```shell
trivy fs --severity HIGH,CRITICAL --scanners vuln --dependency-tree . --skip-dirs .\.gitlab-ci-local\ --skip-dirs .\docker\
```

```shell
trivy image --severity HIGH,CRITICAL --scanners vuln --dependency-tree 9a0718e0aab1fa8698463d5a7a70515c9bd3b34af9f01dc65efa43d557de7460
```

## GitLab CI/CD

### gitlab-ci-local

1. Don't download artifacts every time for Maven
    1. ```yaml
       variables:
            MAVEN_OPTS: "-Dmaven.repo.local=$CI_PROJECT_DIR/.m2/repository"
       ```

###

Examples

https://gitlab.com/gitlab-org/gitlab-foss/-/blob/master/lib/gitlab/ci/templates/Maven.gitlab-ci.yml

## Repository Settings

- Linear history
- Squashing commits

## Codestyle

1. Again wildcard imports
2. Commity z pustymi zmianami, gdzie mnóstwo linii kodu nagle zamienia się w gwiazdkę, albo gwiazdka w mnóstwo linii
   kodu. Po prostu spam.
3. Ryzyko niełatwych do wykrycia błędów w przypadku zaimportowania wszystkiego (kiedy chcemy w tym konkretnym przypadku
   użyć czegoś akurat z innego pakietu)
4. https://www.javadude.com/posts/20040522-import-on-demand-is-evil/
5. https://stackoverflow.com/a/147461/10699128

## Build tools

- https://github.com/awslabs/git-secrets
- https://github.com/gitleaks/gitleaks

## Backend

### Java

- **OpenRewrite**
    - Great tool for upgrading Java language version and/or more complex dependencies like Spring Boot

- **SonarLint**
    - IntelliJ inspections extender

## Frontend

### Node, Angular, JS

- `npm`
- `npx`
- `nvm` / `nvm-windows`
- `.nvmrc`
