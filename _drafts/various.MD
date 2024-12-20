##

How to deep dive into compatibility in Java / Maven / POM.

Example:

https://repo.maven.apache.org/maven2/org/apache/camel/springboot/spring-boot/3.10.0/spring-boot-3.10.0.pom

references

```xml
<!--  Spring-Boot target version  -->
<spring-boot-version>2.4.5</spring-boot-version>
```

## Codestyle

1. Again wildcard imports
2. Commity z pustymi zmianami, gdzie mnóstwo linii kodu nagle zamienia się w gwiazdkę, albo gwiazdka w mnóstwo linii
   kodu. Po prostu spam.
3. Ryzyko niełatwych do wykrycia błędów w przypadku zaimportowania wszystkiego (kiedy chcemy w tym konkretnym przypadku
   użyć czegoś akurat z innego pakietu)
4. https://www.javadude.com/posts/20040522-import-on-demand-is-evil/
5. https://stackoverflow.com/a/147461/10699128

## Faster Git on WSL

- https://markentier.tech/posts/2020/10/faster-git-under-wsl2/

- https://markentier.tech/posts/2021/10/autocrlf-true-considered-harmful/
