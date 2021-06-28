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
A graph is defined as a set of vertices relating to each other through links. Each of these elements (vertices and links) exhibit the following properties:
- they are identified by a 64-bit integer
- they are associated to named data chunks (this data chunks can be retrieved independantly)

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
- *find(int nbExcpected, primaryCondition lambda, alternativeCondition lambda)* returns a set of nbExcpected elements matching the primary condition, otherwise the alternative one.

#### Dealing with data
- *get(long vertex/edge, String key)* returns the data associated to the given vertex or edge.
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
The dynamic view for displaying graphs relies on the Vis JavaScript framework.

### Static view
The static view for displaying graphs relies on GraphViz.
