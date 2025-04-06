package megamek.server.victory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import megamek.common.Game;
import megamek.common.Player;

class BattlefieldControlVictoryTest {

    private BattlefieldControlVictory victory;
    private Game game;
    private Map<String, Object> context;

    @BeforeEach
    void setUp() {
        victory = new BattlefieldControlVictory();
        game = mock(Game.class);
        context = new HashMap<>();
    }

    @Test
    void testNoPlayersAlive() {
        when(game.getPlayersList()).thenReturn(new ArrayList<>());

        VictoryResult result = victory.checkVictory(game, context);

        assertTrue(result.isDraw(), "Le résultat devrait être un match nul quand aucun joueur n'est vivant");
    }

    @Test
    void testSinglePlayerVictory() {
        Player winner = new Player(1, "Winner");
        winner.setTeam(Player.TEAM_NONE);

        when(game.getPlayersList()).thenReturn(List.of(winner));
        when(game.getLiveDeployedEntitiesOwnedBy(winner)).thenReturn(1);

        VictoryResult result = victory.checkVictory(game, context);

        assertTrue(result.isVictory(), "Le résultat devrait être une victoire");
        assertEquals(1, result.getWinningPlayer(), "Le joueur 1 devrait être le gagnant");
    }

    @Test
    void testSingleTeamVictory() {
        Player player1 = new Player(1, "Player1");
        player1.setTeam(1);
        Player player2 = new Player(2, "Player2");
        player2.setTeam(1);

        when(game.getPlayersList()).thenReturn(List.of(player1, player2));
        when(game.getLiveDeployedEntitiesOwnedBy(player1)).thenReturn(1);
        when(game.getLiveDeployedEntitiesOwnedBy(player2)).thenReturn(1);

        VictoryResult result = victory.checkVictory(game, context);

        assertTrue(result.isVictory(), "Le résultat devrait être une victoire");
        assertEquals(1, result.getWinningTeam(), "L'équipe 1 devrait être gagnante");
    }

    @Test
    void testNoVictoryWithMultipleTeams() {
        Player player1 = new Player(1, "Player1");
        player1.setTeam(1);
        Player player2 = new Player(2, "Player2");
        player2.setTeam(2);

        when(game.getPlayersList()).thenReturn(List.of(player1, player2));
        when(game.getLiveDeployedEntitiesOwnedBy(player1)).thenReturn(1);
        when(game.getLiveDeployedEntitiesOwnedBy(player2)).thenReturn(1);

        VictoryResult result = victory.checkVictory(game, context);

        assertFalse(result.isVictory(), "Il ne devrait pas y avoir de victoire avec plusieurs équipes vivantes");
    }

    @Test
    void testNoVictoryWithUnteamedPlayer() {
        Player player1 = new Player(1, "Player1");
        player1.setTeam(1);
        Player player2 = new Player(2, "Player2");
        player2.setTeam(Player.TEAM_NONE);

        when(game.getPlayersList()).thenReturn(List.of(player1, player2));
        when(game.getLiveDeployedEntitiesOwnedBy(player1)).thenReturn(1);
        when(game.getLiveDeployedEntitiesOwnedBy(player2)).thenReturn(1);

        VictoryResult result = victory.checkVictory(game, context);

        assertFalse(result.isVictory(), "Il ne devrait pas y avoir de victoire avec un joueur sans équipe");
    }
}