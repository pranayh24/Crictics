package org.url.newcricscorebackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.url.newcricscorebackend.entities.Match;
import org.url.newcricscorebackend.service.MatchService;

import java.util.List;

@RestController
@RequestMapping("/matches")
public class MatchController {

    @Autowired
    private MatchService scrapper;

    @GetMapping("/get-all-matches")
    public ResponseEntity<List<Match>> getMatches() {
        System.out.println("GET /matches/get-all-matches endpoint called");
        List<Match> matches = scrapper.getMatches();
        return new ResponseEntity<>(matches, HttpStatus.OK);
    }

    @GetMapping("set-toss-details")
    public ResponseEntity<Boolean> setTossDetails(@RequestParam(name = "id") Long matchId, @RequestParam(name = "winner") String tossWinner, @RequestParam(name = "decision") String tossDecision) {
        try {
            scrapper.setTossDetails(matchId, tossWinner, tossDecision);
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/match/{id}")
    public ResponseEntity<Match> getMatchById(@PathVariable Long id) {
        return new ResponseEntity<>(scrapper.getMatchByMatchId(id), HttpStatus.OK);
    }

    @GetMapping("/get-completed-matches")
    public ResponseEntity<List<Match>> getCompletedMatches(@RequestParam(defaultValue = "3") int limit) {
        System.out.println("GET /matches/get-completed-matches endpoint called");
        List<Match> completedMatches = scrapper.getRecentCompletedMatches(limit);
        return new ResponseEntity<>(completedMatches, HttpStatus.OK);
    }
}
