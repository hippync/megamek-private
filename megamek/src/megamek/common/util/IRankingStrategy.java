package megamek.common.util;

import megamek.common.Player;

public interface IRankingStrategy {
    public void updateRankings(Player[] winners, Player[] losers);
}