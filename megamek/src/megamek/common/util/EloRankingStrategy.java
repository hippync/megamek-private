package megamek.common.util;

import megamek.common.Player;

/**
 * Implémentation du système de notation Elo pour MegaMek.
 * Permet d'évaluer le niveau des joueurs (humains ou IA) dans un cadre compétitif.
 */
public class EloRankingStrategy implements RankingStrategy {

    // Valeurs par défaut pour un environnement compétitif
    private static final int DEFAULT_K_FACTOR = 40;       
    private static final int DEFAULT_RATING_DIVISOR = 400;
    private static final int DEFAULT_EXP_BASE = 10;

    private final int kFactor;
    private final int ratingDivisor;
    private final int exponentBase;

    // Constructeur par défaut
    public EloRankingStrategy() {
        this(DEFAULT_K_FACTOR, DEFAULT_RATING_DIVISOR, DEFAULT_EXP_BASE);
    }

    // Constructeur personnalisé (pour extensibilité ou ajustements futurs)
    public EloRankingStrategy(int kFactor, int ratingDivisor, int exponentBase) {
        this.kFactor = kFactor;
        this.ratingDivisor = ratingDivisor;
        this.exponentBase = exponentBase;
    }

    @Override
    public void updateRankings(Player[] winners, Player[] losers) {
        double averageWinnerRating = computeAverageRating(winners);
        double averageLoserRating = computeAverageRating(losers);

        double expectedScoreWinners = computeExpectedScore(averageWinnerRating, averageLoserRating);
        double expectedScoreLosers = 1 - expectedScoreWinners;

        // Mise à jour des gagnants
        for (Player winner : winners) {
            int updatedRating = calculateNewRating(winner.getRanking(), 1, expectedScoreWinners);
            winner.setRanking(updatedRating);
        }

        // Mise à jour des perdants
        for (Player loser : losers) {
            int updatedRating = calculateNewRating(loser.getRanking(), 0, expectedScoreLosers);
            loser.setRanking(updatedRating);
        }
    }

    private double computeAverageRating(Player[] players) {
        if (players == null || players.length == 0) return 0;
        double total = 0;
        for (Player player : players) {
            total += player.getRanking();
        }
        return total / players.length;
    }

    private double computeExpectedScore(double ratingA, double ratingB) {
        double exponent = (ratingB - ratingA) / (double) ratingDivisor;
        return 1.0 / (1.0 + Math.pow(exponentBase, exponent));
    }

    private int calculateNewRating(double currentRating, int actualScore, double expectedScore) {
        double delta = kFactor * (actualScore - expectedScore);
        return (int) Math.round(currentRating + delta);
    }
}
