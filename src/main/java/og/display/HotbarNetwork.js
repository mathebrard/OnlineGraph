class Hotbar {

    constructor() {
        this.mainContainer = $("<div></div>")
            .attr({
                id: "vis-network-hotbar"
            });
        this.main = $("<div></div>").css({
            display: "none",
			width: "50%"
        });
        this.mainChangeGraph = $("<div></div>").css({
            display: "none",
        });

        this.addPanelDragAndResize();
        // this.addPanelSortedNodes(props);
        // this.addPanelFonctionNodes (props);
        this.nbEntries = 0;
    }

    addStats(visnetwork){
    	var node = document.createElement("div");                 // Create a <li> node
    	node.id="stats"
    	/*node.css({
    		background-color: gray;
        });*/
	    node.textContent = "Number of vertices : " + visnetwork.body.data.nodes.length +" , number of edges : " + visnetwork.body.data.edges.length ;         // Create a text node
    	document.body.appendChild(node);
    	
    }
    addPanelDragAndResize() {
        let container = $("<div></div>");

        // creation du drag
        let draggerCreation = () => {
            let canMove = false;
            let dragger = $("<span></span>");
            let mouse = {}
            let pos = {}
            let draggingPanel = () => {
                if (canMove) {
                    this.mainContainer.css({
                        top: mouse.top - pos.top - 10,
                        left: mouse.left - pos.left - 10
                    });
                    requestAnimationFrame(draggingPanel);
                }
            }
            dragger
                .css({
                    display: "inline-block",
                    padding: "10px",
                    "background-color": "red",
                    width: "20px",
                    height: "20px"
                }).mousedown((ev) => {
                canMove = true;
                mouse.top = ev.clientY;
                mouse.left = ev.clientX;
                requestAnimationFrame(draggingPanel);
            });

            $(window).mousemove((ev) => {
                mouse.top = ev.clientY;
                mouse.left = ev.clientX;
            }).mouseup((ev) => {
                canMove = false;
            });
            this.mainContainer.mousedown((ev) => {
                pos.top = ev.offsetY;
                pos.left = ev.offsetX;
            });
            return dragger
        }

        let resizerC = $("<div id='nav-personaliz'>Personalize your graph</div>");
        let changerC = $("<div id='nav-personaliz'>Change your graph</div>");

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

                    this.mainChangeGraph.css({
                        display: "none"
                    })
                    this.main.css({
                        display: "inherit",
						width: "60%",
						float : "right"

                    })
					$( "#mynetwork" ).css( "width", "40%" );
					$( "#mynetwork" ).css( "width", "40%" )

                    changerC.css({
                        "background-color": "LightGray",
                    })
                    resizerC.css({
                        "background-color": "gray",
                    })
                    isClick = true;

                } else {
                    this.main.css({
                        display: "none"
                    })
					$( "#mynetwork" ).css( "width", "80%" );

                    resizerC.css({
                        "background-color": "LightGray",
                    })
                    isClick = false;
                }
            })
            return resizerC;
        }
        let changerCreation = () => {
            let isClick = false;
            changerC.css({
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
                        display: "none"
                    })
                    this.mainChangeGraph.css({
                        display: "inherit"
                    })
                    changerC.css({
                        "background-color": "gray",
                    })
                    resizerC.css({
                        "background-color": "LightGray",
                    })
                    isClick = true;

                } else {
                    this.mainChangeGraph.css({
                        display: "none"
                    })
                    changerC.css({
                        "background-color": "LightGray",
                    })
                    isClick = false;
                }
            })
            return changerC;
        }
        //    container.append(draggerCreation())
        container.append(resizerCreation());
        //container.append(changerCreation());
        this.mainContainer.append(container);
    }

    addPanelLinkPropertiesToFunction(attributes, propertiesNode,propertiesEdge) {
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

        function createSelect(propertiesNode,propertiesEdge, inputmin, inputmax, inputfx, input1, input2, attribut) {
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
			if (attribut.includes("Node")){
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
			}
			else if (attribut.includes("Edge")){
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

			if (attribut.includes("Node")){
				myselect.addClass("node-select")
			}
			else if (attribut.includes("Edge")){
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
			if (attribut.includes("Node")){
				$.each(propertiesNode, (key, value) => {
                myselect.append($("<option></option>")
                    .text(key)
                    .attr({
                        value: key
                    })
                )
            });
				}
			else if (attribut.includes("Edge")){
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
                    .append(createSelect(propertiesNode,propertiesEdge, inputmin, inputmax, inputfunction, input1, input2, attribut))
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

        //TODO size textboxinput
        Array.from(document.getElementsByClassName('parameters-values-borders')).forEach.call(input, function (el) {
            // Do stuff here

            el.addEventListener('input', resizeInput); // bind the "resizeInput" callback on "input" event
            resizeInput.call(el); // immediately call the function
        });


        function resizeInput() {
            this.style.width = this.value.length + "ch";
        }
    }

    addPanelChangeLabel(network, visnetwork, listPropertiesNode,listPropertiesEdge) {
        let conteneur = $("<div></div>");
        let selectNode = $("<select></select>").change((ev) => {
            var text = $(ev.target).find("option:selected").text(); //only time the find is required
            var name = $(ev.target).attr('name');
            network.getListNodes().forEach((node) => {
                if (node.params[text])
                    node.setLabel(visnetwork, node.params[text]);
            });
            visnetwork.redraw();
        });
        Object.keys(listPropertiesNode).forEach(function (k) {
            selectNode.append($("<option></option>").text(k));
        });
        selectNode.val("label");

        let selectEdge = $("<select></select>").change((ev) => {
            var text = $(ev.target).find("option:selected").text(); //only time the find is required
            var name = $(ev.target).attr('name');
            network.getListEdges().forEach((node) => {
                if (node.params[text])
                    node.setLabel(visnetwork, node.params[text]);
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

    addPanelLinkChangeGraph(attributes, visnetwork) {

        let table = $("<table></table>").append(
            $("<tr></tr>")
                .append($("<th></th>")
                    .text("Display property"))
                .append($("<th></th>")
                    .text("Graph element"))
        );

        Object.keys(attributes).forEach((attribut) => {

            let inputfunction;
            switch (attributes[attribut].type) {
                case "add" :
                    switch (attribut) {
                        case "Add node":
                            let input = $("<input class='parameters-values-borders'>")/*.click(function(){
                                visnetwork.body.data.nodes.add([
                                    {
                                        "id": this.value,
                                        "props": {}
                                    }]
                                )
                                console.log(visnetwork.body.data.nodes)
                            });*/



                            inputfunction = $("<span>").text("id : ").append(input);
                            break;
                        case "Add edge":
                            inputfunction = $("<span>").text("id : ").append($("<input class='parameters-values-borders'>"))
                                .append($("<span>").text("from : ")).append($("<input class='parameters-values-borders'>"))
                                .append($("<span>").text("to : ")).append($("<input class='parameters-values-borders'>"))
                            break;
                        default:
                    }

                    break;
                case "remove":
                    inputfunction = $("<span>").text("id : ").append($("<input class='parameters-values-borders'>"));
                    break;
            }

            let line = $("<tr></tr>")
                .append($("<td></td>")
                    .text(attribut))
                //                .append($("<td></td>").append($("<div class='select-style'></div>")
                //                    .append(createSelect(properties, inputmin, inputmax, inputfunction, input1, input2, attribut))
                //                ))
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

            table.append(line);
        });

        this.mainChangeGraph.append(table);
        let input = document.getElementsByClassName('parameters-values-borders'); // get the input element

        //TODO size textboxinput
        Array.from(document.getElementsByClassName('parameters-values-borders')).forEach.call(input, function (el) {
            // Do stuff here

            el.addEventListener('input', resizeInput); // bind the "resizeInput" callback on "input" event
            resizeInput.call(el); // immediately call the function
        });


        function resizeInput() {
            this.style.width = this.value.length + "ch";
        }
    }

addPanelChangeLabel(network, visnetwork, listPropertiesNode,listPropertiesEdge) {
        let conteneur = $("<div></div>");
        let selectNode = $("<select class = 'select-propertie-value node-select'></select>").change((ev) => {
            var text = $(ev.target).find("option:selected").text(); //only time the find is required
            var name = $(ev.target).attr('name');
            network.getListNodes().forEach((node) => {
                if (node.params[text])
                    node.setLabel(visnetwork, node.params[text]);
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
                if (node.params[text])
                    node.setLabel(visnetwork, node.params[text]);
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

    addPanelFonctionNodes(properties, toDoFunctionCallback) {
        function textbox(id, text, value, name) {
            return $("<div></div>")
                .append(
                    $("<label></label>")
                        .attr({
                            "id": "label-" + id,
                            "for": name
                        })
                        .text(text)
                )
                .append(
                    $("<input>").attr({
                        id: id,
                        name: "name",
                        value: value
                    })
                );
        }

        let fonction_sort_node = "20";

        let select = $("<select></select>").change(function (ev) {
            $("#valeur-min-select").val(properties[this.value].valeur_min);
            $("#valeur-max-select").val(properties[this.value].valeur_max);
            $("#fonction-select").val(properties[this.value].fonction);
        });

        let container = $("<div></div>");
        container.append(textbox("valeur-min-select", "Valeur Minimale", properties[Object.keys(properties)[0]].valeur_min, "valeur_min"));
        container.append(textbox("valeur-max-select", "Valeur Maximale", properties[Object.keys(properties)[0]].valeur_max, "valeur_max"));
        container.append($("<div></div>")
            .append(
                $("<label></label>")
                    .attr({
                        "id": "label-fonction-select"
                    })
                    .text("Fonction d'affichage")
            )
            .append($("<input>")
                .attr({
                    type: "text",
                    id: "fonction-select",
                    value: fonction_sort_node
                })
            ).append($("<button></button>")
                .text("Valider")
                .click((ev) => {
                    properties[select.val()].fonction = $("#fonction-select").val();
                    toDoFunctionCallback(
                        select.val(),
                        properties[select.val()].fonction);
                })
            )
        );

        $.each(properties, (key, value) => {
            properties[key].fonction = fonction_sort_node;
            select.append($("<option></option>")
                .text(key)
            )
        });

        this.main.append($("<div></div>")
            .append(select)
            .append(container)
        );
    }

    // fonction rajoutant un panel permettant de faire une sélection d'une propriété
    // et fonction de cette propriété, réajuste le graphique
    addPanelSortedNodes(properties) {
        function createTextBox(id, min, max, dft, name, textLabel, todo) {
            if (min >= max) {
                min = max - 1;
            }
            let span = $("<span></span>")
                .text(dft)
                .attr({
                    "id": "span-" + id
                });
            let input = $("<input>").attr({
                "type": "range",
                "min": min,
                "max": max,
                "step": 1,
                "value": dft,
                "name": name,
                "id": id
            }).on("input", (ev) => {
                span.text(input.val());
                todo(input.val());
            });
            return $("<div></div>")
                .append(
                    $("<label></label>")
                        .text(textLabel)
                        .attr({"for": name})
                )
                .append(input)
                .append(span);

        }

        let select = $("<select></select>");
        $.each(properties, (key, value) => {
            select.append($("<option></option>")
                .text(value.name)
            )
        });

        this.main.append($("<div></div>")
            .append(select)
            .append(createTextBox("min-selection-idawi", 0, 100, 0, "minSelect", "Valeur minimale", (val) => {
                $("#max-selection-idawi").attr({
                    min: val
                });
                $("#span-max-selection-idawi").text($("#max-selection-idawi").val());
            }))
            .append(createTextBox("max-selection-idawi", 0, 100, 10, "maxSelect", "Valeur maximale", (val) => {

            }))
        );
    }

    addEntry(label, todo) {
        this.main.append($("<div></div>")
            .append($("<input>")
                .attr({
                    type: "radio",
                    id: "radio-hotbar-" + this.nbEntries,
                    name: "hotbar",
                    value: "single"
                })
                .prop({
                    checked: (this.nbEntries == 0)
                })
            )
            .append($("<label></label>")
                .text(label)
                .attr({
                    for: "radio-hotbar-" + this.nbEntries
                })
            )
            .change((e) => {
                todo(e);
            })
        )
        this.nbEntries++;
    }

    addSelectorBackgroundColor() {
        let inputColor = $("<input>")
            .attr({
                type: "color",
                value: "#f0f0f0",
            })
            .change(function () {
                $("div.vis-network").css({
                    "background-color": this.value
                });
            });
        let selectorBgColor = $("<div></div>")
            .append($("<label></label>")
                .text("Sélection de la couleur de background")
            )
            .append(inputColor);
        this.main.append(selectorBgColor);
    }

    generateHTML() {
        return this.mainContainer.append(this.main) && this.mainContainer.append(this.mainChangeGraph);
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