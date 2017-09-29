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
	public ArrayList<String> setById = new ArrayList<>();
	public ArrayList<Vertex<Integer>> vertexById = new ArrayList<>();
	public HashMap<String, ArrayList<Integer>> synonymSets = new HashMap<>(50000);
	public Digraph<Boolean> wordNet = new Digraph<>();
	
	/*
	 * Constructor takes the name of the two input files
	 */
	public WordNet(String synsets, String hypernyms){
		if(synsets == null || hypernyms == null)
			throw new NullPointerException();
		
		try(Scanner synsetsFile = new Scanner(new BufferedReader(new FileReader(synsets)));
			Scanner hypernymsFile = new Scanner(new BufferedReader(new FileReader(hypernyms)))){
			
			while(synsetsFile.hasNextLine()){
				String[] synset = synsetsFile.nextLine().split(",");
				int id = Integer.parseInt(synset[0]);
				String allSynonyms = synset[1];
				String[] splitSynonyms = allSynonyms.split(" ");
				
				// Populate arraylist to access an entire synset by its corresponding ID
				setById.add(allSynonyms);
				
				// Maintain a reference to a vertex created for a synset in the wordnet graph
				// Allows constant-time access to a vertex corresponding to a synset
				vertexById.add(wordNet.insertVertex(id));

				// Keep track of synsets a given noun is in
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
			
			// Link vertices with an 'is-a' relationship
			while(hypernymsFile.hasNextLine()){
				String[] hypernymSet = hypernymsFile.nextLine().split(",");
				int length = hypernymSet.length;
				
				int fromId = Integer.parseInt(hypernymSet[0]);
				Vertex<Integer> v = (Vertex<Integer>) vertexById.get(fromId);
				
				for(int i = 1; i < length; i++){
					int toId = Integer.parseInt(hypernymSet[i]);
					Vertex<Integer> w = (Vertex<Integer>) vertexById.get(toId);
					wordNet.insertEdge(v, w, true);	
				}
			}
			
			if(!wordNet.isRooted())
				throw new IllegalArgumentException("Input does not correspond to a rooted DAG");	
			
		} catch(FileNotFoundException e1){
			e1.printStackTrace();
		}
	}
	
	/* 
	 * Returns all WordNet nouns
	 */
	public Iterable<String> nouns(){
		Set<String> temp_nouns = new HashSet<>();
		ArrayList<String> nouns;
		for(String group : setById){
			for(String noun : group.split(" ")){
				temp_nouns.add(noun);
			}
		}
		nouns = new ArrayList<>(temp_nouns);
		
		return nouns;
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
		
		SAP sap = new SAP(wordNet);
		
		ArrayList<Integer> nounASets = synonymSets.get(nounA);
		ArrayList<Integer> nounBSets = synonymSets.get(nounB);
		
		int length = sap.length(nounASets, nounBSets);

		if(length == -1){
			System.out.println("No common ancestor found");
			return -1;
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
		
		SAP sap = new SAP(wordNet);
		
		ArrayList<Integer> nounASets = synonymSets.get(nounA);
		ArrayList<Integer> nounBSets = synonymSets.get(nounB);
		
		int ancestor = sap.ancestor(nounASets, nounBSets);
		
		if(ancestor == -1){
			System.out.println("No common ancestor found");
			return null;
		}
		
		String ancestorSysnet = setById.get(ancestor);
		
		return ancestorSysnet;
	}
	
	// Unit testing
	public static void main(String[] args) {
		WordNet wn = new WordNet("synsets.txt", "hypernyms.txt");	
		//System.out.println(wn.distance("transgression", "opposition"));
	}

}
