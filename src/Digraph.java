import structs.graphs.AdjacencyMapGraph;
import structs.graphs.Edge;
import structs.graphs.Vertex;
import java.util.HashMap;
import java.util.Map;

public class Digraph<V,E> extends AdjacencyMapGraph<V,E>{
	
	public Digraph(boolean isDirected){
		super(isDirected);
	}
	
	public boolean isRooted(){
		boolean isRooted = false;
		
		for(Vertex<V> v : vertices()){
			if(outDegree(v) == 0)
				isRooted = true;
		}
		
		return isRooted;
	}
	
	public boolean isDAG(){
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
