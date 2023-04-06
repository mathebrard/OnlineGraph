var observer = new myIdawiObserver();


    // fetch(
    //   "http://localhost:8081/api////og.GraphService/get/randomGraph?what=content"
    // ).then((data) => console.log(data.text()));

    const source = new EventSource("http://localhost:8081/api////og.GraphService/get/randomGraph?what=content");

    source.addEventListener("message", (event) => {
      const data = event.data;
      var lines = data.split(/\r?\n/);
      //console.log(lines)
      var headerraw = lines.shift();
      console.log(headerraw);
      // process the data received from the server
    });

    source.addEventListener("error", (event) => {
      // handle errors
    });


const N = 50;
const nodes = [...Array(N).keys()].map(() => ({
  // Initial velocity in random direction
  vx: Math.random(),
  vy: Math.random(),
  vz: Math.random(),
}));

const Graph = ForceGraph3D()(document.getElementById("3d-graph"))
  .linkColor(() => "rgba(255, 255, 255, 1)")
  .linkWidth(1)
  .linkOpacity(0.5);

Graph.onEngineTick(() => {
  const { nodes, links } = Graph.graphData();

  // If the distance between two nodes < D, add a link
  const D = 100;
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
