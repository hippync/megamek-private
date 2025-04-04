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
}
