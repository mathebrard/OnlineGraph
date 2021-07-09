//get urls parameters
const queryString = window.location.search;
const urlParams = new URLSearchParams(queryString);
const gid = urlParams.get('gid');
const refreshRate = parseInt("refresh") ? refreshRate : 1000;

const shapeAllowedNode = ["ellipse", "circle", "database", "box", "text", "image", "circularImage", "diamond", "dot", "star", "triangle", "triangleDown", "hexagon", "square", "icon"]
const arrowShapeAllowed = ["arrow", "bar", "box", "circle", "crow", "curve", "diamond", "image", "inv_curve", "inv_triangle", "triangle", "vee"];

let lastChangeIndex;


$.getJSON("/api/og/og.GraphService/get/" + gid, function (json) {
    //if graph is known
    if (json['results'].length != 0) {
        lastChangeIndex = json['results'][0]['nbChanges'];

        // create vis dataset with json's vertices
        let nodes = new vis.DataSet(json['results'][0]['vertices']);

        // set json's arcs to be directed
        let arc = json['results'][0]['arcs']
        for (let i = 0; i < arc.length; i++) {
            try {
                arc[i]["properties"]["directed"] = true
            } catch {
                arc[i]["properties"] = {};
                arc[i]["properties"]["directed"] = true
            }
        }

        // set json's edges from/to vertices
        let edge = json['results'][0]['edges']
        for (let i = 0; i < edge.length; i++) {
            try {
                edge[i]["from"] = edge[i]["ends"][0];
                edge[i]["to"] = edge[i]["ends"][edge[i]["ends"].length - 1];
            } catch {
                edge[i]["properties"] = {};
                edge[i]["from"] = edge[i]["ends"][0];
                edge[i]["to"] = edge[i]["ends"][edge[i]["ends"].length - 1];
            }

        }
        // create vis dataset with json's arcs and edges
        let edges = new vis.DataSet($.extend(arc, edge));

        // defaults props
        let props = json['results'][0]['properties'];

        // create a network
        let container = document.getElementById("mynetwork");
        let data = {
            nodes: nodes,
            edges: edges,
        };

        // set network background color
        if (props["background color"] != undefined) {
            container.style.backgroundColor = props["background color"]
        }

        // options pour l'initialisation de visnetwork
        let options = {
            nodes: {
                color: {},
                scaling: {
                    label: {
                        enabled: true
                    },
                    customScalingFunction: function (min, max, total, value) {
                        return 0.05 * value;
                    }
                },
                font: {},
                hidden: (props["default vertex hidden"] === 'true'),
				shapeProperties: {
				    interpolation: false    // 'true' for intensive zooming
				  }
            },
            edges: {
                arrows: {
                    to: {}
                },
                scaling: {
                    label: {
                        enabled: true
                    },
                    customScalingFunction: function (min, max, total, value) {
                        return 0.05 * value;
                    }
                },
            },
            autoResize: true,
            physics: {
                //enabled: false
                stabilization: true,
                //adaptiveTimestep: true,
                //timestep: true,
                barnesHut: {
                    //springLength: 200,
                    centralGravity: 1,
                    gravitationalConstant: -500,
                    //damping: .05
                    avoidOverlap: .5
                },
                maxVelocity: 0.9
            },
            layout: {
                randomSeed:0,
				improvedLayout:false,
            }
        }

        //process default properties (push them into the vis options)
        if (props["default vertex shape"] in shapeAllowedNode) {
            options["nodes"]["shape"] = props["default vertex shape"];
        } else if (props["default vertex shape"] == "rectangle")
            options["nodes"]["shape"] = "box";
        if (props["default vertex mass"]) {
            options["nodes"]["mass"] = parseInt(props["default vertex mass"]);
        }
        if (props["default vertex borderColor"]) {
            options["nodes"]["color"]["border"] = props["default vertex borderColor"];
        }
        if (props["default vertex fillColor"]) {
            options["nodes"]["color"]["background"] = props["default vertex fillColor"];
        }
        if (props["default vertex borderWidth"]) {
            options["nodes"]["borderWidth"] = parseInt(props["default vertex borderWidth"]);
        }
        if (props["default vertex image"]) {
            options["nodes"]["image"] = props["default vertex image"];
        }
        if (props["default vertex value"]) {
            options["nodes"]["value"] = parseInt(props["default vertex value"]);
        }
        if (props["default vertex label"]) {
            options["nodes"]["label"] = props["default vertex label"];
        }
        if (props["default vertex size"]) {
            options["nodes"]["size"] = parseInt(props["default vertex size"]);
        }
        if (props["default vertex labelColor"]) {
            options["nodes"]["font"]["color"] = props["default vertex labelColor"];
        }
        if (props["default edge directed"]) {
            options["edges"]["arrows"]["to"]["enabled"] = (props["default edge directed"] === "true");
        }
        if (props["default edge arrowShape"] in arrowShapeAllowed) {
            options["edges"]["arrows"]["to"]["type"] = props["default edge arrowShape"];
        }
        if (props["default edge arrowImage"]) {
            options["edges"]["arrows"]["to"]["src"] = props["default edge arrowImage"];
        }
        if (props["default edge width"]) {
            options["edges"]["arrows"]["to"]["imageHeight"] = parseInt(props["default edge width"]) * 40;
            options["edges"]["arrows"]["to"]["imageWidth"] = parseInt(props["default edge width"]) * 40;
            options["edges"]["width"] = parseInt(props["default edge width"]);
        }
        if (props["default edge color"]) {
            options["edges"]["color"] = props["default edge color"];
        }
        if (props["default edge dashes"]) {
            options["edges"]["dashes"] = (props["default edge dashes"] === 'true');
        }
        if (props["default edge label"]) {
            options["edges"]["label"] = props["default edge label"];
        }

        //generate visnetwork and network
        let visnetwork = new vis.Network(container, data, options);
        let network = generateNetwork(nodes, edges)

        //save numbers of edges and arcs
        network.nbArcs = arc.length;
        network.nbEdges = edge.length;

        //process params for all nodes
        network.getListNodes().forEach((node) => {
            try {
                node.processDefaultParams(visnetwork, network, props);
                node.processParams(visnetwork, network);
            } catch (error) {
                console.log(error)
            }
        });

        //process params for all edges
        network.getListEdges().forEach((edge) => {
            try {
                edge.processDefaultParams(visnetwork, network, props);
                edge.processParams(visnetwork, network);
            } catch (error) {
                console.log(error)
            }
        });

        //save all nodes props
        let allpropsNodes = {}
		console.log(network.allPropsNodes)
        for (let prop in network.allPropsNodes) {
            allpropsNodes[network.allPropsNodes[prop]] = {
                "name": network.allPropsNodes[prop]
            }
        }

        //save all edges props
        let allpropsEdges = {}
        for (let prop in network.allPropsEdges) {
            allpropsEdges[network.allPropsEdges[prop]] = {
                "name": network.allPropsEdges[prop]
            }
        }


        // personalization menu attributes and associated function
        let attributes = {
            "Node color": {
                type: "color",
                functionApply: (key, fx) => {

                    if (key != undefined && key != "") {
                        network.getListNodes().forEach((node) => {
                            try {

                                if (node.params[key])
                                    node.setBackgroundColor(visnetwork, fx.getcolor(node.params[key]));
                                else if (node.defaultparams[key])
                                    node.setBackgroundColor(visnetwork, fx.getcolor(node.defaultparams[key]));
                            } catch (error) {
                                console.log(error);
                            }
                        });


                    }
                    visnetwork.redraw();
                }
            },
            "Node Border color": {
                type: "color",
                functionApply: (key, fx) => {
                    if (key != "") {
                        network.getListNodes().forEach((node) => {
                            try {

                                if (node.params[key])
                                    node.setBorderColor(visnetwork, fx.getcolor(node.params[key]))
                                else if (node.defaultparams[key])
                                    node.setBorderColor(visnetwork, fx.getcolor(node.defaultparams[key]))
                            } catch (error) {
                                console.log(error);
                            }
                        });


                    }
                    visnetwork.redraw();
                }
            },
            "Node size": {
                type: "function",
                function: "20",
                dftfunction: "20",
                functionApply: (key, fx) => {
                    let newFX = new Function("x", "return Math.max(0," + fx + ");");
                    if (key != undefined && key != "") {
                        network.getListNodes().forEach((node) => {
                            try {

                                if (node.params[key])
                                    node.setSize(visnetwork, newFX(node.params[key]));
                                else if (node.defaultparams[key])
                                    node.setSize(visnetwork, newFX(node.defaultparams[key]));
                            } catch (error) {
                                console.log(error);
                            }
                        });


                    }
                    visnetwork.redraw();
                }
            },
            "Node Border size": {
                type: "function",
                function: "0",
                dftfunction: "0",
                functionApply: (key, fx) => {
                    let newFX = new Function("x", "return " + fx);
                    network.getListNodes().forEach((node) => {
                        try {

                            if (node.params[key]) {
                                let newSize = newFX(node.params[key]);
                                visnetwork.body.data.nodes.updateOnly({
                                    id: node.id,
                                    borderWidth: newSize
                                });
                            } else if (node.defaultparams[key]) {
                                let newSize = newFX(node.defaultparams[key]);
                                visnetwork.body.data.nodes.updateOnly({
                                    id: node.id,
                                    borderWidth: newSize
                                });
                            }
                        } catch (error) {
                            console.log(error);
                        }
                    });


                    visnetwork.redraw();
                }
            },
            "Node Label color": {
                type: "color",
                functionApply: (key, fx) => {

                    if (key != undefined && key != "") {
                        network.getListNodes().forEach((node) => {
                            try {

                                if (node.params[key])
                                    node.setLabelColor(visnetwork, fx.getcolor(node.params[key]));
                                else if (node.defaultparams[key])
                                    node.setLabelColor(visnetwork, fx.getcolor(node.defaultparams[key]));
                            } catch (error) {
                                console.log(error);
                            }
                        });
                    }
                    visnetwork.redraw();
                }
            },
            "Edge color": {
                type: "color",
                functionApply: (key, fx) => {

                    if (key != undefined && key != "") {
                        network.getListEdges().forEach((node) => {
                            try {

                                if (node.params[key])
                                    node.setColor(visnetwork, fx.getcolor(node.params[key]));
                                else if (node.defaultparams[key])
                                    node.setColor(visnetwork, fx.getcolor(node.defaultparams[key]));
                            } catch (error) {
                                console.log(error);
                            }
                        });


                    }
                    visnetwork.redraw();
                }
            },
            "Edge label color": {
                type: "color",
                functionApply: (key, fx) => {

                    if (key != undefined && key != "") {
                        network.getListEdges().forEach((node) => {
                            try {

                                if (node.params[key])
                                    node.setLabelColor(visnetwork, fx.getcolor(node.params[key]));
                                else if (node.defaultparams[key])
                                    node.setLabelColor(visnetwork, fx.getcolor(node.defaultparams[key]));
                            } catch (error) {
                                console.log(error);
                            }
                        });


                    }
                    visnetwork.redraw();
                }
            },
            "Edge width": {
                type: "function",
                function: "0",
                dftfunction: "0",
                functionApply: (key, fx) => {
                    let newFX = new Function("x", "return " + fx);
                    network.getListEdges().forEach((node) => {
                        try {

                            if (node.params[key]) {
                                let newSize = newFX(node.params[key]);
                                visnetwork.body.data.edges.updateOnly({
                                    id: node.id,
                                    width: newSize
                                });
                            } else if (node.defaultparams[key]) {
                                let newSize = newFX(node.defaultparams[key]);
                                visnetwork.body.data.edges.updateOnly({
                                    id: node.id,
                                    width: newSize
                                });
                            }
                        } catch (error) {
                            console.log(error);
                        }
                    });


                    visnetwork.redraw();
                }
            },
            "Edge arrow size": {
                type: "function",
                function: "0",
                dftfunction: "0",
                functionApply: (key, fx) => {
                    let newFX = new Function("x", "return " + fx);
                    network.getListEdges().forEach((node) => {
                        try {

                            if (node.params[key]) {
                                let newSize = newFX(node.params[key]);
                                visnetwork.body.data.edges.updateOnly({
                                    id: node.id,
                                    arrows: {
                                        middle: {
                                            scaleFactor: newSize
                                        }
                                    }
                                });
                            } else if (node.defaultparams[key]) {
                                let newSize = newFX(node.defaultparams[key]);
                                visnetwork.body.data.nodes.updateOnly({
                                    id: node.id,
                                    arrows: {
                                        middle: {
                                            scaleFactor: newSize
                                        }
                                    }
                                });
                            }
                        } catch (error) {
                            console.log(error);
                        }
                    });


                    visnetwork.redraw();
                }
            },
            "Edge dashed": {
                type: "function",
                function: "0",
                dftfunction: "0",
                functionApply: (key, fx) => {
                    let newFX = new Function("x", "return " + fx);
                    network.getListEdges().forEach((node) => {
                        try {

                            if (node.params[key]) {
                                let newSize = newFX(node.params[key]);
                                visnetwork.body.data.edges.updateOnly({
                                    id: node.id,
                                    dashes: [newSize, newSize]
                                });
                            } else if (node.defaultparams[key]) {
                                let newSize = newFX(node.defaultparams[key]);
                                visnetwork.body.data.edges.updateOnly({
                                    id: node.id,
                                    dashes: [newSize, newSize]

                                });
                            }
                        } catch (error) {
                            console.log(error);
                        }
                    });


                    visnetwork.redraw();
                }
            }


        };

        // personalization menu creation
        hotbar = new Hotbar();
        hotbar.addPanelLinkPropertiesToFunction(attributes, allpropsNodes, allpropsEdges)
        hotbar.addPanelChangeLabel(network, visnetwork, allpropsNodes, allpropsEdges);
        hotbar.addStats(visnetwork, network);
        $("#information-network").append(hotbar.generateHTML());


        //process graph changes
        changesLoop();

        //fit graph in window
        fitWindow(visnetwork);

        //check if there are errors
        errorTab();


        ////////////////////////////////////////////////////
        /////////////////// FUNCTIONS //////////////////////
        ////////////////////////////////////////////////////

        //call processChanges on changes json every refreshrate seconds
        function changesLoop() {

            $.getJSON("/api/og/og.GraphService/changes/" + gid + "/" + lastChangeIndex, function (json1) {
                processChanges(json1['results'][0], network, visnetwork, props)
                setTimeout(changesLoop, refreshRate);
            });
        }

        //fit graph in window
        function fitWindow(visnetwork) {
            visnetwork.fit({
                //minZoomLevel: 0,
                animation: {
                    duration: 900,
                    easingFunction: "linear"
                }
            });
            setTimeout(function () {
                fitWindow(visnetwork)
            }, 1000);
        }

        function processChanges(json, network, visnetwork, defaultProps) {
            let last = 0;
            for (let change in json) {
                if (json[change]['type'] == "AddVertex") {
                    //add vertex
                    let newNode = network.addVertex(visnetwork, defaultProps, json[change]['vertexInfo'])

                    //change the label according to the user selection in the personalization menu
                    let select = document.getElementById("select-label");
                    try {
                        let text = select.options[select.selectedIndex].text;
                    } catch (error) {
                        let text = "label";
                    }
                    try {
                        if (newNode.params[text])
                            newNode.setLabel(visnetwork, node.params[text]);
                        else if (node.defaultparams[text])
                            newNode.setLabel(visnetwork, node.defaultparams[text]);
                    } catch (error) {
                        //console.log(error);
                    }

                    //refresh the selects in the personalization menu
                    refreshSelects();
                } else if (json[change]['type'] == "RemoveVertex") {
                    network.removeVertex(visnetwork, json[change]['elementID'])
                    visnetwork.fit({
                        animation: {
                            duration: 1000,
                            easingFunction: "linear"
                        }
                    });
                } else if (json[change]['type'] == "AddEdge") {
                    //normalize ends to 'from' and 'to' to add edge
                    let jsoncorrect = json[change]['edgeInfo'];
                    jsoncorrect["from"] = jsoncorrect["ends"][0]
                    jsoncorrect["to"] = jsoncorrect["ends"][jsoncorrect["ends"].length - 1]
                    let newEdge = network.addEdge(visnetwork, network, defaultProps, jsoncorrect,"edge")

                    //refresh the selects in the personalization menu
                    refreshSelects();

                    //change the label according to the user selection in the personalization menu
                    let select = document.getElementById("select-label");
					let text                    
					try {
                        text = select.options[select.selectedIndex].text;
                    } catch (error) {
                        text = "label";
                    }
                    try {
						if (newEdge.params!=undefined){
                            if (newEdge.params[text])
                                newEdge.setLabel(visnetwork, newEdge.params[text]);
                            else if (newEdge.defaultparams[text])
                                newEdge.setLabel(visnetwork, newEdge.defaultparams[text]);
						}
                    } catch (error) {
                        console.log(error);
                    }
                } else if (json[change]['type'] == "RemoveEdge") {
                    network.removeEdge(visnetwork,network, json[change]['elementID'],"edge")

                } else if (json[change]['type'] == "EdgeDataChange") {
                    if (json[change]['name'] == "properties") {
                        $.getJSON("/api/og/og.GraphService/get/" + gid, function (jsoncomplet) {
                            //get edge
                            let n = network.getEdge(json[change]['id'])
                            //get and process properties
                            if (n != undefined) {
                                for (let a in jsoncomplet['results'][0]["edges"]) {
                                    if (jsoncomplet['results'][0]["edges"][a]["id"] == json[change]['id']) {
                                        n.params = jsoncomplet['results'][0]["edges"][a]["properties"]
                                        n.processParams(visnetwork, network)
                                    }
                                }
                            }
                        });
                    }
                } else if (json[change]['type'] == "AddArc") {
                    //normalize ends to 'from' and 'to' to add edge
                    let jsoncorrect = json[change]['edgeInfo'];
                    jsoncorrect["directed"] = true;
                    let newArc = network.addEdge(visnetwork,network ,defaultProps, jsoncorrect,"arc")

                    //refresh the selects in the personalization menu
                    refreshSelects();

                    //change the label according to the user selection in the personalization menu
                    let select = document.getElementById("select-label");
                    try {
                        let text = select.options[select.selectedIndex].text;
                    } catch (error) {
                        let text = "label";
                    }
                    try {
                        if (newArc.params[text])
                            newArc.setLabel(visnetwork, newArc.params[text]);
                        else if (newArc.defaultparams[text])
                            newArc.setLabel(visnetwork, newArc.defaultparams[text]);
                    } catch (error) {
                        //console.log(error);
                    }
                } else if (json[change]['type'] == "RemoveArc") {
                    network.removeEdge(visnetwork,network, json[change]['elementID'],"arc")

                } else if (json[change]['type'] == "ArcDataChange") {
                    if (json[change]['name'] == "properties") {
                        $.getJSON("/api/og/og.GraphService/get/" + gid, function (jsoncomplet) {
                            //get arc
                            let n = network.getEdge(json[change]['id'])
                            //get and process properties
                            if (n != undefined) {
                                for (let a in jsoncomplet['results'][0]["arcs"]) {
                                    if (jsoncomplet['results'][0]["arcs"][a]["id"] == json[change]['id']) {
                                        n.params = jsoncomplet['results'][0]["arcs"][a]["properties"]
                                        n.processParams(visnetwork, network)
                                    }
                                }
                            }
                        });
                    }
                } else if (json[change]['type'] == "VertexDataChange") {
                    if (json[change]['name'] == "properties") {

                        $.getJSON("/api/og/og.GraphService/get/" + gid, function (jsoncomplet) {
                            //get node
                            let n = network.getNode(json[change]['id']);
                            //get and process properties
                            if (n != undefined) {
                                for (let a in jsoncomplet['results'][0]["vertices"]) {
                                    if (jsoncomplet['results'][0]["vertices"][a]["id"] == json[change]['id']) {
                                        n.params = jsoncomplet['results'][0]["vertices"][a]["properties"];
                                        n.processParams(visnetwork, network);
                                    }
                                }
                            }
                        });
                    }
                }
                //save last change index
                last = change;
            }
            //refresh stats
            document.getElementById("stats").textContent = "Number of vertices : " + visnetwork.body.data.nodes.length + " , number of edges : " + network.nbEdges + " , number of arcs : " + network.nbArcs;
            //save last change index
			if (lastChangeIndex!=last && last!=0)
            	lastChangeIndex = parseInt(lastChangeIndex, 10) + parseInt(last, 10) + 1;
            refreshSelects();
        }

        //refresh personalization menu's selects with the changes
        function refreshSelects() {
            //for each selects in personalization menu
            $(".select-propertie-value").each((index, select) => {
                //if select is for node personalization
                if (select.classList.contains("node-select")) {
                    let values = [];

                    for (let prop in network.allPropsNodes) {
                        allpropsNodes[network.allPropsNodes[prop]] = {
                            "name": network.allPropsNodes[prop]
                        }
                    }
                    for (let i = 0; i < select.options.length; i++)
                        values.push(select.options[i].value);
                    let keys = [];
                    for (let k in allpropsNodes) keys.push(k);
                    //for each property in allpropsnodes
                    for (let i = 0; i < keys.length; i++) {
                        // if select dont contains property, add it
                        if (!values.includes(keys[i])) {
                            // create new option element
                            let opt = document.createElement('option');
                            // create text node to add to option element (opt)
                            opt.appendChild(document.createTextNode(keys[i]));
                            // set value property of opt
                            opt.value = keys[i];
                            // add opt to end of select box (sel)
                            select.appendChild(opt);
                            values.push(keys[i])
                        }
                    }
                } else if (select.classList.contains("edge-select")) {
                    //if select is for edge personalization
                    let values = [];
                    for (let prop in network.allPropsEdges) {
                        allpropsEdges[network.allPropsEdges[prop]] = {
                            "name": network.allPropsEdges[prop]
                        }
                    }
                    for (let i = 0; i < select.options.length; i++)
                        values.push(select.options[i].value);
                    let keys = [];
                    for (let k in allpropsEdges) keys.push(k);
                    for (let i = 0; i < keys.length; i++) {
                        //for each property in allpropsedges
                        if (!values.includes(keys[i])) {
                            // if select dont contains property, add it
                            let opt = document.createElement('option');
                            // create text node to add to option element (opt)
                            opt.appendChild(document.createTextNode(keys[i]));
                            // set value property of opt
                            opt.value = keys[i];
                            // add opt to end of select box (sel)
                            select.appendChild(opt);
                            values.push(keys[i])
                        }
                    }
                }
            });
        }

        //if there are errors in the graph
        function errorTab() {
            $.getJSON("/api/og/og.GraphService/listProblems/" + gid, function (json) {
                //if graph has error
                if (json['errors'].length != 0) {
                    let div = document.createElement('div');
                    //if error tab not already created, create it
                    if (document.getElementById("errors") == null) {
                        div.id = "errors";
                        div.style.width = "24%"
                        div.style.float = "right"
                        div.style.border = "1px solid lightgray"
                        let title = document.createElement('p');
                        title.textContent = "Errors"
                        title.style.fontSize = "26px"
                        div.appendChild(title);
                        document.getElementById("wrapper").appendChild(div);
                        $("#errors").css("overflow", "auto");
                        $("#errors").css("word-break", "break-all");
                        $("#errors").css("word-wrap", "break-word")
                    }
                    //add all errors
                    for (let i = 0; i < json['errors'].length; i++) {
                        let e = document.createElement('p');
                        e.textContent = json['errors'][i]['type'] + " : " + json['errors'][i]['msg']
                        div.appendChild(e);
                    }
                }
                //check for new errors every 3 seconds
                setTimeout(errorTab, 3000);
            });
        }

    }
    else //if graph don't exist
    {
        document.getElementById("mynetwork").remove();
        let span = document.createElement('p');
        span.style.fontSize = "22px";
        span.style.paddingLeft = "1em";
        span.style.paddingTop = ".8em";
        span.textContent = "The specified graph does not exists .";
        document.body.appendChild(span);
        let element = document.createElement('a');
        element.text = "Click to access the list of graphs"
        element.href = "/web/og/display/ls.html"
        element.style.fontSize = "22px";
        element.style.paddingLeft = "1em";
        document.body.appendChild(element);
    }


}).fail( //if getjson fails
    function () {
            console.log("error");
            document.getElementById("mynetwork").remove();
            document.body.appendChild(document.createTextNode("The specified graph does not exists ."));
            let element = document.createElement('a');
            element.text = "Click to access the list of graphs"
            element.href = "/api/og/og.GraphService/listGraphs"
            document.body.appendChild(element);
    });

