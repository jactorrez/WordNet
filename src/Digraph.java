import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import structs.graphs.AdjacencyMapGraph;
import structs.graphs.Edge;
import structs.graphs.Vertex;

public class Digraph<E> extends AdjacencyMapGraph<Integer,E>{
	private HashMap<Integer, Vertex<Integer>> vertexByID;

	public Digraph(){
		super(true);
		vertexByID = new HashMap<>();
	}
	
	public Digraph(int V){
		super(true);
		vertexByID = new HashMap<>(V);
	}
	
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
	
	public Vertex<Integer> insertVertex(int val){
		Vertex<Integer> v = super.insertVertex(val);
		vertexByID.put(val, v);
		return v;
	}
	
	public Vertex<Integer> getVertexByID(int val){
		Vertex<Integer> v = vertexByID.get(val);
		return v;
	}
	
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
	
	private void DFS(Vertex<Integer> v, Map<Vertex<Integer>, Boolean> marked){
		marked.put(v, true);
		for(Edge<E> e : outgoingEdges(v)){
			Vertex<Integer> w= opposite(v, e);
			if(!marked.get(w)){
				DFS(w, marked);
			}
		}
	}
	
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
	
	/* Quick way to check if the graph contains a cycle */
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
