function hexToRgbA(hex) {
  let c;
  if (/^#([A-Fa-f0-9]{3}){1,2}$/.test(hex)) {
    c = hex.substring(1).split("");
    if (c.length == 3) {
      c = [c[0], c[0], c[1], c[1], c[2], c[2]];
    }
    c = "0x" + c.join("");
    return (opacity) => {
      return `rgba(${(c >> 16) & 255}, ${(c >> 8) & 255}, ${
        c & 255
      },${opacity})`;
    };
  }
  return null;
}

export let graph;
let linksProperties = {};
export let nodesProperties = {};

let vertexSpace = 10;
// todo: recup les valeur a partir des propriétés
let vertexLabel = "label";
let vertexSize = "size";
let rgbColor = "color";


let nodesStore = [];

export function updateGraph(graph, data) {
  const { nodes, links } = graph.graphData();

  initVertices(data, nodes);
  initArcs(data);

  graph.graphData({ nodes: [...nodes], links: [...links] });
}

export function initGraph(data) {
  let D = 100;
  let initialNodes = [];
  let initialLinks = [];

  initVertices(data, initialNodes);
  initArcs(data);
  //initLinks(data, initialLinks);

  var editor = ace.edit("editor", {
    theme: "ace/theme/monokai",
    mode: "ace/mode/javascript",
    value: "vertexSpace = 10;",
  });
  var editor2 = ace.edit("editor2", {
    theme: "ace/theme/monokai",
    mode: "ace/mode/javascript",
  });

  editor2.setReadOnly(true);

  function executeCode() {
    var code = editor.getValue();
    eval(code);
  }

  let Graph = ForceGraph3D()(document.getElementById("3d-graph"))
    .linkColor(() => "rgba(255, 255, 255, 1)")
    .linkWidth((link) => {
      const linkProperties =
        linksProperties[link.source.id + "-" + link.target.id];
      if (linkProperties && linkProperties.width) {
        return linkProperties.width;
      }
      return 1;
    })
    .onNodeClick((node) => {
      var str = JSON.stringify(nodesStore, null, 2);
      var editor = ace.edit("editor", {
        theme: "ace/theme/monokai",
        mode: "ace/mode/javascript",
      });
      editor.setOptions({
        enableBasicAutocompletion: true,
      });

      nodesStore.forEach((nodeStore) => {
        if(nodeStore.id === node.id){
            editor.setValue(JSON.stringify(nodeStore, null, 2), 0);
        }
    })
    // editor.setValue(str, 0); // To see all nodes on click
      editor.session.setMode("ace/mode/javascript");
    })
    .nodeColor((node) => {
      const nodeProperties = nodesProperties[node.id];
      if (nodeProperties && nodeProperties.color) {
        return nodeProperties.color(1);
      }
      return "rgba(255, 255, 255, 1)";
    })
    .nodeThreeObjectExtend(true)
    .nodeThreeObject((node) => {
      const nodeProperties = nodesProperties[node.id];
      if (nodeProperties && nodeProperties.label) {
        // extend node with text sprite
        const sprite = new SpriteText(nodeProperties.label);
        sprite.color = "lightgrey";
        sprite.textHeight = 5;
        return sprite;
      }
    })
    .linkColor((link) => {
      const { source, target } = link;
      const dx = Math.abs(source.x - target.x);
      const dy = Math.abs(source.y - target.y);
      const dz = Math.abs(source.z - target.z);
      const dist = dx + dy + dz;
      const opacity = 1 - dist / D;
      const link_properties = linksProperties[source.id + "-" + target.id];

      if (link_properties && link_properties.color) {
        return link_properties.color(opacity);
      }
      return `rgba(255, 255, 255, ${opacity})`;
    })
    .linkOpacity(1.0)
    .linkThreeObjectExtend(true)
    .linkThreeObject((link) => {
      let link_properties =
        linksProperties[link.source.id + "-" + link.target.id];
      if (link_properties && link_properties.label) {
        // extend link with text sprite
        const sprite = new SpriteText(
          linksProperties[link.source.id + "-" + link.target.id].label
        );
        sprite.color = "lightgrey";
        sprite.textHeight = 3;
        return sprite;
      }
    })
    .linkPositionUpdate((sprite, { start, end }) => {
      const middlePos = Object.assign(
        ...["x", "y", "z"].map((c) => ({
          [c]: start[c] + (end[c] - start[c]) / 2, // calc middle point
        }))
      );

      // Position sprite
      if (sprite) {
        Object.assign(sprite.position, middlePos);
      }
    })
    .linkDirectionalArrowLength(3.5)
    .linkDirectionalArrowRelPos(1);

  Graph.cooldownTime(Infinity)
    .d3AlphaDecay(0)
    .d3VelocityDecay(0)

    // Deactivate existing forces
    .d3Force("center", null)
    .d3Force("charge", null)
    .d3Force("link", null)

    // Add collision and bounding box forces
    .d3Force("collide", d3.forceCollide(Graph.nodeRelSize()))

    // Add nodes
    .graphData({ nodes: initialNodes, links: initialLinks });

  return Graph;
}

function initLinks(data, init_links) {
  data["links"].forEach((link) => {
    init_links.push({
      source: link["from"],
      target: link["to"],
    });
  });
}

function initArcs(data) {
  data["arcs"].forEach(arc => {
    linksProperties[arc["from"] + "-" + arc["to"]] = {
      arrowShape: arc["properties"]["arrowShape"],
      arrowSize: arc["properties"]["arrowSize"],
      color: hexToRgbA(arc["properties"]["color"]),
      label: arc["properties"]["label"],
      style: arc["properties"]["style"],
      width: arc["properties"]["width"],
    };
  });
}

function containsNode(nodes, node) {
  for (let i = 0; i < nodes.length; i++) {
    const element = nodes[i];
    if (element["id"] === node["id"]) {
      return true;
    }
  }
  return false;
}

function initVertices(data, initialNodes) {
    data["vertices"].forEach(vertex => {
        vertex["properties"]["x"] += vertexSpace;
        vertex["properties"]["y"] += vertexSpace;
        vertex["properties"]["z"] += vertexSpace;
        nodesProperties[vertex["id"]] = {
            color: hexToRgbA(vertex["properties"]["color"]),
            label: vertex["properties"]["label"],
            size: vertex["properties"]["width"],
            x: vertex["properties"]["x"] + vertexSpace,
            y: vertex["properties"]["y"] + vertexSpace,
            z: vertex["properties"]["z"] + vertexSpace,
        };
        console.log(vertex);

    if (!containsNode(initialNodes, vertex)) {
      initialNodes.push({
        id: vertex["id"],
        color: hexToRgbA(vertex["properties"]["color"]),
        label: vertex["properties"]["label"],
        size: vertex["properties"]["width"],
        x: vertex["properties"]["x"] + vertexSpace,
        y: vertex["properties"]["y"] + vertexSpace,
        z: vertex["properties"]["z"] + vertexSpace,
      });
      nodesStore.push({
        id: vertex["id"],
        color: hexToRgbA(vertex["properties"]["color"]),
        label: vertex["properties"]["label"],
        size: vertex["properties"]["width"],
        x: vertex["properties"]["x"] + vertexSpace,
        y: vertex["properties"]["y"] + vertexSpace,
        z: vertex["properties"]["z"] + vertexSpace,
      });
    }
  });
}

