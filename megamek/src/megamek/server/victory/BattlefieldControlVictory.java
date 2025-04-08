/*
 * Copyright (c) 2007-2008 Ben Mazur (bmazur@sev.org)
 * Copyright (c) 2024 - The MegaMek Team. All Rights Reserved.
 *
 * This file is part of MegaMek.
 *
 * MegaMek is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MegaMek is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MegaMek. If not, see <http://www.gnu.org/licenses/>.
 */
package megamek.server.victory;

import megamek.common.Game;
import megamek.common.Player;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class represents a battlefield control (units of only one team left alive) victory
 * Note that this currently does not exclude gun emplacements or spawns (MekWarriors, Missiles) from
 * the test.
 */
public class BattlefieldControlVictory implements VictoryCondition, Serializable {

    @Override
    public VictoryResult checkVictory(Game game, Map<String, Object> ctx) {
        var alivePlayers = game.getPlayersList().stream()
                                 .filter(p -> game.getLiveDeployedEntitiesOwnedBy(p) > 0)
                                 .toList();

        if (alivePlayers.isEmpty()) {
            return VictoryResult.drawResult();
        }

        if (alivePlayers.size() == 1) {
            Player last = alivePlayers.get(0);
            if (last.getTeam() == Player.TEAM_NONE) {
                return new VictoryResult(true, last.getId(), Player.TEAM_NONE);
            }
        }

        Set<Integer> aliveTeams = alivePlayers.stream()
                                        .map(Player::getTeam)
                                        .filter(t -> t != Player.TEAM_NONE)
                                        .collect(Collectors.toSet());

        boolean anyUnteamed = alivePlayers.stream().anyMatch(p -> p.getTeam() == Player.TEAM_NONE);

        if (aliveTeams.size() == 1 && !anyUnteamed) {
            return new VictoryResult(true, Player.PLAYER_NONE, aliveTeams.iterator().next());
        }

        return VictoryResult.noResult();
    }
}
