import java.util.HashMap;

public class SAP {
	private HashMap<Integer, Integer> ancestors = new HashMap<>();
	Digraph<Integer, Boolean> graph;

	// Constructor takes Digraph (not necessarily a DAG)
	public SAP(Digraph G){
		this.graph = G;
	}
	
	// Is the digraph a directed acyclic graph? 
	public boolean isDAG(){
		return graph.isDAG();
	}
	
	// Is the digraph a rooted DAG?
	public boolean isRootedDAG(){
		return graph.isRooted();
	}
	
	// Length of shortest ancestral path between v and w; -1 if no such path
	public int length(int v, int w){
		
		return 0;
	}
	
	// A common ancestor of v and w that participates in a shortest ancestral path
	public int ancestor(int v, int w){
		
	}
	
	// Length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
	public int length(Iterable<Integer> v, Iterable<Integer> w){
		
	}
	
	// A common ancestor that participates in shortest ancestral path; -1 if no such path
	public int ancestor(Iterable<Integer> v, Iterable<Integer> w){
		
	}
	
	// Unit testing
	public static void main(String[] args){
		
	}
	
	
}
