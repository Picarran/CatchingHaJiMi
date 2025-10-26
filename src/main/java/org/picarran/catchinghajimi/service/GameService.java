package org.picarran.catchinghajimi.service;

import org.picarran.catchinghajimi.entity.GameState;

import javax.servlet.http.HttpSession;
import java.util.Map;

public interface GameService {
    String startNewGame(HttpSession session, Long userId);
    GameState getGameStateFromSessionOrDb(HttpSession session, String gameUuid);
    Map<String,Object> applyClick(HttpSession session, String gameUuid, int r, int c);
}
