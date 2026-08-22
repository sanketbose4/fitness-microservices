package com.fitness.aiservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GeminiService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public GeminiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String getAnswer(String question) {
        try {
            Map<String, Object> requestBody = buildGeminiRequest(question);

            String url = geminiApiUrl + "?key=" + geminiApiKey;

            String response = webClient.post()
                    .uri(url)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("Gemini API Response received");

            return extractTextFromResponse(response);

        } catch (WebClientResponseException e) {
            log.error("Gemini API Error - Status: {}, Body: {}",
                    e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("Gemini API failed: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Failed to process Gemini response", e);
            throw new RuntimeException("Gemini processing failed", e);
        }
    }

    private Map<String, Object> buildGeminiRequest(String question) {
        Map<String, Object> requestBody = new HashMap<>();

        Map<String, Object> part = new HashMap<>();
        part.put("text", question);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(part));

        requestBody.put("contents", List.of(content));


        return requestBody;
    }

    private String extractTextFromResponse(String response) throws Exception {
        Map<String, Object> map = objectMapper.readValue(response, Map.class);

        List<Map<String, Object>> candidates =
                (List<Map<String, Object>>) map.get("candidates");

        if (candidates == null || candidates.isEmpty()) {
            return "No response from AI";
        }

        Map<String, Object> content =
                (Map<String, Object>) candidates.get(0).get("content");

        List<Map<String, Object>> parts =
                (List<Map<String, Object>>) content.get("parts");

        return parts.get(0).get("text").toString();
    }
}