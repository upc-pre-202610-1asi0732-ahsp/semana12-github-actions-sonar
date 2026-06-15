package com.example.cicd.service;

import org.springframework.stereotype.Service;

@Service
public class GreetingService {

    private static final int MAX_NAME_LENGTH = 40;

    public GreetingResponse buildGreeting(String name) {
        String normalizedName = normalizeName(name);
        return new GreetingResponse("Hola, " + normalizedName + "!", normalizedName.length());
    }

    String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            return "Mundo";
        }

        String trimmedName = name.trim();

        if (trimmedName.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("El nombre no puede superar " + MAX_NAME_LENGTH + " caracteres.");
        }

        return trimmedName;
    }

    public String hello(String name) {
        if (name == null) {
            return "";
        }
        if (name == null) {
            return "";
        }
        return "Hola";
    }
}
