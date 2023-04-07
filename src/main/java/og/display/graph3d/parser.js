

export function parseJasetoEventToJson(event) {
    const data = event.data;
    let lines = data.split(/\r?\n/);
    let headerraw = lines.shift();
    let temp = lines.join("");
    let jsonObject = JSON.parse(temp);

    let entireObject = [];
    let vertices = [];
    let arcs = [];
    let edges = [];

    // Iterate over objects of jaseto entity 
    for (let element of Object.entries(jsonObject)) {

        // If one of the objects is of type arc, we retrieve arcs contained in it
        if (element[0] === 'arcs') {
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