package org.picarran.catchinghajimi.service;

import org.picarran.catchinghajimi.entity.GameState;

import javax.servlet.http.HttpSession;
import java.util.Map;

public interface GameService {
    String startNewGame(HttpSession session, Long userId);

    GameState getGameStateFromSessionOrDb(HttpSession session, String gameUuid);

    Map<String, Object> applyClick(HttpSession session, String gameUuid, int r, int c);

    /**
     * Get user's best (minimum clicks) per levelType. Keys: "1"=rect, "2"=diamond,
     * "3"=circle.
     * If no success for a level, the key may be absent.
     */
    Map<String, Integer> getBestClicksByLevel(Long userId);

    /**
     * Leaderboard per level: returns a map of levelType -> list of entries, each
     * entry is a map with keys:
     * username (String), clicks (Integer)
     * Sorted by clicks ascending.
     */
    Map<String, java.util.List<java.util.Map<String, Object>>> getLeaderboard();
}
