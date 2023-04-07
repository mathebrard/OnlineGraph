const source = new EventSource(
  "http://localhost:8081/api////og.GraphService/get2/randomGraph?what=content"
);

// Listener of backend events
source.addEventListener("message", (event) => {
  const data = event.data;
  var lines = data.split(/\r?\n/);
  var headerraw = lines.shift();
  var temp = lines.join("");
  var jsonToProcess = JSON.parse(temp);

  var jsonFormatted = parseJaseto(jsonToProcess);
  initGraph(jsonFormatted);
  
  console.log("json formated = ", jsonFormatted);
});

function initGraph(data) {
  let init = [], arcsBis = [], listOfVerticesIds = [];
  data["vertices"].forEach((vertex) => {
    listOfVerticesIds.push(vertex.id)
    init.push({
      id: vertex.id,
      vx: 0,
      vy: 0,
      vz: 0,
    });
  });

  data["arcs"].forEach((arc) => {
    if(listOfVerticesIds.includes(arc.from) && listOfVerticesIds.includes(arc.to)) {
        arcsBis.push({
          id: arc["id"],
          source: arc.from,
          target: arc.to,
        });
    }
  });
  console.log("arcbis : ", arcsBis, " list ids : ", listOfVerticesIds, " init : ", init)

  let Graph = ForceGraph3D()(document.getElementById("3d-graph"))
    // .linkColor(() => "rgba(255, 255, 255, 1)")
    .linkWidth(1)
    // .linkColor((link) => {
    //   const { source, target } = link;
    //   const dx = Math.abs(source.x - target.x);
    //   const dy = Math.abs(source.y - target.y);
    //   const dz = Math.abs(source.z - target.z);
    //   const dist = dx + dy + dz;
    //   const opacity = 1 - dist / D;
    //   return `rgba(255, 255, 255, ${opacity})`;
    // })
    .linkOpacity(1.0);

  Graph.cooldownTime(Infinity)
//     .d3AlphaDecay(0)
//     .d3VelocityDecay(0)

//     // Deactivate existing forces
//     .d3Force("center", null)
//     .d3Force("charge", null)
//     .d3Force("link", null)

//     // Add collision and bounding box forces
//     .d3Force("collide", d3.forceCollide(Graph.nodeRelSize()))
//     .d3Force("box", () => {
//       const { nodes, links } = Graph.graphData();
//       const CUBE_HALF_SIDE = Graph.nodeRelSize() * 30 * 0.5;

//       nodes.forEach((node) => {
//         const x = node.x || 0,
//           y = node.y || 0,
//           z = node.z || 0;

//         // bounce on box walls
//         if (Math.abs(x) > CUBE_HALF_SIDE) {
//           node.vx *= -1;
//         }
//         if (Math.abs(y) > CUBE_HALF_SIDE) {
//           node.vy *= -1;
//         }
//         if (Math.abs(z) > CUBE_HALF_SIDE) {
//           node.vz *= -1;
//         }
//       });
//     })

    // Add nodes
    .graphData({ nodes: init, links: arcsBis });

  Graph.onEngineTick(() => {
    const { nodes, links } = Graph.graphData();

  });

  return Graph;
}



/**
 * 
 * @param {*} jsonObject to change in order to make it fit the function initGraph that will print the graph correctly
 * @returns 
 */
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

        if (element[0] === "vertices") {
          for (let vertex of element[1].elements) {
            //retrieve every properties of every arc that has properties different from null
            if (vertex.properties !== null && vertex.properties !== undefined) {
                let locations = vertex.properties.location.split(",");
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
    entireObject = {
        arcs: arcs,
        edges: edges,
        vertices: vertices
    }

    
    return entireObject;
}

