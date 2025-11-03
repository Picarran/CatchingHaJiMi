package org.picarran.catchinghajimi.service.impl;

/**
 * BFS shortest path from start to any boundary cell.
 * Returns list of points from start to boundary (inclusive). If no path, returns null.
 * Neighbor rule: 4-directional (up, down, left, right).
 */
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.picarran.catchinghajimi.entity.GameRecordDO;
import org.picarran.catchinghajimi.entity.GameState;
import org.picarran.catchinghajimi.entity.Point;
import org.picarran.catchinghajimi.mapper.GameRecordMapper;
import org.picarran.catchinghajimi.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameServiceImpl implements GameService {

    @Autowired
    private GameRecordMapper gameRecordMapper;

    @Autowired
    private ObjectMapper objectMapper;

    // locks per gameUuid to avoid concurrent modifications
    private final ConcurrentHashMap<String, Object> locks = new ConcurrentHashMap<>();

    @Override
    public String startNewGame(HttpSession session, Long userId) {
        // Default board size for all map types
        int rows = 11, cols = 11;
        String uuid = UUID.randomUUID().toString();
        Point cat = new Point(rows / 2, cols / 2);
        GameState gs = new GameState();
        gs.setGameUuid(uuid);
        gs.setRows(rows);
        gs.setCols(cols);
        gs.setCat(cat);
        gs.setBlocked(new ArrayList<>());
        gs.setClicks(0);
        gs.setStartTime(LocalDateTime.now());
        gs.setLastUpdateTime(gs.getStartTime());
        gs.setFinished(false);
        gs.setResult(null);
        // set level type from session if provided ("1"/"rect" or "2"/"diamond" or
        // "3"/"circle")
        Object lv = session.getAttribute("levelType");
        String levelType = (lv == null) ? "1" : String.valueOf(lv);
        if ("diamond".equalsIgnoreCase(levelType))
            levelType = "2";
        if ("rect".equalsIgnoreCase(levelType))
            levelType = "1";
        if ("circle".equalsIgnoreCase(levelType))
            levelType = "3";
        gs.setLevelType(levelType);

        session.setAttribute("game_" + uuid, gs);

        GameRecordDO record = new GameRecordDO();
        record.setUserId(userId);
        record.setGameUuid(uuid);
        record.setStartTime(gs.getStartTime());
        record.setLastUpdateTime(gs.getLastUpdateTime());
        record.setClicks(0);
        record.setResult("ONGOING");
        // state_json left null until finished
        gameRecordMapper.insert(record);

        return uuid;
    }

    @Override
    public GameState getGameStateFromSessionOrDb(HttpSession session, String gameUuid) {
        Object o = session.getAttribute("game_" + gameUuid);
        if (o != null)
            return (GameState) o;
        // try load from DB
        QueryWrapper<GameRecordDO> qw = new QueryWrapper<>();
        qw.eq("game_uuid", gameUuid).orderByDesc("id").last("limit 1");
        GameRecordDO rec = gameRecordMapper.selectOne(qw);
        if (rec == null)
            return null;
        if ("ONGOING".equals(rec.getResult())) {
            try {
                return objectMapper.readValue(rec.getStateJson(), GameState.class);
            } catch (Exception e) {
                return null;
            }
        }
        // finished -> return state as well
        try {
            return objectMapper.readValue(rec.getStateJson(), GameState.class);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Map<String, Object> applyClick(HttpSession session, String gameUuid, int r, int c) {
        Object lock = locks.computeIfAbsent(gameUuid, k -> new Object());
        Map<String, Object> resp = new HashMap<>();
        synchronized (lock) {
            GameState gs = (GameState) session.getAttribute("game_" + gameUuid);
            if (gs == null) {
                resp.put("success", false);
                resp.put("message", "游戏未找到");
                return resp;
            }
            if (gs.isFinished()) {
                resp.put("success", false);
                resp.put("message", "游戏已结束");
                return resp;
            }
            // validate
            if (r < 0 || c < 0 || r >= gs.getRows() || c >= gs.getCols()) {
                resp.put("success", false);
                resp.put("message", "无效坐标");
                return resp;
            }
            Point click = new Point(r, c);
            if (gs.getCat() != null && gs.getCat().equals(click)) {
                resp.put("success", false);
                resp.put("message", "不能点猫所在格子");
                return resp;
            }
            if (gs.getBlocked().contains(click)) {
                resp.put("success", false);
                resp.put("message", "格子已被阻挡");
                return resp;
            }

            // apply block
            gs.getBlocked().add(click);
            gs.setClicks(gs.getClicks() + 1);
            gs.setLastUpdateTime(LocalDateTime.now());

            // BFS from cat to any boundary. If no path -> WIN. If path, move cat to next
            // step; if cat reaches boundary -> LOSE
            List<Point> path = bfsShortestPath(gs.getCat(), gs.getRows(), gs.getCols(), gs.getBlocked(),
                    gs.getLevelType());
            if (path == null) {
                // win
                gs.setFinished(true);
                gs.setResult("WIN");
                saveFinishedStateToDb(gs, session);
            } else {
                // path found: next position is path[1]
                if (path.size() >= 2) {
                    Point next = path.get(1);
                    gs.setCat(next);
                    // if at border -> lose
                    if (isAtBorder(next, gs.getRows(), gs.getCols(), gs.getLevelType())) {
                        gs.setFinished(true);
                        gs.setResult("LOSE");
                        saveFinishedStateToDb(gs, session);
                    }
                }
            }

            // update session
            session.setAttribute("game_" + gameUuid, gs);

            // prepare response
            Map<String, Object> data = new HashMap<>();
            data.put("cat", gs.getCat());
            data.put("blocked", gs.getBlocked());
            data.put("clicks", gs.getClicks());
            data.put("finished", gs.isFinished());
            data.put("result", gs.getResult());
            data.put("message", "");
            resp.put("success", true);
            resp.put("data", data);
            return resp;
        }
    }

    private void saveFinishedStateToDb(GameState gs, HttpSession session) {
        try {
            String json = objectMapper.writeValueAsString(gs);
            QueryWrapper<GameRecordDO> qw = new QueryWrapper<>();
            qw.eq("game_uuid", gs.getGameUuid()).orderByDesc("id").last("limit 1");
            GameRecordDO rec = gameRecordMapper.selectOne(qw);
            if (rec != null) {
                rec.setStateJson(json);
                rec.setResult(gs.getResult());
                rec.setLastUpdateTime(LocalDateTime.now());
                long duration = Duration.between(gs.getStartTime(), gs.getLastUpdateTime()).getSeconds();
                rec.setDurationSeconds((int) duration);
                rec.setClicks(gs.getClicks());
                gameRecordMapper.updateById(rec);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isAtBorder(Point p, int rows, int cols, String levelType) {
        // Rectangle keeps original rule
        if ("1".equals(levelType) || levelType == null) {
            return p.getR() == 0 || p.getR() == rows - 1 || p.getC() == 0 || p.getC() == cols - 1;
        }
        // For masked shapes (diamond, circle): border = inside cell with any 4-neighbor
        // outside mask
        int r = p.getR(), c = p.getC();
        if (!inBounds(r, c, rows, cols) || !isInsideShape(r, c, rows, cols, levelType))
            return false;
        int[][] del = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
        for (int[] d : del) {
            int nr = r + d[0], nc = c + d[1];
            boolean insideNeighbor = inBounds(nr, nc, rows, cols) && isInsideShape(nr, nc, rows, cols, levelType);
            if (!insideNeighbor)
                return true;
        }
        return false;
    }

    /**
     * BFS shortest path from start to any boundary cell.
     * Returns list of points from start to boundary (inclusive). If no path,
     * returns null.
     * Neighbor rule: 4-directional (up, down, left, right).
     */
    private List<Point> bfsShortestPath(Point start, int rows, int cols, List<Point> blocked, String levelType) {
        Set<String> blockedSet = new HashSet<>();
        for (Point b : blocked)
            blockedSet.add(b.getR() + ":" + b.getC());

        Queue<Point> q = new ArrayDeque<>();
        Map<String, Point> parent = new HashMap<>();
        q.add(start);
        parent.put(start.getR() + ":" + start.getC(), null);

        while (!q.isEmpty()) {
            Point cur = q.poll();
            if (isAtBorder(cur, rows, cols, levelType)) {
                // build path
                List<Point> path = new ArrayList<>();
                Point p = cur;
                while (p != null) {
                    path.add(0, p);
                    String key = p.getR() + ":" + p.getC();
                    p = parent.get(key);
                }
                return path;
            }
            for (Point nb : neighbors(cur, rows, cols, levelType)) {
                String k = nb.getR() + ":" + nb.getC();
                if (blockedSet.contains(k))
                    continue;
                if (parent.containsKey(k))
                    continue;
                parent.put(k, cur);
                q.add(nb);
            }
        }
        return null; // no path
    }

    private boolean isInsideShape(int r, int c, int rows, int cols, String levelType) {
        if ("2".equals(levelType)) { // diamond shape
            int cr = rows / 2, cc = cols / 2;
            int radius = Math.min(rows, cols) / 2;
            int dist = Math.abs(r - cr) + Math.abs(c - cc);
            return dist <= radius;
        } else if ("3".equals(levelType)) { // circle shape
            int cr = rows / 2, cc = cols / 2;
            int radius = Math.min(rows, cols) / 2;
            int dr = r - cr, dc = c - cc;
            int dist2 = dr * dr + dc * dc;
            return dist2 <= radius * radius;
        }
        // default: rectangle includes all cells within bounds
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }

    private boolean inBounds(int r, int c, int rows, int cols) {
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }

    private List<Point> neighbors(Point p, int rows, int cols, String levelType) {
        List<Point> res = new ArrayList<>();
        int r = p.getR(), c = p.getC();
        int[][] del = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } }; // up, down, left, right
        for (int[] d : del) {
            int nr = r + d[0], nc = c + d[1];
            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && isInsideShape(nr, nc, rows, cols, levelType))
                res.add(new Point(nr, nc));
        }
        return res;
    }
}
