package megamek.server.victory.rating;

import megamek.server.victory.VictoryResult;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

/**
 * Manages the rating system for competitive play.
 * This class coordinates rating updates and maintains player rating data.
 */
public class RatingManager implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<Integer, PlayerRating> playerRatings;
    private final RatingSystem ratingSystem;

    public RatingManager(RatingSystem ratingSystem) {
        this.playerRatings = new HashMap<>();
        this.ratingSystem = ratingSystem;
    }

    /**
     * Registers a new player in the rating system
     * 
     * @param playerId The player's unique ID
     * @param isBot    Whether the player is an AI
     */
    public void registerPlayer(int playerId, boolean isBot) {
        if (!playerRatings.containsKey(playerId)) {
            playerRatings.put(playerId, new PlayerRating(playerId, isBot));
        }
    }

    /**
     * Updates ratings based on a match result
     * 
     * @param result The VictoryResult containing match outcome
     */
    public void updateRatings(VictoryResult result) {
        ratingSystem.updateRatings(playerRatings, result);
    }

    /**
     * Gets a player's current rating
     * 
     * @param playerId The player's unique ID
     * @return The player's rating, or null if player not found
     */
    public PlayerRating getPlayerRating(int playerId) {
        return playerRatings.get(playerId);
    }

    /**
     * Gets all player ratings sorted by score
     * 
     * @return Map of player IDs to their ratings
     */
    public Map<Integer, PlayerRating> getAllRatings() {
        return new HashMap<>(playerRatings);
    }
}