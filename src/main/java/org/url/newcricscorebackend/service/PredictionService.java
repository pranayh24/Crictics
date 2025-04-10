package org.url.newcricscorebackend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.url.newcricscorebackend.entities.Match;
import org.url.newcricscorebackend.entities.PredResponseDTO;
import org.url.newcricscorebackend.entities.PredictionDTO;
import org.url.newcricscorebackend.util.MatchSelection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
public class PredictionService {

    private final String flaskBaseUrl;
    private final RestTemplate restTemplate;

    @Autowired
    private MatchSelection matchSelection;

    public PredictionService() {
        this.flaskBaseUrl = "http://localhost:5000";
        this.restTemplate = new RestTemplate();
    }

    public boolean health() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(flaskBaseUrl + "/health", String.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            return false;
        }
    }

    public PredResponseDTO predictForMatch(PredictionDTO predictionDTO) {
        try {
            // Convert PredictionDTO to Map
            Map<String, Object> matchData = new HashMap<>();
            matchData.put("team1", predictionDTO.team1());
            matchData.put("team2", predictionDTO.team2());
            matchData.put("battingTeam", predictionDTO.battingTeam());
            matchData.put("venue", predictionDTO.venue());
            matchData.put("currentRuns", Integer.parseInt(predictionDTO.currentRuns()));
            matchData.put("currentWickets", Integer.parseInt(predictionDTO.currentWickets().trim()));
            matchData.put("currentOvers", predictionDTO.currentOvers());
            matchData.put("tossWinner", predictionDTO.tossWinner());
            matchData.put("tossDecision", predictionDTO.tossDecision());
            matchData.put("target", Integer.parseInt(predictionDTO.target()));

            // Send to Flask service
            ResponseEntity<String> response = getPredictions(matchData);

            // Parse response JSON to PredResponseDTO
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return parseFlaskResponse(response.getBody());
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private PredResponseDTO parseFlaskResponse(String responseJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseJson);

            int predictedScore = root.get("estimated_score").asInt();
            Map<String, Double> winProbabilities = new HashMap<>();

            JsonNode probNode = root.get("win_probabilities");
            Iterator<String> fieldNames = probNode.fieldNames();
            while (fieldNames.hasNext()) {
                String team = fieldNames.next();
                winProbabilities.put(team, probNode.get(team).asDouble());
            }

            return new PredResponseDTO(predictedScore, winProbabilities);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResponseEntity<String> getPredictions(Map<String, Object> matchData) {
        try {
            if(matchData == null) {
                return ResponseEntity
                        .badRequest()
                        .body("Match data is empty or null");
            }

            if(!health()) {
                return ResponseEntity
                        .status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("Prediction service is unavailable");
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(matchData, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    flaskBaseUrl + "/api/predict",
                    HttpMethod.POST,
                    requestEntity,
                    String.class);

            return response;
        } catch(HttpClientErrorException e) {
            return ResponseEntity
                    .status(e.getStatusCode())
                    .body("Error while predicting " + e.getResponseBodyAsString());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting prediction " + e.getMessage());
        }
    }

    public PredictionDTO mapMatchToPredictionDTO(Match match) {
        // Extract batting info (runs, wickets, overs) from score strings
        String[] battingInfo = extractBattingInfo(match.getBattingTeamScore());

        return new PredictionDTO(
                match.getBattingTeam(),
                match.getBowlingTeam(),
                match.getBattingTeam(),
                extractVenue(match.getMatchNumberVenue()),
                battingInfo[0], // runs
                battingInfo[1], // wickets
                battingInfo[2], // overs
                match.getTossWinner(),
                match.getTossDecision(),
                extractTarget(match.getBowlingTeamScore())
        );
    }

    private String extractTeam(String team) {
        return matchSelection.getFullTeamName(team);
    }


    private String extractTarget(String bowlingTeamScore) {
        String runsScored = bowlingTeamScore.substring(0, bowlingTeamScore.indexOf('-'));
        int target = Integer.parseInt(runsScored) + 1;
        return Integer.toString(target);
    }

    private String extractVenue(String matchNumberVenue) {
        return matchNumberVenue.substring(matchNumberVenue.indexOf(',')).trim();
    }

    private String[] extractBattingInfo(String battingTeamScore) {
        String[] battingInfo = new String[3];

        battingInfo[0] = battingTeamScore.substring(0, battingTeamScore.indexOf('-')); // runs
        battingInfo[1] = battingTeamScore.substring(battingTeamScore.indexOf('-') + 1, battingTeamScore.indexOf('(')); // wickets
        battingInfo[2] = battingTeamScore.substring(battingTeamScore.indexOf('(') + 1, battingTeamScore.indexOf('O') - 1);

        return battingInfo;
    }
}
