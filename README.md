# I.T Pokemon's Game.

<center>

![alt text](https://i.ibb.co/XyV51Jg/backgroundsecound.jpg)

</center>

## **********************************_Introduction_**********************************

**@authers Tal Schreiber & Itay Sharabi**

##### ***Package: src/api***
* NodeData class:
This class represents the node and all its characteristics.
implements node_data

* EdgeData class: 
This class represents an edge and its characteristics.
implements edge_data

* DWGraph_DS class:
This class represents the graph and all his characteristics.
implements directed_weighted_graph 

* DWGraph_Algo class:
This class represents all the algorithms we want to execute in this graph
implements dw_graph_algorithms

#### ***Package: src/gameClient***
* Agent class: 
This class represents an agent. 
Some information about agents is stored as well as some logic to run throuout the game.

* Pokemon class: 
This class represents a pokemon. 
Some information about pokemons is stored as well as some logic to run throuout the game.
* Arena class:
This class represents the arena of the game, which contains agents, pokemons 
and the underlying game graph.
The class has some valuable methods given to us. All were not changed.

*******************Main Methodology implementation*******************

*********_DWGraph_DS classes methods:_*********

We Have used three HashMaps that represent the graph.
nodes: This HashMap represents the nodes on the graph by key and value(node_data).
outEdges: This HashMap represents the outgoing edges from each node (by key) to all of it's neighbors.
inEdges: This HashMap represents the incoming edges from each node (by key) to all of it's neighbors. 

>`public DWGraph_DS(directed_weighted_graph g)`
* Was made for making a deep copied graph.
Main reason for this constructor is for the copy() method of DWGraph_Algo class.
Using deep copy constructor of node_data and edge_data classes.

>`public void connect(int src, int dest, double w)` 
* First need to ask if those nodes exist on this graph, no--> do nothing.
Second, need to ask if the edge's weight is greater than or equal to 0 (due to the requirement of the interface), no--> do nothing.
Next need to ask if those two nodes are same nodes, yes --> break this function , else continue connecting those node.
Next we'll connect those nodes on both hashmaps(in/outEdges).
Note: If this node is already connected their weight will be updated according to the last connection

>`public node_data removeNode(int key)`
* First we need to ask if our graph contains this key node, no--> return null.
Second, we delete all outGoing edges from the key node, and then all incoming edges into key node with an iterator.
We delete the node from the nodes hashmap and return the deleted node.

>`public void removeEdge(int src, int dest)`
* First we check if there is an edge between src and dest, and src != dest. 
If so --> remove from both incoming and outgoing edge maps.
Otherwise --> return null.

*********_DWGraph_Algo class methods:_*********

>`public directed_weighted_graph copy()` 
* This method is using a deep copy constructor that was made in the DWGraph_DS class and return a deep copy of the graph.

>`public boolean isConnected()`
* This method returns true iff there's a valid path from any node to every other.
We do this by traversing the graph Breadt-First twice: 
First time: Regular BFS traverse on the graph from vertex V
Second time: Transpose all graph's edges and execute the same bfs from same vertex V.

>`public int shortestPathDist(int src, int dest)` 
* Execute the *shortestPath(src, dest)* method to receive that relevant path from src to dest.
Extract and return the required value which is the current weight of the 'dest' node.
This is done by simply accessing the last element of the given list and calling element.getWeight().
* 
>`public List<node_info> shortestPath(int src, int dest)` 
* Traverse the current class graph Breadth-First inorder to find the shortest path from node 'src' to node 'dest'.
This is done using a `Priority-Queue` data structure that prioritizes nodes by the lowest current path by weight.
This method was implemented with inspiration from Dijkstra's algorithm.
<center> 
<h3> Illustration of Dijkstra </h3>

![alt text](https://upload.wikimedia.org/wikipedia/commons/5/57/Dijkstra_Animation.gif)

</center>

>`public boolean save(String file)/load(String file)`
* Save and load the graph into / from a file located in the path that 'file' represents
This is done by Serializing and Deserializing graphs into json formats.

*********_Agent class methods:_*********

![alt text](https://i.ibb.co/VQWPFBx/agent.jpg)

* curr_fruit: the target of the best pokemon of this agent.

*********_Pokemon class methods:_*********
* isTracked: a boolean parameter that shown if this pokemon are tracked by any agent.
* we decided to show a multi type of pokemon that are separated by values and 
not by types.  

![alt text](https://i.ibb.co/fxXCcbQ/lowValue.jpg)
* value range: 0-5.

![alt text](https://i.ibb.co/McYBzxw/medium-Value.jpg)
* value range: 5-10.

![alt text](https://i.ibb.co/N292DD6/picatchu.jpg)
* value range: 10-15.

![alt text](https://i.ibb.co/9c6D7Lb/highvalue.jpg)
* value range: 15 and above.

# How To Run
* Download project zip file from **https://github.com/ItaySharabi/OOP_Ex2.git** or **https://github.com/TalSchreiber95/OOP_Ex2**

### Manually:

* Start IntelliJ, or any other Java workspace environment, and open Ex2.java class.

![alt text](https://i.ibb.co/PzVG7S5/Manual-Run.png)

* Simply Change the input of the fields 'id' and 'level' as you wish.
* Hit RUN (Green 'Play' button), and the game should be running! 

### Through Terminal (Command Prompt)

* Navigate throughout your computer to look for the extracted folder, using the `cd` and `dir` commands.
* Find OOP_Ex2.jar file (`dir` command shows directory content).

![alt text](https://i.ibb.co/KjRkST1/run.png)

* Use the command:
    >*`java -jar OOP_Ex2.jar id level`*

* The game should be now running with the arguments `id`, `level`!
* Enjoy! :)


