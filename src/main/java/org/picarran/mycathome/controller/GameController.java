package org.picarran.mycathome.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.picarran.mycathome.entity.GameState;
import org.picarran.mycathome.entity.UserDO;
import org.picarran.mycathome.service.GameService;
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
    public String startGame(HttpServletRequest req){
        HttpSession session = req.getSession();
        UserDO u = (UserDO) session.getAttribute("loginUser");
        if(u==null) return "redirect:/user/login";
        String gameUuid = gameService.startNewGame(session, u.getId());
        return "redirect:/game/play/"+gameUuid;
    }

    @GetMapping("/game/play/{gameUuid}")
    public String play(@PathVariable String gameUuid, HttpSession session, Model model) throws Exception{
        GameState state = gameService.getGameStateFromSessionOrDb(session, gameUuid);
        if(state==null){ model.addAttribute("mapJson","null"); model.addAttribute("gameUuid",gameUuid); }
        else{
            String mapJson = objectMapper.writeValueAsString(state);
            model.addAttribute("mapJson", mapJson);
            model.addAttribute("gameUuid", gameUuid);
        }
        return "game";
    }
}
