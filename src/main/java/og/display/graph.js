const queryString = window.location.search;
const urlParams = new URLSearchParams(queryString);
const gid = urlParams.get('gid')

$.getJSON("/api/og/og.GraphStorageService/get/" + gid, function (json) {
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
                customScalingFunction: function (min, max, total, value) {
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
    let network = generateNetwork(nodes, edges);
    network.getListNodes().forEach((node) => {
        node.setDefaultColor(visnetwork);
        node.processParams(visnetwork, network);
        console.log(node)
    });
    network.getListEdges().forEach((node) => {
        node.processParams(visnetwork, network);
        console.log(node)
    });

    //hotbar things
    /*let props = {
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
        },
        "age": {
            "name": "age",
        }
    };*/
    let unknownprops = {}
    for (let prop in network.unknowProps) {
        unknownprops[network.unknowProps[prop]] = {
            "name": network.unknowProps[prop]
        }
    }
    let allprops = {}
    for (let prop in network.allProps) {
        allprops[network.allProps[prop]] = {
            "name": network.allProps[prop]
        }
    }
    let attributes = {
        "Node color": {
            type: "color",
            functionApply: (key, fx) => {

                if (key != undefined && key != "") {
                    network.getListNodes().forEach((node) => {
                        node.setBackgroundColor(visnetwork, fx.getcolor(node.params[key]));
                    });
                } else {
                    /*network.getListNodes().forEach((node) => {
                        node.setDefaultBackgroundColor(visnetwork);
                    });*/
                }
                visnetwork.redraw();
            }
        },

        "Border color": {
            type: "color",
            functionApply: (key, fx) => {
                if (key != "") {
                    network.getListNodes().forEach((node) => {
                        if (node.params[key])
                            node.setBorderColor(visnetwork, fx.getcolor(node.params[key]))
                    });
                } else {
                    /*network.getListNodes().forEach((node) => {
                        node.setDefaultBorderColor(visnetwork);
                    });*/
                }
                visnetwork.redraw();
            }
        },
        "Node size": {
            type: "function",
            function: "20",
            dftfunction: "20",
            functionApply: (key, fx) => {
                console.log(key, fx);
                /*var newFX = new Function("x", "return " + fx);
                network.getListNodes().forEach((node) => {
                    let newSize = newFX(node.params[key]);

                    visnetwork.body.data.nodes.updateOnly({
                        id: node.id,
                        value: newSize
                    });
                });*/
                let newFX = new Function("x", "return Math.max(0," + fx +");");
                console.log("return " + fx)
                if (key != undefined && key != "" ) {
                    network.getListNodes().forEach((node) => {
                        console.log(node.params[key])
                        if (node.params[key])
                            node.setSize(visnetwork, newFX(node.params[key]))
                    });
                } else {
                    /*console.log("passed")
                    network.getListNodes().forEach((node) => {
                        node.setSize(visnetwork, newFX)
                    });*/
                }
                visnetwork.redraw();
            }
        },
        "Border size": {
            type: "function",
            function: "0",
            dftfunction: "0",
            functionApply: (key, fx) => {
                var newFX = new Function("x", "return " + fx);
                network.getListNodes().forEach((node) => {
                    if (node.params[key]) {
                        let newSize = newFX(node.params[key]);
                        console.log(newSize)
                        visnetwork.body.data.nodes.updateOnly({
                            id: node.id,
                            borderWidth: newSize
                        });
                    }
                });
                visnetwork.redraw();
            }
        }

    };
    let attributesChangeGraph = {
        "Add node": {
            type: "add",
            functionApply: (key, fx) => {

                if (key != undefined && key != "") {
                    network.getListNodes().forEach((node) => {
                        node.setBackgroundColor(visnetwork, fx.getcolor(node.params[key]));
                    });
                } else {
                    /*network.getListNodes().forEach((node) => {
                        node.setDefaultBackgroundColor(visnetwork);
                    });*/
                }
                visnetwork.redraw();
            }
        },

        "Remove node": {
            type: "remove",
            functionApply: (key, fx) => {
                if (key != "") {
                    network.getListNodes().forEach((node) => {
                        if (node.params[key])
                            node.setBorderColor(visnetwork, fx.getcolor(node.params[key]))
                    });
                } else {
                    /*network.getListNodes().forEach((node) => {
                        node.setDefaultBorderColor(visnetwork);
                    });*/
                }
                visnetwork.redraw();
            }
        },
        "Add edge": {
            type: "add",
            function: "20",
            dftfunction: "20",
            functionApply: (key, fx) => {
                console.log(key, fx);
                /*var newFX = new Function("x", "return " + fx);
                network.getListNodes().forEach((node) => {
                    let newSize = newFX(node.params[key]);

                    visnetwork.body.data.nodes.updateOnly({
                        id: node.id,
                        value: newSize
                    });
                });*/
                let newFX = new Function("x", "return Math.max(0," + fx +");");
                console.log("return " + fx)
                if (key != undefined && key != "" ) {
                    network.getListNodes().forEach((node) => {
                        console.log(node.params[key])
                        if (node.params[key])
                            node.setSize(visnetwork, newFX(node.params[key]))
                    });
                } else {
                    /*console.log("passed")
                    network.getListNodes().forEach((node) => {
                        node.setSize(visnetwork, newFX)
                    });*/
                }
                visnetwork.redraw();
            }
        },
        "Remove edge": {
            type: "remove",
            function: "0",
            dftfunction: "0",
            functionApply: (key, fx) => {
                var newFX = new Function("x", "return " + fx);
                network.getListNodes().forEach((node) => {
                    if (node.params[key]) {
                        let newSize = newFX(node.params[key]);
                        console.log(newSize)
                        visnetwork.body.data.nodes.updateOnly({
                            id: node.id,
                            borderWidth: newSize
                        });
                    }
                });
                visnetwork.redraw();
            }
        }

    };
// CREATION DU MENU DE CHOIX
    hotbar = new Hotbar();
    hotbar.addPanelLinkPropertiesToFunction(attributes, unknownprops);
    hotbar.addPanelLinkChangeGraph(attributesChangeGraph, visnetwork,json);
    hotbar.addPanelChangeLabel(network, visnetwork, allprops);

    /*hotbar.addEntry("Sélection Simple", (e) => {
        TYPESELECTION = "simple";
    });
    hotbar.addEntry("Sélection Multiple", (e) => {
        TYPESELECTION = "multiple";
    });*/

    $("#information-network").append(hotbar.generateHTML());

    console.log(visnetwork.body.data.nodes[0])


});


