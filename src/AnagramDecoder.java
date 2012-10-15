import java.io.*;


/**
 * This class is the entry point for the Anagram Decoder project.
 * 
 * @author Jeff Goldsworthy
 *
 */
public class AnagramDecoder {
    // This stores the dictionary words.
    public static StringTable words = new StringTable();
    
    // This stores words which have already been found.
    public static StringTable foundWords = new StringTable();
    
    // This stores partial words from the dictionary so that it can be checked
    // whether a particular combination is on track to find a real word.
    public static StringTable partialWords = new StringTable();
    
    // This stores the original string to be unscrambled.
    public static String origString;
    
    
	/**
	 * Entry point for the program.
	 * 
	 * @param args
	 * Contains the arguments for the program.  The first parameter is a filename
	 * of the dictionary of words to use.  The second parameter is anagram to
	 * unscramble. 
	 */
	public static void main(String[] args) {
        if (args.length != 2) {
        	System.out.println("Usage: java AnagramDecoder DICTIONARY ANAGRAM");
            System.exit(1);
        }
        
        // Grab the command line arguments.
        String filename = args[0];
        origString = args[1];
        
        // Load up the hash table with the dictionary words.
        loadWords(filename);
        
        decode("", origString);
	}
    
    
	/**
	 * Loads the words into the hash table.
     * 
	 * @param filename
     * File to read from.
	 */
	private static void loadWords(String filename) {
		try {
			FileInputStream fstream = new FileInputStream(filename);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
            
			String word;
			
			while ((word = br.readLine()) != null) {
				// DEBUG
                //if (word.substring(0, 1).equals("g")) {
                //	System.out.println(word);
                //}
				
                // Store the line in the hash table.
				words.insert(word);
				
				// Store the partials for this word so we can quickly see if a
				// combination is on track.
				for (int i = 1; i < (word.length()); ++i) {
					if (!partialWords.contains(word.substring(0, i))) {
						// DEBUG
						//System.out.println("Inserting to partial [" + word.substring(0, i) + "]");
						partialWords.insert(word.substring(0, i));
					}
				}
			}
            
			br.close();
		}
        catch (Exception e) {
        	System.err.println("Error attempting to load dictionary: " + 
        			e.getMessage());
        }
	}

    
	/**
	 * This method is recursively called and does the work of decoding the
	 * anagram.
	 * 
	 * @param beginning
	 * The beginning part of the anagram.
	 * 
	 * @param ending
	 * The ending part of the anagram.
	 */
    public static void decode(String beginning, String ending) {
        // Check if the ending has 
    	if (ending.length() <= 1) {
            // If the beginning string has any characters it needs to be checked
    		// for word existence.
    		if (beginning.length() > 0) {
    			if (beginning.substring(beginning.length() - 1).equals(" ")) {
    				if(!lastWordExists(beginning) 
    						|| phraseAlreadyFound(beginning)) {
    					// The last word does not exist and therefore this cannot be
    					// a valid anagram or the overall phrase has already been
    					// calculated.
    					return;
    				}
    			}
    		}
    		
            // If the last word exists, and hasn't already been found then a
    		// valid anagram has been found.  Otherwise drop out of the
    		// function.
    		if (lastWordExists(beginning + ending)
    				&& !phraseAlreadyFound(beginning + ending)) {
    			System.out.println(beginning + ending);
                
    			// Save the entire beginning so that another permutation
    			// doesn't try this same set of words.
    			foundWords.insert(beginning + ending);
    		}
    	}
    	else {
            // Check if there is some characters in the beginning string and
    		// whether the last character is a space.  This indicates that
    		// at attempt is being made to call the previous characters a word.
    		if (beginning.length() > 0) {
    			if (beginning.substring(beginning.length() - 1).equals(" ")) {
    				if(!lastWordExists(beginning) 
    						|| phraseAlreadyFound(beginning)) {
    					// The last word does not exist and therefore this cannot be
    					// a valid anagram or the overall phrase has already been
    					// calculated.
    					return;
    				}
    				else {
    					// Save the entire beginning so that another permutation
    					// doesn't try this same set of words.
    					foundWords.insert(beginning);
    				}
    			}
				else {
                    // Make sure we're on the right track.  If what has been
					// combined together so far may actually lead to a word
					// in the dictionary.
                    if (!partialLastWordExists(beginning)) {
                        // There is no word in the dictionary starting like
                    	// this so bail.
                    	return;
                    }
				}
    		}

    		for (int i = 0; i < ending.length(); ++i) {
                // This works because by starting at 0 and using it as the end
    			// character index which is non-inclusive means the first
    			// iteration will shift the left most character to the
    			// beginning and so on.
    			String temp = ending.substring(0, i) 
    					+ ending.substring(i + 1);

    			// Attempt to treat this as a word.
    			decode(beginning + ending.charAt(i) + " ", temp);

    			// Try the other letter combinations.
    			decode(beginning + ending.charAt(i), temp);
    		}
    	}
    }
    
    
    /**
     * This method determines if the last word in the string exists in the
     * dictionary.
     * 
     * @param target
     * Target string.
     * 
     * @return
     * This method returns true if the word exists, and false otherwise.
     */
    public static boolean lastWordExists(String target) {
    	String[] pieces = target.split(" ");
        
    	// DEBUG
        //if (words.contains(pieces[pieces.length - 1])) {
        	//System.out.println("Target [" + target + "] Last word is [" + pieces[pieces.length - 1] + "] in words " + words.contains(pieces[pieces.length - 1]));
        //}
        // END DEBUG
    	
    	return words.contains(pieces[pieces.length - 1]);
    }
    
    
    /**
     * This method determines if the target has already been calculated.
     * 
     * @param target
     * The string to search for.
     * 
     * @return
     * This method returns true if the string has already been calculated
     * and false otherwise.
     */
    public static boolean phraseAlreadyFound(String target) {
    	return foundWords.contains(target);
    }
    
    
    /**
     * This method determines if the last word in the target begins a word that
     * is in the dictionary.
     * 
     * @param target
     * The target string.
     * 
     * @return
     * This method returns true if the last word in the target begins a word
     * that is in the dictionary.
     */
    public static boolean partialLastWordExists(String target) {
    	String[] pieces = target.split(" ");
    	
    	return partialWords.contains(pieces[pieces.length - 1]);
    }
}
