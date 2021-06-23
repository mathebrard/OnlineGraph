const queryString = window.location.search;
const urlParams = new URLSearchParams(queryString);
const gid = urlParams.get('gid');
const refreshRate = parseInt("refresh") ? refreshRate : 1000;
let lastChangesAskedTime;

$.getJSON("/api/og/og.GraphStorageService/get/" + gid, function (json) {
	lastChangesAskedTime = new Date().getTime();
    // create an array with nodes
    var nodes = new vis.DataSet(json['value']['vertices']);

    // create an array with edges
    var edges = new vis.DataSet(json['value']['edges']);
    // defaults props
    var props = json['value']['props'];
    // create a network
    var container = document.getElementById("mynetwork");
    var data = {
        nodes: nodes,
        edges: edges,
    };

    if (props["background color"] != undefined) {
        container.style.backgroundColor = props["background color"];
    }
    var shapeAllowedNode = ["ellipse", "circle", "database", "box", "text","image", "circularImage", "diamond", "dot", "star", "triangle", "triangleDown", "hexagon", "square","icon"]
    var shapeAllowedEdge = ["arrow", "bar", "box", "circle", "crow", "curve", "diamond", "image", "inv_curve", "inv_triangle", "triangle", "vee"]
    // options pour l'initialisation de visnetwork TODO unkonwn property
    let options = {
        nodes: {
            mass: parseInt(props["default vertex mass"]),
            color: {
                border: props["default vertex color.border"],
                background: props["default vertex background color"]
            },
            borderWidth: parseInt(props["default vertex borderWidth"]),
            image: props["default vertex image"],
            hidden: (props["default vertex hidden"] === 'true'),
            value: parseInt(props["default vertex value"]),
            label: props["default vertex label"],
            size: parseInt(props["default vertex size"]),
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
        edges: {
            arrows: {
                to: {
                    enabled: (props["default edge directed"] === "true"),
                    src: props["default edge arrow image"],
                    imageHeight: parseInt(props["default edge width"]) * 40,
                    imageWidth: parseInt(props["default edge width"]) * 40
                }
            },
            color: props["default edge color"],
            width: parseInt(props["default edge width"]),
            dashes: (props["default edge dashes"] === 'true'),
            label: props["default edge label"],
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
            //enabled: false
            stabilization: true,
             adaptiveTimestep: true,
             //timestep: true,
           barnesHut: {
                springLength: 200,
            }
        },
        layout: {
            improvedLayout: true,
            /*hierarchical: {        
      		sortMethod: 'directed'
            }*/
        }
    }
    if (props["default vertex shape"] in shapeAllowedNode){
    	options["node"]["shape"] = props["default vertex shape"];
    }
    if (props["default edge arrow type"] in shapeAllowedEdge){
    	options["edge"]["arrows"]["to"]["type"] = props["default edge arrow type"];
    }
   console.log(options)
    var visnetwork = new vis.Network(container, data, options);
    let network = generateNetwork(nodes, edges);
    network.getListNodes().forEach((node) => {
        //node.setDefaultColor(visnetwork);
        node.processDefaultParams(visnetwork, network, props);
        node.processParams(visnetwork, network);
    });
    network.getListEdges().forEach((node) => {
        node.processDefaultParams(visnetwork, network, props);
        node.processParams(visnetwork, network);
    });
	console.log(network.allProps)

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
                /*var newFX = new Function("x", "return " + fx);
                network.getListNodes().forEach((node) => {
                    let newSize = newFX(node.params[key]);

                    visnetwork.body.data.nodes.updateOnly({
                        id: node.id,
                        value: newSize
                    });
                });*/
                let newFX = new Function("x", "return Math.max(0," + fx + ");");
                console.log("return " + fx)
                if (key != undefined && key != "") {
                    network.getListNodes().forEach((node) => {
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
                /*var newFX = new Function("x", "return " + fx);
                network.getListNodes().forEach((node) => {
                    let newSize = newFX(node.params[key]);

                    visnetwork.body.data.nodes.updateOnly({
                        id: node.id,
                        value: newSize
                    });
                });*/
                let newFX = new Function("x", "return Math.max(0," + fx + ");");
                console.log("return " + fx)
                if (key != undefined && key != "") {
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
    console.log(allprops)
    hotbar.addPanelLinkPropertiesToFunction(attributes, allprops);
    hotbar.addPanelLinkChangeGraph(attributesChangeGraph, visnetwork, json);
    hotbar.addPanelChangeLabel(network, visnetwork, allprops);

    /*hotbar.addEntry("Sélection Simple", (e) => {
        TYPESELECTION = "simple";
    });
    hotbar.addEntry("Sélection Multiple", (e) => {
        TYPESELECTION = "multiple";
    });*/

	$("#information-network").append(hotbar.generateHTML());
	
	/*$.getJSON("/api/og/og.GraphStorageService/changes/" + gid + "," + 0, function (json1) {

	    processChanges(json1, network, visnetwork, props)
	});*/
	
	doAjax();

	function doAjax() {
	    $.getJSON("/api/og/og.GraphStorageService/changes/" + gid + "," + lastChangesAskedTime, function (json1) {
	    	lastChangesAskedTime = new Date().getTime()/1000;
		    processChanges(json1, network, visnetwork, props)
		    setTimeout(doAjax, refreshRate);
		});
	}
    
	function processChanges(json, network, visnetwork, defaultProps) {
	    for (let change in json['value']) {
	        if (json['value'][change]['type'] == "AddVertex") {
	            network.addVertex(visnetwork, defaultProps, json['value'][change]['vertexInfo'])
			    visnetwork.fit({
	    			  //minZoomLevel: 0,
	    			  animation: {
	    				  duration: 1000,
	    				  easingFunction: "linear"
	    			  }
	    			});
	        } else if (json['value'][change]['type'] == "RemoveVertex") {
	            network.removeVertex(visnetwork, json['value'][change]['elementID'])
			    visnetwork.fit({
	    			  //minZoomLevel: 0,
			    	animation: {
	    				  duration: 1000,
	    				  easingFunction: "linear"
	    			  }
	    			});
	        } else if (json['value'][change]['type'] == "AddEdge") {
	            network.addEdge(visnetwork, defaultProps, json['value'][change]['edgeInfo'])
	        } else if (json['value'][change]['type'] == "RemoveEdge") {
	            network.removeEdge(visnetwork, json['value'][change]['elementID'])
	        }
	    }
	}

});