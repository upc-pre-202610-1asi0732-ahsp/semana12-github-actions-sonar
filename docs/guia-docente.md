# Guía docente - Demo CI/CD Nivel 2

## Objetivo de aprendizaje

Al finalizar la sesión, el estudiante debe comprender cómo un cambio de código puede recorrer un flujo automatizado de integración y despliegue:

```text
Código → Build → Test → Coverage → SonarQube → Quality Gate → Docker Image → Deploy
```

---

## Agenda sugerida de 90 minutos

| Tiempo | Actividad |
|---:|---|
| 10 min | Explicar problema: despliegue manual vs automatizado |
| 10 min | Mostrar arquitectura Jenkins + SonarQube + Docker |
| 15 min | Levantar ambiente con Docker Compose |
| 15 min | Revisar código Spring Boot y pruebas |
| 15 min | Configurar token de SonarQube en Jenkins |
| 15 min | Ejecutar pipeline completo |
| 10 min | Revisar resultados y preguntas |

---

## Conceptos clave

### Integración continua

Jenkins valida automáticamente que el código compile y que las pruebas pasen.

### Cobertura

JaCoCo mide qué parte del código fue ejecutada por las pruebas.

### Calidad de código

SonarQube identifica problemas como:

- Bugs.
- Vulnerabilidades.
- Code smells.
- Duplicación.
- Falta de cobertura.

### Quality Gate

El Quality Gate funciona como una puerta de control. Si no se cumple la calidad mínima, el pipeline falla y no se despliega.

### Despliegue continuo

Si el código pasa las validaciones, Jenkins construye la imagen Docker y despliega el contenedor.

---

## Actividad práctica sugerida

1. Ejecutar el pipeline y comprobar que pasa.
2. Modificar una prueba para que falle.
3. Ejecutar nuevamente el pipeline.
4. Observar que no se despliega.
5. Corregir la prueba.
6. Ejecutar nuevamente.
7. Revisar métricas en SonarQube.

---

## Preguntas para discusión

1. ¿Por qué un build exitoso no garantiza buena calidad?
2. ¿Qué diferencia hay entre CI y CD?
3. ¿Por qué conviene guardar tokens en Jenkins Credentials y no en el repositorio?
4. ¿Qué pasaría si no existiera Quality Gate?
5. ¿Qué faltaría para acercar esta demo a un ambiente empresarial?

---

## Extensiones posibles

Para una siguiente sesión se puede agregar:

- Docker Registry.
- Despliegue por ambientes: dev, qa y prod.
- Notificaciones por correo.
- Ramas GitFlow.
- Pull requests.
- Escaneo de dependencias.
- Kubernetes.
