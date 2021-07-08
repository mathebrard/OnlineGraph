class Node {

    constructor(id, params, defaultparams) {
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
        this.shape = "circle";
        this.borderWidth = 2;
        this.mass = 1;
        this.label = undefined
        this.defaultparams = {}
        this.labelColor = "#000000";
    }


    processParams(visnetwork, network) {
        let currentNode = this;
        Object.keys(this.params).forEach(function (key) {

            network.allPropsNodes.push(key)
            if (key == 'fillColor') {
                currentNode.setColor(visnetwork, currentNode.params[key]);
            } else if (key == 'borderColor') {
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
            } else if (key == 'labelColor') {
                currentNode.setLabelColor(visnetwork, currentNode.params[key]);
            } else {
                network.unknowProps.push(key);
            }
        })
    }

    processDefaultParams(visnetwork, network, props) {
        let currentNode = this;
        Object.keys(props).forEach(function (key) {
            //network.allProps.push(key)
            if (key.includes("vertex"))
                currentNode.defaultparams[key] = props[key]
            if (key == 'default vertex fillColor') {
                currentNode.setColor(visnetwork, props[key]);
            } else if (key == 'default vertex borderColor') {
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
            } else if (key == 'default vertex labelColor') {
                currentNode.setLabelColor(visnetwork, props[key]);
            } else {
                network.unknowProps.push(key);
            }
        })
    }


    setColor(visnetwork, colorbg, colorborder) {

        this.setBackgroundColor(visnetwork, colorbg);
        this.setBorderColor(visnetwork, colorborder);
    }

    setLabelColor(visnetwork, labelcolor) {
        this.labelColor = labelcolor;
        visnetwork.body.data.nodes.updateOnly({
            id: this.id,
            font: {
                color: this.labelColor
            }
        });
    }

    setSize(visnetwork, size) {
        let unsizableShapes = ["image", "circularImage", "diamond", "dot", "star", "triangle", "triangleDown", "hexagon", "square", "icon"];
        this.size = parseInt(size);
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
                    value: 1
                });
            } else {
                visnetwork.body.data.nodes.updateOnly({
                    id: this.id,
                    size: this.size
                });
            }
        } catch (error) {
            console.log(error);
        }

    }

    setImage(visnetwork, image) {
        this.image = image;
        try {

            visnetwork.body.data.nodes.updateOnly({
                id: this.id,
                shape: image,
                image: this.image
            });
        } catch (error) {
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
        } catch (error) {
            console.log(error);
        }


    }

    setShape(visnetwork, shape) {
        this.shape = shape;
        if (this.shape == "rectangle")
            this.shape = "box"
        try {

            visnetwork.body.data.nodes.updateOnly({
                id: this.id,
                shape: this.shape
            });
        } catch (error) {
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
        } catch (error) {
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
        } catch (error) {
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
        } catch (error) {
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
        } catch (error) {
            console.log(error + "color");
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
        } catch (error) {
            console.log(error + "color")
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
                    middle: {
                        type: "arrow",
                        enabled: true
                    }
                }
            });
        });
        return res;
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
            network.allPropsEdges.push(key);
            if (key == 'arrowShape') {
                currentEdge.setArrowType(visnetwork, currentEdge.params[key]);
            } else if (key == 'dashes') {
                currentEdge.setDashes(visnetwork, currentEdge.params[key]);
            } else if (key == 'directed') {
                currentEdge.setDirected(visnetwork, currentEdge.params[key]);
            } else if (key == 'color') {
                currentEdge.setColor(visnetwork, currentEdge.params[key]);
            } else if (key == 'arrowImage') {
                currentEdge.setArrowImage(visnetwork, currentEdge.params[key]);
            } /*else if (key == 'mass') {
                currentEdge.setMass(visnetwork, currentEdge.params[key]);
            }*/ else if (key == 'label') {
                currentEdge.setLabel(visnetwork, currentEdge.params[key]);
            } else if (key == 'arrowScale') {
                currentEdge.setScale(visnetwork, currentEdge.params[key]);
            } else if (key == 'width') {
                currentEdge.setWidth(visnetwork, currentEdge.params[key]);
            } else {
                network.unknowProps.push(key);
            }
        })
    }

    processDefaultParams(visnetwork, network, props) {
        let currentEdge = this;
        Object.keys(props).forEach(function (key) {
            if (key.includes("edge"))
                if (key == 'default edge arrowShape') {
                    currentEdge.setArrowType(visnetwork, props[key]);
                } else if (key == 'default edge dashes') {
                    currentEdge.setDashes(visnetwork, props[key]);
                } else if (key == 'default edge directed') {
                    currentEdge.setDirected(visnetwork, props[key]);
                } else if (key == 'default edge color') {
                    currentEdge.setColor(visnetwork, props[key]);
                } else if (key == 'default edge arrowImage') {
                    currentEdge.setArrowImage(visnetwork, props[key]);
                } /*else if (key == 'mass') {
                currentEdge.setMass(visnetwork, currentEdge.params[key]);
            }*/ else if (key == 'default edge label') {
                    currentEdge.setLabel(visnetwork, props[key]);
                } else if (key == 'default edge arrowScale') {
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
                    middle: {
                        type: this.arrowtype
                    }
                }
            });
        } catch (error) {
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
        } catch (error) {
            console.log(error);
        }

    }

    setDirected(visnetwork, directed) {
        this.directed = directed;

        try {
            visnetwork.body.data.edges.updateOnly({
                id: this.id,
                arrows: {
                    middle: {
                        enabled: ((this.directed + "") == 'true')
                    }
                }
            });
        } catch (error) {
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
        } catch (error) {
            console.log(error + "color");
        }

    }

    setArrowImage(visnetwork, arrowImage) {
        this.arrowImage = arrowImage;
        try {
            visnetwork.body.data.edges.updateOnly({
                id: this.id,
                arrows: {
                    middle: {
                        src: this.arrowImage,
                        type: 'image',
                        imageHeight: this.width * 40,
                        imageWidth: this.width * 40
                    }
                }
            });
        } catch (error) {
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
        } catch (error) {
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
        } catch (error) {
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
        } catch (error) {
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
        this.allPropsNodes = [];
        this.allPropsEdges = [];
        this.nbArcs = 0;
        this.nbEdges = 0;
    }


    addNode(label, params) {
        let ID;
        ID = label;
        let newNode = new Node(ID, params);
        this.listNodes.push(newNode);
        return newNode;
    }

    addVertex(visNetwork, defaultparams, params) {
        let ID;
        ID = params['id'];
        let newNode = new Node(ID, params, defaultparams);
        try {
            visNetwork.body.data.nodes.add([{id: ID, props: params}])

            this.listNodes.push(newNode);
            newNode.processDefaultParams(visNetwork, this, defaultparams)
            newNode.processParams(visNetwork, this, params)
        } catch (error) {
            console.log(error)
        }
        return newNode;
    }

    removeVertex(visNetwork, ID) {
        visNetwork.body.data.nodes.remove([ID])
        var removeIndex = this.listNodes.map(item => item.id).indexOf(ID);
        ~removeIndex && this.listNodes.splice(removeIndex, 1);
        return true;
    }

    removeEdge(visNetwork, ID) {
        visNetwork.body.data.edges.remove([ID])

        var removeIndex = this.listEdges.map(item => item.id).indexOf(ID);
        ~removeIndex && this.listEdges.splice(removeIndex, 1);

        return true;
    }

    addEdge(visNetwork, defaultparams, params) {
        let ID;
        ID = params['id'];
        let from = params['from'];
        let to = params['to'];
        let newNode = new Link(from, to, ID, params, defaultparams);
        try {
            visNetwork.body.data.edges.add([{id: ID, from: from, to: to, props: params}])
            this.listEdges.push(newNode);
            newNode.processDefaultParams(visNetwork, this, defaultparams)
            newNode.processParams(visNetwork, this, params)
        } catch (error) {
            console.log(error)
        }

        return newNode;
    }

    linkNode(from, to, idd, params, defaultparams) {
        try {
            from.linkTo(to);
            let newLink = new Link(from, to, idd, params, defaultparams);
            this.listEdges.push(newLink);
        } catch (e) {
            console.log(e)
        }
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

    getEdge(ID) {
        let res = null;
        this.listEdges.forEach((node) => {
            if (node.id == ID) {
                res = node;
                return;
            }
        })
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
}

function generateNetwork(nodes, edges) {

    let network = new Network();

    nodes.forEach((n) => {
        network.addNode(n.id, n['properties']);
    });
    edges.forEach((e) => {
        network.linkNode(network.getNode(e['from']), network.getNode(e['to']), e.id, e['properties'], e['properties']);
    });
    return network;
}