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


## Graph model
A graph is defined as a set of vertices relating to each other through links. Each of these elements (vertices and links) exhibit the properties:
- they are identified by a 64-bit integer
- they are associated to a named data chunks

## API
### Primitives
#### Topology
- *addVertex(long)*
- *removeVertex(long)*
- *addEdge(long from, long to)*
- *removeEdge(long)*
- *forEachVertex(long u, lambda)*
- *forEachEdge(long e, lambda)*
- *forEachOutEdge(long u, lamba f(u) -> bool)*

#### Dealing with data
- *get(long vertex/edge, String key)* returns the data associated to the given vertex or edge.
- set(long vertex/edge, String key)

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
- edge list
- ADJ lists
- GraphViz
- JSON

## Display

Vertex properties:
- shape
- fill color
- label
- label color
- scale
- border color
- bolder width
- hidden

Edge properties:
- style
- color
- width
- label
- arrow shape
- arrow size
- directed

### Dynamic view
The dynamic view for displaying graphs relies on the Vis JavaScript framework.

### Static view
The static view for displaying graphs relies on GraphViz.
