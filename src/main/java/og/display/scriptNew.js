class BackEndEventRetriever {
    constructor(newUrl){
        this.url = newUrl;
    }
}

// fetch(
//   "http://localhost:8081/api////og.GraphService/get/randomGraph?what=content"
// ).then((data) => console.log(data.text()));

const source = new EventSource(
  "http://localhost:8081/api////og.GraphService/get2/randomGraph?what=content"
);

source.addEventListener("message", (event) => {
  const data = event.data;
  var lines = data.split(/\r?\n/);
  var headerraw = lines.shift();
  var temp = lines.join("");
  var jsonToProcess = JSON.parse(temp);

  var jsonFormatted = parseJaseto(jsonToProcess);
  
  console.log("json formated = ", jsonFormatted);
});

function parseJaseto(jsonObject){
    let entireObject = [];
    let vertices = [];
    let arcs = [];
    let edges = [];

    // Iterate over objects of jaseto entity 
    for (let element of Object.entries(jsonObject)) {

        // If one of the objects is of type arc, we retrieve arcs contained in it
        if(element[0] === 'arcs'){
            for (let arc of element[1].elements) {
                //retrieve every properties of every arc that has properties different from null
                if (arc.properties !== null && arc.properties !== undefined) {
                    let temp = {
                        id: arc.id,
                        from: arc.from,
                        to: arc.to,
                        properties: arc.properties
                    };
                    arcs.push(temp);
                }
            }
        }

//         edges{
// data: 				"#class": "og.GraphService$EdgeInfo",
// data: 				"ends": {
// data: 					"#class": "it.unimi.dsi.fastutil.longs.LongOpenHashSet",
// data: 					"elements": [
// data: 						-7304607186208734480,
// data: 						7873111096608120161
// data: 					],
// data: 					"size": 2
// data: 				},
// data: 				"id": -7679892859513199228,
// data: 				"properties": {
// data: 					"#class": "java.util.HashMap",
// data: 					"color": "#50777a",
// data: 					"style": "dashed",
// data: 					"width": 8
// data: 				}
// data: 			},

        if (element[0] === "edges") {
          for (let edge of element[1].elements) {
            //retrieve every properties of every arc that has properties different from null
            if (edge.properties !== null && edge.properties !== undefined) {
              let temp = {
                id: edge.id,
                ends: edge.ends,
                properties: edge.properties,
              };
              edges.push(temp);
            }
          }
        }

//         {
// data: 				"#class": "og.GraphService$VertexInfo",
// data: 				"id": 8325795240509049676,
// data: 				"properties": {
// data: 					"#class": "java.util.HashMap",
// data: 					"bar": 61,
// data: 					"foo": 0.41617622547870226,
// data: 					"location": "0.18050006126421914,0.4322806124664764,0.01955134938794789",
// data: 					"shape": "triangle",
// data: 					"width": 9
// data: 				}
// data: 			},

        if (element[0] === "vertices") {
          for (let vertex of element[1].elements) {
            //retrieve every properties of every arc that has properties different from null
            if (vertex.properties !== null && vertex.properties !== undefined) {
                let locations = vertex.location.split(",");
                let x = locations[0];
                let y = locations[1];
                let z = locations[2];
                
              let temp = {
                id: vertex.id,
                x: x,
                y: y,
                z: z,
                properties: vertex.properties,
              };
              vertices.push(temp);
            }
          }
        }
    }
    return arrayOfObjectsToReturn;
    
}


// source.addEventListener("error", (event) => {
//   // handle errors
// });

const N = 2;
const nodes = [...Array(N).keys()].map(() => ({
  // Initial velocity in random direction
  vx: 0,
  vy: 0,
  vz: 0,
}));

const Graph = ForceGraph3D()(document.getElementById("3d-graph"))
  .linkColor(() => "rgba(255, 255, 255, 1)")
  .linkWidth(1)
  .linkOpacity(0.5);

Graph.onEngineTick(() => {
  const { nodes, links } = Graph.graphData();

  // If the distance between two nodes < D, add a link
  const D = 2;

  nodes[0].x = 1;
  nodes[0].y = 1;
  nodes[0].z = 1;

   nodes[1].x = 20;
   nodes[1].y = 20;
   nodes[1].z = 20;
  for (let i = 0; i < N; i++) {
    for (let j = i + 1; j < N; j++) {
      const dx = Math.abs(nodes[i].x - nodes[j].x);
      const dy = Math.abs(nodes[i].y - nodes[j].y);
      const dz = Math.abs(nodes[i].z - nodes[j].z);
      const dist = dx + dy + dz;
      const linkIdx = links.findIndex(
        (l) => l.source === nodes[i] && l.target === nodes[j]
      );
      const link = links[linkIdx];

      if (dist < D && linkIdx === -1) {
        Graph.graphData({
          nodes: [...nodes],
          links: [...links, { source: nodes[i], target: nodes[j] }],
        });
      } else if (dist > D && linkIdx !== -1) {
        // Find current link and remove it
        if (linkIdx !== -1) {
          links.splice(linkIdx, 1);
          Graph.graphData({ nodes, links });
        }
      }
    }
  }
});

Graph.cooldownTime(Infinity)
  .d3AlphaDecay(0)
  .d3VelocityDecay(0)

  // Deactivate existing forces
  .d3Force("center", null)
  .d3Force("charge", null)
  .d3Force("link", null)

  // Add collision and bounding box forces
  .d3Force("collide", d3.forceCollide(Graph.nodeRelSize()))
  .d3Force("box", () => {
    const CUBE_HALF_SIDE = Graph.nodeRelSize() * N * 0.5;

    nodes.forEach((node) => {
      const x = node.x || 0,
        y = node.y || 0,
        z = node.z || 0;

      // bounce on box walls
      if (Math.abs(x) > CUBE_HALF_SIDE) {
        node.vx *= -1;
      }
      if (Math.abs(y) > CUBE_HALF_SIDE) {
        node.vy *= -1;
      }
      if (Math.abs(z) > CUBE_HALF_SIDE) {
        node.vz *= -1;
      }
    });
  })

  // Add nodes
  .graphData({ nodes, links: [] });
