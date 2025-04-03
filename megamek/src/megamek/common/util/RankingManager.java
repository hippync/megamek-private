package megamek.common.util;

import megamek.common.Player;

public class RankingManager {
    private final IRankingStrategy strategy;

    public RankingManager(IRankingStrategy strategy) {
        this.strategy = strategy;
    }

    public void updateRankings(Player[] winners, Player[] losers) {
        this.strategy.updateRankings(winners, losers);
    }
}