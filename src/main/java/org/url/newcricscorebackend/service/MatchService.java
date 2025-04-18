package org.url.newcricscorebackend.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.url.newcricscorebackend.entities.Match;
import org.url.newcricscorebackend.entities.MatchStatus;
import org.url.newcricscorebackend.repositories.MatchRepository;
import org.url.newcricscorebackend.util.MatchSelection;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@EnableScheduling
public class MatchService {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private MatchSelection matchSelection;

    @Scheduled(fixedRate = 20000)
    public List<Match> getMatches() {
        List<Match> matches = new ArrayList<>();
        try {
            String url = "https://www.cricbuzz.com/cricket-match/live-scores";
            Document doc = Jsoup.connect(url).get();
            Elements elements = doc.select("div.cb-mtch-lst.cb-tms-itm");

            for(Element match : elements) {
                String teamsHeading = match.select("h3.cb-lv-scr-mtch-hdr").select("a").text();
                String matchNumberVenue = match.select("span").text();

                Elements matchBatTeamInfo = match.select("div.cb-hmscg-bat-txt");
                String battingTeam = matchBatTeamInfo.select("div.cb-hmscg-tm-nm").text();
                String score = matchBatTeamInfo.select("div.cb-hmscg-tm-nm+div").text();

                Elements bowlTeamInfo = match.select("div.cb-hmscg-bwl-txt");
                String bowlTeam = bowlTeamInfo.select("div.cb-hmscg-tm-nm").text();
                String bowlTeamScore = bowlTeamInfo.select("div.cb-hmscg-tm-nm+div").text();

                String textLive = match.select("div.cb-text-live").text();
                String textComplete = match.select("div.cb-text-complete").text();

                String matchLink = match.select("a.cb-lv-scrs-well.cb-lv-scrs-well-live").attr("href").toString();

                Match match1 = new Match();
                match1.setMatchNumberVenue(matchNumberVenue);
                match1.setTeamHeading(teamsHeading);
                match1.setBattingTeam(matchSelection.getFullTeamName(battingTeam));
                match1.setBattingTeamScore(score);
                match1.setBowlingTeam(matchSelection.getFullTeamName(bowlTeam));
                match1.setBowlingTeamScore(bowlTeamScore);
                match1.setLiveText(textLive);
                match1.setMatchLink(matchLink);
                match1.setTextComplete(textComplete);
                match1.setMatchStatus();
                match1.setMatchDate(Date.valueOf(LocalDate.now()));

                if(matchSelection.matchSelector(match1.getBattingTeam(), match1.getBowlingTeam())) {
                    matches.add(match1);
                    updateMatch(match1);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        printMatches(matches);
        return matches;
    }

    public void updateMatch(Match match1) {
        Match existingMatch = matchRepository.getMatchByTeamHeading(match1.getTeamHeading());

        if(existingMatch == null) {
            setTossDetails(match1);
            matchRepository.save(match1);
        } else {
            match1.setMatchId(existingMatch.getMatchId());

            // Preserve manually set toss details if they exist
            if (existingMatch.getTossWinner() != null && !existingMatch.getTossWinner().isEmpty()) {
                match1.setTossWinner(existingMatch.getTossWinner());
            }
            if (existingMatch.getTossDecision() != null && !existingMatch.getTossDecision().isEmpty()) {
                match1.setTossDecision(existingMatch.getTossDecision());
            }

            matchRepository.save(match1);
        }
    }


    public boolean setTossDetails(Long matchId, String tossWinner, String tossDecision) {
        Match match = matchRepository.getMatchByMatchId(matchId);
        match.setTossWinner(tossWinner);
        match.setTossDecision(tossDecision);
        matchRepository.save(match);
        return true;
    }

    public void printMatches(List<Match> matches) {

        for(Match match : matches) {

            System.out.println(match.getTeamHeading());
            System.out.println(match.getBattingTeam());
            System.out.println(match.getBattingTeamScore());
            if(!match.getLiveText().isEmpty()) System.out.println(match.getLiveText());
            if(!match.getTextComplete().isEmpty())System.out.println(match.getTextComplete());
            System.out.println(match.getBowlingTeam());
            if(!match.getBowlingTeamScore().isEmpty()) System.out.println(match.getBowlingTeamScore());
            System.out.println(match.getStatus());
            System.out.println(match.getMatchDate());

            System.out.println("---------------------");
        }
    }

    private void setTossDetails(Match match) {
        String text = match.getLiveText();
        String[] tossDetails = text.split(" ");
        String tossWinner = tossDetails[0];
        if(tossDetails.length < 4) {
            return;
        }
        String tossDecision = tossDetails[3];

        match.setTossWinner(tossWinner);
        match.setTossDecision(tossDecision);
    }

    public Match getMatchByMatchId(Long matchId) {
        return matchRepository.getMatchByMatchId(matchId);
    }

    public List<Match> getRecentCompletedMatches(int limit) {
        return matchRepository.findByStatusOrderByMatchDateDesc(MatchStatus.COMPLETED, PageRequest.of(0, limit));
    }

    /*@Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000")  // Your React dev server
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowCredentials(true);
            }
        };
    }*/
}
