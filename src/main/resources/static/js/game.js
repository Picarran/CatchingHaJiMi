// Minimal frontend to render INITIAL_MAP and handle clicks
document.addEventListener('DOMContentLoaded', function(){
  try{
    var map = (typeof INITIAL_MAP === 'string')? JSON.parse(INITIAL_MAP): INITIAL_MAP;
  }catch(e){
    console.error('Invalid INITIAL_MAP', e);
    var map = null;
  }
  if(!map){ document.getElementById('game-area').innerText = '地图数据不可用'; return; }

  var rows = map.rows, cols = map.cols;
  var area = document.getElementById('game-area');
  var grid = document.createElement('div'); grid.className='grid';
  grid.style.width = (cols*40)+'px';

  function cellId(r,c){return 'cell-'+r+'-'+c}

  for(var r=0;r<rows;r++){
    for(var c=0;c<cols;c++){
      var cell = document.createElement('div'); cell.className='cell'; cell.id = cellId(r,c);
      var dot = document.createElement('div'); dot.className='dot';
      cell.appendChild(dot);
      (function(rr,cc){
        cell.addEventListener('click', function(){ onCellClick(rr,cc); });
      })(r,c);
      grid.appendChild(cell);
    }
  }
  area.appendChild(grid);

  function refresh(map){
    // clear all
    var blocked = map.blocked || [];
    document.querySelectorAll('.dot').forEach(function(d){ d.className='dot'; });
    blocked.forEach(function(p){ var el = document.getElementById(cellId(p.r,p.c)); if(el) el.firstChild.classList.add('blocked'); });
    if(map.cat){ var catEl = document.getElementById(cellId(map.cat.r,map.cat.c)); if(catEl) catEl.firstChild.classList.add('cat'); }
  }

  function onCellClick(r,c){
    fetch('/api/game/'+GAME_UUID+'/click',{
      method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({r:r,c:c})
    }).then(res=>res.json()).then(handleResp).catch(err=>console.error(err));
  }

  function handleResp(json){
    if(!json) return;
    if(!json.success){ alert(json.message || '点击失败'); return; }
    var data = json.data;
    refresh({cat:data.cat, blocked:data.blocked});
    if(data.finished){
      var text = '结果: '+data.result+'，点击数: '+data.clicks+'。';
      document.getElementById('modal-text').innerText = text;
      document.getElementById('modal').style.display='flex';
    }
  }

  // initial render
  refresh(map);

  document.getElementById('to-dashboard').addEventListener('click', function(){ location.href='/dashboard'; });
  document.getElementById('restart').addEventListener('click', function(){ location.href='/game/start'; });
});
