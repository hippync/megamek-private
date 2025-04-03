package megamek.server.victory.rating;

import megamek.server.victory.VictoryResult;
import megamek.common.Player;
import java.util.Map;

/**
 * Implementation of the Elo rating system.
 * This is a simple implementation that can be replaced or modified as needed.
 */
public class EloRatingSystem extends RatingSystem {
    private static final double DEFAULT_K_FACTOR = 32.0;
    private static final double PROVISIONAL_K_FACTOR = 64.0;
    private static final int PROVISIONAL_GAMES_THRESHOLD = 30;

    @Override
    public void updateRatings(Map<Integer, PlayerRating> ratings, VictoryResult result) {
        int winner = result.getWinningPlayer();

        // Don't update ratings for draws or invalid results
        if (winner == Player.PLAYER_NONE || result.isDraw()) {
            return;
        }

        // Get winner's rating
        PlayerRating winnerRating = ratings.get(winner);
        if (winnerRating == null) {
            return;
        }

        // Update ratings for all other players in the ratings map
        for (Map.Entry<Integer, PlayerRating> entry : ratings.entrySet()) {
            int playerId = entry.getKey();
            if (playerId != winner) {
                PlayerRating loserRating = entry.getValue();
                updatePairRating(winnerRating, loserRating, 1.0);
            }
        }
    }

    @Override
    protected double calculateExpectedScore(double playerRating, double opponentRating) {
        return 1.0 / (1.0 + Math.pow(10.0, (opponentRating - playerRating) / 400.0));
    }

    @Override
    protected double getKFactor(PlayerRating playerRating) {
        return playerRating.getGamesPlayed() < PROVISIONAL_GAMES_THRESHOLD
                ? PROVISIONAL_K_FACTOR
                : DEFAULT_K_FACTOR;
    }

    private void updatePairRating(PlayerRating winner, PlayerRating loser, double score) {
        double expectedScore = calculateExpectedScore(winner.getRating(), loser.getRating());

        double winnerK = getKFactor(winner);
        double loserK = getKFactor(loser);

        winner.setRating(winner.getRating() + winnerK * (score - expectedScore));
        loser.setRating(loser.getRating() + loserK * (expectedScore - score));

        winner.incrementGamesPlayed();
        loser.incrementGamesPlayed();
    }
}