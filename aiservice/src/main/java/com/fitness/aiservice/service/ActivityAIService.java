package com.fitness.aiservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAIService {

    private final GeminiService geminiService;
    private final RecommendationRepository recommendationRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateRecommendation(Activity activity) {
        try {
            String prompt = createPromptForActivity(activity);
            String aiResponse = geminiService.getAnswer(prompt);
            log.info("Raw AI Response received");

            // Extract and parse the JSON from AI response
            String jsonContent = extractJsonFromResponse(aiResponse);
            log.debug("Extracted JSON content length: {}", jsonContent.length());

            Recommendation recommendation = processAiResponse(activity, jsonContent);

            log.info("✓ Recommendation successfully saved with ID: {}", recommendation.getId());
            return jsonContent;
        } catch (Exception e) {
            log.error("Failed to generate recommendation for activity: {}", activity.getId(), e);
            return "Unable to generate recommendation at this time";
        }
    }

    private String extractJsonFromResponse(String aiResponse) {
        // Remove markdown code blocks if present
        String cleaned = aiResponse
                .replaceAll("```json\\n", "")
                .replaceAll("```\\n", "")
                .replaceAll("```", "")
                .trim();

        log.debug("Cleaned AI response: {}", cleaned);
        return cleaned;
    }

    private Recommendation processAiResponse(Activity activity, String jsonContent) {
        try {
            // Parse the JSON directly
            JsonNode rootNode = objectMapper.readTree(jsonContent);

            log.info("Successfully parsed AI recommendation JSON");

            // Extract individual fields from the JSON response
            JsonNode analysisNode = rootNode.path("analysis");
            JsonNode improvementsNode = rootNode.path("improvements");
            JsonNode suggestionsNode = rootNode.path("suggestions");
            JsonNode safetyNode = rootNode.path("safety");

            // Build improvements list
            List<String> improvementsList = new ArrayList<>();
            if (improvementsNode.isArray()) {
                improvementsNode.forEach(item -> {
                    String area = item.path("area").asText("");
                    String rec = item.path("recommendation").asText("");
                    if (!area.isEmpty() && !rec.isEmpty()) {
                        improvementsList.add(area + ": " + rec);
                    }
                });
            }
            log.debug("✓ Improvements extracted: {} items", improvementsList.size());

            // Build suggestions list
            List<String> suggestionsList = new ArrayList<>();
            if (suggestionsNode.isArray()) {
                suggestionsNode.forEach(item -> {
                    String workout = item.path("workout").asText("");
                    String description = item.path("description").asText("");
                    if (!workout.isEmpty() && !description.isEmpty()) {
                        suggestionsList.add(workout + " - " + description);
                    }
                });
            }
            log.debug("✓ Suggestions extracted: {} items", suggestionsList.size());

            // Build safety list
            List<String> safetyList = new ArrayList<>();
            if (safetyNode.isArray()) {
                safetyNode.forEach(item -> {
                    String safety = item.asText("");
                    if (!safety.isEmpty()) {
                        safetyList.add(safety);
                    }
                });
            }
            log.debug("✓ Safety guidelines extracted: {} items", safetyList.size());

            // Create recommendation object with ALL fields populated
            Recommendation recommendation = Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .activityType(activity.getType())  // ✅ Set activityType
                    .analysis(analysisNode.toString())
                    .recommendation("AI Recommendation generated for " + activity.getType())  // ✅ Set recommendation
                    .improvements(improvementsList)  // ✅ Set improvements list
                    .suggestions(suggestionsList)  // ✅ Set suggestions list
                    .safety(safetyList)  // ✅ Set safety list
                    .createdAt(LocalDateTime.now())
                    .build();

            log.debug("✓ Recommendation object built with all fields populated");
            log.debug("  - activityType: {}", recommendation.getActivityType());
            log.debug("  - improvements count: {}", recommendation.getImprovements() != null ? recommendation.getImprovements().size() : 0);
            log.debug("  - suggestions count: {}", recommendation.getSuggestions() != null ? recommendation.getSuggestions().size() : 0);
            log.debug("  - safety count: {}", recommendation.getSafety() != null ? recommendation.getSafety().size() : 0);

            // Save to database
            Recommendation saved = recommendationRepository.save(recommendation);
            log.info("✓✓ Recommendation saved to MongoDB with ID: {}", saved.getId());
            log.info("✓✓ Activity Type: {}", saved.getActivityType());
            log.info("✓✓ Improvements: {} items", saved.getImprovements() != null ? saved.getImprovements().size() : 0);

            return saved;
        } catch (Exception e) {
            log.error("Failed to process AI response", e);
            e.printStackTrace();
            throw new RuntimeException("Failed to process AI recommendation", e);
        }
    }

    private String createPromptForActivity(Activity activity) {
        return String.format("""
Analyze this fitness activity and provide detailed recommendations in the following EXACT JSON format:
    {
        "analysis": {
            "overall": "Overall analysis here",
            "pace": "Pace analysis here",
            "heartRate": "Heart rate analysis here",
            "caloriesBurned": "Calories analysis here"
        },
        "improvements": [
            {
                "area": "Area name",
                "recommendation": "Detailed recommendation"
            }
        ],
        "suggestions": [
            {
                "workout": "Workout name",
                "description": "Detailed workout description"
            }
        ],
        "safety": [
            "Safety guideline 1",
            "Safety guideline 2"
        ]
    }
    
Analyze this activity:
    Activity Type: %s
    Duration: %d minutes
    Calories Burned: %d
    Additional Metrics: %s

Provide detailed analysis focusing on:
1. Performance metrics and efficiency
2. Areas for improvement
3. Next workout suggestions tailored to this activity
4. Important safety guidelines and precautions for this type of exercise

Safety Considerations:
- Warn about overexertion if duration or heart rate is abnormally high
- Recommend proper hydration and recovery time
- Suggest warm-up and cool-down practices
- Advise on injury prevention techniques
- Recommend consulting a medical professional if any concerns arise

IMPORTANT: Return ONLY the JSON object with no markdown code blocks, no extra text, and no explanations.
""",
                activity.getType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getAdditionalMetrics()
        );
    }
}