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
                    try {
                        network.getListNodes().forEach((node) => {
                            if (node.params[key])
                                node.setBackgroundColor(visnetwork, fx.getcolor(node.params[key]));
                            else if (node.defaultparams[key])
                                node.setBackgroundColor(visnetwork, fx.getcolor(node.defaultparams[key]));
                        });	
                    } 
                    catch (error) {
                    	  console.log(error);
                    }

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
                    try {
                        network.getListNodes().forEach((node) => {
                            if (node.params[key])
                                node.setBorderColor(visnetwork, fx.getcolor(node.params[key]))
                            else if (node.defaultparams[key])
                                node.setBorderColor(visnetwork, fx.getcolor(node.defaultparams[key]))

                        });
                    } 
                    catch (error) {
                    	  console.log(error);
                    }

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
                    try {
                        network.getListNodes().forEach((node) => {
                            if (node.params[key])
                                node.setSize(visnetwork, newFX(node.params[key]));
                            else if (node.defaultparams[key])
                                node.setSize(visnetwork, newFX(node.defaultparams[key]));
                        });
                    } 
                    catch (error) {
                    	  console.log(error);
                    }

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
                try {
                    network.getListNodes().forEach((node) => {
                        if (node.params[key]) {
                            let newSize = newFX(node.params[key]);
                            console.log(newSize)
                            visnetwork.body.data.nodes.updateOnly({
                                id: node.id,
                                borderWidth: newSize
                            });
                        }
                        else if (node.defaultparams[key]){
                        	let newSize = newFX(node.defaultparams[key]);
                            console.log(newSize)
                            visnetwork.body.data.nodes.updateOnly({
                                id: node.id,
                                borderWidth: newSize
                            });
                        }
                    });
                } 
                catch (error) {
                	  console.log(error);
                }

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
    //hotbar.addPanelLinkChangeGraph(attributesChangeGraph, visnetwork, json);
    hotbar.addPanelChangeLabel(network, visnetwork, allprops);
    hotbar.addStats(visnetwork);

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
	fitWindow(visnetwork);
	function doAjax() {
	    $.getJSON("/api/og/og.GraphStorageService/changes/" + gid + "," + lastChangesAskedTime, function (json1) {
	    	lastChangesAskedTime = new Date().getTime()/1000;
		    processChanges(json1, network, visnetwork, props)
		    setTimeout(doAjax, refreshRate);
		});
	}
	function fitWindow(visnetwork) {
	    visnetwork.fit({
			  //minZoomLevel: 0,
			  animation: {
				  duration: 50,
				  easingFunction: "linear"
			  }
			});
	    setTimeout(doAjax, 50);
	}
	function processChanges(json, network, visnetwork, defaultProps) {
	    for (let change in json['value']) {
	        if (json['value'][change]['type'] == "AddVertex") {
	            network.addVertex(visnetwork, defaultProps, json['value'][change]['vertexInfo'])
			    /*visnetwork.fit({
	    			  //minZoomLevel: 0,
	    			  animation: {
	    				  duration: 1000,
	    				  easingFunction: "linear"
	    			  }
	    			});*/
	            var select = document.getElementById("select-label");
	            var text = select.options[select.selectedIndex].text;	            
	            network.getListNodes().forEach((node) => {
	                console.log(node);
	                console.log(node.params[text])
	                if (node.params[text])
	                    node.setLabel(visnetwork, node.params[text]);
	                else if(node.defaultparams[text])
	                    node.setLabel(visnetwork, node.defaultparams[text]);
	            });
	            refreshSelects();
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
	            refreshSelects();
	            var select = document.getElementById("select-label");
	            var text = select.options[select.selectedIndex].text;
	            network.getListEdges().forEach((node) => {
	                if (node.params[text])
	                    node.setLabel(visnetwork, node.params[text]);
	                else if(node.defaultparams[text])
	                    node.setLabel(visnetwork, node.defaultparams[text]);
	            });
	        } else if (json['value'][change]['type'] == "RemoveEdge") {
	            network.removeEdge(visnetwork, json['value'][change]['elementID'])
	        }
	    }
	    //document.getElementById("stats").textContent = "Number of vertices : " + network.listNodes.length +" , number of edges : " + network.listEdges.length ;         // Create a text node
	    document.getElementById("stats").textContent = "Number of vertices : " + visnetwork.body.data.nodes.length +" , number of edges : " + visnetwork.body.data.edges.length ;         // Create a text node

	}
	
	function refreshSelects(){
		$(".select-propertie-value").each((index, select) => {
			let values = [];
		    for (let prop in network.allProps) {
		        allprops[network.allProps[prop]] = {
		            "name": network.allProps[prop]
		        }
		    }
			for (let i=0;i<select.options.length;i++)
				values.push(select.options[i].value);
			for (let i=0;i<allprops.length;i++){
				if (!allprops[i] in values){
					console.log(allprops[i])
					// create new option element
					var opt = document.createElement('option');
					// create text node to add to option element (opt)
					opt.appendChild( document.createTextNode(allprops[i]) );
					// set value property of opt
					opt.value = allprops[i];
					// add opt to end of select box (sel)
					sel.appendChild(opt); 
				}
			}
        });
	}

});