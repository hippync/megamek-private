package megamek.common.util;

import megamek.common.Player;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EloRankingStrategyTest {

    @Test
    void testEloRankingUpdate() {
        EloRankingStrategy rankingStrategy = new EloRankingStrategy();

        Player winner = new Player(1, "Winner");
        winner.setRanking(1500);

        Player loser = new Player(2, "Loser");
        loser.setRanking(1400);

        Player[] winners = { winner };
        Player[] losers = { loser };

        rankingStrategy.updateRankings(winners, losers);

        // Vérifier que le gagnant a gagné des points
        assertTrue(winner.getRanking() > 1500, "Le gagnant devrait avoir un ranking plus élevé");

        // Vérifier que le perdant a perdu des points
        assertTrue(loser.getRanking() < 1400, "Le perdant devrait avoir un ranking plus bas");
    }
    
    @Test
    void testEloExactScoreUpdate() {
        // Configuration des paramètres Elo (K = 40, Diviseur = 400, Base = 10)
        EloRankingStrategy strategy = new EloRankingStrategy(40, 400, 10);

        // Création des joueurs avec des classements initiaux
        Player winner = new Player(1, "Winner");
        winner.setRanking(1500);

        Player loser = new Player(2, "Loser");
        loser.setRanking(1400);

        Player[] winners = { winner };
        Player[] losers = { loser };

        // Calcul manuel du score attendu
        double expectedScoreWinner = 1.0 / (1.0 + Math.pow(10, (1400 - 1500) / 400.0)); // ≈ 0.640
        double expectedScoreLoser = 1.0 - expectedScoreWinner;                          // ≈ 0.360

        int expectedWinnerRating = (int) Math.round(1500 + 40 * (1 - expectedScoreWinner)); // ≈ 1514
        int expectedLoserRating = (int) Math.round(1400 + 40 * (0 - expectedScoreLoser));   // ≈ 1386

        // Mise à jour des classements via la stratégie Elo
        strategy.updateRankings(winners, losers);

        // Assertions avec résultats attendus
        assertEquals(expectedWinnerRating, winner.getRanking(), "Classement du gagnant incorrect");
        assertEquals(expectedLoserRating, loser.getRanking(), "Classement du perdant incorrect");
    }
}
