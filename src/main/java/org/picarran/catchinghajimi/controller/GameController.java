package org.picarran.catchinghajimi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.picarran.catchinghajimi.entity.GameState;
import org.picarran.catchinghajimi.entity.UserDO;
import org.picarran.catchinghajimi.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class GameController {

    @Autowired
    private GameService gameService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/game/start")
    public String startGame(HttpServletRequest req) {
        HttpSession session = req.getSession();
        UserDO u = (UserDO) session.getAttribute("loginUser");
        if (u == null)
            return "redirect:/user/login";
        // optional level param: "1" (rect) or "2" (diamond)
        String level = req.getParameter("level");
        if (level != null) {
            session.setAttribute("levelType", level);
        }
        String gameUuid = gameService.startNewGame(session, u.getId());
        return "redirect:/game/play/" + gameUuid;
    }

    @GetMapping("/game/play/{gameUuid}")
    public String play(@PathVariable String gameUuid, HttpSession session, Model model) throws Exception {
        GameState state = gameService.getGameStateFromSessionOrDb(session, gameUuid);
        if (state == null) {
            model.addAttribute("mapJson", "null");
            model.addAttribute("gameUuid", gameUuid);
        } else {
            String mapJson = objectMapper.writeValueAsString(state);
            model.addAttribute("mapJson", mapJson);
            model.addAttribute("gameUuid", gameUuid);
            // best score for current map level
            Object uo = session.getAttribute("loginUser");
            if (uo instanceof org.picarran.catchinghajimi.entity.UserDO) {
                org.picarran.catchinghajimi.entity.UserDO u = (org.picarran.catchinghajimi.entity.UserDO) uo;
                java.util.Map<String, Integer> bestMap = gameService.getBestClicksByLevel(u.getId());
                Integer best = bestMap.get(state.getLevelType());
                if (best == null) {
                    // try normalized keys if levelType like 'rect'/'diamond'/'circle'
                    String lt = state.getLevelType();
                    if ("rect".equalsIgnoreCase(lt))
                        best = bestMap.get("1");
                    else if ("diamond".equalsIgnoreCase(lt))
                        best = bestMap.get("2");
                    else if ("circle".equalsIgnoreCase(lt))
                        best = bestMap.get("3");
                }
                model.addAttribute("bestClicks", best);
            }
        }
        return "game";
    }
}
