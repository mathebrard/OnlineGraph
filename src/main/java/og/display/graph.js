const queryString = window.location.search;
const urlParams = new URLSearchParams(queryString);
const gid = urlParams.get('gid');
const refreshRate = parseInt("refresh") ? refreshRate : 1000;
let lastChangesAskedTime;

$.getJSON("/api/og/og.GraphService/get/" + gid, function (json) {
	lastChangesAskedTime = new Date().getTime();
    // create an array with nodes
    var nodes = new vis.DataSet(json['vertices']);

    // create an array with edges
    var edges = new vis.DataSet(json['edges']);
    // defaults props
    var props = json['properties'];
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
        	color : {},
            scaling: {
                label: {
                    enabled: true
                },
                customScalingFunction: function (min, max, total, value) {
                    return 0.05 * value;
                }
            },
            hidden: (props["default vertex hidden"] === 'true'),
            //title: htmlTitle("<ul><li>Hello World</li></ul>")
        },
        edges: {
        	arrows : {
        		to:{}
        		},
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
                //springLength: 200,
        	   centralGravity: 2,
        	   gravitationalConstant : -500,
        	   //damping: .05
            },
            maxVelocity : 0.9
        },
        layout: {
            improvedLayout: true,
            /*hierarchical: {        
      		sortMethod: 'directed'
            }*/
        }
    }
    if (props["default vertex shape"] in shapeAllowedNode){
    	options["nodes"]["shape"] = props["default vertex shape"];
    }
    if (props["default vertex mass"]){
    	options["nodes"]["mass"] = parseInt(props["default vertex mass"]);
    }
    if (props["default vertex borderColor"]){
    	options["nodes"]["color"]["border"] = props["default vertex borderColor"];
    }
    if (props["default vertex fillColor"]){
    	options["nodes"]["color"]["background"] = props["default vertex fillColor"];
    }
    if (props["default vertex borderWidth"]){
    	options["nodes"]["borderWidth"] = parseInt(props["default vertex borderWidth"]);
    }
    
    if (props["default vertex image"]){
    	options["nodes"]["image"] = props["default vertex image"];
    }
    
    if (props["default vertex value"]){
    	options["nodes"]["value"] = parseInt(props["default vertex value"]);
    }
    if (props["default vertex label"]){
    	options["nodes"]["label"] = props["default vertex label"];
    }
    if (props["default vertex size"]){
    	options["nodes"]["size"] = parseInt(props["default vertex size"]);
    }
    if (props["default edge directed"]){
    	options["edges"]["arrows"]["to"]["enabled"] = (props["default edge directed"] === "true");
    }
    var arrowShapeAllowed = ["arrow", "bar", "box", "circle", "crow", "curve", "diamond", "image", "inv_curve", "inv_triangle", "triangle", "vee"]
    if (props["default edge arrowShape"] in arrowShapeAllowed){
    	options["edges"]["arrows"]["to"]["type"] = props["default edge arrowShape"];
    }
    if (props["default edge arrowImage"]){
    	options["edges"]["arrows"]["to"]["src"] = props["default edge arrowImage"];
    }
    if (props["default edge width"]){
    	options["edges"]["arrows"]["to"]["imageHeight"] = parseInt(props["default edge width"]) * 40;
    	options["edges"]["arrows"]["to"]["imageWidth"] = parseInt(props["default edge width"]) * 40;
    	options["edges"]["width"] =parseInt(props["default edge width"]);
    }
    if (props["default edge color"]){
    	options["edges"]["color"] = props["default edge arrowShape"];
    }
    if (props["default edge dashes"]){
    	options["edges"]["dashes"] =(props["default edge dashes"] === 'true');
    }
    if (props["default edge label"]){
    	options["edges"]["label"] = props["default edge arrowShape"];
    }
    var visnetwork = new vis.Network(container, data, options);
    let network = generateNetwork(nodes, edges)
    network.getListNodes().forEach((node) => {
        //node.setDefaultColor(visnetwork);
        try{
	        node.processDefaultParams(visnetwork, network, props);
	        node.processParams(visnetwork, network);
        }
        catch(error){
        	console.log(error)
        }
    });
    network.getListEdges().forEach((node) => {
        try{
	        node.processDefaultParams(visnetwork, network, props);
	        node.processParams(visnetwork, network);
        }
        catch(error){
        	console.log(error)
        }
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
	console.log(visnetwork)
	fitWindow(visnetwork);
	function doAjax() {
	    $.getJSON("/api/og/og.GraphService/changes/" + gid + "," + lastChangesAskedTime, function (json1) {
	    	lastChangesAskedTime = new Date().getTime()/1000;
	    	console.log("trigger")
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
	    setTimeout(function(){fitWindow(visnetwork)}, 50);
	}
	function processChanges(json, network, visnetwork, defaultProps) {
		console.log(json)
	    for (let change in json) {
	        if (json[change]['type'] == "AddVertex") {
	    		console.log( json[change]['vertexInfo'])
	            network.addVertex(visnetwork, defaultProps, json[change]['vertexInfo'])
			    /*visnetwork.fit({
	    			  //minZoomLevel: 0,
	    			  animation: {
	    				  duration: 1000,
	    				  easingFunction: "linear"
	    			  }
	    			});*/
	            var select = document.getElementById("select-label");
	            try {
		            var text = select.options[select.selectedIndex].text;
	            }
	            catch(error){
	            	var text = "label";
	            }
	            network.getListNodes().forEach((node) => {
	            try{
	                if (node.params[text])
	                    node.setLabel(visnetwork, node.params[text]);
	                else if(node.defaultparams[text])
	                    node.setLabel(visnetwork, node.defaultparams[text]);
	            }
	            catch(error){
	            	console.log(error);
	            }
	            });
	            refreshSelects();
	        } else if (json[change]['type'] == "RemoveVertex") {
	            network.removeVertex(visnetwork, json[change]['elementID'])
			    visnetwork.fit({
	    			  //minZoomLevel: 0,
			    	animation: {
	    				  duration: 1000,
	    				  easingFunction: "linear"
	    			  }
	    			});
	        } else if (json[change]['type'] == "AddEdge") {
	            network.addEdge(visnetwork, defaultProps, json[change]['edgeInfo'])
	            refreshSelects();
	            var select = document.getElementById("select-label");
	            console.log(select.selectedIndex)
	            try {
		            var text = select.options[select.selectedIndex].text;
	            }
	            catch(error){
	            	var text = "label";
	            }
	            network.getListEdges().forEach((node) => {
	            try{
	                if (node.params[text])
	                    node.setLabel(visnetwork, node.params[text]);
	                else if(node.defaultparams[text])
	                    node.setLabel(visnetwork, node.defaultparams[text]);
	            }
	            catch(error){
	            	console.log(error);
	            }
	            });
	        } else if (json[change]['type'] == "RemoveEdge") {
	            network.removeEdge(visnetwork, json[change]['elementID'])
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