const tools = [
  {id:"fps",cat:"gaming",icon:"🎮",name:"FPS Calculator",desc:"Estimate gaming FPS from your hardware and target resolution.",fields:[["gpu","GPU class","select",["Entry-level","Mid-range","High-end"]],["resolution","Resolution","select",["1080p","1440p","4K"]],["settings","Graphics settings","select",["Low","Medium","High","Ultra"]]]},
  {id:"edpi",cat:"gaming",icon:"🎯",name:"eDPI Calculator",desc:"Calculate effective DPI for competitive FPS games.",fields:[["dpi","Mouse DPI","number"],["sens","In-game sensitivity","number"]]},
  {id:"sens",cat:"gaming",icon:"🖱️",name:"Sensitivity Converter",desc:"Convert sensitivity between common DPI values.",fields:[["sens","Current sensitivity","number"],["from","Current DPI","number"],["to","Target DPI","number"]]},
  {id:"random",cat:"gaming",icon:"🎲",name:"Random Game Picker",desc:"Can't decide what to play? Let GameTools choose.",fields:[["games","Games (comma separated)","text"]]},
  {id:"bottleneck",cat:"pc",icon:"⚙️",name:"Bottleneck Calculator",desc:"Get a simple CPU/GPU balance estimate.",fields:[["cpu","CPU tier","select",["Entry-level","Mid-range","High-end"]],["gpu","GPU tier","select",["Entry-level","Mid-range","High-end"]]]},
  {id:"psu",cat:"pc",icon:"🔌",name:"PSU Wattage Calculator",desc:"Estimate a sensible power-supply capacity.",fields:[["gpu","GPU power (watts)","number"],["cpu","CPU power (watts)","number"],["extra","Other system power (watts)","number"]]},
  {id:"storage",cat:"pc",icon:"💾",name:"Storage Calculator",desc:"Estimate how many games fit on your drive.",fields:[["drive","Drive size (GB)","number"],["game","Average game size (GB)","number"]]},
  {id:"percentage",cat:"general",icon:"％",name:"Percentage Calculator",desc:"Calculate percentages quickly.",fields:[["value","Number","number"],["percent","Percentage","number"]]},
  {id:"convert",cat:"general",icon:"↔️",name:"Unit Converter",desc:"Convert common units without leaving the page.",fields:[["value","Value","number"],["unit","Convert","select",["km → miles","miles → km","GB → MB","MB → GB","kg → lb","lb → kg"]]]},
  {id:"qr",cat:"general",icon:"▦",name:"QR Code Generator",desc:"Create a QR code from text or a URL.",fields:[["text","Text or URL","text"]]}
];

const grids={gaming:document.querySelector("#gamingGrid"),pc:document.querySelector("#pcGrid"),general:document.querySelector("#generalGrid")};
const panel=document.querySelector("#toolPanel"), content=document.querySelector("#toolContent"), search=document.querySelector("#toolSearch");
document.querySelector("#year").textContent=new Date().getFullYear();
document.querySelector("#toolCount").textContent=tools.length;

function card(t){return `<article class="tool-card" data-id="${t.id}"><div class="tool-icon">${t.icon}</div><h3>${t.name}</h3><p>${t.desc}</p></article>`}
function render(list=tools){
  Object.values(grids).forEach(g=>g.innerHTML="");
  list.forEach(t=>grids[t.cat]?.insertAdjacentHTML("beforeend",card(t)));
  document.querySelectorAll(".tool-card").forEach(c=>c.onclick=()=>openTool(c.dataset.id));
  document.querySelector("#noResults").hidden=list.length!==0;
  document.querySelectorAll(".section").forEach(s=>s.style.display=list.length===0?"none":"block");
}
function fieldHTML(f){
  const [id,label,type,opts]=f;
  if(type==="select") return `<div class="field"><label for="${id}">${label}</label><select id="${id}">${opts.map(o=>`<option>${o}</option>`).join("")}</select></div>`;
  return `<div class="field"><label for="${id}">${label}</label><input id="${id}" type="${type==="number"?"number":"text"}" step="any"></div>`;
}
function openTool(id){
  const t=tools.find(x=>x.id===id); if(!t)return;
  content.innerHTML=`<p class="eyebrow">${t.cat.toUpperCase()}</p><h2>${t.icon} ${t.name}</h2><p>${t.desc}</p><div class="form-grid">${t.fields.map(fieldHTML).join("")}</div><button class="action" id="calculate">Calculate</button><div id="result" class="result" hidden></div>`;
  panel.hidden=false; panel.scrollIntoView({behavior:"smooth",block:"center"});
  document.querySelector("#calculate").onclick=()=>calculate(t);
}
function calculate(t){
  const v=id=>document.querySelector("#"+id)?.value;
  let text="";
  if(t.id==="edpi") text=`<div class="big-result">${((+v("dpi")||0)*(+v("sens")||0)).toFixed(2)} eDPI</div><p>eDPI = DPI × in-game sensitivity.</p>`;
  if(t.id==="sens") text=`<div class="big-result">${(((+v("sens")||0)*(+v("to")||0))/(+v("from")||1)).toFixed(4)}</div><p>Estimated sensitivity at your target DPI.</p>`;
  if(t.id==="percentage") text=`<div class="big-result">${(((+v("value")||0)*(+v("percent")||0))/100).toFixed(2)}</div>`;
  if(t.id==="psu"){const watts=(+v("gpu")||0)+(+v("cpu")||0)+(+v("extra")||0); const recommended=Math.ceil((watts*1.35)/50)*50; text=`<div class="big-result">${recommended}W</div><p>Estimated recommendation with headroom. Actual PSU needs vary by hardware.</p>`;}
  if(t.id==="storage"){const n=(+v("drive")||0)/(+v("game")||1); text=`<div class="big-result">About ${Math.floor(n)} games</div><p>Based on the average game size you entered.</p>`;}
  if(t.id==="bottleneck"){const a=v("cpu"),b=v("gpu"); text=`<div class="big-result">${a===b?"Balanced":"Potential imbalance"}</div><p>This is a simple tier comparison, not a benchmark.</p>`;}
  if(t.id==="fps"){const gpu=v("gpu"),res=v("resolution"),set=v("settings"); let base={ "Entry-level":70,"Mid-range":120,"High-end":190}[gpu]; if(res==="1440p")base*=.7;if(res==="4K")base*=.45; if(set==="Low")base*=1.25;if(set==="High")base*=.8;if(set==="Ultra")base*=.65;text=`<div class="big-result">~${Math.round(base)} FPS</div><p>Rough estimate for planning only. Real FPS depends on the specific game and hardware.</p>`;}
  if(t.id==="random"){const list=v("games").split(",").map(x=>x.trim()).filter(Boolean); text=list.length?`<div class="big-result">${list[Math.floor(Math.random()*list.length)]}</div>`:"<p>Enter at least two games separated by commas.</p>";}
  if(t.id==="convert"){const n=+v("value")||0; const u=v("unit"); const m={"km → miles":n*.621371,"miles → km":n*1.609344,"GB → MB":n*1024,"MB → GB":n/1024,"kg → lb":n*2.20462,"lb → kg":n*.453592}; text=`<div class="big-result">${(m[u]??n).toFixed(4)}</div>`;}
  if(t.id==="qr"){const textValue=v("text"); if(!textValue){text="<p>Enter text or a URL first.</p>"}else{text=`<div class="big-result">QR ready</div><p>For the first release, QR generation will be added with a lightweight browser library.</p>`}}
  const r=document.querySelector("#result"); r.innerHTML=text;r.hidden=false;
}
search.addEventListener("input",e=>{const q=e.target.value.toLowerCase().trim();render(q?tools.filter(t=>(t.name+" "+t.desc).toLowerCase().includes(q)):tools)});
document.querySelector("#closeTool").onclick=()=>{panel.hidden=true};
render();
