package org.url.newcricscorebackend.entities;


public record PredictionDTO(
        String team1,
        String team2,
        String battingTeam,
        String venue,
        String currentRuns,
        String currentWickets,
        String currentOvers,
        String tossWinner,
        String tossDecision,
        String target
) {
    public PredictionDTO {
    }
}
