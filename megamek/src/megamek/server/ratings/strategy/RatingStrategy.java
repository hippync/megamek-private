package megamek.server.ratings.strategy;

import megamek.common.Game;
import megamek.server.ratings.model.PlayerRating;

public interface RatingStrategy {
    void updateRatings(Game game, PlayerRating player1Rating, PlayerRating player2Rating, boolean player1Won);
}
