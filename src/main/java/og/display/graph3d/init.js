function hexToRgbA(hex) {
    let c;
    if (/^#([A-Fa-f0-9]{3}){1,2}$/.test(hex)) {
        c = hex.substring(1).split('');
        if (c.length == 3) {
            c = [c[0], c[0], c[1], c[1], c[2], c[2]];
        }
        c = '0x' + c.join('');
        return (opacity) => { return `rgba(${(c >> 16) & 255}, ${(c >> 8) & 255}, ${c & 255},${opacity})` };
    }
    return null;
}

let graph;
let linksProperties = {};
let nodesProperties = {};

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

    const para = document.createElement("p");
    var str = JSON.stringify(data, null, 2);
    const node = document.createTextNode(str);
    para.appendChild(node);
    para.setAttribute("style", "background-color: red;");

    const element = document.getElementById("jsonPrinter");
    element.appendChild(para);

    initVertices(data, initialNodes);
    initArcs(data);
    //initLinks(data, initialLinks);

    let Graph = ForceGraph3D()(document.getElementById('3d-graph'))
        .linkColor(() => 'rgba(255, 255, 255, 1)')
        .linkWidth((link) => {
            const linkProperties = linksProperties[link.source.id + "-" + link.target.id];
            if (linkProperties && linkProperties.width) {
                return linkProperties.width;
            }
            return 1;
        })

        .nodeColor((node) => {
            const nodeProperties = nodesProperties[node.id];
            if (nodeProperties && nodeProperties.color) {
                return nodeProperties.color(1);
            }
            return 'rgba(255, 255, 255, 1)';
        })
        .nodeThreeObjectExtend(true)
        .nodeThreeObject(node => {

            const nodeProperties = nodesProperties[node.id];
            if (nodeProperties && nodeProperties.label) {
                // extend node with text sprite
                const sprite = new SpriteText(nodeProperties.label);
                sprite.color = 'lightgrey';
                sprite.textHeight = 5;
                return sprite;
            }
        })
        .linkColor((link) => {
            const { source, target } = link;
            const dx = Math.abs(source.x - target.x) ;
            const dy = Math.abs(source.y - target.y) ;
            const dz = Math.abs(source.z - target.z) ;
            const dist = dx + dy + dz;
            const opacity = 1 - (dist / D);
            const link_properties = linksProperties[source.id + "-" + target.id];

            if (link_properties && link_properties.color) {
                return link_properties.color(opacity);
            }
            return `rgba(255, 255, 255, ${opacity})`;
        })
        .linkOpacity(1.0)
        .linkThreeObjectExtend(true)
        .linkThreeObject(link => {
            let link_properties = linksProperties[link.source.id + "-" + link.target.id];
            if (link_properties && link_properties.label) {
                // extend link with text sprite
                const sprite = new SpriteText(linksProperties[link.source.id + "-" + link.target.id].label);
                sprite.color = 'lightgrey';
                sprite.textHeight = 3;
                return sprite;
            }
        })
        .linkPositionUpdate((sprite, { start, end }) => {
            const middlePos = Object.assign(...['x', 'y', 'z'].map(c => ({
                [c]: start[c] + (end[c] - start[c]) / 2 // calc middle point
            })));

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
        .d3Force('center', null)
        .d3Force('charge', null)
        .d3Force('link', null)

        // Add collision and bounding box forces
        .d3Force('collide', d3.forceCollide(Graph.nodeRelSize()))

        // Add nodes
        .graphData({ nodes: initialNodes, links: initialLinks });

    return Graph;
}

function initLinks(data, init_links) {
    data["links"].forEach(link => {
        init_links.push({
            source: link["from"],
            target: link["to"]
        });
    });
}

function initArcs(data) {
    data["arcs"].forEach(arc => {
        console.log(arc["properties"]["color"], /^#([A-Fa-f0-9]{3}){1,2}$/.test(arc["properties"]["color"]));

        linksProperties[arc["from"] + "-" + arc["to"]] = {
            arrowShape: arc["properties"]["arrowShape"],
            arrowSize: arc["properties"]["arrowSize"],
            color: hexToRgbA(arc["properties"]["color"]),
            label: arc["properties"]["label"],
            style: arc["properties"]["style"],
            width: arc["properties"]["width"]
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
        vertex["properties"]["x"] += 10;
        vertex["properties"]["y"] += 10;
        vertex["properties"]["z"] += 10;
        nodesProperties[vertex["id"]] = {
            color: hexToRgbA(vertex["properties"]["color"]),
            label: vertex["properties"]["label"],
            size: vertex["properties"]["width"],
            x: vertex["properties"]["x"] + 10,
            y: vertex["properties"]["y"] + 10,
            z: vertex["properties"]["z"] + 10,
        };
        console.log(vertex);

        if (!containsNode(initialNodes, vertex)) {
            initialNodes.push({
                id: vertex["id"],
            });
        }

    });
}

