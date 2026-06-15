# Demo CI/CD Nivel 2 - Spring Boot JDK 17 + Jenkins + SonarQube + Docker 

Este proyecto es una base didáctica para una sesión docente de CI/CD de nivel intermedio.

Incluye:

- Aplicativo Java Spring Boot con JDK 17.
- Pruebas unitarias y de controlador.
- Cobertura con JaCoCo.
- Análisis de calidad con SonarQube.
- Validación de Quality Gate.
- Jenkins Pipeline.
- Construcción de imagen Docker.
- Despliegue de la aplicación como contenedor.
- Docker Compose para Jenkins, SonarQube, PostgreSQL y despliegue opcional.

---

## 1. Arquitectura de la demo

```text
Codigo fuente
    ↓
Jenkins Pipeline
    ↓
mvn clean verify
    ↓
JUnit + JaCoCo
    ↓
SonarQube
    ↓
Quality Gate
    ↓
docker build
    ↓
docker run
    ↓
App Spring Boot desplegada
```

Servicios principales:

| Servicio         |             URL local | Descripción                |
| ---------------- | --------------------: | -------------------------- |
| Jenkins          | http://localhost:8080 | Orquestador CI/CD          |
| SonarQube        | http://localhost:9000 | Calidad de código          |
| Aplicativo       | http://localhost:8081 | App Spring Boot desplegada |
| PostgreSQL Sonar |               Interno | Base de datos de SonarQube |

---

## 2. Requisitos

Instalar previamente:

- Docker Desktop o Docker Engine.
- Git, si usarás repositorio SCM.
- Navegador web.
- Opcional: JDK 17 y Maven, solo si deseas probar la app fuera de Docker.

> En Linux, SonarQube puede requerir ajustar parámetros del sistema:
>
> ```bash
> sudo sysctl -w vm.max_map_count=524288
> sudo sysctl -w fs.file-max=131072
> ```

---

## 3. Levantar la plataforma

Desde la raíz del proyecto:

```bash
docker compose up -d --build
```

Verificar contenedores:

```bash
docker ps
```

Servicios esperados:

```text
jenkins-demo
sonarqube-demo
postgres-sonar-demo
```

---

## 4. Ingresar a Jenkins

Abrir:

```text
http://localhost:8080
```

Obtener contraseña inicial:

```bash
docker exec jenkins-demo cat /var/jenkins_home/secrets/initialAdminPassword
```

Instalar plugins sugeridos por Jenkins si lo solicita.

La imagen ya incluye plugins básicos para Pipeline, Git, credenciales, JUnit y vista de stages.

---

## 5. Ingresar a SonarQube

Abrir:

```text
http://localhost:9000
```

Credenciales iniciales:

```text
Usuario: admin
Clave: admin
```

SonarQube solicitará cambiar la clave inicial.

Luego crear un token:

```text
My Account → Security → Generate Token
```

Nombre sugerido del token:

```text
jenkins-demo-token
```

Copiar el token generado.

---

## 6. Registrar token de SonarQube en Jenkins

En Jenkins:

```text
Manage Jenkins → Credentials → System → Global credentials → Add Credentials
```

Configurar:

| Campo | Valor |
|---|---|
| Kind | Secret text |
| Secret | token generado en SonarQube |
| ID | sonar-token |
| Description | Token SonarQube demo |

El ID debe ser exactamente:

```text
sonar-token
```

porque el `Jenkinsfile` lo usa así:

```groovy
withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')])
```

---

## 7. Crear el pipeline en Jenkins

### Opción A: con Git, recomendada para explicar CI/CD

1. Crear un repositorio Git.
2. Subir todo el contenido de esta carpeta.
3. En Jenkins crear un nuevo item tipo `Pipeline`.
4. Elegir:

```text
Pipeline script from SCM
```

5. Configurar el repositorio Git.
6. Script Path:

```text
Jenkinsfile
```

7. Ejecutar `Build Now`.

---

### Opción B: demo local sin Git remoto

Esta opción sirve para una clase rápida.

1. Crear un nuevo item tipo `Pipeline`.
2. En `Pipeline`, seleccionar:

```text
Pipeline script
```

3. Copiar y pegar el contenido del archivo `Jenkinsfile`.
4. Ejecutar `Build Now`.

El pipeline intentará hacer `checkout scm`. Si no hay SCM configurado, copiará las fuentes desde:

```text
/workspace/ci-cd-springboot-nivel2
```

Esa carpeta existe porque está montada desde `docker-compose.yml`.

---

## 8. Validar resultados

### Jenkins

El pipeline debe ejecutar las etapas:

```text
Obtener codigo
Compilar, probar y generar cobertura
Analisis SonarQube
Validar Quality Gate
Construir imagen Docker
Desplegar aplicativo
Smoke test
```

### SonarQube

Abrir:

```text
http://localhost:9000
```

Debe aparecer el proyecto:

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

### Aplicativo

Abrir:

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

Health check:

```text
http://localhost:8081/actuator/health
```

Respuesta esperada:

```json
{
  "status": "UP"
}
```

---

## 9. Ejecutar la app manualmente sin Jenkins

También puedes probar el aplicativo directamente:

```bash
cd app
mvn clean verify
docker build -t demo-springboot:manual .
docker run --rm -p 8081:8080 demo-springboot:manual
```

O usando Docker Compose:

```bash
docker compose --profile manual-app up -d --build app-demo
```

---

## 10. Qué demuestra este Nivel 2

Este laboratorio demuestra:

| Tema | Evidencia |
|---|---|
| Compilación automatizada | `mvn clean verify` |
| Pruebas unitarias | Reportes JUnit en Jenkins |
| Cobertura | JaCoCo XML y HTML |
| Calidad de código | Proyecto en SonarQube |
| Quality Gate | Pipeline falla si Sonar no aprueba |
| Empaquetado | JAR Spring Boot |
| Contenerización | `docker build` |
| Despliegue | `docker run` de la app |
| Verificación post-deploy | Smoke test con `/actuator/health` |

---

## 11. Comandos útiles

Ver logs de Jenkins:

```bash
docker logs -f jenkins-demo
```

Ver logs de SonarQube:

```bash
docker logs -f sonarqube-demo
```

Ver logs de la app desplegada:

```bash
docker logs -f demo-springboot-app
```

Eliminar app desplegada por Jenkins:

```bash
docker rm -f demo-springboot-app
```

Bajar todo el laboratorio:

```bash
docker compose down
```

Bajar todo y eliminar volúmenes:

```bash
docker compose down -v
```

---

## 12. Recomendaciones para explicar en clase

Para una sesión docente, puedes dividir la explicación así:

1. **Problema inicial:** compilar y desplegar manualmente es repetitivo y propenso a errores.
2. **CI:** Jenkins ejecuta compilación y pruebas.
3. **Calidad:** SonarQube evalúa cobertura, duplicación, bugs y code smells.
4. **Quality Gate:** no todo build exitoso debe llegar a despliegue.
5. **CD:** si el código pasa la calidad, Jenkins genera imagen Docker y despliega.
6. **Evidencia:** Jenkins, reportes JUnit, JaCoCo, SonarQube y app desplegada.

---

## 13. Nota de seguridad

Esta demo usa:

- Jenkins con acceso al socket Docker del host.
- Contraseñas simples para PostgreSQL de SonarQube.
- Token de SonarQube en Jenkins.
- Red Docker compartida.

Esto es aceptable para laboratorio académico, pero no debe usarse igual en producción.

Para producción se recomienda:

- Jenkins agents separados.
- Docker registry privado.
- Gestión segura de secretos.
- TLS/HTTPS.
- Usuarios no root.
- Políticas de calidad por rama.
- Ambientes separados: desarrollo, certificación y producción.
