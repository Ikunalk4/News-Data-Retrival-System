package com.inshort.newshub.client;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inshort.newshub.entity.GeminiResponse;

@Component
public class GeminiClient {

	@Value("${gemini.api.key}")
	private String apiKey;

	private final RestTemplate restTemplate = new RestTemplate();

	public GeminiResponse extractQueryInsights(String userQuery) throws IOException {
		String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + apiKey;

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		Map<String, Object> request = Map.of("contents", List.of(Map.of("parts",
				List.of(Map.of("text", "Extract entities, concepts, and user intent from: " + userQuery)))));
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

		return parseGeminiResponse(response.getBody());
	}

	private GeminiResponse parseGeminiResponse(String json) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(json);

		String text = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

		GeminiResponse response = new GeminiResponse();

		String entitiesText = extractEntities(text);
		String intentText = extractIntent(text);

		if (entitiesText != null) {
			String[] entityArray = entitiesText.split(",");
			response.setEntities(Arrays.stream(entityArray).map(String::trim).collect(Collectors.toList()));
		}
		if (intentText != null) {
			response.setIntent(intentText.trim().toLowerCase());
		}
		return response;
	}

	private String extractEntities(String text) {
		int startIndex = text.indexOf("Entities:") + "Entities:".length();
		int endIndex = text.indexOf("Intent:");
		if (startIndex >= 0 && endIndex > startIndex) {
			return text.substring(startIndex, endIndex).trim();
		}
		return null;
	}

	private String extractIntent(String text) {
		int startIndex = text.indexOf("Intent:") + "Intent:".length();
		if (startIndex >= 0) {
			return text.substring(startIndex).trim();
		}
		return null;
	}

	public String summarize(String text) {
		String prompt = "Summarize the following news article:\n" + text;

		String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + apiKey;

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		Map<String, Object> body = Map.of("contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))));

		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

		try {
			ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

			if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode root = mapper.readTree(response.getBody());

				return root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
			} else {
				return "Summary not available.";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "Error summarizing article.";
		}
	}
}
