// Minimal frontend to render INITIAL_MAP and handle clicks
document.addEventListener("DOMContentLoaded", function () {
  try {
    var map =
      typeof INITIAL_MAP === "string" ? JSON.parse(INITIAL_MAP) : INITIAL_MAP;
  } catch (e) {
    console.error("Invalid INITIAL_MAP", e);
    var map = null;
  }
  if (!map) {
    document.getElementById("game-area").innerText = "地图数据不可用";
    return;
  }

  var rows = map.rows,
    cols = map.cols;
  var level = (map.levelType || "1").toString(); // '1' (rect) or '2' (diamond)
  // expose rows/cols to CSS for responsive board sizing
  // Prefer scoping variables to the grid element to avoid any cascade issues
  var area = document.getElementById("game-area");
  var grid = document.createElement("div");
  grid.className = "grid";
  try {
    grid.style.setProperty("--rows", rows);
    grid.style.setProperty("--cols", cols);
  } catch (e) {}
  // width is controlled by CSS variables now (see style.css)

  function cellId(r, c) {
    return "cell-" + r + "-" + c;
  }

  function isInsideShape(r, c) {
    if (level === "2") {
      var cr = Math.floor(rows / 2),
        cc = Math.floor(cols / 2);
      var radius = Math.floor(Math.min(rows, cols) / 2);
      return Math.abs(r - cr) + Math.abs(c - cc) <= radius;
    } else if (level === "3") {
      var cr2 = Math.floor(rows / 2),
        cc2 = Math.floor(cols / 2);
      var radius2 = Math.floor(Math.min(rows, cols) / 2);
      var dr = r - cr2,
        dc = c - cc2;
      return dr * dr + dc * dc <= radius2 * radius2;
    }
    return true; // rect includes all
  }

  for (var r = 0; r < rows; r++) {
    for (var c = 0; c < cols; c++) {
      if (!isInsideShape(r, c)) {
        var spacer = document.createElement("div");
        spacer.className = "spacer";
        grid.appendChild(spacer);
        continue;
      }
      var cell = document.createElement("div");
      cell.className = "cell";
      cell.id = cellId(r, c);
      var dot = document.createElement("div");
      dot.className = "dot";
      cell.appendChild(dot);
      (function (rr, cc) {
        cell.addEventListener("click", function () {
          onCellClick(rr, cc);
        });
      })(r, c);
      grid.appendChild(cell);
    }
  }
  area.appendChild(grid);

  function refresh(map) {
    // clear all
    var blocked = map.blocked || [];
    document.querySelectorAll(".dot").forEach(function (d) {
      d.className = "dot";
    });
    blocked.forEach(function (p) {
      var el = document.getElementById(cellId(p.r, p.c));
      if (el) el.firstChild.classList.add("blocked");
    });
    if (map.cat) {
      var catEl = document.getElementById(cellId(map.cat.r, map.cat.c));
      if (catEl) catEl.firstChild.classList.add("cat");
    }
  }

  function onCellClick(r, c) {
    fetch("/api/game/" + GAME_UUID + "/click", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ r: r, c: c }),
    })
      .then((res) => res.json())
      .then(handleResp)
      .catch((err) => console.error(err));
  }

  function handleResp(json) {
    if (!json) return;
    if (!json.success) {
      alert(json.message || "点击失败");
      return;
    }
    var data = json.data;
    refresh({ cat: data.cat, blocked: data.blocked });
    if (data.finished) {
      var text = "结果: " + data.result + "，点击数: " + data.clicks + "。";
      document.getElementById("modal-text").innerText = text;
      document.getElementById("modal").style.display = "flex";
    }
  }

  // initial render
  refresh(map);

  document
    .getElementById("to-dashboard")
    .addEventListener("click", function () {
      location.href = "/dashboard";
    });
  document.getElementById("restart").addEventListener("click", function () {
    location.href = "/game/start?level=" + encodeURIComponent(level);
  });

  // in-game map switching removed; selection happens on dashboard
});
