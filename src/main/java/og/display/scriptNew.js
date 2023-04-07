const source = new EventSource(
  "http://localhost:8081/api////og.GraphService/get2/randomGraph?what=content"
);

// Listener of backend events
source.addEventListener("message", (event) => {
  const data = event.data;
  var lines = data.split(/\r?\n/);
  var headerraw = lines.shift();
  var temp = lines.join("");
  if(temp !== "EOT") {
      var jsonToProcess = JSON.parse(temp);
      var jsonFormatted = parseJaseto(jsonToProcess);
    
      initGraph(jsonFormatted);
  }

});


/**
 * Print the graph updated with  a JSON given in parameter
 * @param {*} data the JSON formated in order to print the graph correctly
 * @returns 
 */
function initGraph(data) {
  let init = [], arcsBis = [], listOfVerticesIds = [];
  data.vertices.forEach((vertex) => {
    listOfVerticesIds.push(vertex.id)
    console.log("vertex : ", vertex)
    init.push({
        fx: vertex.x*150,
        fy: vertex.y*150,
        fz: vertex.z*150,
        id: vertex.id,
        color: vertex.properties.color,
        description: vertex.properties.label,
        width: vertex.properties.width,
        vx: 0,
        vy: 0,
        vz: 0,
    });
  });

  data.arcs.forEach((arc) => {
    // Create arcs only if vertices to link exist
    if(listOfVerticesIds.includes(arc.from) && listOfVerticesIds.includes(arc.to)) {
        console.log("arc : ", arc)
        arcsBis.push({
            color: arc.properties.color,
            id: arc.id,
            source: arc.from,
            target: arc.to,
            shape: arc.properties.shape
        });
    }
  });

  let Graph = ForceGraph3D()(document.getElementById("3d-graph"))
    // .linkColor(() => "rgba(255, 255, 255, 1)")
    .linkWidth(1)
    .linkColor((link) => {
      //   const { source, target } = link;
      //   const dx = Math.abs(source.x - target.x);
      //   const dy = Math.abs(source.y - target.y);
      //   const dz = Math.abs(source.z - target.z);
      //   const dist = dx + dy + dz;
      //   const opacity = 1 - dist / D;
      return link.color;
    })
    .nodeLabel("description")
    .linkOpacity(1.0);

  Graph.cooldownTime(Infinity)
    // Add nodes and links
    .graphData({ nodes: init, links: arcsBis });

  Graph.onEngineTick(() => {
    const { nodes, links } = Graph.graphData();

  });

  return Graph;
}



/**
 * parse the json in order to make it fit for initGraph method
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
      if (element[0] === "arcs") {
        for (let arc of element[1].elements) {
          //retrieve every properties of every arc that has properties different from null
          if (arc.properties !== null && arc.properties !== undefined) {
            let temp = {
              id: arc.id,
              from: arc.from,
              to: arc.to,
              properties: arc.properties,
            };
            arcs.push(temp);
          }
        }
      }

      // If one of the objects is of type edges, we retrieve edges contained in it
      if (element[0] === "edges" && element[1].elements) {
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

      // If one of the objects is of type vertices, we retrieve vertices contained in it
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

