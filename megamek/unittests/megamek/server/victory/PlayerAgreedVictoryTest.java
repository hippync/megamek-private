package megamek.server.victory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import megamek.common.Game;
import megamek.common.Player;

class PlayerAgreedVictoryTest {

    @Mock
    private Game game;

    private PlayerAgreedVictory victory;
    private Map<String, Object> ctx;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        victory = new PlayerAgreedVictory();
        ctx = new HashMap<>();
    }

    @Test
    void testNoVictoryWhenNoContext() {
        when(game.isForceVictory()).thenReturn(false);
        VictoryResult result = victory.checkVictory(game, ctx);
        assertFalse(result.isVictory());
    }

    @Test
    void testPlayerVictoryWhenAllAgree() {
        // Setup
        when(game.isForceVictory()).thenReturn(true);
        when(game.getVictoryPlayerId()).thenReturn(1);
        when(game.getVictoryTeam()).thenReturn(Player.TEAM_NONE);

        Player winningPlayer = mock(Player.class);
        when(winningPlayer.getId()).thenReturn(1);
        when(winningPlayer.isObserver()).thenReturn(false);
        when(winningPlayer.doesNotAdmitDefeat()).thenReturn(false);
        when(winningPlayer.getTeam()).thenReturn(1);

        Player losingPlayer = mock(Player.class);
        when(losingPlayer.getId()).thenReturn(2);
        when(losingPlayer.isObserver()).thenReturn(false);
        when(losingPlayer.doesNotAdmitDefeat()).thenReturn(false);
        when(losingPlayer.getTeam()).thenReturn(2);

        List<Player> players = new ArrayList<>();
        players.add(winningPlayer);
        players.add(losingPlayer);
        when(game.getPlayersList()).thenReturn(players);

        // Execute
        VictoryResult result = victory.checkVictory(game, ctx);

        // Verify
        assertTrue(result.isVictory());
        assertEquals(1, result.getWinningPlayer());
        assertEquals(Player.TEAM_NONE, result.getWinningTeam());
    }

    @Test
    void testNoVictoryWhenPlayerDisagrees() {
        // Setup
        when(game.isForceVictory()).thenReturn(true);
        when(game.getVictoryPlayerId()).thenReturn(1);
        when(game.getVictoryTeam()).thenReturn(Player.TEAM_NONE);

        Player winningPlayer = mock(Player.class);
        when(winningPlayer.getId()).thenReturn(1);
        when(winningPlayer.isObserver()).thenReturn(false);
        when(winningPlayer.doesNotAdmitDefeat()).thenReturn(false);
        when(winningPlayer.getTeam()).thenReturn(1);

        Player disagreeingPlayer = mock(Player.class);
        when(disagreeingPlayer.getId()).thenReturn(2);
        when(disagreeingPlayer.isObserver()).thenReturn(false);
        when(disagreeingPlayer.doesNotAdmitDefeat()).thenReturn(true);
        when(disagreeingPlayer.getTeam()).thenReturn(2);

        List<Player> players = new ArrayList<>();
        players.add(winningPlayer);
        players.add(disagreeingPlayer);
        when(game.getPlayersList()).thenReturn(players);

        // Execute
        VictoryResult result = victory.checkVictory(game, ctx);

        // Verify
        assertFalse(result.isVictory());
    }

    @Test
    void testTeamVictoryWhenAllAgree() {
        // Setup
        when(game.isForceVictory()).thenReturn(true);
        when(game.getVictoryPlayerId()).thenReturn(Player.PLAYER_NONE);
        when(game.getVictoryTeam()).thenReturn(1);

        Player team1Player1 = mock(Player.class);
        when(team1Player1.getId()).thenReturn(1);
        when(team1Player1.getTeam()).thenReturn(1);
        when(team1Player1.isObserver()).thenReturn(false);
        when(team1Player1.doesNotAdmitDefeat()).thenReturn(false);

        Player team1Player2 = mock(Player.class);
        when(team1Player2.getId()).thenReturn(2);
        when(team1Player2.getTeam()).thenReturn(1);
        when(team1Player2.isObserver()).thenReturn(false);
        when(team1Player2.doesNotAdmitDefeat()).thenReturn(false);

        Player team2Player = mock(Player.class);
        when(team2Player.getId()).thenReturn(3);
        when(team2Player.getTeam()).thenReturn(2);
        when(team2Player.isObserver()).thenReturn(false);
        when(team2Player.doesNotAdmitDefeat()).thenReturn(false);

        List<Player> players = new ArrayList<>();
        players.add(team1Player1);
        players.add(team1Player2);
        players.add(team2Player);
        when(game.getPlayersList()).thenReturn(players);

        // Execute
        VictoryResult result = victory.checkVictory(game, ctx);

        // Verify
        assertTrue(result.isVictory());
        assertEquals(Player.PLAYER_NONE, result.getWinningPlayer());
        assertEquals(1, result.getWinningTeam());
    }

    @Test
    void testNoVictoryWhenTeamDisagrees() {
        // Setup
        when(game.isForceVictory()).thenReturn(true);
        when(game.getVictoryPlayerId()).thenReturn(Player.PLAYER_NONE);
        when(game.getVictoryTeam()).thenReturn(1);

        Player team1Player = mock(Player.class);
        when(team1Player.getId()).thenReturn(1);
        when(team1Player.getTeam()).thenReturn(1);
        when(team1Player.isObserver()).thenReturn(false);
        when(team1Player.doesNotAdmitDefeat()).thenReturn(false);

        Player team2Player = mock(Player.class);
        when(team2Player.getId()).thenReturn(2);
        when(team2Player.getTeam()).thenReturn(2);
        when(team2Player.isObserver()).thenReturn(false);
        when(team2Player.doesNotAdmitDefeat()).thenReturn(true);

        List<Player> players = new ArrayList<>();
        players.add(team1Player);
        players.add(team2Player);
        when(game.getPlayersList()).thenReturn(players);

        // Execute
        VictoryResult result = victory.checkVictory(game, ctx);

        // Verify
        assertFalse(result.isVictory());
    }
}