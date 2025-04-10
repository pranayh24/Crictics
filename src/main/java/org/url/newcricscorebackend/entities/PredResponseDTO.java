package org.url.newcricscorebackend.entities;

import java.util.Map;

public class PredResponseDTO {
    private int predictedScore;

    private Map<String, Double> winProbabilities;

    public PredResponseDTO(int predictedScore, Map<String, Double> winProbabilities) {
        this.predictedScore = predictedScore;
        this.winProbabilities = winProbabilities;
    }

    public int getPredictedScore() {
        return predictedScore;
    }

    public void setPredictedScore(int predictedScore) {
        this.predictedScore = predictedScore;
    }

    public Map<String, Double> getWinProbabilities() {
        return winProbabilities;
    }

    public void setWinProbabilities(Map<String, Double> winProbabilities) {
        this.winProbabilities = winProbabilities;
    }
}
