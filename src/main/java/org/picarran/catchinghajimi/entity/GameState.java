package org.picarran.catchinghajimi.entity;

import java.time.LocalDateTime;
import java.util.List;

public class GameState {
    private String gameUuid;
    private int rows;
    private int cols;
    private Point cat;
    private List<Point> blocked;
    private int clicks;
    private LocalDateTime startTime;
    private LocalDateTime lastUpdateTime;
    private boolean finished;
    private String result;
    private Integer durationSeconds;
    // level type: "1" or "rect" for矩形；"2"或"diamond"为菱形
    private String levelType;

    public String getGameUuid() {
        return gameUuid;
    }

    public void setGameUuid(String gameUuid) {
        this.gameUuid = gameUuid;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public Point getCat() {
        return cat;
    }

    public void setCat(Point cat) {
        this.cat = cat;
    }

    public List<Point> getBlocked() {
        return blocked;
    }

    public void setBlocked(List<Point> blocked) {
        this.blocked = blocked;
    }

    public int getClicks() {
        return clicks;
    }

    public void setClicks(int clicks) {
        this.clicks = clicks;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(LocalDateTime lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public String getLevelType() {
        return levelType;
    }

    public void setLevelType(String levelType) {
        this.levelType = levelType;
    }
}
