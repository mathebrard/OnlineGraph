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

let linksProperties = {};
let nodesProperties = {};

export function initGraph(data) {
    let D = 100;
    let initialNodes = [];
    let initialLinks = [];

    initVertices(data, initialNodes);
    initArcs(data);
    //initLinks(data, initialLinks);

    console.log(nodesProperties);

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
            const dx = Math.abs(source.x - target.x);
            const dy = Math.abs(source.y - target.y);
            const dz = Math.abs(source.z - target.z);
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
        .d3Force('box', () => {
            const { nodes, links } = Graph.graphData();
            const CUBE_HALF_SIDE = Graph.nodeRelSize() * 30 * 0.5;

            nodes.forEach(node => {
                const x = node.x || 0, y = node.y || 0, z = node.z || 0;

                // bounce on box walls
                if (Math.abs(x) > CUBE_HALF_SIDE) { node.vx *= -1; }
                if (Math.abs(y) > CUBE_HALF_SIDE) { node.vy *= -1; }
                if (Math.abs(z) > CUBE_HALF_SIDE) { node.vz *= -1; }
            });
        })

        // Add nodes
        .graphData({ nodes: initialNodes, links: initialLinks });

    Graph.onEngineTick(() => {
        const { nodes, links } = Graph.graphData();

        // If the distance between two nodes < D, add a link
        for (let i = 0; i < nodes.length; i++) {
            for (let j = i + 1; j < nodes.length; j++) {
                const dx = Math.abs(nodes[i].x - nodes[j].x);
                const dy = Math.abs(nodes[i].y - nodes[j].y);
                const dz = Math.abs(nodes[i].z - nodes[j].z);
                const dist = dx + dy + dz;
                const linkIdx = links.findIndex(l => l.source === nodes[i] && l.target === nodes[j]);
                const link = links[linkIdx];

                if (dist < D && linkIdx === -1) {

                    Graph.graphData({
                        nodes: [...nodes],
                        links: [...links, { source: nodes[i], target: nodes[j] }]
                    });

                } else if (dist > D && linkIdx !== -1) {
                    // Find current link and remove it
                    if (linkIdx !== -1) {
                        links.splice(linkIdx, 1);
                        Graph.graphData({ nodes, links });
                    }
                }
                else if (linkIdx !== -1) {
                    // Change link opacity based on distance

                }
            }
        }

    });

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

function initVertices(data, initialNodes) {
    data["vertices"].forEach(vertex => {
        console.log(vertex);
        nodesProperties[vertex["id"]] = {
            color: hexToRgbA(vertex["properties"]["color"]),
            label: vertex["properties"]["label"],
            size: vertex["properties"]["width"],
            x: vertex["properties"]["x"],
            y: vertex["properties"]["y"],
            z: vertex["properties"]["z"]
        };

        initialNodes.push({
            id: vertex["id"],
            vx: Math.random(),
            vy: Math.random(),
            vz: Math.random()
        });
    });
}

