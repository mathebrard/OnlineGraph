# OnlineGraph
OnlineGraph is a ***cloud graph library***. 
It consists of an (HTTP) server which exposes a set of (Web) services for the ***storage***, the ***manipulation*** and the ***graphical*** rendering of graphs.

OnlineGraph is developped at Inria/I3S Computer Science Laboratory of Université Côte d'Azur. Its development team is composed of:
- Luc Hogie (project leader)
- Fedi Ghalloussi (Bachelor's degree intern)
- Antonin Lacomme (Master's degree intern)

## Installation
You don't need to install anything is you want to start using/evaluating OnlineGraph. A demo server is running in our lab and we made it accessible to anyone on the Internet. Please feel free to play with it
[here](http://138.96.16.35:8081/web/og/display/graph.html?gid=myGraph).

But you may be more likely willing to install your own instance. To do that, please [download an archive containing the latest binaries](https://www.i3s.unice.fr/~hogie/software/onlineGraph/onlinegraph-jars.tgz) and unpack it in a directory of your choice. Then go to that directory and execute the following command to start the server:
```bash
java -cp $(ls *.jar | tr '\n' :) og.RunServer 8080
````

You can also copy/paste this code to your Linux terminal. It will download and run the last version.
```bash
wget https://www.i3s.unice.fr/~hogie/software/onlineGraph/onlinegraph-jars.tgz && \
tar xzvf onlinegraph-jars.tgz && \
rm -f onlinegraph-jars.tgz && \
java -cp $(ls onlinegraph-jars/*.jar | tr '\n' :) og.RunServer 8080
````

echo oi && \
echo oi

## Viewing graphs
http://localhost:8081/web/og/display/graph.html?gid=myGraph

## Graph model
A *graph* is defined as the aggregation of 3 correlated sets of elements called *vertices*, *edges* and *arcs*.
Vertices are connected to one another through either edges or arcs.
Arcs have one *source* vertex and one *destination* vertex. Edges have *two or more ends*. When an edge has more than two ends, it is called an *hyperedge*.
Within its respective sets, each of these animals is identified by a 64-bit integer, and is associated to a  (key, value) mapping, whose the key is the name of some value that can be anything. A particular entry of this map is named *properties* and stores the following information, useful to graph rendering.

Common properties:
- color
- width
- fill color
- label
- label color

Vertex-specific properties:
- shape (point, circle, square, triangle, rectangle)
- scale

Properties for all edges/arcs:
- style (solid, dashed)
- color
- width
- label

Arcs add these properties:
- arrow shape (none, normal, diamond)
- arrow size


In the following, vertices and edges will be referred to as their ID.

## API
### Requests
The API is composed of a set of web services whose the URL is formed against the following pattern:
http://***server***:***port***/api/og/og.GraphService/***operation***/***parameters***
Where:
- ***server*** is the DNS name or IP address of the server running OnlineGraph ;
- ***port*** is the TCP port the server is listening to requests ;
- ***operation*** is the name of the web service offered to users ;
- ***parameters*** is the sequence of parameters passed to the operation. Parameters are delimited by the "/" character.
### Results
In response of calling a webservice, you get a chunk of JSON that follows the pattern:
```json
{
  "errors": [],
  "results": []
}
```
If the _errors_ array is left empty, then the execution went fine and you will get results, if any.


### Primitives
##### creating a new graph
To create a graph named "myGraph", use:
```
http://localhost:8081/api/og/og.GraphService/create/myGraph
````

#### Topology
In order to minimize the number of calls to the server, primites use multiplicity when possible.

##### Addition of new vertices
To adds vertices 145, 21, and 43 to graph "myGraph", use:
```
http://localhost:8081/api/og/og.GraphService/addV/myGraph/145,21,43
```
If you need to add more vertices than what an URL can embed, you can pass any number of 8-bytes ID in the data body of an HTTP POST requests.
##### Removal of existing vertices
This works similarly to the addition.
```
http://localhost:8081/api/og/og.GraphService/removeV/myGraph/145,21,43
```
##### Adding new edges
For the moment, the addition of multiple edges in one shot is not supported. Edges must be added individually like this:
```
http://localhost:8081/api/og/og.GraphService/addEdge/myGraph/145,21
```
##### Removing existing edges
But edges can be removed in groups, like this:
```
http://localhost:8081/api/og/og.GraphService/removeE/myGraph/11,2,5,6
```
##### Obtaining a list of vertices
```
http://localhost:8081/api/og/og.GraphService/vertices/myGraph
```
##### Obtaining a list of edges
```
http://localhost:8081/api/og/og.GraphService/edges/myGraph
```
##### Picking random elements
Many graph algorithms rely on the ability to pick random elements from graphs. 
```
http://localhost:8081/api/og/og.GraphService/randomV/myGraph/5
```
Picks 5 vertices in the graph and this does the same with edges:
```
http://localhost:8081/api/og/og.GraphService/randomE/myGraph/5
```

#### Dealing with data
As already said, both vertices and edges are associated to data slot identified by a name.
To get the content of the "foo" data slots associated to vertices 45 and 56, do:
```
http://localhost:8081/api/og/og.GraphService/getVertexData/myGraph/45,56/foo
```
- set(long vertex/edge, String key)

#### Graph dynamics
```
http://localhost:8081/api/og/og.GraphService/changes/myGraph/5567
```
returns the list of changes that occured on the given graph from the given moment in time.

### Algorithms
#### Traversal

- BFS
- Random walk

#### Clustering coefficient
- of a single node

### Import formats
- edge list
- ADJ lists
- GraphViz (under progress)

### Export formats
- JSON
- edge list
- ADJ lists
- GraphViz (as *dot* text or as any output image produced by *dot*, *circo*, *neato* and *fdp*)

## Display


### Dynamic view
The dynamic view for displaying graphs relies on the [Vis]https://visjs.org/ JavaScript framework.

### Static view
The static view for displaying graphs relies on GraphViz.
