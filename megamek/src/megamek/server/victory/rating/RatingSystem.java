package megamek.server.victory.rating;

import megamek.server.victory.VictoryResult;
import java.util.Map;

/**
 * Abstract base class for implementing different rating systems.
 * This allows for easy swapping between different rating algorithms (Elo,
 * Glicko, etc.)
 */
public abstract class RatingSystem {

    /**
     * Updates player ratings based on the match result
     * 
     * @param ratings Current ratings of all players
     * @param result  The victory result containing match outcome
     */
    public abstract void updateRatings(Map<Integer, PlayerRating> ratings, VictoryResult result);

    /**
     * Calculates the expected score for a player against an opponent
     * 
     * @param playerRating   The player's current rating
     * @param opponentRating The opponent's current rating
     * @return Expected score between 0 and 1
     */
    protected abstract double calculateExpectedScore(double playerRating, double opponentRating);

    /**
     * Calculates the K-factor for rating adjustments
     * 
     * @param playerRating The player's rating data
     * @return The K-factor to use for this player
     */
    protected abstract double getKFactor(PlayerRating playerRating);
}