package org.url.newcricscorebackend.util;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class MatchSelection {

    private static final Map<String, String> TEAM_NAME_MAPPING = new HashMap<>();

    private static final Set<String> TEAM_NAME_SET = new HashSet<>();

    static {
        TEAM_NAME_MAPPING.put("AUS", "Australia");
        TEAM_NAME_MAPPING.put("PAK", "Pakistan");
        TEAM_NAME_MAPPING.put("IND", "India");
        TEAM_NAME_MAPPING.put("ENG", "England");
        TEAM_NAME_MAPPING.put("NZ", "New Zealand");
        TEAM_NAME_MAPPING.put("SA", "South Africa");
        TEAM_NAME_MAPPING.put("WI", "West Indies");
        TEAM_NAME_MAPPING.put("SL", "Sri Lanka");
        TEAM_NAME_MAPPING.put("BAN", "Bangladesh");
        TEAM_NAME_MAPPING.put("AFG", "Afghanistan");
        TEAM_NAME_MAPPING.put("ZIM", "Zimbabwe");
        TEAM_NAME_MAPPING.put("IRE", "Ireland");
        TEAM_NAME_MAPPING.put("SCO", "Scotland");
        TEAM_NAME_MAPPING.put("UAE", "United Arab Emirates");
        TEAM_NAME_MAPPING.put("NEP", "Nepal");
        TEAM_NAME_MAPPING.put("CSK", "Chennai Super Kings");
        TEAM_NAME_MAPPING.put("MI", "Mumbai Indians");
        TEAM_NAME_MAPPING.put("LSG", "Lucknow Super Giants");
        TEAM_NAME_MAPPING.put("DC", "Delhi Capitals");
        TEAM_NAME_MAPPING.put("RCB", "Royal Challengers Bengaluru");
        TEAM_NAME_MAPPING.put("GT", "Gujarat Giants");
        TEAM_NAME_MAPPING.put("RR", "Rajasthan Royals");
        TEAM_NAME_MAPPING.put("PBKS", "Punjab Kings");
        TEAM_NAME_MAPPING.put("KKR", "Kolkata Knight Riders");
        TEAM_NAME_MAPPING.put("SRH", "Sunrisers Hyderabad");

        TEAM_NAME_SET.addAll(TEAM_NAME_MAPPING.values());
    }

    

    public boolean matchSelector(String team1, String team2) {
        if(!TEAM_NAME_SET.contains(team1) && !TEAM_NAME_SET.contains(team2)) {
            return false;
        }
        return true;
    }

    public String getFullTeamName(String teamName) {
        return TEAM_NAME_MAPPING.get(teamName);
    }

}
