package org.picarran.mycathome.controller;

import org.picarran.mycathome.entity.GameState;
import org.picarran.mycathome.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/game")
public class ApiGameController {

    @Autowired
    private GameService gameService;

    @PostMapping("/{gameUuid}/click")
    public ResponseEntity<?> click(@PathVariable String gameUuid, @RequestBody Map<String,Integer> body, HttpSession session){
        int r = body.getOrDefault("r", -1);
        int c = body.getOrDefault("c", -1);
        Map<String,Object> resp = gameService.applyClick(session, gameUuid, r, c);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{gameUuid}/state")
    public ResponseEntity<?> state(@PathVariable String gameUuid, HttpSession session){
        GameState gs = gameService.getGameStateFromSessionOrDb(session, gameUuid);
        Map<String,Object> resp = new HashMap<>();
        resp.put("success", gs!=null);
        resp.put("data", gs);
        return ResponseEntity.ok(resp);
    }
}
