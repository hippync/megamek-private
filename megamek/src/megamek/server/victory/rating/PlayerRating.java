package megamek.server.victory.rating;

import java.io.Serializable;

/**
 * Represents a player's rating in the competitive system.
 * This class is designed to be flexible enough to support different rating
 * algorithms.
 */
public class PlayerRating implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int playerId;
    private double rating;
    private int gamesPlayed;
    private boolean isBot;

    public PlayerRating(int playerId, boolean isBot) {
        this.playerId = playerId;
        this.isBot = isBot;
        this.rating = 1500.0; // Default starting Elo
        this.gamesPlayed = 0;
    }

    public int getPlayerId() {
        return playerId;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void incrementGamesPlayed() {
        this.gamesPlayed++;
    }

    public boolean isBot() {
        return isBot;
    }
}