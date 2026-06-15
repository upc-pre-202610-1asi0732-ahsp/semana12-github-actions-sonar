# Solucion del problema con Mockito en GreetingControllerTest

## Problema

El test `GreetingControllerTest` usaba `@MockBean` para reemplazar `GreetingService` por un mock de Mockito.
En algunos entornos, especialmente cuando se ejecuta con versiones recientes de Java o combinaciones especificas de Spring Boot / Mockito / Byte Buddy, Mockito puede fallar al intentar crear el mock de la clase `GreetingService`.

## Correccion aplicada

Se elimino el uso de Mockito en el test del controlador.

Antes:

```java
@MockBean
private GreetingService greetingService;
```

Ahora:

```java
@WebMvcTest(GreetingController.class)
@Import(GreetingService.class)
```

Con esto el test sigue siendo un test de capa web con `MockMvc`, pero usa una instancia real de `GreetingService` dentro del contexto de prueba.

## Ventajas de la correccion

- Evita dependencia directa de Mockito en este test.
- Mantiene el test simple para alumnos.
- Valida el endpoint real `/api/greetings`.
- Valida respuestas HTTP 200 y 400.
- Permite ejecutar `mvn clean verify` sin el error de mock.

## Tests incluidos

1. `shouldReturnGreeting`: valida saludo con nombre.
2. `shouldReturnDefaultGreetingWhenNameIsMissing`: valida saludo por defecto.
3. `shouldReturnBadRequestWhenNameIsTooLong`: valida manejo de error 400.
