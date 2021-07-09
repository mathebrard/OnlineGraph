class Hotbar {

    constructor(visnetwork) {
        this.mainContainer = $("<div></div>")
            .attr({
                id: "vis-network-hotbar"
            });
        this.main = $("<div></div>").css({
            display: "none",
            width: "50%"
        });
        this.addButtons(visnetwork);
        this.nbEntries = 0;
    }

	//add nb of nodes and edges
    addStats(visnetwork, network) {
        var node = document.createElement("div");
        node.id = "stats"
        /*node.css({
            background-color: gray;
        });*/
        node.textContent = "Number of vertices : " + visnetwork.body.data.nodes.length + " , number of edges : " + network.nbEdges + " , number of arcs : " + network.nbArcs;
        node.style.paddingLeft = "1em";
        // Create a text node
        document.body.appendChild(node);

    }

	//add upper buttons
    addButtons(visnetwork) {
        let container = $("<div></div>");

        let resizerC = $("<div id='nav-personaliz'>Personalize your graph</div>");
        let fitgraph = $("<div id='nav-personaliz-fitgraph'>Fit graph in window</div>");
        let github = $("<div id='nav-personaliz-github'>Go to Github page</div>");

        let githubCreation = () => {
            github.css({
                display: "inline-block",
                padding: "10px",
                "background-color": "LightGray",
                width: "fit-content",
                height: "fit-content",
                border: "solid",
                cursor: "pointer"
            }).mouseup((ev) => {
                let url = "https://github.com/lhogie/OnlineGraph"
                window.open(url, '_blank');
            })

            return github;
        }
        let fitgraphCreation = () => {
            fitgraph.css({
                display: "inline-block",
                padding: "10px",
                "background-color": "LightGray",
                width: "fit-content",
                height: "fit-content",
                border: "solid",
                cursor: "pointer"
            }).mouseup((ev) => {
            visnetwork.fit({
                //minZoomLevel: 0,
                animation: {
                    duration: 500,
                    easingFunction: "linear"
                }
            });
            })

            return fitgraph;
        }

        let resizerCreation = () => {
            let isClick = false;
            resizerC.css({
                display: "inline-block",
                padding: "10px",
                "background-color": "LightGray",
                width: "fit-content",
                height: "fit-content",
                border: "solid",
                cursor: "pointer"
            }).mouseup((ev) => {
                if (isClick == false) {

                    this.main.css({
                        display: "inherit",
                        width: "52%",
                        float: "right",
                        paddingLeft: "1em"

                    });
                    $("#mynetwork").css("width", "100%");
                    $("#stats").css("width", "48%");

                    resizerC.css({
                        "background-color": "gray",
                    })
                    isClick = true;

                } else {
                    this.main.css({
                        display: "none"
                    })
                    $("#mynetwork").css("width", "75%");
                    $("#stats").css("width", "75%");

                    resizerC.css({
                        "background-color": "LightGray",
                    })
                    isClick = false;
                }
            })
            return resizerC;
        }
        container.append(resizerCreation());
        container.append(fitgraphCreation());
        container.append(githubCreation());
        this.mainContainer.append(container);
    }

	//create and add personalization menu
    addPanelLinkPropertiesToFunction(attributes, propertiesNode, propertiesEdge) {
        function applyColorModification(attribut, myselect, inputmin, inputmax, input1, input2) {
            let min = parseInt(inputmin.val()), max = parseInt(inputmax.val());
            if (!isNaN(min) && !isNaN(max)) {
                let c1 = hexToRgb(input1.val()), c2 = hexToRgb(input2.val());

                let aff = (x1, y1, x2, y2) => {
                    let a = (y2 - y1) / (x2 - x1);
                    return {
                        a: (y2 - y1) / (x2 - x1),
                        b: y1 - a * x1,
                        calc: function (x) {
                            return this.a * x + this.b;
                        }
                    }
                }

                let colorRegression = {
                    r: aff(min, c1.r, max, c2.r),
                    g: aff(min, c1.g, max, c2.g),
                    b: aff(min, c1.b, max, c2.b),
                    parse: (x) => {
                        let val = (x | 0).toString(16);
                        (val.length == 1) ? val = "0" + val : val = val;
                        return val
                    },
                    getcolor: function (x) {
                        return "#" + this.parse(this.r.calc(x)) + this.parse(this.g.calc(x)) + this.parse(this.b.calc(x))
                    }
                }
                attribut.functionApply(myselect.currentValue, colorRegression);
            }
        }

        function createSelect(propertiesNode, propertiesEdge, inputmin, inputmax, inputfx, input1, input2, attribut) {
            let myselect = $("<select></select>")
                .addClass("select-propertie-value")
                .change(function (ev) {
                    let valeur = this.value;
                    if (myselect.currentValue != undefined) {
                        $(".select-propertie-value").each((index, select) => {
                            $(select.options).each((index, option) => {
                                if (option.value == myselect.currentValue) {
                                    $(option).css({display: "inherit"});
                                }
                            });
                        });
                    }
                    myselect.currentValue = valeur;
                    if (attribut.includes("Node")) {
                        if (valeur != "") {
                            inputmin.val(propertiesNode[valeur].valeur_min);
                            inputmax.val(propertiesNode[valeur].valeur_max);
                        } else {
                            inputmin.val("");
                            inputmax.val("");

                            if (attributes[attribut].type == "function" && valeur == "") {
                                attributes[attribut].functionApply(myselect.currentValue, new Function(attributes[attribut].dftfunction));
                            }
                            if (attributes[attribut].type == "color" && valeur == "") {
                                attributes[attribut].functionApply(myselect.currentValue);
                            }
                        }
                    } else if (attribut.includes("Edge")) {
                        if (valeur != "") {
                            inputmin.val(propertiesEdge[valeur].valeur_min);
                            inputmax.val(propertiesEdge[valeur].valeur_max);
                        } else {
                            inputmin.val("");
                            inputmax.val("");

                            if (attributes[attribut].type == "function" && valeur == "") {
                                attributes[attribut].functionApply(myselect.currentValue, new Function(attributes[attribut].dftfunction));
                            }
                            if (attributes[attribut].type == "color" && valeur == "") {
                                attributes[attribut].functionApply(myselect.currentValue);
                            }
                        }
                    }

                });

            if (attribut.includes("Node")) {
                myselect.addClass("node-select")
            } else if (attribut.includes("Edge")) {
                myselect.addClass("edge-select")
            }
            if (attributes[attribut].type == "color") {
                inputfx.append($("<button></button>")
                    .text("Appliquer")
                    .click(() => {
                        applyColorModification(attributes[attribut], myselect, inputmin, inputmax, input1, input2);
                    })
                )
            }

            if (inputfx != undefined) {
                inputfx.keypress((ev) => {
                    if (ev.keyCode == 13) {
                        attributes[attribut].functionApply(myselect.currentValue, inputfx.val());
                    }
                });
            }
            myselect.append($("<option></option>").text(""));
            if (attribut.includes("Node")) {
                $.each(propertiesNode, (key, value) => {
                    myselect.append($("<option></option>")
                        .text(key)
                        .attr({
                            value: key
                        })
                    )
                });
            } else if (attribut.includes("Edge")) {
                $.each(propertiesEdge, (key, value) => {
                    myselect.append($("<option></option>")
                        .text(key)
                        .attr({
                            value: key
                        })
                    )
                });
            }

            return myselect;
        }

        let table = $("<table></table>").append(
            $("<tr></tr>")
                .append($("<th></th>")
                    .text("Display property"))
                .append($("<th></th>")
                    .text("Graph property"))
                .append($("<th></th>")
                    .text("f(x)"))
        );

        Object.keys(attributes).forEach((attribut) => {
            let inputmin = $("<input>").attr({readonly: false, class: "parameters-values-borders"});
            let inputmax = $("<input>").attr({readonly: false, class: "parameters-values-borders"});

            let input1 = $("<input>").attr({type: "color", class: "form-control-color"});
            let input2 = $("<input>").attr({type: "color", class: "form-control-color"});

            let inputfunction;
            switch (attributes[attribut].type) {
                case "function" :
                    inputfunction = $("<input class='parameters-values-borders'>");
                    break;
                case "color":
                    let color = "black";
                    switch (attribut) {
                        case "Node color":
                            color = new Node(0, "", {}).dftColorbg;
                            break;
                        case "Border color":
                            color = new Node(0, "", {}).dftColorborder;
                            break;
                        default:
                    }
                    input1.attr({value: color});
                    input2.attr({value: color});

                    inputfunction = $("<div></div>")
                        .append($("<span></span>").text("min : ")).append(inputmin).append(input1)
                        .append($("<span></span>").text(" max : ")).append(inputmax).append(input2);
                    break;
                case "label" :
                    inputfunction = $("<input>");
                    break;
            }

            if (attributes[attribut].type == "function" || attributes[attribut].type == "label") {
                inputfunction.val(attributes[attribut].function);
            }

            let line = $("<tr></tr>")
                .append($("<td></td>")
                    .text(attribut))
                .append($("<td></td>").append($("<div class='select-style'></div>")
                    .append(createSelect(propertiesNode, propertiesEdge, inputmin, inputmax, inputfunction, input1, input2, attribut))
                ))
                /*.append($("<td></td>")
                    .append($("<table></table>")
                        .append($("<tr></tr>")
                            .append($("<td></td>").text("min"))
                            .append($("<div class='input-style'></div>").append($("<td></td>").append(inputmin)))

                            .append($("<td></td>").text("max"))
                            .append($("<div class='input-style'></div>").append($("<td></td>").append(inputmax)))
                        )
                ))*/
                .append($("<td></td>")
                    .append(inputfunction));

            table.append(line)
        });

        this.main.append(table);
        let input = document.getElementsByClassName('parameters-values-borders'); // get the input element

        Array.from(document.getElementsByClassName('parameters-values-borders')).forEach.call(input, function (el) {

            el.addEventListener('input', resizeInput); // bind the "resizeInput" callback on "input" event
            resizeInput.call(el); // immediately call the function
        });


        function resizeInput() {
            this.style.width = this.value.length + "ch";
        }
    }

    addPanelChangeLabel(network, visnetwork, listPropertiesNode, listPropertiesEdge) {
        let conteneur = $("<div></div>");
        let selectNode = $("<select class = 'select-propertie-value node-select'></select>").change((ev) => {
            var text = $(ev.target).find("option:selected").text(); //only time the find is required
            var name = $(ev.target).attr('name');
            network.getListNodes().forEach((node) => {
				try{
					console.log(text)
	                if (text=="id")
	                    node.setLabel(visnetwork, node.id);
	                else if (node.params[text])
	                    node.setLabel(visnetwork, node.params[text]);
				}
				catch(error){
					console.log(error)
				}
            });
            visnetwork.redraw();
        });
        Object.keys(listPropertiesNode).forEach(function (k) {
            selectNode.append($("<option></option>").text(k));
        });
        selectNode.val("label");

        let selectEdge = $("<select class = 'select-propertie-value edge-select'></select>").change((ev) => {
            var text = $(ev.target).find("option:selected").text(); //only time the find is required
            var name = $(ev.target).attr('name');
            network.getListEdges().forEach((node) => {
				try{
	                if (node.params[text])
	                    node.setLabel(visnetwork, node.params[text]);
				}
				catch(error){
					console.log(error)
				}

            });
            visnetwork.redraw();
        });
        Object.keys(listPropertiesEdge).forEach(function (k) {
            selectEdge.append($("<option></option>").text(k));
        });
        selectEdge.val("label");

        conteneur.append($("<table></table>")
            .append($("<tr></tr>")
                .append($("<td></td>").text("Node label"))
                .append($("<td></td>").append(selectNode))
                .append($("<td></td>").text("Edge label"))
                .append($("<td></td>").append(selectEdge))
            )
        );
        this.main.append(conteneur)
    }

    generateHTML() {
        return this.mainContainer.append(this.main);
    }

}

function hexToRgb(hex) {
    var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
    return {
        r: parseInt(result[1], 16),
        g: parseInt(result[2], 16),
        b: parseInt(result[3], 16)
    };
}