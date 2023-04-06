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
    let arrayOfObjectsToReturn = []

    // Iterate over objects of jaseto entity 
    for (let element of Object.entries(jsonObject)) {

        // If one of the objects is of type arc, we retrieve arcs contained in it
        if(element[0] === 'arcs'){
            for (let arc of element[1].elements) {
                //retrieve every properties of every arc that has properties different from null
                if (arc.properties !== null && arc.properties !== undefined) {
                    let temp = {
                        id: arc.id,
                        properties: arc.properties
                    };
                    arrayOfObjectsToReturn.push(temp);
                }
            }
        }

        if (element[0] === "edges") {
        //   for (let arc of element[1].elements) {
        //     //retrieve every properties of every arc that has properties different from null
        //     if (arc.properties !== null && arc.properties !== undefined) {
        //       let temp = {
        //         id: arc.id,
        //         properties: arc.properties,
        //       };
        //       arrayOfObjectsToReturn.push(temp);
        //     }
        //   }
        }

        if (element[0] === "vertices") {
        //   for (let arc of element[1].elements) {
        //     //retrieve every properties of every arc that has properties different from null
        //     if (arc.properties !== null && arc.properties !== undefined) {
        //       let temp = {
        //         id: arc.id,
        //         properties: arc.properties,
        //       };
        //       arrayOfObjectsToReturn.push(temp);
        //     }
        //   }
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
