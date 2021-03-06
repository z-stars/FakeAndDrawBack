package com.fakeanddraw.dataproviders.repository;

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
import org.springframework.transaction.annotation.Transactional;
import com.fakeanddraw.core.domain.Game;
import com.fakeanddraw.core.domain.GameFactory;
import com.fakeanddraw.core.domain.Match;
import com.fakeanddraw.core.domain.MatchFactory;
import com.fakeanddraw.core.domain.MatchStatus;
import com.fakeanddraw.core.domain.Player;
import javassist.NotFoundException;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MatchRepositoryTest {

  @Autowired
  private GameRepository gameRepository;

  @Autowired
  private MatchRepository matchRepository;

  @Autowired
  private GameFactory gameFactory;

  @Autowired
  private MatchFactory matchFactory;

  @Autowired
  private PlayerRepository playerRepository;

  @Test
  public void createAndFindById() {

    Game game = gameRepository.create(gameFactory.createNewGame("123asd"));

    Match match = matchRepository.create(matchFactory.createNewMatch(game));

    assertNotNull(match);
    assertNotNull(match.getMatchId());
    assertEquals(game.getGameId(), match.getGame().getGameId());

    Optional<Match> matchOptional = matchRepository.findMatchById(-1);

    assertFalse(matchOptional.isPresent());

    matchOptional = matchRepository.findMatchById(match.getMatchId());

    assertTrue(matchOptional.isPresent());
    assertEquals(match.getStatus(), matchOptional.get().getStatus());
    assertEquals(match.getCreatedDate(), matchOptional.get().getCreatedDate());
    assertEquals(match.getJoinTimeout(), matchOptional.get().getJoinTimeout());
    assertEquals(match.getDrawTimeout(), matchOptional.get().getDrawTimeout());
  }

  @Test
  public void createAndFindByGameCode() {

    Game game = gameRepository.create(gameFactory.createNewGame("123asd"));

    Match match = matchRepository.create(matchFactory.createNewMatch(game));

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
  public void createAndFindByPlayerId() {

    Game game = gameRepository.create(gameFactory.createNewGame("123asd"));
    Match match = matchRepository.create(matchFactory.createNewMatch(game));
    Player player = playerRepository.create(new Player("sessionId_123", "userName_123"));
    matchRepository.addPlayerToMatch(match, player);

    Optional<Match> matchByPlayerId = matchRepository.findLastMatchByPlayerId(player.getPlayerId());

    assertTrue(matchByPlayerId.isPresent());
    assertEquals(match.getStatus(), matchByPlayerId.get().getStatus());
    assertEquals(match.getCreatedDate(), matchByPlayerId.get().getCreatedDate());
    assertEquals(match.getJoinTimeout(), matchByPlayerId.get().getJoinTimeout());
    assertEquals(match.getDrawTimeout(), matchByPlayerId.get().getDrawTimeout());
  }

  @Test
  public void createAndFindByPlayerSessionId() {

    Game game = gameRepository.create(gameFactory.createNewGame("123asd"));
    Match match = matchRepository.create(matchFactory.createNewMatch(game));
    Player player = playerRepository.create(new Player("sessionId_123", "userName_123"));
    matchRepository.addPlayerToMatch(match, player);

    Optional<Match> matchByPlayerId =
        matchRepository.findLastMatchByPlayerSessionId(player.getSessionId());

    assertTrue(matchByPlayerId.isPresent());
    assertEquals(match.getStatus(), matchByPlayerId.get().getStatus());
    assertEquals(match.getCreatedDate(), matchByPlayerId.get().getCreatedDate());
    assertEquals(match.getJoinTimeout(), matchByPlayerId.get().getJoinTimeout());
    assertEquals(match.getDrawTimeout(), matchByPlayerId.get().getDrawTimeout());
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
    match.setMatchId(-1);
    match.setStatus(MatchStatus.JOIN);
    matchRepository.update(match);
  }
}
