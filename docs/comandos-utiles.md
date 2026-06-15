# Comandos útiles

## Levantar laboratorio

```bash
docker compose up -d --build
```

## Ver contenedores

```bash
docker ps
```

## Contraseña inicial de Jenkins

```bash
docker exec jenkins-demo cat /var/jenkins_home/secrets/initialAdminPassword
```

## Reiniciar Jenkins

```bash
docker restart jenkins-demo
```

## Ver logs de Jenkins

```bash
docker logs -f jenkins-demo
```

## Ver logs de SonarQube

```bash
docker logs -f sonarqube-demo
```

## Ver logs de PostgreSQL Sonar

```bash
docker logs -f postgres-sonar-demo
```

## Ver logs de la app

```bash
docker logs -f demo-springboot-app
```

## Probar endpoint

```bash
curl http://localhost:8081/api/greetings?name=Juan
```

## Probar health check

```bash
curl http://localhost:8081/actuator/health
```

## Eliminar app desplegada

```bash
docker rm -f demo-springboot-app
```

## Bajar laboratorio sin borrar datos

```bash
docker compose down
```

## Bajar laboratorio borrando datos

```bash
docker compose down -v
```

## Ejecutar Maven localmente

```bash
cd app
mvn clean verify
```

## Generar imagen manual

```bash
cd app
docker build -t demo-springboot:manual .
```

## Ejecutar imagen manual

```bash
docker run --rm -p 8081:8080 demo-springboot:manual
```
