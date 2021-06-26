# OnlineGraph

OnlineGraph is a cloud graph library. In OnlineGraph, graphs are stored on a server which also provide a set of Web services to manipulate them.


## Installation
Use the following command to start the server: 
java og.RunServer

## Architecture
OnlineGraph comes in the form of a server. It relies on Idawi.

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
- *long removeEdge(long)*
- *forEachVertex(long u, lambda)*
- *forEachEdge(long e, lambda)*
- *forEachOutEdge(long u, lamba f(u) -> bool)*

#### Dealing with data
- get(long vertex/edge, String key)
- set(long vertex/edge, String key)

### Algorithms
#### Traversal
BFS
Random walk
#### Clustering coefficient
BFS
Random walk

## Display
### Dynamic view
The dynamic view for displaying graphs relies on the Vis JavaScript framework.

### Static view
The static view for displaying graphs relies on GraphViz.
