package megamek.server.victory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import megamek.common.Game;
import megamek.common.Player;

class PlayerAgreedVictoryTest {

    private PlayerAgreedVictory victory;
    private Game game;
    private Map<String, Object> context;

    @BeforeEach
    void setUp() {
        victory = new PlayerAgreedVictory();
        game = mock(Game.class);
        context = new HashMap<>();
    }

    @Test
    void testNoVictoryWhenNoContext() {
        VictoryResult result = victory.checkVictory(game, context);

        assertFalse(result.isVictory(), "Pas de victoire sans contexte");
    }

    @Test
    void testPlayerVictoryWhenAllAgree() {
        Player winner = new Player(1, "Winner");
        Player loser1 = new Player(2, "Loser1");
        Player loser2 = new Player(3, "Loser2");

        when(loser1.doesNotAdmitDefeat()).thenReturn(false);
        when(loser2.doesNotAdmitDefeat()).thenReturn(false);

        when(game.getPlayersList()).thenReturn(List.of(winner, loser1, loser2));

        context.put("victoryPlayerId", 1);
        context.put("victoryTeam", Player.TEAM_NONE);

        VictoryResult result = victory.checkVictory(game, context);

        assertTrue(result.isVictory(), "Devrait être une victoire quand tous les joueurs sont d'accord");
        assertEquals(1, result.getWinningPlayer(), "Le joueur 1 devrait être le gagnant");
    }

    @Test
    void testNoVictoryWhenPlayerDisagrees() {
        Player winner = new Player(1, "Winner");
        Player loser1 = new Player(2, "Loser1");
        Player loser2 = new Player(3, "Loser2");

        when(loser1.doesNotAdmitDefeat()).thenReturn(true);
        when(loser2.doesNotAdmitDefeat()).thenReturn(false);

        when(game.getPlayersList()).thenReturn(List.of(winner, loser1, loser2));

        context.put("victoryPlayerId", 1);
        context.put("victoryTeam", Player.TEAM_NONE);

        VictoryResult result = victory.checkVictory(game, context);

        assertFalse(result.isVictory(), "Pas de victoire quand un joueur n'est pas d'accord");
    }

    @Test
    void testTeamVictoryWhenAllAgree() {
        Player winner1 = new Player(1, "Winner1");
        winner1.setTeam(1);
        Player winner2 = new Player(2, "Winner2");
        winner2.setTeam(1);
        Player loser1 = new Player(3, "Loser1");
        loser1.setTeam(2);
        Player loser2 = new Player(4, "Loser2");
        loser2.setTeam(2);

        when(loser1.doesNotAdmitDefeat()).thenReturn(false);
        when(loser2.doesNotAdmitDefeat()).thenReturn(false);

        when(game.getPlayersList()).thenReturn(List.of(winner1, winner2, loser1, loser2));

        context.put("victoryPlayerId", Player.PLAYER_NONE);
        context.put("victoryTeam", 1);

        VictoryResult result = victory.checkVictory(game, context);

        assertTrue(result.isVictory(), "Devrait être une victoire quand toutes les équipes sont d'accord");
        assertEquals(1, result.getWinningTeam(), "L'équipe 1 devrait être gagnante");
    }

    @Test
    void testNoVictoryWhenTeamDisagrees() {
        Player winner1 = new Player(1, "Winner1");
        winner1.setTeam(1);
        Player winner2 = new Player(2, "Winner2");
        winner2.setTeam(1);
        Player loser1 = new Player(3, "Loser1");
        loser1.setTeam(2);
        Player loser2 = new Player(4, "Loser2");
        loser2.setTeam(2);

        when(loser1.doesNotAdmitDefeat()).thenReturn(true);
        when(loser2.doesNotAdmitDefeat()).thenReturn(false);

        when(game.getPlayersList()).thenReturn(List.of(winner1, winner2, loser1, loser2));

        context.put("victoryPlayerId", Player.PLAYER_NONE);
        context.put("victoryTeam", 1);

        VictoryResult result = victory.checkVictory(game, context);

        assertFalse(result.isVictory(), "Pas de victoire quand un membre d'une équipe n'est pas d'accord");
    }
}