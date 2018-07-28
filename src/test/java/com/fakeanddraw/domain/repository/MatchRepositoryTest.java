package com.fakeanddraw.domain.repository;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.fakeanddraw.domain.model.Game;
import com.fakeanddraw.domain.model.GameFactory;
import com.fakeanddraw.domain.model.Match;
import com.fakeanddraw.domain.model.MatchFactory;
import com.fakeanddraw.domain.model.MatchStatus;
import javassist.NotFoundException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MatchRepositoryTest {

  @Autowired
  private GameRepository gameRepository;

  @Autowired
  private MatchRepository matchRepository;

  @Autowired
  private GameFactory gameFactory;

  @Autowired
  private MatchFactory matchFactory;

  @Test
  public void createAndFindByGameCode() {

    Game game = gameRepository.create(gameFactory.createNewGame("123asd"));

    Match match = matchFactory.createNewMatch(game);
    match.setJoinTimeout(match.getCreatedDate());
    match.setDrawTimeout(match.getCreatedDate());


    match = matchRepository.create(match);

    assertNotNull(match);
    assertNotNull(match.getMatchId());
    assertEquals(game.getGameId(), match.getGame().getGameId());

    Optional<Match> matchOptional = matchRepository.findLastMatchByGameCode("");

    assertFalse(matchOptional.isPresent());

    matchOptional = matchRepository.findLastMatchByGameCode(game.getGameCode());

    assertTrue(matchOptional.isPresent());
    assertEquals(match.getStatus(), matchOptional.get().getStatus());
    assertEquals(match.getCreatedDate(), matchOptional.get().getCreatedDate());
    assertEquals(match.getJoinTimeout(), matchOptional.get().getJoinTimeout());
    assertEquals(match.getDrawTimeout(), matchOptional.get().getDrawTimeout());
  }

  @Test
  public void update() {

    Game game = gameRepository.create(gameFactory.createNewGame("123asd"));

    Match match = matchFactory.createNewMatch(game);

    match = matchRepository.create(match);

    match.setStatus(MatchStatus.DRAW);

    try {
      matchRepository.update(match);
    } catch (NotFoundException e) {
      assertTrue(false);
    }

    Optional<Match> matchOptional = matchRepository.findLastMatchByGameCode(game.getGameCode());

    assertTrue(matchOptional.isPresent());

    assertEquals(MatchStatus.DRAW, matchOptional.get().getStatus());
  }

  @Test(expected = NotFoundException.class)
  public void updateNotFound() throws NotFoundException {
    Match match = matchFactory.createEmptyMatch();
    match.setMatchId(1);
    match.setStatus(MatchStatus.JOIN);
    matchRepository.update(match);
  }
}