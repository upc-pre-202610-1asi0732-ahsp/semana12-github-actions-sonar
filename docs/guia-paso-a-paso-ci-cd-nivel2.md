# Guía paso a paso - Laboratorio CI/CD Nivel 2

## Objetivo del ejercicio

Implementar un flujo CI/CD intermedio para un aplicativo Java Spring Boot con JDK 17 usando:

- Jenkins como orquestador del pipeline.
- SonarQube como herramienta de análisis de calidad.
- JaCoCo para cobertura de pruebas.
- Docker para construir y desplegar el aplicativo.
- PostgreSQL como base de datos de SonarQube.

El flujo completo será:

```text
Código fuente
   ↓
Jenkins
   ↓
Compilación Maven
   ↓
Pruebas unitarias
   ↓
Cobertura JaCoCo
   ↓
Análisis SonarQube
   ↓
Quality Gate
   ↓
Docker build
   ↓
Docker run
   ↓
Aplicativo Spring Boot desplegado
```

---

## 1. Requisitos previos

Antes de iniciar, validar que la máquina tenga instalado:

- Docker Desktop o Docker Engine.
- Git, si se usará repositorio.
- Navegador web.
- Opcional: JDK 17 y Maven, solo si se desea probar la aplicación localmente fuera de Docker.

En Windows, se recomienda usar:

- Docker Desktop con backend WSL2.
- PowerShell o Git Bash.

---

## 2. Descomprimir el proyecto

Descomprimir el archivo:

```text
ci-cd-springboot-nivel2.zip
```

Por ejemplo, en Windows:

```text
D:\LABS\ci-cd-springboot-nivel2
```

La estructura esperada es:

```text
ci-cd-springboot-nivel2/
├── app/
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/
├── ci/
│   └── wait-for-sonar-quality-gate.sh
├── docs/
├── jenkins/
│   ├── Dockerfile
│   └── plugins.txt
├── docker-compose.yml
├── Jenkinsfile
└── README.md
```

---

## 3. Validación recomendada antes de levantar Jenkins

El pipeline usa `curl` y `jq` para consultar el Quality Gate de SonarQube.

Abrir el archivo:

```text
jenkins/Dockerfile
```

Validar que instale `docker.io`, `maven`, `curl` y `jq`.

Contenido recomendado:

```dockerfile
FROM jenkins/jenkins:lts-jdk17

USER root

RUN apt-get update && \
    apt-get install -y docker.io maven curl jq && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

USER jenkins
```

Si tu archivo no tiene `curl jq`, agrégalos antes de construir los contenedores.

---

## 4. Levantar Jenkins, SonarQube y PostgreSQL

Ubicarse en la raíz del proyecto:

```bash
cd ci-cd-springboot-nivel2
```

Levantar los servicios:

```bash
docker compose up -d --build
```

Verificar los contenedores:

```bash
docker ps
```

Deberías ver algo similar a:

```text
jenkins-demo
sonarqube-demo
postgres-sonar-demo
```

---

## 5. Ingresar a Jenkins

Abrir en el navegador:

```text
http://localhost:8080
```

Obtener la clave inicial:

```bash
docker exec jenkins-demo cat /var/jenkins_home/secrets/initialAdminPassword
```

Copiar la clave y pegarla en la pantalla inicial de Jenkins.

Luego:

1. Seleccionar instalación de plugins sugeridos.
2. Crear usuario administrador.
3. Confirmar URL de Jenkins, por ejemplo:

```text
http://localhost:8080
```

---

## 6. Ingresar a SonarQube

Abrir en el navegador:

```text
http://localhost:9000
```

Credenciales iniciales:

```text
Usuario: admin
Clave: admin
```

SonarQube pedirá cambiar la contraseña inicial.

Usar una contraseña simple para laboratorio, por ejemplo:

```text
Admin12345
```

No usar contraseñas simples en ambientes reales.

---

## 7. Crear token en SonarQube

Dentro de SonarQube:

```text
My Account → Security → Generate Token
```

Nombre sugerido:

```text
jenkins-demo-token
```

Tipo:

```text
User Token
```

Generar el token y copiarlo.

Importante: el token solo se muestra una vez.

---

## 8. Registrar token de SonarQube en Jenkins

En Jenkins ingresar a:

```text
Manage Jenkins → Credentials → System → Global credentials → Add Credentials
```

Configurar:

| Campo       | Valor                       |
| ----------- | --------------------------- |
| Kind        | Secret text                 |
| Secret      | Token generado en SonarQube |
| ID          | sonar-token                 |
| Description | Token SonarQube demo        |

El ID debe ser exactamente:

```text
sonar-token
```

Esto es importante porque el `Jenkinsfile` lo usa con ese nombre.

---

## 9. Crear el pipeline en Jenkins

Hay dos formas de ejecutar el ejercicio.

---

## Opción A: Pipeline usando Git

Esta opción es la más parecida a un flujo real de CI/CD.

### 9.1. Crear repositorio Git

Desde la raíz del proyecto:

```bash
git init
git add .
git commit -m "Laboratorio CI/CD nivel 2"
```

Luego subirlo a GitHub, GitLab, Bitbucket o un Git local.

### 9.2. Crear pipeline en Jenkins

En Jenkins:

```text
New Item → Pipeline
```

Nombre sugerido:

```text
demo-ci-cd-springboot
```

Seleccionar:

```text
Pipeline script from SCM
```

Configurar:

```text
SCM: Git
Repository URL: URL de tu repositorio
Branch: main o master
Script Path: Jenkinsfile
```

Guardar y ejecutar:

```text
Build Now
```

---

## Opción B: Pipeline local sin Git remoto

Esta opción es más rápida para una sesión docente.

En Jenkins:

```text
New Item → Pipeline
```

Nombre sugerido:

```text
demo-ci-cd-local
```

Seleccionar:

```text
Pipeline
```

En la sección Pipeline elegir:

```text
Pipeline script
```

Copiar y pegar todo el contenido del archivo:

```text
Jenkinsfile
```

Guardar y ejecutar:

```text
Build Now
```

El `Jenkinsfile` intentará hacer `checkout scm`. Si no hay SCM configurado, usará las fuentes montadas desde:

```text
/workspace/ci-cd-springboot-nivel2
```

---

## 10. Etapas esperadas del pipeline

Al ejecutar el pipeline, Jenkins debe mostrar las siguientes etapas:

```text
Obtener codigo
Compilar, probar y generar cobertura
Analisis SonarQube
Validar Quality Gate
Construir imagen Docker
Desplegar aplicativo
Smoke test
```

### 10.1. Obtener código

Jenkins obtiene el código desde Git o desde la carpeta local montada.

### 10.2. Compilar y probar

Ejecuta:

```bash
mvn -B clean verify
```

Esto compila el proyecto, ejecuta pruebas y genera cobertura JaCoCo.

### 10.3. Analizar con SonarQube

Ejecuta:

```bash
mvn -B sonar:sonar
```

Envía a SonarQube:

- Código fuente.
- Resultado de pruebas.
- Cobertura JaCoCo.
- Métricas de calidad.

### 10.4. Validar Quality Gate

El script:

```text
ci/wait-for-sonar-quality-gate.sh
```

consulta el resultado del análisis en SonarQube.

Si el Quality Gate no aprueba, el pipeline falla y no despliega.

### 10.5. Construir imagen Docker

Jenkins ejecuta:

```bash
docker build -t demo-springboot:$BUILD_NUMBER -t demo-springboot:latest app
```

### 10.6. Desplegar aplicativo

Jenkins elimina el contenedor anterior, si existe, y crea uno nuevo:

```bash
docker rm -f demo-springboot-app || true

docker run -d \
  --name demo-springboot-app \
  --network cicd-net \
  -p 8081:8080 \
  demo-springboot:latest
```

### 10.7. Smoke test

Jenkins valida que la aplicación esté viva consultando:

```text
/actuator/health
```

---

## 11. Validar SonarQube

Abrir:

```text
http://localhost:9000
```

Buscar el proyecto:

```text
demo-ci-cd-springboot
```

Validar:

- Bugs.
- Vulnerabilities.
- Code Smells.
- Coverage.
- Duplications.
- Quality Gate.

---

## 12. Validar aplicación desplegada

Abrir en navegador:

```text
http://localhost:8081/actuator/health
```

Respuesta esperada:

```json
{
  "status": "UP"
}
```

Probar endpoint funcional:

```text
http://localhost:8081/api/greetings?name=Juan
```

Respuesta esperada:

```json
{
  "message": "Hola, Juan!",
  "nameLength": 4
}
```

---

## 13. Ver reportes en Jenkins

Dentro del build de Jenkins revisar:

```text
Console Output
```

También revisar:

```text
Test Result
```

Y artefactos archivados:

```text
app/target/site/jacoco/**
app/target/*.jar
```

---

## 14. Probar la aplicación manualmente, sin Jenkins

Esto sirve para explicar la diferencia entre proceso manual y proceso automatizado.

Desde la raíz del proyecto:

```bash
cd app
mvn clean verify
```

Construir imagen:

```bash
docker build -t demo-springboot:manual .
```

Ejecutar contenedor:

```bash
docker run --rm -p 8081:8080 demo-springboot:manual
```

Validar:

```text
http://localhost:8081/actuator/health
```

---

## 15. Comandos útiles

Ver logs de Jenkins:

```bash
docker logs -f jenkins-demo
```

Ver logs de SonarQube:

```bash
docker logs -f sonarqube-demo
```

Ver logs de PostgreSQL:

```bash
docker logs -f postgres-sonar-demo
```

Ver logs de la app:

```bash
docker logs -f demo-springboot-app
```

Eliminar app desplegada:

```bash
docker rm -f demo-springboot-app
```

Bajar los servicios principales:

```bash
docker compose down
```

Bajar todo y eliminar volúmenes:

```bash
docker compose down -v
```

Volver a construir Jenkins desde cero:

```bash
docker compose build --no-cache jenkins
```

---

## 16. Errores frecuentes y solución

### Error 1: SonarQube no inicia

Ver logs:

```bash
docker logs -f sonarqube-demo
```

En Linux o WSL puede requerir:

```bash
sudo sysctl -w vm.max_map_count=524288
sudo sysctl -w fs.file-max=131072
```

Luego reiniciar:

```bash
docker compose restart sonarqube
```

### Error 2: Jenkins no puede ejecutar Docker

Verificar que el socket Docker esté montado en `docker-compose.yml`:

```yaml
- /var/run/docker.sock:/var/run/docker.sock
```

Verificar dentro de Jenkins:

```bash
docker exec -it jenkins-demo docker version
```

### Error 3: El pipeline falla en Quality Gate por `jq: not found`

Agregar `jq` al archivo:

```text
jenkins/Dockerfile
```

Debe quedar:

```dockerfile
RUN apt-get update && \
    apt-get install -y docker.io maven curl jq && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*
```

Reconstruir Jenkins:

```bash
docker compose build --no-cache jenkins
docker compose up -d jenkins
```

### Error 4: Jenkins no encuentra credencial `sonar-token`

Revisar en Jenkins:

```text
Manage Jenkins → Credentials
```

Debe existir una credencial tipo `Secret text` con ID exacto:

```text
sonar-token
```

### Error 5: La aplicación no responde en el puerto 8081

Verificar si el contenedor está creado:

```bash
docker ps
```

Ver logs:

```bash
docker logs -f demo-springboot-app
```

Validar que el puerto no esté ocupado por otro proceso.

---

## 17. Guion sugerido para explicar en clase

### Parte 1: Introducción

Explicar el problema:

```text
Compilar, probar, revisar calidad y desplegar manualmente puede generar errores y pérdida de tiempo.
```

### Parte 2: Arquitectura

Mostrar:

```text
Jenkins + SonarQube + PostgreSQL + Docker + Spring Boot
```

### Parte 3: Aplicativo

Mostrar el código Spring Boot:

- Controlador REST.
- Servicio.
- Pruebas unitarias.
- Pruebas de controlador.
- Actuator health.

### Parte 4: Pipeline

Explicar cada etapa del `Jenkinsfile`.

### Parte 5: Calidad

Mostrar SonarQube:

- Bugs.
- Vulnerabilities.
- Code Smells.
- Coverage.
- Quality Gate.

### Parte 6: Despliegue

Mostrar que Jenkins genera imagen Docker y levanta el contenedor.

### Parte 7: Cierre

Explicar que este laboratorio es de nivel intermedio y que el siguiente nivel podría agregar:

- Docker Registry.
- Ambientes dev, qa y prod.
- Versionado de imágenes.
- Rollback.
- Notificaciones.
- Despliegue en Kubernetes.

---

## 18. Evidencias esperadas del ejercicio

Al finalizar, el alumno debería poder mostrar:

| Evidencia | Dónde verla |
|---|---|
| Pipeline ejecutado | Jenkins |
| Pruebas ejecutadas | Jenkins / surefire reports |
| Cobertura generada | JaCoCo |
| Proyecto analizado | SonarQube |
| Quality Gate validado | Jenkins y SonarQube |
| Imagen Docker creada | `docker images` |
| App desplegada | `http://localhost:8081` |
| Health check exitoso | `/actuator/health` |

---

## 19. Cierre conceptual

Este laboratorio demuestra un flujo CI/CD intermedio:

```text
No basta con que el código compile.
También debe pasar pruebas, tener cobertura, cumplir calidad y recién después desplegarse.
```

