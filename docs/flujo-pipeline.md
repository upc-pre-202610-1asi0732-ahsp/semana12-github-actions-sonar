# Flujo del Jenkins Pipeline

El pipeline está definido en el archivo:

```text
Jenkinsfile
```

## Etapas

### 1. Obtener código

Obtiene el código desde Git. Si no existe SCM configurado, usa la carpeta montada para demo local.

### 2. Compilar, probar y generar cobertura

Ejecuta:

```bash
mvn -B clean verify
```

Esto genera:

```text
target/surefire-reports/
target/site/jacoco/
target/site/jacoco/jacoco.xml
```

### 3. Análisis SonarQube

Ejecuta:

```bash
mvn sonar:sonar
```

Envia métricas hacia:

```text
http://sonarqube:9000
```

### 4. Validar Quality Gate

El script:

```text
ci/wait-for-sonar-quality-gate.sh
```

consulta la API de SonarQube hasta obtener el resultado del Quality Gate.

Si el estado no es `OK`, el pipeline falla.

### 5. Construir imagen Docker

Ejecuta:

```bash
docker build -t demo-springboot:$BUILD_NUMBER -t demo-springboot:latest app
```

### 6. Desplegar aplicativo

Ejecuta:

```bash
docker rm -f demo-springboot-app || true
docker run -d --name demo-springboot-app --network cicd-net -p 8081:8080 demo-springboot:latest
```

### 7. Smoke test

Valida que la app esté activa:

```bash
curl -f http://localhost:8081/actuator/health
```

Desde Jenkins puede usar:

```bash
http://host.docker.internal:8081/actuator/health
```

o la red Docker:

```bash
http://demo-springboot-app:8080/actuator/health
```
