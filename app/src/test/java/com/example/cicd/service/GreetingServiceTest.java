package com.example.cicd.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GreetingServiceTest {

    private final GreetingService greetingService = new GreetingService();

    @Test
    @DisplayName("Debe retornar Mundo cuando el nombre es nulo")
    void shouldReturnDefaultNameWhenNameIsNull() {
        GreetingResponse response = greetingService.buildGreeting(null);

        assertThat(response.message()).isEqualTo("Hola, Mundo!");
        assertThat(response.nameLength()).isEqualTo(5);
    }

    @Test
    @DisplayName("Debe limpiar espacios antes y después del nombre")
    void shouldTrimName() {
        GreetingResponse response = greetingService.buildGreeting("  Juan Carlos  ");

        assertThat(response.message()).isEqualTo("Hola, Juan Carlos!");
        assertThat(response.nameLength()).isEqualTo(11);
    }

    @Test
    @DisplayName("Debe rechazar nombres mayores al límite permitido")
    void shouldRejectLongNames() {
        String longName = "A".repeat(41);

        assertThatThrownBy(() -> greetingService.buildGreeting(longName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no puede superar");
    }
}
