import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class Outcast {
	private WordNet wordNet;
	
	public Outcast(WordNet W){
		this.wordNet = W;
	}
	
	/*
	 *  Given an array of WordNet nouns, return an outcast
	 */
	public String outcast(String[] nouns){
		String outcastNoun = null;
		int outcastDist = 0;
		
		for(int i = 0, len = nouns.length; i < len ; i++){
			String nounA = nouns[i];
			int currentDist = 0;
			
			for(int j = 0; j < len; j++){
				if(i == j)
					continue;
				
				String nounB = nouns[j];
				
				currentDist += wordNet.distance(nounA, nounB);
			}
			
			if(currentDist > outcastDist){
				outcastDist = currentDist;
				outcastNoun = nounA;
			}
		}	
		return outcastNoun;
	}
	
	/*
	 * Unit testing
	 */
	public static void main(String[] args){
		WordNet wn = new WordNet("synsets.txt", "hypernyms.txt");
		Outcast outcast = new Outcast(wn);
		
		try(Scanner outcastFile = new Scanner(new BufferedReader(new FileReader("outcast11.txt")))){
			String[] nouns = new String[11];
			int index = 0;
			
			while(outcastFile.hasNext()){
				String n = outcastFile.next();
				nouns[index++] = n;
			}
			
			long startTime = System.nanoTime();
			System.out.println("Calculating outcast...");
			System.out.println("Outcast is: " + outcast.outcast(nouns));
			long endTime = System.nanoTime();
			System.out.println("Found value after " + (endTime - startTime));
			
		} catch(FileNotFoundException e){
			e.printStackTrace();
		}
	}
}
