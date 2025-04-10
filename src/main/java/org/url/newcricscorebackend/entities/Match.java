package org.url.newcricscorebackend.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name="matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long matchId;
    private String teamHeading;
    private String battingTeam;
    private String battingTeamScore;
    private String matchNumberVenue;
    private String bowlingTeam;
    private String bowlingTeamScore;
    private String liveText;
    private String textComplete;
    private String matchLink;
    private Date matchDate;
    private String tossWinner;
    private String tossDecision;

    @Enumerated(EnumType.STRING)
    private MatchStatus status;
    private String matchType;

    public void setMatchStatus(){
        if(textComplete.isBlank()){
            this.status= MatchStatus.LIVE;
        }
        else{
            this.status = MatchStatus.COMPLETED;
        }
    }


}
