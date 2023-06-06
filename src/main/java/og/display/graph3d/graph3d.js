import { initGraph, updateGraph } from './init.js';
import { parseJasetoEventToJson } from './parser.js';


let graph;

const source = new EventSource(
  "http://localhost:8081/api////og.GraphService/get2/randomGraph?what=content"
);

// const source = new EventSource(
//     "http://localhost:6789/mocks/server_sse.php"
// );

source.addEventListener("message", (event) => {
    console.log(event);
    let jsonFormatted = parseJasetoEventToJson(event);

    if (!graph) {
        graph = initGraph(jsonFormatted);
    } else {
        updateGraph(graph, jsonFormatted);
    }
});