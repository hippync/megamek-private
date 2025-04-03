package megamek.common.util;

/**
 * https://mattmazzola.medium.com/implementing-the-elo-rating-system-a085f178e065
*/
import megamek.common.Player;

public class EloRankingStrategy implements IRankingStrategy{

    private static final int DEFAULT_SCALE_FACTOR = 400;
    private static final int DEFAULT_K = 32;
    private static final int DEFAULT_EXPONENT_FACTOR = 10;

    private final int scaleFactor;
    private final int k;
    private final int exponentFactor;

    public EloRankingStrategy() {
        this.scaleFactor = DEFAULT_SCALE_FACTOR;
        this.k = DEFAULT_K;
        this.exponentFactor = DEFAULT_EXPONENT_FACTOR;
    }

    public EloRankingStrategy(int scaleFactor, int k, int exponentFactor) {
        this.scaleFactor = scaleFactor;
        this.k = k;
        this.exponentFactor = exponentFactor;
    }

    public void updateRankings(Player[] winners, Player[] losers) {
        double ratingWinner = calculateAverageRating(winners);
        double ratingLoser = calculateAverageRating(losers);

        double expectedScoreWinner = 1 / (1.0 + Math.pow(exponentFactor, (ratingLoser - ratingWinner) / scaleFactor));
        double expectedScoreLoser = 1 - expectedScoreWinner;

        for (Player winner : winners) {
            double individualRatingUpdate = winner.getRanking() + k * (1 - expectedScoreWinner);
            winner.setRanking((int) individualRatingUpdate);
        }
        for (Player loser : losers) {
            double individualRatingUpdate = loser.getRanking() + k * (0 - expectedScoreLoser);
            loser.setRanking((int) individualRatingUpdate);
        }
    }

    private static double calculateAverageRating(Player[] players) {
        double sum = 0;
        for (Player player : players) {
            sum += player.getRanking();
        }
        return sum / players.length;
    }
}