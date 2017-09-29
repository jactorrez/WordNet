import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import structs.graphs.Vertex;

public class WordNet {
	private ArrayList<String> setById = new ArrayList<>();
	private ArrayList<Vertex<Integer>> vertexById = new ArrayList<>();
	private HashMap<String, ArrayList<Integer>> synonymSets = new HashMap<>(90000);
	private Digraph<Boolean> wordNet = new Digraph<>();
	private SAP sap;
	/* --- Caching previously made queries --- */
	private HashMap<String, HashMap<String, Integer>> distanceCache = new HashMap<>();
	private HashMap<String, HashMap<String, Integer>> ancestorCache = new HashMap<>();

	/*
	 * Constructor takes the name of the two input files
	 */
	public WordNet(String synsets, String hypernyms){
		if(synsets == null || hypernyms == null)
			throw new NullPointerException();
		
		try(Scanner synsetsFile = new Scanner(new BufferedReader(new FileReader(synsets)));
			Scanner hypernymsFile = new Scanner(new BufferedReader(new FileReader(hypernyms)))){
			
			while(synsetsFile.hasNextLine()){
				// Split line using comma as delimiter to access the id, synset, and gloss by index
				String[] synset = synsetsFile.nextLine().split(",");
				
				// Store the id of the current synset
				int id = Integer.parseInt(synset[0]);
				
				// Store the current synset as a whole
				String allSynonyms = synset[1];
				
				// Split the whole synset to access the individual nouns
				String[] splitSynonyms = allSynonyms.split(" ");
				
				// Populate setByID to access an entire synset by its corresponding id
				setById.add(allSynonyms);
				
				// Maintain a reference to the vertex created in the digraph
				// Allows constant-time access to a vertex by using its id as the index
				vertexById.add(wordNet.insertVertex(id));

				// Populate synonymSets to keep track of which synsets a given noun appears in
				for (String s : splitSynonyms){
					ArrayList<Integer> list = synonymSets.get(s);
					
					if(list != null){
						list.add(id);
					} else{
						synonymSets.put(s, new ArrayList<Integer>());
						synonymSets.get(s).add(id);						
					}
				}	
			}
			
			// Link previously created vertices to signify 'is-a' relationship
			while(hypernymsFile.hasNextLine()){
				// Split line and use comma as a delimiter to access the 'from' index and 'to' indices 
				String[] hypernymSet = hypernymsFile.nextLine().split(",");				
				int fromId = Integer.parseInt(hypernymSet[0]);
				// Get reference to vertex corresponding to the synset with id 'fromId'
				Vertex<Integer> v = (Vertex<Integer>) vertexById.get(fromId);
				
				// Iterate through the line, attaching the previous vertex to its hypernyms
				for(int i = 1, length = hypernymSet.length; i < length; i++){
					int toId = Integer.parseInt(hypernymSet[i]);
					Vertex<Integer> w = (Vertex<Integer>) vertexById.get(toId);
					wordNet.insertEdge(v, w, true);	
				}
			}
			
			
			// Check if the graph created is a rooted DAG, throw an exception if it's not  
			if(!wordNet.isRooted())
				throw new IllegalArgumentException("Input does not correspond to a rooted DAG");	
			
			sap = new SAP(wordNet); 
			
		} catch(FileNotFoundException e1){
			e1.printStackTrace();
		}
	}
	
	/* 
	 * Returns all WordNet nouns
	 */
	public Iterable<String> nouns(){
		Set<String> temp_nouns = new HashSet<>();
		for(String group : setById){
			for(String noun : group.split(" ")){
				temp_nouns.add(noun);
			}
		}
		return (new ArrayList<String>(temp_nouns));
	}
	
	/*
	 * Is the word a WordNet noun?
	 */
	public boolean isNoun(String word){
		if(word == null)
			throw new NullPointerException();
		
		boolean found = (synonymSets.get(word) != null ? true : false);
		return found;
	}
	
	/*
	 * Distance between nounA and nounB (defined below)
	 */
	public int distance(String nounA, String nounB){
		if((nounA == null || nounB == null) || (!isNoun(nounA)|| !isNoun(nounB))) 
			throw new IllegalArgumentException("The noun given was either null or does not exist in the wordnet graph");
		
		int length = 0;
		int cached = getCachedDistance(nounA, nounB);
		
		if(cached != -1){
			System.out.println("Retrieving cached value");
			length = cached;
		} else {
			System.out.println("Value not stored in cache");

			ArrayList<Integer> nounASets = synonymSets.get(nounA);
			ArrayList<Integer> nounBSets = synonymSets.get(nounB);
			
			length = sap.length(nounASets, nounBSets);
			cacheDistance(nounA, nounB, length);
		}
			
		return length;
	}
	
	/*
	 * A synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
	 * in a shortest ancestral path
	 */
	public String sap(String nounA, String nounB){
		if(!isNoun(nounA)|| !isNoun(nounB))
			throw new IllegalArgumentException("A noun which is not in any synset was given");
		
		int ancestor = 0;
		int cached = getCachedAncestor(nounA, nounB);
		
		if(cached != -1){
			System.out.println("Retrieving cached value");
			ancestor = cached;
		} else{
			System.out.println("Value not cached");
			ArrayList<Integer> nounASets = synonymSets.get(nounA);
			ArrayList<Integer> nounBSets = synonymSets.get(nounB);
			ancestor = sap.ancestor(nounASets, nounBSets);
			cacheAncestor(nounA, nounB, ancestor);
		}

		String ancestorSysnet = setById.get(ancestor);
		return ancestorSysnet;
	}
	
	/* 
	 * Retrieves the cached distance for two nouns 
	 */
	private int getCachedDistance(String nounA, String nounB){
		if(distanceCache.get(nounA) == null){
			return -1;
		}
		
		int ancestor = distanceCache.get(nounA).get(nounB);
		return ancestor;
	}
	
	/*
	 * Caches a given distance between two nouns
	 */
	private void cacheDistance(String nounA, String nounB, int dist){
		HashMap<String, Integer> mapA;
		HashMap<String, Integer> mapB;
		
		if(distanceCache.get(nounA) == null){
			distanceCache.put(nounA, new HashMap<String, Integer>());
			mapA = distanceCache.get(nounA);
		} else{
			mapA = distanceCache.get(nounA);
		}
		
		if(distanceCache.get(nounB) == null){
			distanceCache.put(nounB, new HashMap<String, Integer>());
			mapB = distanceCache.get(nounB);
		} else{
			mapB = distanceCache.get(nounB);
		}
		
		mapA.put(nounB, dist);
		mapB.put(nounA, dist);
	}
	
	/*
	 * Retrieves a cached ancestor for two nouns 
	 */
	private int getCachedAncestor(String nounA, String nounB){
		if(ancestorCache.get(nounA) == null){
			return -1;
		}
		
		int ancestor = ancestorCache.get(nounA).get(nounB);
		return ancestor;
	}
	
	/*
	 * Caches a given ancestor for a given pair of nouns
	 */
	private void cacheAncestor(String nounA, String nounB, int id){
		HashMap<String, Integer> mapA;
		HashMap<String, Integer> mapB;
		
		if(ancestorCache.get(nounA) == null){
			ancestorCache.put(nounA, new HashMap<String, Integer>());
			mapA = ancestorCache.get(nounA);
		} else{
			mapA = ancestorCache.get(nounA);
		}
		
		if(ancestorCache.get(nounB) == null){
			ancestorCache.put(nounB, new HashMap<String, Integer>());
			mapB = ancestorCache.get(nounB);
		} else{
			mapB = ancestorCache.get(nounB);
		}
		
		mapA.put(nounB, id);
		mapB.put(nounA, id);
	}
	
	// Unit testing
	public static void main(String[] args) {
		WordNet wn = new WordNet("synsets.txt", "hypernyms.txt");	
		Scanner userIn = new Scanner(System.in);
		
		while(userIn.hasNext()){
			String[] nouns = userIn.nextLine().split(" ");
			long startTime = System.nanoTime();
			System.out.println("SAP is: " + wn.sap(nouns[0], nouns[1]));
			long endTime = System.nanoTime();
			System.out.println("Method took " + ((endTime - startTime)) + " milliseconds to run");
		}
		
		userIn.close();
	}
}
