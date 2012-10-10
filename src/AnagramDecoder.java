import java.io.*;


/**
 * This class is the entry point for the Anagram Decoder project.
 * 
 * @author Jeff
 *
 */
public class AnagramDecoder {
    // This stores the dictionary words.
    public static StringTable words = new StringTable();
    
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
                // Store the line in the hash table.
                System.out.println(word);
				words.insert(word);
			}
		}
        catch (Exception e) {
        	System.err.println("Error attempting to load dictionary: " + e.getMessage());
        }
	}

    
	/**
	 * 
	 * @param beginning
	 * @param ending
	 */
    public static void decode(String beginning, String ending) {
    	if (ending.length() <= 1) {
    		if (lastWordExists(beginning + ending)) {
    			System.out.println(beginning + ending);
    		}
    		else {
    			// The last word does not exist and therefore this cannot be
    			// a valid anagram.
                return;
    		}
    	}
    	else {
            // Check if there is some characters in the beginning string and
    		// whether the last character is a space.  This indicates that
    		// at attempt is being made to call the previous characters a word.
    		if ((beginning.length() > 0)
    				&& (beginning.substring(beginning.length() - 1).equals(" "))) {
    			if(!lastWordExists(beginning)) {
    				// The last word does not exist and therefore this cannot be
    				// a valid anagram.
    				return;
    			}
                
    			for (int i = 0; i < ending.length(); ++i) {
    				String temp = ending.substring(0, i) 
    						+ ending.substring(i + 1);
                    
    				// Attempt to treat this as a word.
    				decode(beginning + ending.charAt(i) + " ", temp);
    				
    				// Try the other letter combinations.
    				decode(beginning + ending.charAt(i), temp);
    			}
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
    	
    	return words.contains(pieces[pieces.length - 2]);
    }
}
