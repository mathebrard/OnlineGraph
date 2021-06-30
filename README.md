# OnlineGraph
OnlineGraph is a cloud graph library. 
OnlineGraph comes in the form of a server that exposes a set of Web services for the storage and manipulation of graphs.
It relies on Idawi.

OnlineGraph is developped at Inria/I3S Computer Science Laboratory of Université Côte d'Azur. Its development team is composed of:
- Luc Hogie (project leader)
- Fedi Ghalloussi (Bachelor's degree intern)
- Antonin Lacomme (Master's degree intern)

## Installation
Use the following command to start the server: 
java og.RunServer

## Viewing graphs
http://localhost:8081/web/og/display/graph.html?gid=myGraph

## Graph model
A graph is defined as a set of vertices relating to each other through links. Each of these elements (vertices and links) exhibit the following properties:
- they are identified by a 64-bit integer
- they are associated to named data chunks (this data chunks can be retrieved independantly)

In the rest of this article, vertices and edges will be referred to as their ID.

## API
The API is composed of a set of web services whose the URL is formed against the following pattern:
http://***server***:***port***/api/og/og.GraphService/***operation***/***parameters***
Where:
- ***server*** is the DNS name or IP address of the server running OnlineGraph ;
- ***port*** is the TCP port the server is listening to requests ;
- ***operation*** is the name of the web service offered to users ;
- ***parameters*** is the sequence of parameters passed to the operation. Parameters are delimited by the "/" character.

### Primitives
##### creating a new graph
To create a graph named "myGraph", use:

http://localhost:8081/api/og/og.GraphService/create/myGraph

#### Topology
In order to minimize the number of calls to the server, primites use multiplicity when possible.

##### Addition of new vertices
To adds vertices 145, 21, and 43 to graph "myGraph", use:
http://localhost:8081/api/og/og.GraphService/addV/myGraph/145,21,43
If you need to add more vertices than what an URL can embed, you can pass any number of 8-bytes ID in the data body of an HTTP POST requests.
##### Removal of existing vertices
This works similarly to the addition.
http://localhost:8081/api/og/og.GraphService/removeV/myGraph/145,21,43
##### Adding new edges
For the moment, the addition of multiple edges in one shot is not supported. Edges must be added individually like this:
http://localhost:8081/api/og/og.GraphService/addEdge/myGraph/145,21
##### Removing existing edges
But edges can be removed in groups, like this:
http://localhost:8081/api/og/og.GraphService/removeE/myGraph/11,2,5,6
##### Obtaining a list of vertices
http://localhost:8081/api/og/og.GraphService/vertices/myGraph
##### Obtaining a list of edges
http://localhost:8081/api/og/og.GraphService/edges/myGraph
##### Picking random elements
Many graph algorithms rely on the ability to pick random elements from graphs. 
http://localhost:8081/api/og/og.GraphService/randomV/myGraph/5
Picks 5 vertices in the graph and this does the same with edges:
http://localhost:8081/api/og/og.GraphService/randomE/myGraph/5

#### Dealing with data
As already said, both vertices and edges are associated to data slot identified by a name.
To get the content of the "foo" data slots associated to vertices 45 and 56, do:
http://localhost:8081/api/og/og.GraphService/getVertexData/myGraph/45,56/foo
- set(long vertex/edge, String key)

#### Graph dynamics
- *changes(long vertex/edge, long second)* returns the list of changes that occured on the given graph from the given moment in time.

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

Vertex properties:
- shape (point, circle, square, triangle, rectangle)
- fill color
- label
- label color
- scale
- border color
- bolder width
- hidden

Edge properties:
- style (solid, dashed)
- color
- width
- label
- arrow shape (none, normal, diamond)
- arrow size
- directed

### Dynamic view
The dynamic view for displaying graphs relies on the [Vis]https://visjs.org/ JavaScript framework.

### Static view
The static view for displaying graphs relies on GraphViz.
