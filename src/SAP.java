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
	Digraph<Boolean> graph;

	// Constructor takes Digraph (not necessarily a DAG)
	public SAP(Digraph<Boolean> G){
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
		ArrayList<Integer> listA = new ArrayList<>(); 
		listA.add(v);
		
		ArrayList<Integer> listB = new ArrayList<>();
		listB.add(w);
		
		return length(listA, listB);
	}
	
	// A common ancestor of v and w that participates in a shortest ancestral path
	public int ancestor(int v, int w){
		ArrayList<Integer> listA = new ArrayList<>(); 
		listA.add(v);
		ArrayList<Integer> listB = new ArrayList<>();
		listB.add(w);
		
		return ancestor(listA, listB);
	}
	
	// Length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
	public int length(Iterable<Integer> v, Iterable<Integer> w){
		CommonAncestor ancestor = findCommonAncestor(v, w);
		int length = ancestor.getDistance();
		return length;
	}
	
	// A common ancestor that participates in shortest ancestral path; -1 if no such path
	public int ancestor(Iterable<Integer> v, Iterable<Integer> w){
		CommonAncestor ancestor = findCommonAncestor(v, w);
		if(ancestor.getAncestor() == null)
			return -1;
		
		int vertex = ancestor.getAncestor().getElement();
		
		return vertex;
	}
	
	private CommonAncestor findCommonAncestor(Iterable<Integer> listA, Iterable<Integer> listB){
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
	
	private void calculateAncestorDistances(Vertex<Integer> source, Map<Vertex<Integer>, Integer> ancestors){
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
	
	private CommonAncestor compareAndFindCommonAncestor(Vertex<Integer> v, Map<Vertex<Integer>, Integer> foundAncestors){
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

	public class CommonAncestor{
		private int distance;
		private Vertex<Integer> ancestor;
		
		public CommonAncestor(Vertex<Integer> v, int d){
			distance = d;
			ancestor = v;
		}
		
		public int getDistance(){
			return this.distance;
		}
		
		public Vertex<Integer> getAncestor(){
			return this.ancestor;
		}
	
		public void setDistance(int d){
			this.distance = d;
		}
		
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
