package megamek.server.ratings.strategy;

import megamek.common.Game;
import megamek.server.ratings.model.PlayerRating;

public class EloStrategy implements RatingStrategy {

    @Override
    public void updateRatings(Game game, PlayerRating player1Rating, PlayerRating player2Rating, boolean player1Won) {
        double K = 30;
        double player1Elo = player1Rating.getRating();
        double player2Elo = player2Rating.getRating();

        double expectedScorePlayer1 = 1 / (1 + Math.pow(10, (player2Elo - player1Elo) / 400));
        double expectedScorePlayer2 = 1 / (1 + Math.pow(10, (player1Elo - player2Elo) / 400));

        double scorePlayer1 = player1Won ? 1 : 0;
        double scorePlayer2 = player1Won ? 0 : 1;

        player1Rating.setRating(player1Elo + K * (scorePlayer1 - expectedScorePlayer1));
        player2Rating.setRating(player2Elo + K * (scorePlayer2 - expectedScorePlayer2));
    }
}
