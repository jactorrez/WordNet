import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import structs.graphs.AdjacencyMapGraph;
import structs.graphs.Edge;
import structs.graphs.Vertex;

public class Digraph<V,E> extends AdjacencyMapGraph<V,E>{
	
	public Digraph(){
		super(true);
	}
	
	public boolean isRooted(){
		for(Vertex<V> v : vertices()){
			if(outDegree(v) == 0){
				if(isConnected(v)){
					return true;
				}
			}	
		} 

		return false;
	}
	
	private boolean isConnected(Vertex<V> v){
		HashMap<Vertex<V>, Boolean> marked = new HashMap<>();
		Integer markCount = 0;
		int verticesCount = numVertices();
		
		reverseDFS(v, marked, markCount);
		if(markCount == verticesCount){
			return true;
		}
		
		return false;
	}
	
	private void reverseDFS(Vertex<V> v, Map<Vertex<V>, Boolean> marked, Integer markCount){
		marked.put(v, true);
		markCount++;
		for(Edge<E> e : incomingEdges(v)){
			Vertex<V> w = opposite(v, e);
			if(!marked.get(w)){
				reverseDFS(w, marked, markCount);
			}
		}
	}
	
	private void DFS(Vertex<V> v, Map<Vertex<V>, Boolean> marked){
		marked.put(v, true);
		for(Edge<E> e : outgoingEdges(v)){
			Vertex<V> w= opposite(v, e);
			if(!marked.get(w)){
				DFS(w, marked);
			}
		}
	}
	
	private void BFS(Vertex<V> v, )
	
	public boolean isDAG(){
		boolean hasSource = findSource();
		
		if(!hasSource)
			return false;
		
		boolean isAcyclic = true;
		HashMap<Vertex<V>, Boolean> visited = new HashMap<>();
		HashMap<Vertex<V>, Boolean> visiting = new HashMap<>();

		for(Vertex<V> v : vertices()){
			visited.put(v, false);
		}
		
		for(Vertex<V> v : vertices()){
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
		
		for(Vertex<V> v : vertices()){
			if(inDegree(v) == 0){
				found = true;
				break;
			}
		}
		
		return found;
	}
	
	public boolean findCycle(Vertex<V> source, Map<Vertex<V>, Boolean> visited, Map<Vertex<V>, Boolean> visiting){
		visiting.put(source, true);
		boolean cycleFound = false; 
		
		for(Edge<E> e : outgoingEdges(source)){
			Vertex<V> w = opposite(source, e);
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
