import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

import structs.graphs.Edge;
import structs.graphs.Vertex;

public class SAP {
	
	private Digraph<Boolean> graph;

	/*
	 * Constructor takes a Digraph (not necessarily a DAG)
	 */
	public SAP(Digraph<Boolean> G){
		if(G == null)
			throw new NullPointerException("Argument given is null");
		
		this.graph = G;
	}
	
	/*
	 * Checks if the digraph is a DAG
	 */
	public boolean isDAG(){
		return graph.isDAG();
	}
	
	/*
	 * Checks if the digraph is rooted
	 */
	public boolean isRootedDAG(){
		return graph.isRooted();
	}
	
	/*
	 * Returns the length of the shortest-ancestral path between v and w; -1 if no such path exists
	 */
	public int length(int v, int w){
		ArrayList<Integer> listA = new ArrayList<>(); 
		listA.add(v);
		
		ArrayList<Integer> listB = new ArrayList<>();
		listB.add(w);
		
		return length(listA, listB);
	}
	
	/*
	 * Returns a common ancestor of v and w that participates in a shortest-ancestral path; -1 if no such path exists
	 */
	public int ancestor(int v, int w){
		ArrayList<Integer> listA = new ArrayList<>(); 
		listA.add(v);
		ArrayList<Integer> listB = new ArrayList<>();
		listB.add(w);
		
		return ancestor(listA, listB);
	}
	
	/*
	 * Returns the length of the shortest-ancestral path between any vertex in v and any vertex in w; -1 if no such path exists
	 */
	public int length(Iterable<Integer> v, Iterable<Integer> w){
		if(v == null || w == null)
			throw new NullPointerException("Argument given was null");
		
		CommonAncestor ancestor = findCommonAncestor(v, w);
		int length = ancestor.getDistance();
		return length;
	}
	
	/*
	 * Returns the common ancestor that participates in a shortest ancestral path; -1 if no such path exists
	 */
	public int ancestor(Iterable<Integer> v, Iterable<Integer> w){
		if(v == null || w == null)
			throw new NullPointerException("Argument given was null");
		
		CommonAncestor ancestor = findCommonAncestor(v, w);
		if(ancestor.getAncestor() == null)
			return -1;
		
		int vertex = ancestor.getAncestor().getElement();
		
		return vertex;
	}
	
	/*
	 * Finds the common ancestor that participates in a shortest ancestral path for any vertex in listA and any vertex in listB
	 */
	private CommonAncestor findCommonAncestor(Iterable<Integer> listA, Iterable<Integer> listB){
		if(listA == null || listB == null)
			throw new NullPointerException("Argument given was null");
		
		HashMap<Vertex<Integer>, Integer> ancestors = new HashMap<>();
		CommonAncestor currentMinAncestor = new CommonAncestor(null, Integer.MAX_VALUE);

		for(int valB : listB){
			Vertex<Integer> vertexB = graph.getVertexByID(valB);
			calculateAncestorDistances(vertexB, ancestors);		
			
			for(int valA : listA){
				Vertex<Integer> vertexA = graph.getVertexByID(valA);
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
	
	/*
	 * Calculates and stores the distance(s) from a given source vertex to all of its ancestors
	 */
	private void calculateAncestorDistances(Vertex<Integer> source, Map<Vertex<Integer>, Integer> ancestors){
		if(source == null || ancestors == null)
			throw new NullPointerException("Argument given was null");
		
		LinkedList<Vertex<Integer>> notVisited = new LinkedList<>();
		int distFromSource = 0;
		notVisited.addFirst(source);
		
		while(!notVisited.isEmpty()){
			Vertex<Integer> currentSource = notVisited.removeFirst();
			ancestors.put(currentSource, distFromSource++);
			
			for(Edge<Boolean> e : graph.outgoingEdges(currentSource)){
				Vertex<Integer> target = graph.opposite(currentSource, e);
				boolean targetNotVisited = (ancestors.get(target) == null ? true : false);
				
				if(targetNotVisited){
					notVisited.addLast(target);
				}
			}
		}	
	}
	
	/*
	 * Finds the common ancestor between two vertices that participates in their shortest-ancestral path 
	 */
	private CommonAncestor compareAndFindCommonAncestor(Vertex<Integer> v, Map<Vertex<Integer>, Integer> foundAncestors){
		if(v == null || foundAncestors == null)
			throw new NullPointerException("Argument given was null");
		
		LinkedList<Vertex<Integer>> notVisited = new LinkedList<>();
		CommonAncestor currentAncestor = new CommonAncestor(null, Integer.MAX_VALUE);
		int distFromV = 0;
		
		notVisited.addFirst(v);
		
		while(!notVisited.isEmpty()){
			Vertex<Integer> source = notVisited.removeFirst();
			distFromV++;
			
			for(Edge<Boolean> e : graph.outgoingEdges(source)){
				Vertex<Integer> target = graph.opposite(source, e);
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
	
	/* 
	 * Internal CommonAncestor data-type to store a common ancestor found for a given pair of vertices
	 */
	public class CommonAncestor{
		private int distance;				// minimum distance associated with this common ancestor instance
		private Vertex<Integer> ancestor;	// vertex associated with this common ancestor instance
		
		/*
		 * Constructor creates an instance of a CommonAncestor with the vertex of the ancestor v and its total path distance d
		 */
		public CommonAncestor(Vertex<Integer> v, int d){
			if(v == null)
				throw new NullPointerException("Argument given was null");
			
			this.distance = d;
			this.ancestor = v;
		}
		
		/*
		 * Returns the minimum path distance associated with this CommonAncestor
		 */
		public int getDistance(){
			return this.distance;
		}
		
		/*
		 * Returns the ancestor associated with this CommonAncestor
		 */
		public Vertex<Integer> getAncestor(){
			return this.ancestor;
		}
		
		/*
		 * Set  the minimum path distance associated with this CommonAncestor
		 */
		public void setDistance(int d){
			this.distance = d;
		}
		
		/*
		 * Sets the ancestor vertex associated with this CommonAncestor
		 */
		public void setAncestor(Vertex<Integer> v){
			this.ancestor = v;
		}
	}
	
	// Unit testing
	public static void main(String[] args) throws FileNotFoundException{
		Scanner digraphFile = new Scanner(new BufferedReader(new FileReader("digraph1.txt")));
		Digraph<Boolean> graph = new Digraph<Boolean>(digraphFile);
		SAP sap = new SAP(graph);
		
		int length = sap.length(1,6);
		int ancestor = sap.ancestor(1,6);
		System.out.printf("Length of pair 9 12 \n%d\n\nancestor \n%d", length, ancestor);
	}
}
