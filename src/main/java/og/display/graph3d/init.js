export function initGraph(data) {
    let D = 100;
    let init = [];
    data["vertices"].forEach(vertex => {
        init.push({
            id: vertex["id"],
            vx: Math.random(),
            vy: Math.random(),
            vz: Math.random()
        });
    });

    let Graph = ForceGraph3D()(document.getElementById('3d-graph'))
        .linkColor(() => 'rgba(255, 255, 255, 1)')
        .linkWidth(1)
        .linkColor((link) => {
            const { source, target } = link;
            const dx = Math.abs(source.x - target.x);
            const dy = Math.abs(source.y - target.y);
            const dz = Math.abs(source.z - target.z);
            const dist = dx + dy + dz;
            const opacity = 1 - (dist / D);
            return `rgba(255, 255, 255, ${opacity})`;
        })
        .linkOpacity(1.0);

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
        .graphData({ nodes: init, links: [] });

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
