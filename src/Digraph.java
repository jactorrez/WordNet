import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import structs.graphs.AdjacencyMapGraph;
import structs.graphs.Edge;
import structs.graphs.Vertex;

public class Digraph<E> extends AdjacencyMapGraph<Integer,E>{
	private HashMap<Integer, Vertex<Integer>> vertexByID;

	/*
	 * Initializes a digraph
	 */
	public Digraph(){
		super(true);
		vertexByID = new HashMap<>();
	}
	
	/*
	 * Initializes a digraph with V vertices
	 */
	public Digraph(int V){
		super(true);
		vertexByID = new HashMap<>(V);
	}
	
	/*
	 * Initializes a digraph from the specified input stream
	 */
	public Digraph(Scanner file){
		super(true);
		Scanner graphContent = file;
		int V = graphContent.nextInt();
		vertexByID = new HashMap<>(V);
		graphContent.nextInt(); 
		
		while(graphContent.hasNextInt()){
			int v_id = graphContent.nextInt();
			int w_id = graphContent.nextInt();
		
			Vertex<Integer> v = vertexByID.get(v_id);
			Vertex<Integer> w = vertexByID.get(w_id);
			
			if(v == null){
				v = insertVertex(v_id);
				vertexByID.put(v_id, v);
			}
			
			if(w == null){
				w = insertVertex(w_id);
				vertexByID.put(w_id, w);
			}
			insertEdge(v, w, null);
		}
	}
	
	/*
	 * Inserts a vertex into the digraph and stores a reference to it to access it via its value
	 */
	public Vertex<Integer> insertVertex(int val){
		Vertex<Integer> v = super.insertVertex(val);
		vertexByID.put(val, v);
		return v;
	}
	
	/*
	 * Returns the vertex in the digraph corresponding to the given value
	 */
	public Vertex<Integer> getVertexByID(int val){
		Vertex<Integer> v = vertexByID.get(val);
		return v;
	}
	
	/*
	 * Checks whether this graph is rooted
	 */
	public boolean isRooted(){
		for(Vertex<Integer> v : vertices()){
			if(outDegree(v) == 0){
				if(isConnected(v)){
					return true;
				}
			}	
		} 
		return false;
	}
	
	/*
	 * Checks whether the graph is connected starting from vertex v
	 */
	private boolean isConnected(Vertex<Integer> v){
		HashMap<Vertex<Integer>, Boolean> marked = new HashMap<>();
		int count = 0;
		int verticesCount = numVertices();
		
		count = reachableNodes(v, marked, count);
		if(count == verticesCount){
			return true;
		}
		
		return false;
	}
	
	/*
	 * Finds all nodes reachable from vertex v
	 */
	private int reachableNodes(Vertex<Integer> v, Map<Vertex<Integer>, Boolean> marked, Integer markCount){
		marked.put(v, true);
		markCount++;
		
		for(Edge<E> e : incomingEdges(v)){
			Vertex<Integer> w = opposite(v, e);
			if(marked.get(w) == null){
				markCount = reachableNodes(w, marked, markCount);
			}
		}
		return markCount;
	}
	
	/*
	 * Performs depth-first search on this digraph, starting at vertex v
	 */
	private void DFS(Vertex<Integer> v, Map<Vertex<Integer>, Boolean> marked){
		marked.put(v, true);
		for(Edge<E> e : outgoingEdges(v)){
			Vertex<Integer> w= opposite(v, e);
			if(!marked.get(w)){
				DFS(w, marked);
			}
		}
	}
	
	/*
	 * Checks whether this digraph is a directed acyclic graph
	 */
	public boolean isDAG(){
		boolean hasSource = findSource();
		
		if(!hasSource)
			return false;
		
		boolean isAcyclic = true;
		HashMap<Vertex<Integer>, Boolean> visited = new HashMap<>();
		HashMap<Vertex<Integer>, Boolean> visiting = new HashMap<>();

		for(Vertex<Integer> v : vertices()){
			visited.put(v, false);
		}
		
		for(Vertex<Integer> v : vertices()){
			if(!visited.get(v)){
				isAcyclic = findCycle(v, visited, visiting);
				if(isAcyclic)
					return true;
			}
		}
		
		return isAcyclic;
	}
	
	/*
	 * Attempts to find a vertex with an in-degree of 0
	 * If no vertex has this characteristic, the digraph is guaranteed to by cyclic
	 */
	public boolean findSource(){
		boolean found = false;
		
		for(Vertex<Integer> v : vertices()){
			if(inDegree(v) == 0){
				found = true;
				break;
			}
		}
		
		return found;
	}
	
	/*
	 * Attempts to find a cycle starting from the given source vertex
	 */
	public boolean findCycle(Vertex<Integer> source, Map<Vertex<Integer>, Boolean> visited, Map<Vertex<Integer>, Boolean> visiting){
		visiting.put(source, true);
		boolean cycleFound = false; 
		
		for(Edge<E> e : outgoingEdges(source)){
			Vertex<Integer> w = opposite(source, e);
			if(visited.get(w)){
				continue;
			} else if(visiting.get(w)){
				return true;
			} else {
				cycleFound = findCycle(w, visited, visiting);
			}
		}	
		
		visited.put(source, true);
		return cycleFound;
	}
}
