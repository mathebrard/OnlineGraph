class Node {

    constructor(id,params,defaultparams) {
        this.id = id;
        this.label = undefined;
        this.messageLabel = undefined;
        this.params = params;
        this.contacts = [];
        this.colorbg = "#FFFFFF";
        this.colorborder = "#648FC9";
        this.listPropertiesToDraw = [];
        this.dftColorbg = "#97c2fc";
        this.dftColorborder = "#648FC9";
        this.size = 25;
        this.hidden = false;
        this.shape = "ellipse";
        this.borderWidth = 2;
        this.mass = 1;
        this.label = undefined
        this.defaultparams = {}
    }


    processParams(visnetwork, network) {
        let currentNode = this;
        Object.keys(this.params).forEach(function (key) {
            network.allProps.push(key)
            if (key == 'background color') {
                currentNode.setColor(visnetwork, currentNode.params[key]);
            } else if (key == 'color.border') {
                currentNode.setBorderColor(visnetwork, currentNode.params[key]);
            } else if (key == 'image') {
                currentNode.setImage(visnetwork, currentNode.params[key]);
            } else if (key == 'hidden') {
                currentNode.setHidden(visnetwork, currentNode.params[key]);
            } else if (key == 'borderWidth') {
                currentNode.setBorderWitdh(visnetwork, currentNode.params[key]);
            } else if (key == 'mass') {
                currentNode.setMass(visnetwork, currentNode.params[key]);
            } else if (key == 'label') {
                currentNode.setLabel(visnetwork, currentNode.params[key]);
            } else if (key == 'shape') {
                currentNode.setShape(visnetwork, currentNode.params[key]);
            } else if (key == 'size') {
                currentNode.setSize(visnetwork, currentNode.params[key]);
            } else {
                network.unknowProps.push(key);
            }
        })
    }
    processDefaultParams(visnetwork, network,props) {
        let currentNode = this;
        Object.keys(props).forEach(function (key) {
            network.allProps.push(key)
            console.log(props)
            console.log(currentNode.defaultparams)
            if (key.includes("vertex"))
                currentNode.defaultparams[key]=props[key]
            if (key == 'default vertex background color') {
                currentNode.setColor(visnetwork, props[key]);
            } else if (key == 'default vertex color.border') {
                currentNode.setBorderColor(visnetwork, props[key]);
            } else if (key == 'default vertex hidden') {
                currentNode.setHidden(visnetwork, props[key]);
            } else if (key == 'default vertex borderWidth') {
                currentNode.setBorderWitdh(visnetwork, props[key]);
            } else if (key == 'default vertex mass') {
                currentNode.setMass(visnetwork, props[key]);
            } else if (key == 'default vertex label') {
                currentNode.setLabel(visnetwork, props[key]);
            } else if (key == 'default vertex shape') {
                currentNode.setShape(visnetwork, props[key]);
            } else if (key == 'default vertex image') {
                currentNode.setImage(visnetwork, props[key]);
            } else if (key == 'default vertex size') {
                currentNode.setSize(visnetwork, props[key]);
            } else {
                network.unknowProps.push(key);
            }
        })
    }

    calcRandomColorByID() {
        let hashCode = function (s) {
            var h = 0, i = s.length;
            while (i >= 0)
                h = (h << 5) - h + (s.charCodeAt(--i) | 0);
            return h;
        };
        let hashId = hashCode(this.id.toString() + this.label);

        let c = {
            r: (hashId & 255).toString(16),
            g: ((hashId >> 8) & 255).toString(16),
            b: ((hashId >> 16) & 255).toString(16),
            getColor: function () {
                return "#" + (this.r.length == 1 ? "0" + this.r : this.r) +
                    (this.g.length == 1 ? "0" + this.g : this.g) +
                    (this.b.length == 1 ? "0" + this.b : this.b);
            }
        }

        return c.getColor();
    }


    setColor(visnetwork, colorbg, colorborder) {
        //this.colorbg = colorbg;
        //this.colorborder = colorborder;
        // console.log("hellloooo" , this.id, colorbg, colorborder);
        //setColorNodeNetwork (visnetwork, this.id, this.colorbg, this.colorborder);
        this.setBackgroundColor(visnetwork, colorbg);
        this.setBorderColor(visnetwork, colorborder);
    }

    setDefaultColor(visnetwork) {
        this.colorbg = /*this.calcRandomColorByID()*/"#FFFFFF";
        this.colorborder = "#648FC9";

        this.setBackgroundColor(visnetwork, this.colorbg);
        this.setBorderColor(visnetwork, this.colorborder);
    }

    setDefaultBackgroundColor(visnetwork) {
        this.colorbg = this.calcRandomColorByID();
        visnetwork.body.data.nodes.updateOnly({
            id: this.id,
            color: {
                background: this.colorbg
            }
        });
    }

    setDefaultBorderColor(visnetwork) {
        this.colorborder = this.dftColorborder;
        visnetwork.body.data.nodes.updateOnly({
            id: this.id,
            color: {
                border: this.colorborder
            }
        });
    }

    setSize(visnetwork, size) {
        let unsizableShapes = ["image", "circularImage", "diamond", "dot", "star", "triangle", "triangleDown", "hexagon", "square", "icon"];
        this.size = parseInt(size);
        console.log("setsize" + size);
        try {
            if (!(this.shape in unsizableShapes)) {
                visnetwork.body.data.nodes.updateOnly({
                    id: this.id,
                    scaling: {
                        min: this.size,
                        max: this.size,
                        label: {
                            enabled: true,
                            min: this.size,
                            max: this.size
                        }
                    },
                    value :1
                });
            } else {
                visnetwork.body.data.nodes.updateOnly({
                    id: this.id,
                    size: this.size
                });
            }       	
        } 
        catch (error) {
        	  console.log(error);
        }

    }

    setImage(visnetwork, image) {
        this.image = image;
        try {

            visnetwork.body.data.nodes.updateOnly({
                id: this.id,
                shape:image,
                image: this.image
            });	
        } 
        catch (error) {
        	  console.log(error);
        }

    }

    setHidden(visnetwork, hidden) {
        this.hidden = (hidden === 'true'); //support hidden as a string
        try {
            visnetwork.body.data.nodes.updateOnly({
                id: this.id,
                hidden: this.hidden
            });	
        } 
        catch (error) {
        	  console.log(error);
        }


    }

    setShape(visnetwork, shape) {
        this.shape = shape;
        try {

            visnetwork.body.data.nodes.updateOnly({
                id: this.id,
                shape: this.shape
            });   	
        } 
        catch (error) {
        	  console.log(error);
        }

    }

    setBorderWitdh(visnetwork, borderWidth) {
        this.borderWidth = borderWidth;
        try {
            visnetwork.body.data.nodes.updateOnly({
                id: this.id,
                borderWidth: this.borderWidth
            });
        } 
        catch (error) {
        	  console.log(error);
        }


    }

    setMass(visnetwork, mass) {
        this.mass = mass;
        try {
            visnetwork.body.data.nodes.updateOnly({
                id: this.id,
                mass: this.mass
            });   	
        } 
        catch (error) {
        	  console.log(error);
        }

    }

    setLabel(visnetwork, label) {
        this.label = label;
        try {
            visnetwork.body.data.nodes.updateOnly({
                id: this.id,
                label: this.label
            });        	
        } 
        catch (error) {
        	  console.log(error);
        }

    }


    setBackgroundColor(visnetwork, color) {
        try {
            if (color != "#000000") {

                this.colorbg = color;
                visnetwork.body.data.nodes.updateOnly({
                    id: this.id,
                    color: {
                        background: color
                    }
                });
            }      	
        } 
        catch (error) {
        	  console.log(error);
        }


    }

    setBorderColor(visnetwork, color) {
        this.colorborder = color;
        try {
            visnetwork.body.data.nodes.updateOnly({
                id: this.id,
                color: {
                    border: color
                }
            });        	
        } 
        catch (error) {
        	  console.log(error)
        }

    }

    linkTo(node) {
        if (this.contacts.includes(node)) {
            return false;
        }
        this.contacts.push(node);
    }

    getNode() {
        return {
            id: this.id,
            label: this.label,
            value: 20
        }
    }

    getContacts() {
        let res = [];
        this.contacts.forEach((contact) => {
            res.push({
                from: this.id,
                to: contact.id,
                arrows: {
                    to: {
                        type: "arrow",
                        enabled: true
                    }
                }
            });
        });
        return res;
    }

    drawInformation(ctx, x, y, scale, color = "black") {
        ctx.save();
        ctx.fillStyle = color;
        var visibleFontSize = 16 * scale;

        // console.log(scale, visibleFontSize);
        (visibleFontSize > 25) ? visibleFontSize = 25 / scale : visibleFontSize = 16;
        ctx.font = visibleFontSize + "px Arial";

        ctx.textBaseline = 'middle';
        ctx.textAlign = "center";
        ctx.fillText(this.messageLabel, x, y);

        this.listPropertiesToDraw.forEach((property) => {
            ctx.font = "italic" + visibleFontSize * 0.7 + "px Arial";
        });
        ctx.restore();
    }
}

class Link {
    constructor(nfrom, nto, id, params, defaultparams) {
        this.from = nfrom;
        this.to = nto;
        this.id = id;
        this.params = params;
        this.arrowtype = "arrow";
        this.dashes = false;
        this.directed = false;
        this.color = '#848484';
        this.arrowImage = undefined;
        this.mass = 1;
        this.label = undefined;
        this.scale = 1;
        this.width = 1;
        this.defaultparams = defaultparams;
    }

    processParams(visnetwork, network) {
        let currentEdge = this;
        Object.keys(this.params).forEach(function (key) {
            network.allProps.push(key);

            if (key == 'arrow type') {
                currentEdge.setArrowType(visnetwork, currentEdge.params[key]);
            } else if (key == 'dashes') {
                currentEdge.setDashes(visnetwork, currentEdge.params[key]);
            } else if (key == 'directed') {
                currentEdge.setDirected(visnetwork, currentEdge.params[key]);
            } else if (key == 'color') {
                currentEdge.setColor(visnetwork, currentEdge.params[key]);
            } else if (key == 'arrow image') {
                currentEdge.setArrowImage(visnetwork, currentEdge.params[key]);
            } /*else if (key == 'mass') {
                currentEdge.setMass(visnetwork, currentEdge.params[key]);
            }*/ else if (key == 'label') {
                currentEdge.setLabel(visnetwork, currentEdge.params[key]);
            } else if (key == 'arrow scale') {
                currentEdge.setScale(visnetwork, currentEdge.params[key]);
            } else if (key == 'width') {
                currentEdge.setWidth(visnetwork, currentEdge.params[key]);
            } else {
                network.unknowProps.push(key);
            }
        })
    }
    processDefaultParams(visnetwork, network,props) {
        let currentEdge = this;
        Object.keys(props).forEach(function (key) {
            //network.allProps.push(key)
            if (key.includes("edge"))
            	currentEdge.defaultparams[key]=props[key];
            if (key == 'default edge arrow type') {
                currentEdge.setArrowType(visnetwork, props[key]);
            } else if (key == 'default edge dashes') {
                currentEdge.setDashes(visnetwork, props[key]);
            } else if (key == 'default edge directed') {
                currentEdge.setDirected(visnetwork, props[key]);
            } else if (key == 'default edge color') {
                currentEdge.setColor(visnetwork, props[key]);
            } else if (key == 'default edge arrow image') {
                currentEdge.setArrowImage(visnetwork, props[key]);
            } /*else if (key == 'mass') {
                currentEdge.setMass(visnetwork, currentEdge.params[key]);
            }*/ else if (key == 'default edge label') {
                currentEdge.setLabel(visnetwork, props[key]);
            } else if (key == 'default edge arrow scale') {
                currentEdge.setScale(visnetwork, props[key]);
            } else if (key == 'default edge width') {
                currentEdge.setWidth(visnetwork, props[key]);
            } else {
                //network.unknowProps.push(key);
            }
        })
    }

    setArrowType(visnetwork, arrowtype) {
        this.arrowtype = arrowtype;
        try {
            visnetwork.body.data.edges.updateOnly({
                id: this.id,
                arrows: {
                    to: {
                        type: this.arrowtype
                    }
                }
            });
        } 
        catch (error) {
        	  console.log(error);
        }

    }

    setDashes(visnetwork, dashes) {
        this.dashes = dashes;
        try {

            if (typeof this.dashes === 'boolean') {
                visnetwork.body.data.edges.updateOnly({
                    id: this.id,
                    dashes: (this.dashes != false) //TODO refaire
                });
            } else if (this.dashes instanceof Array) {
                visnetwork.body.data.edges.updateOnly({
                    id: this.id,
                    dashes: this.dashes //TODO refaire
                });
            }
        } 
        catch (error) {
        	  console.log(error);
        }

    }

    setDirected(visnetwork, directed) {
        this.directed = directed;
        try {
            visnetwork.body.data.edges.updateOnly({
                id: this.id,
                arrows: {
                    to: {
                        enabled: (this.directed === 'true')
                    }
                }
            });
        } 
        catch (error) {
        	  console.log(error);
        }

    }

    setColor(visnetwork, color) {
        this.color = color;
        try {
            visnetwork.body.data.edges.updateOnly({
                id: this.id,
                color: this.color
            });
        } 
        catch (error) {
        	  console.log(error);
        }

    }

    setArrowImage(visnetwork, arrowImage) {
        this.arrowImage = arrowImage;
        try {
            visnetwork.body.data.edges.updateOnly({
                id: this.id,
                arrows: {
                    to: {
                        src: this.arrowImage,
                        type: 'image',
                        imageHeight: this.width * 40,
                        imageWidth: this.width * 40
                    }
                }
            });
        } 
        catch (error) {
        	  console.log(error);
        }

    }

    /*setMass(visnetwork, mass) {
        this.mass = mass;
        visnetwork.body.data.edges.updateOnly({
            id: this.id,
            mass: this.mass
        });
    }*/

    setLabel(visnetwork, label) {
        this.label = label;
        try {
            visnetwork.body.data.edges.updateOnly({
                id: this.id,
                label: this.label
            });
        } 
        catch (error) {
        	  console.log(error);
        }

    }

    setScale(visnetwork, scale) {
        this.scale = scale;
        try {
            visnetwork.body.data.edges.updateOnly({
                id: this.id,
                scaling: {
                    min: this.scale,
                    max: this.scale,
                    customScalingFunction: function (min, max, total, value) {
                        if (max === min) {
                            return 0.5;
                        } else {
                            var scale = 1 / (max - min);
                            return Math.max(0, (value - min) * scale);
                        }
                    }
                },
                value: this.scale
            });	
        } 
        catch (error) {
        	  console.log(error);
        }

    }

    setWidth(visnetwork, width) {
        this.width = width;
        try {
            visnetwork.body.data.edges.updateOnly({
                id: this.id,
                width: this.width
            });
        } 
        catch (error) {
        	  console.log(error);
        }

    }
}

class Network {
    constructor() {
        this.listNodes = [];
        this.listEdges = [];
        this.visEdges = null;
        this.visNodes = null;
        this.unknowProps = [];
        this.allProps = [];
    }


    addNode(label, params) {
        let ID;
        //this.listNodes.length > 0 ? ID = this.listNodes[this.listNodes.length - 1].id + 1 : ID = 0;
        ID = label;
        let newNode = new Node(ID, params);
        //newNode.processParams(this)
        this.listNodes.push(newNode);
        return newNode;
    }

    addVertex(visNetwork,defaultparams, params) {
        let ID;
        ID = params['id'];
        let newNode = new Node(ID, params, defaultparams);
        visNetwork.body.data.nodes.add([{id: ID, props: params}])
        this.listNodes.push(newNode);
        newNode.processDefaultParams(visNetwork,this,defaultparams)
        newNode.processParams(visNetwork,this,params)
        console.log("allProps")
        return newNode;
    }
    removeVertex(visNetwork, ID) {
        visNetwork.body.data.nodes.remove([ID])
        //this.listNodes.pop(newNode); //TODO POP node
        return true;
    }
    removeEdge(visNetwork, ID) {
        visNetwork.body.data.edges.remove([ID])
        //this.listNodes.pop(newNode); //TODO POP node
        return true;
    }
    addEdge(visNetwork,defaultparams, params) {
        let ID;
        ID = params['id'];
        let from = params['from'];
        let to = params['to'];
        let newNode = new Link(from,to,ID, params, defaultparams);
        visNetwork.body.data.edges.add([{id: ID,from : from,to : to, props: params}])
        this.listEdges.push(newNode);
        newNode.processDefaultParams(visNetwork,this,defaultparams)
        newNode.processParams(visNetwork,this,params)
        return newNode;
    }

    linkNode(from, to, idd, params) {
        from.linkTo(to);
        let newLink = new Link(from, to, idd, idd, params);
        this.listEdges.push(newLink);
    }

    getNode(ID) {
        let res = null;
        this.listNodes.forEach((node) => {
            if (node.id == ID) {
                res = node;
                return;
            }
        })
        return res;
    }

    getNodeFromLabel(label) {
        let res = null;
        this.listNodes.forEach((node) => {
            if (node.label == label) {
                res = node;
                return;
            }
        })
        return res;
    }

    getIdFromLabel(name) {
        let res = null;
        this.listNodes.forEach((node) => {
            if (node.label == name) {
                res = node;
                return;
            }
        });
        return res;
    }

    getData() {
        let data = [];
        let link = [];
        this.listNodes.forEach((node) => {
            data.push(node.getNode());
            if (node.getContacts().length > 0) {
                link = link.concat(node.getContacts());
            }
        });

        this.visNodes = new vis.DataSet(data);
        this.visEdges = new vis.DataSet(link);
        return {
            nodes: this.visNodes,
            edges: this.visEdges
        }
    }

    getListNodes() {
        return this.listNodes;
    }

    getListEdges() {
        return this.listEdges;
    }

    getNodes() {
        return this.visNodes;
    }

    getEdges() {
        return this.visEdges;
    }
}

class EventVisnetwork {
    constructor(network, visnetwork, currentNode) {
        this.network = network;
        this.visnetwork = visnetwork;
        this.currentNode = currentNode;
        this.addEventSelectNode();
        this.addEventSelectLink();
        /*
                this.addEvent ("deselectNode", () => {

                });

                this.addEvent("deselectEdge", () => {

                });
         */
    }

    addEventSelectNode() {
        this.addEvent("selectNode", (params) => {
            this.currentNode.evSelectNode(params);
        });
    }

    addEventSelectLink() {
        this.addEvent("selectEdge", (params) => {

        });
    }

    addEvent(nameEvent, todo) {
        this.visnetwork.addEventListener(nameEvent, (params) => {
            console.log("EVENT " + nameEvent, params);
            todo(params);
        });
    }
}

function setColorNodeNetwork(network, idNode, backgroundColor, borderColor) {
    try {
        let node = network.body.nodes[idNode];
        let lastColorBorder = node.options.color.border;
        let lastColorBackground = node.options.color.background;
        node.options.color = {
            border: lastColorBorder,            // ces deux paramètres gèrent les couleurs des liens dépendants du noeud
            background: lastColorBackground,

            highlight: {
                border: borderColor,
                background: backgroundColor,
            },
            hover: {}
        }
    } catch (e) {
        console.log(e);
    }
}

function generateNetwork(nodes, edges) {

    let network = new Network();
    /*// création du graphique network à partir du JSON
    nodes.knownComponents.forEach((n) => {
        network.addNode (n.friendlyName, n);
    });

    nodes.knownComponents.forEach((n) => {
        n.neighbors.forEach((voisin) => {
            network.linkNode (network.getIdFromLabel (n.friendlyName), network.getIdFromLabel (voisin));
        });
    });*/
    nodes.forEach((n) => {
        network.addNode(n.id, n['props']);
    });
    edges.forEach((e) => {
        network.linkNode(network.getNode(e['from']), network.getNode(e['to']), e.id, e['props']);
    });
    return network;
}

// attention, main doit être un composant html natif, pas un composant jQuery
function createNetwork(container, network, options = {}, width = 600, height = 600) {
    let main = document.createElement("div");
    main.style.width = width + "px";
    main.style.height = height + "px";
    container.appendChild(main);

    let visnetwork = new vis.Network(main, network.getData(), options);

    visnetwork.on("afterDrawing", function (ctx) {
        network.getListNodes().forEach((node) => {
            let nodePosition = visnetwork.getPositions([node.id]);
            let colorGenerator = {
                r: parseInt(node.colorbg.substr(1, 2), 16) > 120 ? "00" : "ff",
                g: parseInt(node.colorbg.substr(3, 2), 16) > 120 ? "00" : "ff",
                b: parseInt(node.colorbg.substr(5, 2), 16) > 120 ? "00" : "ff",

                compute: function () {
                    if (this.r == "00" || this.g == "00" || this.b == "00") {
                        return "#000000";
                    }
                    return "#ffffff";
                }
            }

            node.drawInformation(ctx, nodePosition[node.id].x, nodePosition[node.id].y, visnetwork.getScale(), colorGenerator.compute());
        });
    });

    return visnetwork;
}