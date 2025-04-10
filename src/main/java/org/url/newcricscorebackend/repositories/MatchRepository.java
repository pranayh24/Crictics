package org.url.newcricscorebackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.url.newcricscorebackend.entities.Match;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    Match getMatchByMatchId(long id);
    Match getMatchByTeamHeading(String teamHeading);
}
