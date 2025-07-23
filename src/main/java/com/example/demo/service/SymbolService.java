package com.example.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SymbolService {

    private final Set<String> validSymbols = ConcurrentHashMap.newKeySet();

    @Value("${symbol_url}")
    private String symbolUrl;

    @PostConstruct
    public void init() {
        loadSymbols();
    }

    public void loadSymbols() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(symbolUrl, String.class);
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode symbols = root.get("symbols");

            for (JsonNode node : symbols) {
                String symbol = node.get("symbol").asText();
                validSymbols.add(symbol.toUpperCase());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch symbols from Binance", e);
        }
    }

    public boolean isValid(String symbol) {
        return symbol != null && validSymbols.contains(symbol.toUpperCase());
    }

    public Set<String> getAllSymbols() {
        return Collections.unmodifiableSet(validSymbols);
    }
}
