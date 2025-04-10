package org.url.newcricscorebackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.url.newcricscorebackend.entities.Match;
import org.url.newcricscorebackend.entities.PredResponseDTO;
import org.url.newcricscorebackend.entities.PredictionDTO;
import org.url.newcricscorebackend.service.MatchService;
import org.url.newcricscorebackend.service.PredictionService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/prediction")
public class PredictionController {

    @Autowired
    private PredictionService predictionService;

    @Autowired
    private MatchService matchService;

    @GetMapping
    public ResponseEntity<List<PredResponseDTO>> getPredictions() {
        try {
            List<Match> matches = matchService.getMatches();
            List<PredResponseDTO> predResponses = new ArrayList<>();

            for (Match match : matches) {
                PredictionDTO predictionDTO = predictionService.mapMatchToPredictionDTO(match);
                PredResponseDTO response = predictionService.predictForMatch(predictionDTO);
                if (response != null) {
                    predResponses.add(response);
                }
            }

            return ResponseEntity.ok(predResponses);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        if (predictionService.health()) {
            return ResponseEntity.ok("The prediction service is available.");
        } else {
            return ResponseEntity.ok("The prediction service is not available.");
        }
    }


}
