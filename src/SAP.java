import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import structs.graphs.Edge;
import structs.graphs.Vertex;

public class SAP<V> {
	Digraph<V, Boolean> graph;

	// Constructor takes Digraph (not necessarily a DAG)
	public SAP(Digraph<V, Boolean> G){
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
	public int length(V v, V w){
		ArrayList<V> listA = new ArrayList<>(); 
		listA.add(v);
		
		ArrayList<V> listB = new ArrayList<>();
		listB.add(w);
		
		return length(listA, listB);
	}
	
	// A common ancestor of v and w that participates in a shortest ancestral path
	public V ancestor(V v, V w){
		ArrayList<V> listA = new ArrayList<>(); 
		listA.add(v);
		
		ArrayList<V> listB = new ArrayList<>();
		listB.add(w);
		
		return ancestor(listA, listB);
	}
	
	// Length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
	public int length(Iterable<V> v, Iterable<V> w){
		CommonAncestor ancestor = findCommonAncestor(v, w);
		int length = ancestor.getDistance();
		return length;
	}
	
	// A common ancestor that participates in shortest ancestral path; -1 if no such path
	public V ancestor(Iterable<V> v, Iterable<V> w){
		CommonAncestor ancestor = findCommonAncestor(v, w);
		if(ancestor.getAncestor() == null)
			return (V) new Integer(-1);
		
		V vertex = ancestor.getAncestor().getElement();
		
		return vertex;
	}
	
	private CommonAncestor findCommonAncestor(Iterable<V> listA, Iterable<V> listB){
		HashMap<Vertex<V>, Integer> ancestors = new HashMap<>();
		CommonAncestor currentMinAncestor = new CommonAncestor(null, Integer.MAX_VALUE);

		for(V valB : listB){
			Vertex<V> vertexB = graph.getVertexByValue(valB);
			calculateAncestorDistances(vertexB, ancestors);		
			for(V valA : listA){
				Vertex<V> vertexA = graph.getVertexByValue(valA);
				CommonAncestor commonAncestor = compareAndFindCommonAncestor(vertexA, ancestors);
				if(commonAncestor.getDistance() < currentMinAncestor.getDistance()){
					currentMinAncestor.setDistance(commonAncestor.getDistance());
					currentMinAncestor.setAncestor(commonAncestor.getAncestor());
				}
			}
			ancestors = new HashMap<>();
		}
		return currentMinAncestor;
	}
	
	private void calculateAncestorDistances(Vertex<V> source, Map<Vertex<V>, Integer> ancestors){
		LinkedList<Vertex<V>> notVisited = new LinkedList<>();
		int distFromSource = 0;
		notVisited.addFirst(source);
		
		while(!notVisited.isEmpty()){
			Vertex<V> currentSource = notVisited.removeFirst();
			ancestors.put(currentSource, distFromSource++);
			
			for(Edge<Boolean> e : graph.outgoingEdges(currentSource)){
				Vertex<V> target = graph.opposite(currentSource, e);
				boolean targetNotVisited = (ancestors.get(target) == null ? true : false);
				
				if(targetNotVisited){
					notVisited.addLast(target);
				}
			}
		}	
	}
	
	private CommonAncestor compareAndFindCommonAncestor(Vertex<V> v, Map<Vertex<V>, Integer> foundAncestors){
		LinkedList<Vertex<V>> notVisited = new LinkedList<>();
		CommonAncestor currentAncestor = new CommonAncestor(null, Integer.MAX_VALUE);
		int distFromV = 0;
		
		notVisited.addFirst(v);
		
		while(!notVisited.isEmpty()){
			Vertex<V> source = notVisited.removeFirst();
			distFromV++;
			
			for(Edge<Boolean> e : graph.outgoingEdges(source)){
				Vertex<V> target = graph.opposite(source, e);
				Integer distToTarget = foundAncestors.get(target);
				boolean hasBeenVisited = (distToTarget == null ? false : true);
				
				if(!hasBeenVisited){
					notVisited.addLast(target);
				} else {
					int totalDist = distFromV + distToTarget;
					if(totalDist < currentAncestor.getDistance()){
						currentAncestor.setDistance(totalDist);
						currentAncestor.setAncestor(target);
					}
				}
			}
		}	
			
		return currentAncestor;
	}

	
	private class CommonAncestor{
		private int distance;
		private Vertex<V> ancestor;
		
		public CommonAncestor(Vertex<V> v, int d){
			distance = d;
			ancestor = v;
		}
		
		public int getDistance(){
			return this.distance;
		}
		
		public Vertex<V> getAncestor(){
			return this.ancestor;
		}
	
		public void setDistance(int d){
			this.distance = d;
		}
		
		public void setAncestor(Vertex<V> v){
			this.ancestor = v;
		}
	}
	
	// Unit testing
	public static void main(String[] args){
		
	}
	
	
}
