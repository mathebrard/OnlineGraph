import { initGraph } from './graph3d/init.js';

let Graph;

var jqxhr = $.getJSON("./graph.json", function (data) {
    Graph = initGraph(data);
    console.log(Graph.scene());
});