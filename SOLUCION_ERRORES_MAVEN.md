# Correcciones aplicadas al proyecto Semana 12

## Problema principal

El proyecto original tenia el archivo `pom.xml` dentro de la carpeta `app/`. Por eso, si un alumno ejecutaba:

```bash
mvn clean verify
```

desde la raiz del repositorio, Maven fallaba con un mensaje similar a:

```text
The goal you specified requires a project to execute but there is no POM in this directory
```

## Correccion aplicada

Se agrego un `pom.xml` padre en la raiz del repositorio con `packaging` tipo `pom` y el modulo `app`:

```xml
<modules>
    <module>app</module>
</modules>
```

Con esto ahora funcionan ambas opciones:

```bash
mvn clean verify
```

desde la raiz del proyecto, o:

```bash
cd app
mvn clean verify
```

## Requisitos en Mac

Validar Java y Maven:

```bash
java -version
mvn -version
```

Se recomienda JDK 17. Si `mvn -version` no muestra Java 17 o superior, se debe corregir `JAVA_HOME`.

## GitHub Actions

Se agrego el archivo:

```text
.github/workflows/ci.yml
```

El workflow ejecuta:

1. Checkout.
2. JDK 17.
3. `mvn -B clean verify` desde la raiz.
4. Upload de reportes Surefire.
5. Upload de cobertura JaCoCo.
6. Docker build.
7. Smoke test contra `/actuator/health`.

## Comando recomendado para clase

Desde la raiz del proyecto:

```bash
mvn clean verify
```

Luego:

```bash
docker build -t fitmanager-semana12:latest app
docker run -d --name fitmanager-semana12 -p 8081:8080 fitmanager-semana12:latest
curl http://localhost:8081/actuator/health
```
