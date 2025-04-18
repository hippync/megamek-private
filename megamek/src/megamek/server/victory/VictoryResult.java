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
import megamek.common.Report;
import megamek.common.util.EloRankingStrategy;
import megamek.common.util.PlayerRankingManager;


import java.util.*;

/**
 * A Victory Result stores information about the results of checking one or more victory conditions.
 * It includes player scores and a flag if a game-ending victory is achieved or not. A draw as well as
 * a victory are indicated by the isVictory flag being true. The game will typically end when a
 * VictoryResult with isVictory == true is found.
 */
public final class VictoryResult {

    private final List<Report> reports = new ArrayList<>();
    private final Map<Integer, Double> playerScores = new HashMap<>();
    private final Map<Integer, Double> teamScores = new HashMap<>();
    private final PlayerRankingManager rankingManager = new PlayerRankingManager(new EloRankingStrategy());

    private boolean isVictory;

    public VictoryResult(boolean win) {
        this.isVictory = win;
    }

    public VictoryResult(boolean win, int player, int team ) {
        this.isVictory = win;
        if (player != Player.PLAYER_NONE) {
            setPlayerScore(player, 1.0);
        }
        if (team != Player.TEAM_NONE) {
            setTeamScore(team, 1.0);
        }
    }

    public static VictoryResult noResult() {
        return new VictoryResult(false, Player.PLAYER_NONE, Player.TEAM_NONE);
    }
    
    public static VictoryResult drawResult() {
        return new VictoryResult(true, Player.PLAYER_NONE, Player.TEAM_NONE);
    }

    private void updateRankingsForPlayers(Player[] winningPlayers, Player[] losingPlayers) {
        if (winningPlayers.length > 0 && losingPlayers.length > 0) {
            rankingManager.updatePlayerRankings(winningPlayers, losingPlayers);
        }
    }
    
    private void updatePlayerRatings(Game game) {
        int wonPlayer = getWinningPlayer();
        int wonTeam = getWinningTeam();
    
        if (wonPlayer != Player.PLAYER_NONE) {
            Player winner = game.getPlayer(wonPlayer);
            Player[] winningPlayer = { winner };
            Player[] allPlayers = game.getPlayersList().toArray(new Player[0]);
            Player[] losingPlayers = Arrays.stream(allPlayers)
                                           .filter(player -> player != null && player.getId() != wonPlayer)
                                           .toArray(Player[]::new);
            updateRankingsForPlayers(winningPlayer, losingPlayers);
        }
    
        if (wonTeam != Player.TEAM_NONE) {
            Player[] allPlayers = game.getPlayersList().toArray(new Player[0]);
            Player[] winningPlayers = Arrays.stream(allPlayers)
                                           .filter(player -> player != null && player.getTeam() == wonTeam)
                                           .toArray(Player[]::new);
            Player[] losingPlayers = Arrays.stream(allPlayers)
                                           .filter(player -> player != null && player.getTeam() != wonTeam)
                                           .toArray(Player[]::new);
            updateRankingsForPlayers(winningPlayers, losingPlayers);
        }
    }
    
    public void checkAndUpdateVictory(Game game) {
            updatePlayerRatings(game);
    }

    /**
     * @return True if this result indicates a game-ending state - this can be a victory but also a draw!
     * @see #isDraw()
     */
    public boolean isVictory() {
        return isVictory;
    }

    public void setVictory(boolean b) {
        this.isVictory = b;
    }

    public boolean isDraw() {
        return (getWinningPlayer() == Player.PLAYER_NONE) && (getWinningTeam() == Player.TEAM_NONE);
    }

    /**
     * @return the ID of the winning player, or Player.PLAYER_NONE if it's a draw
     */
    public int getWinningPlayer() {
        return getWinningPlayerOrTeam(playerScores, Player.PLAYER_NONE);
    }

    /**
     * @return the ID of the winning team, or Player.TEAM_NONE if it's a draw
     */
    public int getWinningTeam() {
        return getWinningPlayerOrTeam(teamScores, Player.TEAM_NONE);
    }

    /**
     * Incorporates the player and team scores of the given other VictoryResult to this one's scores.
     * Where scores for the same player/team IDs exist in both, the scores are added.
     *
     * @param other another VictoryResult to incorporate the scores from
     */
    void addScores(VictoryResult other) {
        for (int playerId : other.getScoringPlayers()) {
            setPlayerScore(playerId, getPlayerScore(playerId) + other.getPlayerScore(playerId));
        }
        for (int teamId : other.getScoringTeams()) {
            setTeamScore(teamId, getTeamScore(teamId) + other.getTeamScore(teamId));
        }
    }

    public void setPlayerScore(int id, double score) {
        playerScores.put(id, score);
    }

    public void setTeamScore(int id, double score) {
        teamScores.put(id, score);
    }

    double getPlayerScore(int id) {
        return playerScores.getOrDefault(id, 0.0);
    }

    double getTeamScore(int id) {
        return teamScores.getOrDefault(id, 0.0);
    }

    Set<Integer> getScoringPlayers() {
        return playerScores.keySet();
    }

    Set<Integer> getScoringTeams() {
        return teamScores.keySet();
    }

    void addReport(Report r) {
        reports.add(r);
    }

    void addReports(List<Report> reportList) {
        reports.addAll(reportList);
    }

    /**
     * @return List of reports generated by the victory checking. This is usually empty when no
     * victory occurs, but might contain reports about VictoryConditions which are about to be
     * filled or are time triggered
     */
    List<Report> getReports() {
        return reports;
    }

    /**
     * Creates and returns victory/draw reports if this is a victory and updates the game's settings to reflect
     * the victory result.
     *
     * @param game The current {@link Game} for which the victory needs to be processed
     * @return a list of reports generated by victory checking and by the actual processing of the victory
     */
    public List<Report> processVictory(Game game) {
        List<Report> gatheredReports = getReports();
        if (isVictory()) {
            boolean draw = isDraw();
            int wonPlayer = getWinningPlayer();
            int wonTeam = getWinningTeam();

            if (wonPlayer != Player.PLAYER_NONE) {
                gatheredReports.add(Report.publicReport(7200).add(game.getPlayer(wonPlayer).getColorForPlayer()));
            }

            if (wonTeam != Player.TEAM_NONE) {
                gatheredReports.add(Report.publicReport(7200).add("Team " + wonTeam));
            }

            if (draw) {
                // multiple-won draw
                game.setVictoryPlayerId(Player.PLAYER_NONE);
                game.setVictoryTeam(Player.TEAM_NONE);
            } else {
                // nobody-won draw or single player win or single team win
                game.setVictoryPlayerId(wonPlayer);
                game.setVictoryTeam(wonTeam);
            }
        } else {
            game.cancelVictory();
        }
        return gatheredReports;
    }

    private int getWinningPlayerOrTeam(Map<Integer, Double> scores, int defaultPlayerOrTeam) {
        double max = Double.MIN_VALUE;
        int maxPlayerOrTeam = defaultPlayerOrTeam;
        boolean draw = false;
        for (Map.Entry<Integer, Double> entry : scores.entrySet()) {
            if (entry.getValue() == max) {
                draw = true;
            } else if (entry.getValue() > max) {
                draw = false;
                max = entry.getValue();
                maxPlayerOrTeam = entry.getKey();
            }
        }

        return draw ? defaultPlayerOrTeam : maxPlayerOrTeam;
    }

    @Override
    public String toString() {
        return "[VictoryResult] Win: " + isVictory
                + (isVictory ? " Player: " + getWinningPlayer() + " Team: " + getWinningTeam() : "");
    }
}
