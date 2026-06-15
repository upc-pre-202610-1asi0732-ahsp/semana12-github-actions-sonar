package com.example.cicd.api;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.cicd.service.GreetingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(GreetingController.class)
@Import(GreetingService.class)
class GreetingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Debe responder 200 para endpoint de saludo")
    void shouldReturnGreeting() throws Exception {
        mockMvc.perform(get("/api/greetings").param("name", "Ana"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", equalTo("Hola, Ana!")))
                .andExpect(jsonPath("$.nameLength", equalTo(3)));
    }

    @Test
    @DisplayName("Debe responder 200 usando Mundo cuando no se envia nombre")
    void shouldReturnDefaultGreetingWhenNameIsMissing() throws Exception {
        mockMvc.perform(get("/api/greetings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", equalTo("Hola, Mundo!")))
                .andExpect(jsonPath("$.nameLength", equalTo(5)));
    }

    @Test
    @DisplayName("Debe responder 400 cuando el nombre supera el limite permitido")
    void shouldReturnBadRequestWhenNameIsTooLong() throws Exception {
        String longName = "A".repeat(41);

        mockMvc.perform(get("/api/greetings").param("name", longName))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", equalTo(400)))
                .andExpect(jsonPath("$.error", equalTo("Bad Request")))
                .andExpect(jsonPath("$.message", equalTo("El nombre no puede superar 40 caracteres.")));
    }
}
