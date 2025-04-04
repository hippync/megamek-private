package megamek.common.util;

import megamek.common.Player;

/**
 * Définit une stratégie de calcul de classement (ranking) des joueurs.
 * Peut être implémentée avec différents algorithmes comme Elo, Glicko, etc.
 */
public interface RankingStrategy {

    /**
     * Met à jour les classements des joueurs après un match.
     *
     * @param winners joueurs ou IA ayant gagné
     * @param losers  joueurs ou IA ayant perdu
     */
    void updateRankings(Player[] winners, Player[] losers);
}
