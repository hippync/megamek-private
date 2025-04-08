package megamek.common.util;

import megamek.common.Player;

/**
 * Gère la mise à jour du classement des joueurs après chaque match.
 * Utilise une stratégie de classement (ex. : Elo) injectée dynamiquement.
 */
public class PlayerRankingManager {

    private final RankingStrategy rankingStrategy;

    public PlayerRankingManager(RankingStrategy rankingStrategy) {
        this.rankingStrategy = rankingStrategy;
    }

    /**
     * Met à jour le classement des joueurs après un match.
     *
     * @param winners joueurs ou IA ayant gagné
     * @param losers  joueurs ou IA ayant perdu
     */
    public void updatePlayerRankings(Player[] winners, Player[] losers) {
        rankingStrategy.updateRankings(winners, losers);
    }
}
