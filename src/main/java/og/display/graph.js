$.getJSON("/api/og/og.GraphStorageService/get/demo_graph", function (json) {
    // create an array with nodes
    var nodes = new vis.DataSet(json['value']['vertices']);

    // create an array with edges
    var edges = new vis.DataSet(json['value']['edges']);
    // create a network
    var container = document.getElementById("mynetwork");
    var data = {
        nodes: nodes,
        edges: edges,
    };
    // options pour l'initialisation de visnetwork
    let options = {
        nodes: {
            shape: 'dot',
            scaling: {
                label: {
                    enabled: true
                },
                customScalingFunction: function (min,max,total,value) {
                    return 0.05 * value;
                }
            },
            //title: htmlTitle("<ul><li>Hello World</li></ul>")
        },
        autoResize: true,
        physics: {
            // stabilization: true,
            // adaptiveTimestep: true,
            // timestep: true,
            barnesHut: {
                springLength: 200,
            }
        }
    }
    var visnetwork = new vis.Network(container, data, options);
    let network = generateNetwork (nodes,edges);
    network.getListNodes().forEach((node) => {
        node.setDefaultColor(visnetwork);
    });

    //hotbar things
    let props = {
        "CPU": {
            "name": "Nombre de CPU",
            "valeur_min": 1,
            "valeur_max": 512
        },
        "Charge": {
            "name": "charge",
            "valeur_min": 0,
            "valeur_max": 1
        },
        "RAM": {
            "name": "RAM",
            "valeur_min": 1,
            "valeur_max": 64
        },
        "DNS": {
            "name": "DNS",
            "valeur_min": 1,
            "valeur_max": 3
        }
    };

    let attributes = {
        "Node color": {
            type: "color",
            functionApply: (key, fx) => {
                if (key != "") {
                    network.getListNodes().forEach((node) => {
                        node.setBackgroundColor (visnetwork, fx.getcolor (node.params[key]));
                    });
                } else {
                    network.getListNodes().forEach((node) => {
                        node.setDefaultBackgroundColor(visnetwork);
                    });
                }
                visnetwork.redraw ();
            }
        },
        "Node size" : {
            type: "function",
            function: "20",
            dftfunction: "20",
            functionApply: (key, fx) => {
                console.log(key, fx);
                var newFX = new Function ("x", "return " + fx);
                network.getListNodes().forEach((node) => {
                    let newSize = newFX (node.params[key]);

                    visnetwork.body.data.nodes.updateOnly({
                        id: node.id,
                        value: newSize
                    });
                });
                visnetwork.redraw ();
            }
        },
        "Border color": {
            type: "color",
            functionApply: (key, fx) => {
                if (key != "") {
                    network.getListNodes().forEach((node) => {
                        node.setBorderColor(visnetwork, fx.getcolor(node.params[key]))
                    });
                } else {
                    network.getListNodes().forEach((node) => {
                        node.setDefaultBorderColor(visnetwork);
                    });
                }
                visnetwork.redraw ();
            }
        },
        "Border size": {
            type: "function",
            function: "1",
            dftfunction : "1",
            functionApply: (key, fx) => {
                var newFX = new Function ("x", "return " + fx);
                network.getListNodes().forEach((node) => {
                    let newSize = newFX (node.params[key]);

                    visnetwork.body.data.nodes.updateOnly({
                        id: node.id,
                        borderWidth: newSize
                    });
                });
                visnetwork.redraw ();
            }
        }
    };
// CREATION DU MENU DE CHOIX
    hotbar = new Hotbar();
    hotbar.addPanelLinkPropertiesToFunction(attributes, props);
    console.log(nodes);
    hotbar.addPanelChangeLabel(network, visnetwork, Object.keys(nodes));
    hotbar.addEntry("Sélection Simple", (e) => {
        TYPESELECTION = "simple";
    });
    hotbar.addEntry("Sélection Multiple", (e) => {
        TYPESELECTION = "multiple";
    });

    $("#information-network").append(hotbar.generateHTML ());

    // variable implémentant le "noeud" courant
    let currentNode = new SelectionNodes(nodes, network, visnetwork);
    currentNode.node = nodes.localComponent;

    
});


