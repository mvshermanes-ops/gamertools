const gpuDatabase = [
  // 1080p Ultra / 1440p Ultra / 4K Ultra benchmark averages from current published GPU hierarchy data.
  {name:"GeForce RTX 5090",aliases:["RTX 5090"],fps1080:203.8,fps1440:167.3,fps4k:110.8},
  {name:"GeForce RTX 4090",aliases:["RTX 4090"],fps1080:183.6,fps1440:143.4,fps4k:89.1},
  {name:"GeForce RTX 5080",aliases:["RTX 5080"],fps1080:166.9,fps1440:128.3,fps4k:77.4},
  {name:"Radeon RX 7900 XTX",aliases:["RX 7900 XTX"],fps1080:161.5,fps1440:122.3,fps4k:70.6},
  {name:"GeForce RTX 4080 Super",aliases:["RTX 4080 Super"],fps1080:158.9,fps1440:118.6,fps4k:69.4},
  {name:"GeForce RTX 4070 Ti Super",aliases:["RTX 4070 Ti Super"],fps1080:141.2,fps1440:104.0,fps4k:58.5},
  {name:"Radeon RX 7900 XT",aliases:["RX 7900 XT"],fps1080:145.4,fps1440:108.0,fps4k:59.8},
  {name:"GeForce RTX 4070 Super",aliases:["RTX 4070 Super"],fps1080:134.2,fps1440:109.8,fps4k:66.1},
  {name:"Radeon RX 9070 XT",aliases:["RX 9070 XT"],fps1080:156.6,fps1440:116.5,fps4k:65.8},
  {name:"GeForce RTX 5070 Ti",aliases:["RTX 5070 Ti"],fps1080:155.4,fps1440:116.8,fps4k:68.6},
  {name:"GeForce RTX 3090 Ti",aliases:["RTX 3090 Ti"],fps1080:131.7,fps1440:99.9,fps4k:59.3},
  {name:"GeForce RTX 3070",aliases:["RTX 3070"],fps1080:87.2,fps1440:58.2,fps4k:26.2},
  {name:"Radeon RX 7800 XT",aliases:["RX 7800 XT"],fps1080:77.5,fps1440:57.1,fps4k:36.3},
  {name:"GeForce RTX 4060 Ti 16GB",aliases:["RTX 4060 Ti 16GB"],fps1080:89.3,fps1440:60.5,fps4k:31.3},
  {name:"GeForce RTX 4060 Ti",aliases:["RTX 4060 Ti 8GB","RTX 4060 Ti"],fps1080:88.0,fps1440:58.9,fps4k:23.9},
  {name:"Radeon RX 7700 XT",aliases:["RX 7700 XT"],fps1080:68.4,fps1440:49.7,fps4k:31.8},
  {name:"GeForce RTX 4060",aliases:["RTX 4060"],fps1080:71.5,fps1440:47.6,fps4k:17.4},
  {name:"Radeon RX 7600",aliases:["RX 7600"],fps1080:69.9,fps1440:45.4,fps4k:18.4},
  {name:"GeForce RTX 3060 Ti",aliases:["RTX 3060 Ti"],fps1080:74.2,fps1440:51.0,fps4k:19.4},
  {name:"GeForce RTX 3060 12GB",aliases:["RTX 3060","RTX 3060 12GB"],fps1080:61.5,fps1440:41.9,fps4k:22.2},
  {name:"Radeon RX 6650 XT",aliases:["RX 6650 XT"],fps1080:64.3,fps1440:38.0,fps4k:19.0},
  {name:"Radeon RX 6600 XT",aliases:["RX 6600 XT"],fps1080:62.7,fps1440:40.7,fps4k:17.3},
  {name:"Radeon RX 6600",aliases:["RX 6600"],fps1080:51.9,fps1440:24.8,fps4k:14.5},
  {name:"GeForce RTX 3050",aliases:["RTX 3050"],fps1080:44.6,fps1440:29.8,fps4k:12.6},
  {name:"Intel Arc B580",aliases:["Arc B580","B580"],fps1080:71.5,fps1440:50.7,fps4k:27.6},
  {name:"Intel Arc A770 8GB",aliases:["Arc A770","A770"],fps1080:54.2,fps1440:24.9,fps4k:14.0},
  {name:"GeForce GTX 1660 Super",aliases:["GTX 1660 Super","1660 Super"],fps1080:46.8,fps1440:33.3,fps4k:15.0},
  {name:"GeForce GTX 1080",aliases:["GTX 1080"],fps1080:53.0,fps1440:39.4,fps4k:20.0},
  {name:"GeForce GTX 1070 Ti",aliases:["GTX 1070 Ti"],fps1080:51.1,fps1440:37.9,fps4k:19.0},
  {name:"Radeon RX 580 8GB",aliases:["RX 580","RX 580 8GB"],fps1080:35.3,fps1440:26.0,fps4k:14.0},
  {name:"GeForce GTX 1650 Super",aliases:["GTX 1650 Super"],fps1080:33.9,fps1440:21.2,fps4k:11.0},
  {name:"Radeon RX 5500 XT 4GB",aliases:["RX 5500 XT","RX 5500 XT 4GB"],fps1080:33.3,fps1440:23.0,fps4k:12.0},
  {name:"GeForce GTX 1660 Ti",aliases:["GTX 1660 Ti","1660 Ti"],fps1080:49.5,fps1440:36.0,fps4k:17.0},
  {name:"GeForce GTX 1660",aliases:["GTX 1660"],fps1080:44.8,fps1440:32.0,fps4k:15.0},
  {name:"GeForce GTX 1650",aliases:["GTX 1650"],fps1080:29.7,fps1440:19.0,fps4k:9.5},
  {name:"GeForce GTX 1060 6GB",aliases:["GTX 1060 6GB","GTX 1060"],fps1080:37.5,fps1440:26.0,fps4k:13.0},
  {name:"GeForce GTX 1060 3GB",aliases:["GTX 1060 3GB"],fps1080:34.0,fps1440:23.5,fps4k:11.5},
  {name:"GeForce GTX 1050 Ti",aliases:["GTX 1050 Ti"],fps1080:25.0,fps1440:16.0,fps4k:8.0},
  {name:"GeForce GTX 1050",aliases:["GTX 1050"],fps1080:20.0,fps1440:13.0,fps4k:6.5},
  {name:"GeForce GTX 980 Ti",aliases:["GTX 980 Ti"],fps1080:46.0,fps1440:32.0,fps4k:17.0},
  {name:"GeForce GTX 980",aliases:["GTX 980"],fps1080:39.0,fps1440:27.0,fps4k:14.0},
  {name:"GeForce GTX 970",aliases:["GTX 970"],fps1080:34.0,fps1440:24.0,fps4k:12.0},
  {name:"GeForce GTX 960",aliases:["GTX 960"],fps1080:24.0,fps1440:16.0,fps4k:8.0},
  {name:"GeForce GTX 950",aliases:["GTX 950"],fps1080:19.0,fps1440:12.0,fps4k:6.0}
];

const cpuDatabase = [
{name:"Ryzen 9 9950X3D",score:100},{name:"Ryzen 7 9800X3D",score:96},{name:"Core Ultra 9 285K",score:92},{name:"Ryzen 9 9950X",score:91},{name:"Core i9-14900K",score:90},{name:"Ryzen 7 9700X",score:86},{name:"Core Ultra 7 265K",score:85},{name:"Ryzen 7 7800X3D",score:84},{name:"Core i7-14700K",score:83},{name:"Ryzen 9 7900X",score:81},{name:"Ryzen 7 7700X",score:78},{name:"Core i5-14600K",score:77},{name:"Ryzen 5 7600X",score:75},{name:"Core i5-14400F",score:70},{name:"Ryzen 7 5800X3D",score:69},{name:"Ryzen 9 5900X",score:67},{name:"Core i7-12700K",score:66},{name:"Ryzen 7 5700X3D",score:65},{name:"Ryzen 7 5800X",score:62},{name:"Core i5-12600K",score:61},{name:"Ryzen 5 5600X",score:58},{name:"Ryzen 5 5600",score:56},{name:"Core i5-12400F",score:55},{name:"Ryzen 5 5500",score:50},{name:"Ryzen 5 3600",score:43},{name:"Core i5-10400F",score:42},{name:"Ryzen 5 2600",score:34},{name:"Core i7-8700K",score:33},{name:"Core i5-8400",score:28},{name:"Core i7-7700K",score:25},{name:"Ryzen 3 3100",score:23},{name:"Core i3-10100F",score:22},{name:"Core i5-6500",score:18}
];

const tools = [
  {id:"fps",cat:"gaming",icon:"🎮",name:"FPS Calculator",desc:"Estimate FPS using a specific graphics card and published benchmark performance.",fields:[
    ["gpu","Graphics card","select",gpuDatabase.map(g=>g.name)],
    ["resolution","Resolution","select",["1080p","1440p","4K"]],
    ["settings","Graphics settings","select",["Low","Medium","High","Ultra"]]
  ]},
  {id:"edpi",cat:"gaming",icon:"🎯",name:"eDPI Calculator",desc:"Calculate effective DPI for competitive FPS games.",fields:[["dpi","Mouse DPI","number"],["sens","In-game sensitivity","number"]]},
  {id:"sens",cat:"gaming",icon:"🖱️",name:"Sensitivity Converter",desc:"Convert sensitivity between common DPI values.",fields:[["sens","Current sensitivity","number"],["from","Current DPI","number"],["to","Target DPI","number"]]},
  {id:"random",cat:"gaming",icon:"🎲",name:"Random Game Picker",desc:"Can't decide what to play? Let GameTools choose.",fields:[["games","Games (comma separated)","text"]]},
  {id:"bottleneck",cat:"pc",icon:"⚙️",name:"Bottleneck Calculator",desc:"Search for your exact CPU and GPU and estimate which component is more likely to limit gaming performance.",fields:[["cpu","Search / choose your CPU","searchselect",cpuDatabase.map(c=>c.name)],["gpu","Search / choose your GPU","searchselect",gpuDatabase.map(g=>g.name)],["resolution","Gaming resolution","select",["1080p","1440p","4K"]]]},
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
  if(type==="select") return `<div class="field"><label for="${id}">${label}</label><select id="${id}">${opts.map((o,i)=>`<option value="${o}"${i===0?" selected":""}>${o}</option>`).join("")}</select></div>`;\n  if(type==="searchselect") return `<div class="field"><label for="${id}">${label}</label><input id="${id}" type="search" list="${id}Options" placeholder="Type to search..." autocomplete="off"><datalist id="${id}Options">${opts.map(o=>`<option value="${o}">`).join("")}</datalist></div>`;
  return `<div class="field"><label for="${id}">${label}</label><input id="${id}" type="${type==="number"?"number":"text"}" step="any"></div>`;
}
function openTool(id){
  const t=tools.find(x=>x.id===id); if(!t)return;
  content.innerHTML=`<p class="eyebrow">${t.cat.toUpperCase()}</p><h2>${t.icon} ${t.name}</h2><p>${t.desc}</p><div class="form-grid">${t.fields.map(fieldHTML).join("")}</div><button class="action" id="calculate">Calculate</button><div id="result" class="result" hidden></div>`;
  panel.hidden=false; panel.scrollIntoView({behavior:"smooth",block:"center"});
  document.querySelector("#calculate").onclick=()=>{calculate(t); if(id==="fps"){const detect=document.querySelector("#detectGpu"); if(detect) detect.onclick=detectGPU;}};
}
function calculate(t){
  const v=id=>document.querySelector("#"+id)?.value;
  let text="";
  if(t.id==="edpi") text=`<div class="big-result">${((+v("dpi")||0)*(+v("sens")||0)).toFixed(2)} eDPI</div><p>eDPI = DPI × in-game sensitivity.</p>`;
  if(t.id==="sens") text=`<div class="big-result">${(((+v("sens")||0)*(+v("to")||0))/(+v("from")||1)).toFixed(4)}</div><p>Estimated sensitivity at your target DPI.</p>`;
  if(t.id==="percentage") text=`<div class="big-result">${(((+v("value")||0)*(+v("percent")||0))/100).toFixed(2)}</div>`;
  if(t.id==="psu"){const watts=(+v("gpu")||0)+(+v("cpu")||0)+(+v("extra")||0); const recommended=Math.ceil((watts*1.35)/50)*50; text=`<div class="big-result">${recommended}W</div><p>Estimated recommendation with headroom. Actual PSU needs vary by hardware.</p>`;}
  if(t.id==="storage"){const n=(+v("drive")||0)/(+v("game")||1); text=`<div class="big-result">About ${Math.floor(n)} games</div><p>Based on the average game size you entered.</p>`;}
  if(t.id==="bottleneck"){const cpu=cpuDatabase.find(x=>x.name.toLowerCase()===v("cpu").toLowerCase()); const gpu=gpuDatabase.find(x=>x.name.toLowerCase()===v("gpu").toLowerCase()); const res=v("resolution"); if(!cpu||!gpu){text=`<p>Please search and select a CPU and GPU from the suggestions.</p>`;}else{const gpuScore=gpu.fps1080; const cpuFactor=res==="1080p"?1.18:res==="1440p"?0.93:0.72; const cpuEquivalent=cpu.score*cpuFactor; const ratio=cpuEquivalent/gpuScore; let verdict,detail; if(ratio<0.72){verdict="CPU-limited";detail=`Your CPU is the more likely limiting component at ${res}. A faster CPU could improve performance, especially in high-FPS or CPU-heavy games.`;}else if(ratio>1.35){verdict="GPU-limited";detail=`Your GPU is the more likely limiting component at ${res}. A faster GPU would generally have more impact on graphics-bound workloads.`;}else{verdict="Fairly balanced";detail=`The CPU and GPU are in a relatively balanced range for ${res}, although the exact result varies by game, settings and frame-rate target.`;} text=`<div class="big-result">${verdict}</div><p><strong>${cpu.name}</strong> + <strong>${gpu.name}</strong> at <strong>${res}</strong>.</p><p>${detail}</p><p class="muted-note">This is an estimate, not a universal bottleneck percentage. Different games can shift the limiting component substantially.</p>`;}}
  if(t.id==="fps"){
    const gpu=gpuDatabase.find(g=>g.name===v("gpu"));
    const res=v("resolution"),set=v("settings");
    if(gpu){
      let base=res==="1440p"?gpu.fps1440:res==="4K"?gpu.fps4k:gpu.fps1080;
      const multiplier={Low:1.22,Medium:1.10,High:0.92,Ultra:1}[set];
      const fps=Math.max(1,base*multiplier);
      text=`<div class="big-result">~${Math.round(fps)} FPS</div><p><strong>${gpu.name}</strong> benchmark-based estimate at ${res} ${set}. The baseline comes from a published multi-game GPU benchmark hierarchy; actual FPS varies by game, CPU, drivers and settings.</p><button class="action" id="detectGpu" type="button">🔍 Detect my GPU</button><div id="gpuDetectResult" style="margin-top:12px"></div>`;
    }
  }
  if(t.id==="random"){const list=v("games").split(",").map(x=>x.trim()).filter(Boolean); text=list.length?`<div class="big-result">${list[Math.floor(Math.random()*list.length)]}</div>`:"<p>Enter at least two games separated by commas.</p>";}
  if(t.id==="convert"){const n=+v("value")||0; const u=v("unit"); const m={"km → miles":n*.621371,"miles → km":n*1.609344,"GB → MB":n*1024,"MB → GB":n/1024,"kg → lb":n*2.20462,"lb → kg":n*.453592}; text=`<div class="big-result">${(m[u]??n).toFixed(4)}</div>`;}
  if(t.id==="qr"){const textValue=v("text"); if(!textValue){text="<p>Enter text or a URL first.</p>"}else{text=`<div class="big-result">QR ready</div><p>For the first release, QR generation will be added with a lightweight browser library.</p>`}}
  const r=document.querySelector("#result"); r.innerHTML=text;r.hidden=false;
}
search.addEventListener("input",e=>{const q=e.target.value.toLowerCase().trim();render(q?tools.filter(t=>(t.name+" "+t.desc).toLowerCase().includes(q)):tools)});
document.querySelector("#closeTool").onclick=()=>{panel.hidden=true};
render();


async function detectGPU(){
  let renderer="";
  try{
    if(navigator.gpu){
      const adapter=await navigator.gpu.requestAdapter();
      if(adapter?.info) renderer=[adapter.info.vendor,adapter.info.architecture,adapter.info.device,adapter.info.description].filter(Boolean).join(" ");
    }
  }catch(e){}
  if(!renderer){
    try{
      const canvas=document.createElement("canvas");
      const gl=canvas.getContext("webgl")||canvas.getContext("experimental-webgl");
      const debug=gl?.getExtension("WEBGL_debug_renderer_info");
      if(debug) renderer=gl.getParameter(debug.UNMASKED_RENDERER_WEBGL)||"";
      else renderer=gl?.getParameter(gl.RENDERER)||"";
    }catch(e){}
  }
  const normalized=renderer.toLowerCase();
  const match=gpuDatabase.find(g=>g.aliases.some(a=>normalized.includes(a.toLowerCase())));
  const out=document.querySelector("#gpuDetectResult");
  if(!out)return;
  if(match){
    const select=document.querySelector("#gpu");
    if(select) select.value=match.name;
    out.innerHTML=`<p>Detected: <strong>${match.name}</strong>. The calculator has selected the matching benchmark profile.</p>`;
  }else{
    out.innerHTML=`<p>Detected renderer: <strong>${renderer||"Unavailable"}</strong>. We couldn't safely match it to our GPU database. You can still select your GPU manually.</p>`;
  }
}
