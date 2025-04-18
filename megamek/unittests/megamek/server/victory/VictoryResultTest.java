/*
 * Copyright (c) 2024 - The MegaMek Team. All Rights Reserved.
 *
 * This file is part of MegaMek.
 *
 * MegaMek is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MegaMek is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MegaMek. If not, see <http://www.gnu.org/licenses/>.
 */
package megamek.server.victory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import java.util.List;

import megamek.common.Game;
import megamek.common.Player;
import megamek.common.util.EloRankingStrategy;

class VictoryResultTest {

    @Test
    void testGetWinningPlayer() {
        // Trivial case: no players
        VictoryResult testResult = new VictoryResult(false);
        assertSame(Player.PLAYER_NONE, testResult.getWinningPlayer());

        // Case with two players
        int winningPlayer = 0;
        int losingPlayer = 1;

        testResult.setPlayerScore(winningPlayer, 100);
        testResult.setPlayerScore(losingPlayer, 40);

        assertSame(winningPlayer, testResult.getWinningPlayer());

        // Case with three players and a draw
        int secondWinningPlayer = 2;

        testResult.setPlayerScore(secondWinningPlayer, 100);
        assertNotSame(secondWinningPlayer, testResult.getWinningPlayer());
        assertNotSame(winningPlayer, testResult.getWinningPlayer());
        assertSame(Player.PLAYER_NONE, testResult.getWinningPlayer());
    }

    @Test
    void testGetWinningTeam() {
        // Trivial case: no team
        VictoryResult testResult = new VictoryResult(false);
        assertSame(Player.TEAM_NONE, testResult.getWinningTeam());

        // Case with two teams
        int winningTeam = 1;
        int losingTeam = 2;

        testResult.setTeamScore(winningTeam, 100);
        testResult.setTeamScore(losingTeam, 40);

        assertSame(winningTeam, testResult.getWinningTeam());

        // Case with three teams and a draw
        int secondWinningTeam = 3;

        testResult.setTeamScore(secondWinningTeam, 100);
        assertNotSame(secondWinningTeam, testResult.getWinningTeam());
        assertNotSame(winningTeam, testResult.getWinningTeam());
        assertSame(Player.TEAM_NONE, testResult.getWinningTeam());
    }

    @Test
    void testProcessVictory() {
        // Trivial cases
        VictoryResult victoryResult = new VictoryResult(true);

        Player playerMock = mock(Player.class);
        when(playerMock.getColorForPlayer()).thenReturn("");

        Game gameMock = mock(Game.class);
        when(gameMock.getPlayer(anyInt())).thenReturn(playerMock);

        assertTrue(victoryResult.processVictory(gameMock).isEmpty());

        VictoryResult victoryResult2 = new VictoryResult(false);
        assertTrue(victoryResult2.processVictory(gameMock).isEmpty());

        // Less trivial cases
        // Only won player is set
        VictoryResult victoryResult3 = new VictoryResult(true);
        victoryResult3.setPlayerScore(1, 100);
        assertSame(1, victoryResult3.processVictory(gameMock).size());

        // Only won team is set
        VictoryResult victoryResult4 = new VictoryResult(true);
        victoryResult4.setTeamScore(1, 100);
        assertSame(1, victoryResult4.processVictory(gameMock).size());

        // Both player and team winners are set
        VictoryResult victoryResult5 = new VictoryResult(true);
        victoryResult5.setPlayerScore(1, 100);
        victoryResult5.setTeamScore(1, 100);
        assertSame(2, victoryResult5.processVictory(gameMock).size());

        // Draw result
        VictoryResult victoryResult6 = new VictoryResult(true);
        victoryResult6.setPlayerScore(1, 100);
        victoryResult6.setPlayerScore(2, 100);
        assertTrue(victoryResult6.processVictory(gameMock).isEmpty());
    }

    @Test
    void testGetPlayerScoreNull() {
        VictoryResult victoryResult = new VictoryResult(true);

        assertEquals(0.0, victoryResult.getPlayerScore(1), 0.0);
    }

    @Test
    void testGetPlayerScore() {
        VictoryResult victoryResult = new VictoryResult(true);
        victoryResult.setPlayerScore(1, 3);

        assertEquals(3.0, victoryResult.getPlayerScore(1), 0.0);
    }

    @Test
    void testUpdateHiScore_Player() {
        VictoryResult victoryResult = new VictoryResult(false);
        victoryResult.setPlayerScore(1, 1);
        victoryResult.setPlayerScore(2, 2);
        victoryResult.setPlayerScore(3, 1);

        assertEquals(2, victoryResult.getWinningPlayer());
    }

    @Test
    void testUpdateHiScore_Team() {
        VictoryResult victoryResult = new VictoryResult(false);
        victoryResult.setTeamScore(1, 1);
        victoryResult.setTeamScore(2, 2);
        victoryResult.setTeamScore(3, 1);

        assertEquals(2, victoryResult.getWinningTeam());
    }

    @Test
    void testSetPlayerScore() {
        VictoryResult victoryResult = new VictoryResult(true);
        victoryResult.setPlayerScore(1, 3);

        assertEquals(1, victoryResult.getScoringPlayers().size());
        assertEquals(3.0, victoryResult.getPlayerScore(1), 0.0);
    }

    @Test
    void testSetTeamScore() {
        VictoryResult victoryResult = new VictoryResult(true);
        victoryResult.setTeamScore(1, 3);

        assertEquals(1, victoryResult.getScoringTeams().size());
        assertEquals(3.0, victoryResult.getTeamScore(1), 0.0);
    }

    @Test
    void testFindWinningPlayer() {
        VictoryResult victoryResult = new VictoryResult(false);
        victoryResult.setPlayerScore(1, 1);
        victoryResult.setPlayerScore(2, 2);
        victoryResult.setPlayerScore(3, 1);

        assertEquals(2, victoryResult.getWinningPlayer(), "Le joueur avec le score le plus élevé devrait gagner");
    }

    @Test
    void testFindWinningTeam() {
        VictoryResult victoryResult = new VictoryResult(false);
        victoryResult.setTeamScore(1, 1);
        victoryResult.setTeamScore(2, 2);
        victoryResult.setTeamScore(3, 1);

        assertEquals(2, victoryResult.getWinningTeam(), "L'équipe avec le score le plus élevé devrait gagner");
    }

    @Test
    void testCheckAndUpdateVictory_PlayerWin() {
        VictoryResult result = new VictoryResult(true);
        result.setPlayerScore(1, 1.0);
        result.setPlayerScore(2, 0.0);

        Player winner = new Player(1, "Winner");
        winner.setRanking(1500);

        Player loser = new Player(2, "Loser");
        loser.setRanking(1400);

        Game game = mock(Game.class);
        when(game.getPlayer(1)).thenReturn(winner);
        when(game.getPlayer(2)).thenReturn(loser);
        when(game.getPlayersList()).thenReturn(List.of(winner, loser));

        result.checkAndUpdateVictory(game);

        assertTrue(winner.getRanking() > 1500, "Le gagnant devrait avoir un meilleur classement");
        assertTrue(loser.getRanking() < 1400, "Le perdant devrait avoir un moins bon classement");
    }

    @Test
    void testCheckAndUpdateVictory_TeamWin() {
        VictoryResult result = new VictoryResult(true);
        result.setTeamScore(1, 1.0);
        result.setTeamScore(2, 0.0);

        Player winner1 = new Player(1, "Winner1");
        winner1.setRanking(1500);
        winner1.setTeam(1);

        Player winner2 = new Player(2, "Winner2");
        winner2.setRanking(1450);
        winner2.setTeam(1);

        Player loser1 = new Player(3, "Loser1");
        loser1.setRanking(1400);
        loser1.setTeam(2);

        Player loser2 = new Player(4, "Loser2");
        loser2.setRanking(1350);
        loser2.setTeam(2);

        Game game = mock(Game.class);
        when(game.getPlayersList()).thenReturn(List.of(winner1, winner2, loser1, loser2));

        result.checkAndUpdateVictory(game);

        assertTrue(winner1.getRanking() > 1500, "Le gagnant 1 devrait avoir un meilleur classement");
        assertTrue(winner2.getRanking() > 1450, "Le gagnant 2 devrait avoir un meilleur classement");
        assertTrue(loser1.getRanking() < 1400, "Le perdant 1 devrait avoir un moins bon classement");
        assertTrue(loser2.getRanking() < 1350, "Le perdant 2 devrait avoir un moins bon classement");
    }

    @Test
    void testCheckAndUpdateVictory_NoVictory() {
        VictoryResult result = new VictoryResult(false);

        Game game = mock(Game.class);
        result.checkAndUpdateVictory(game);
    }

    @Test
    void testMultipleWinners() {
        EloRankingStrategy rankingStrategy = new EloRankingStrategy();

        Player winner1 = new Player(1, "Winner1");
        winner1.setRanking(1500);

        Player winner2 = new Player(2, "Winner2");
        winner2.setRanking(1450);

        Player loser = new Player(3, "Loser");
        loser.setRanking(1400);

        Player[] winners = { winner1, winner2 };
        Player[] losers = { loser };

        rankingStrategy.updateRankings(winners, losers);

        assertTrue(winner1.getRanking() > 1500, "Winner1 should have a higher ranking");
        assertTrue(winner2.getRanking() > 1450, "Winner2 should have a higher ranking");
        assertTrue(loser.getRanking() < 1400, "Loser should have a lower ranking");
    }

    @Test
    void testVictoryResultUpdatesRanking() {
        // Arrange : deux vrais joueurs
        Player player1 = new Player(1, "Alice");
        player1.setRanking(1500);
        player1.setTeam(1);

        Player player2 = new Player(2, "Bob");
        player2.setRanking(1400);
        player2.setTeam(2);

        // Setup du jeu simulé
        Game game = mock(Game.class);
        when(game.getPlayersList()).thenReturn(List.of(player1, player2));
        when(game.getPlayer(1)).thenReturn(player1);
        when(game.getPlayer(2)).thenReturn(player2);

        // Score individuel : Alice gagne
        VictoryResult result = new VictoryResult(true);
        result.setPlayerScore(1, 1.0);
        result.setPlayerScore(2, 0.0);

        result.checkAndUpdateVictory(game);

        // Assert : les rankings ont été modifiés
        assertTrue(player1.getRanking() > 1500, "Alice devrait avoir gagné du ranking");
        assertTrue(player2.getRanking() < 1400, "Bob devrait avoir perdu du ranking");
    }

}
