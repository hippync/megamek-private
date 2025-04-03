package megamek.server.ratings.service;

import megamek.common.Game;
import megamek.server.ratings.model.PlayerRating;
import megamek.server.ratings.strategy.RatingStrategy;

public class RatingManager {

    private RatingStrategy ratingStrategy;

    public RatingManager(RatingStrategy ratingStrategy) {
        this.ratingStrategy = ratingStrategy;
    }

    public void updateRatings(Game game, PlayerRating player1Rating, PlayerRating player2Rating, boolean player1Won) {
        ratingStrategy.updateRatings(game, player1Rating, player2Rating, player1Won);
    }
}
