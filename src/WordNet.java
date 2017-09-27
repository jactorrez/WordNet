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
	public ArrayList<String> nounsById = new ArrayList<>();
	public ArrayList<Vertex<Integer>> vertexById = new ArrayList<>();
	public HashMap<String,ArrayList<Integer>> synonymSets = new HashMap<>(50000);
	public Digraph<Integer, Boolean> wordNet = new Digraph<>(true);
	private boolean isRooted = false;
	
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
				String[] splitSynonyms = synset[1].split(" ");
				
				// populate arraylist with unsplit synonym set
				nounsById.add(allSynonyms);
				
				// adding vertices to graph and maintaining reference to vertex created
				vertexById.add(wordNet.insertVertexByEl(id));

				// populate hashtable with split synonym set
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
			
			while(hypernymsFile.hasNextLine()){
				String[] hypernymSet = hypernymsFile.nextLine().split(",");
				int length = hypernymSet.length;
				
				if(length == 1)
					isRooted = true;
				
				int fromId = Integer.parseInt(hypernymSet[0]);
				Vertex<Integer> v = (Vertex<Integer>) vertexById.get(fromId);
				
				for(int i = 1; i < length; i++){
					int toId = Integer.parseInt(hypernymSet[i]);
					Vertex<Integer> w = (Vertex<Integer>) vertexById.get(toId);
					wordNet.insertEdge(v, w, true);	
				}
			}
			
			if(isRooted == false)
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
		for(String group : nounsById){
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
		boolean found = synonymSets.get(word) != null ? true : false;
		return found;
	}
	
	/*
	 * Distance between nounA and nounB (defined below)
	 */
	public int distance(String nounA, String nounB){
		if(!isNoun(nounA)|| !isNoun(nounB)) 
			throw new IllegalArgumentException("A noun which is not in any synset was given");
		SAP sap = new SAP(wordNet);
		
		ArrayList<Integer> nounASets = synonymSets.get(nounA);
		ArrayList<Integer> nounBSets = synonymSets.get(nounB);
		
		sap.length(nounASets, nounBSets);
		
		return 5;
	}
	
	/*
	 * A synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
	 * in a shortest ancestral path
	 */
	public String sap(String nounA, String nounB){
		if(!isNoun(nounA)|| !isNoun(nounB))
			throw new IllegalArgumentException("A noun which is not in any synset was given");
		return nounB;
	}
	
	// Unit testing
	public static void main(String[] args) {
		WordNet wn = new WordNet("synsets.txt", "hypernyms.txt");	
	}

}
