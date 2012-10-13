
/**
 * This class implements a hash-based existence table.
 * 
 * @author Jeff
 *
 */
public class StringTable {
    private String[] words = null;
    
    //private int capacity = 10007;
    private int capacity = 100003;
    
    // This stores the number of values.
    private int used = 0;
    
	/**
	 * Class ctor.
	 */
	public StringTable() {
        words = new String[capacity];
        
        // Init the array values.
        for (int i = 0; i < words.length; ++i) {
        	words[i] = null;
        }
	}

	
    /**
     * This method inserts the specified string into the hash table.
     * 
     * @param s
     * The string to store.
     */
	public void insert(String s) {
        if (!contains(s)) {
            // Increment the used count. We know that it will succeed because
        	// if there isn't enough space the table will resize.
        	++used;
            
            // If the load exceeds 70% resize to preserve performance.
        	if (load() > 70) {
        		resize();
        	}
            
        	int targetIndex = getTargetIndex(s);

        	// DEBUG
        	//System.out.println("Target Index: "  + targetIndex);

        	if (words[targetIndex] == null) {
        		// The bucket is available so store the value.
        		words[targetIndex] = s;
                
        		return;
        	}
        	else {
        		// Calculate the skip value and find the next available bucket.
        		int skipValue = getSkipValue(s);

        		// DEBUG
        		//System.out.println("Skip Value: " + skipValue);

        		// The target was already checked, so init to 1.
        		int bucketsChecked = 1;

        		while (bucketsChecked <= capacity) {
        			targetIndex += skipValue;
        			++bucketsChecked;

        			// Check if we need to wrap the index.
        			if (targetIndex >= capacity) {
        				targetIndex = (targetIndex - capacity);
        			}

        			if (words[targetIndex] == null) {
        				// The bucket is available so store the value.
        				words[targetIndex] = s;
                        
        				return;
        			}
        		}

        		// We blew out the hash table.
        		System.out.println("WE BLEW OUT THE HASH TABLE.");
        		System.exit(1);
        	}
        }
	}
	
	
    /**
     * This method determines if the specified string is in the hash table.
     * 
     * @param s
     * The string to search for.
     * 
     * @return
     * This method returns true if the string was found and false otherwise.
     */
	public boolean contains(String s) {
        int targetIndex = getTargetIndex(s);
        
        if (words[targetIndex] == null) {
            // There is nothing in the target bucket, the word does not exist.
        	return false;
        }
        else if (words[targetIndex].equals(s)) {
        	return true;
        }
        else {
        	// The value might be in another bucket.
        	int skipValue = getSkipValue(s);
            
        	// The target was already checked, so init to 1.
            int bucketsChecked = 1;
            
            while (bucketsChecked <= capacity) {
            	targetIndex += skipValue;
                ++bucketsChecked;
                
            	// Check if we need to wrap the index.
                if (targetIndex >= capacity) {
                	targetIndex = (targetIndex - capacity);
                }
                
            	if (words[targetIndex] == null) {
            		// There is nothing in the target bucket, the word does not exist.
            		return false;
            	}
                
            	if (words[targetIndex].equals(s)) {
            		return true;
            	}
            }
            
            // The array is full and the value was not found.
            return false;
        }
	}
    
    
    /**
     * This method returns the target bucket index for the specified string.
     * @param s
     * The string whose target index should be determined.
     * 
     * @return
     * This method returns the index where the specified string should be
     * located.
     */
	private int getTargetIndex(String s) {
        // DEBUG
		//System.out.println(s.hashCode());
        
        return (Math.abs(s.hashCode() % capacity));
	}
    
	
    /**
     * This method gets the skip value for the specified string.
     * 
     * @param s
     * The string whose skip value should be determined.
     * 
     * @return
     * This method returns the skip value for the specified string.
     */
	private int getSkipValue(String s) {
        // DEBUG
		//System.out.println("Getting skip value for [" + s + "]");
        
        // Grab just the first 3 chars (if there are 3).
		String temp = s.substring(0, Math.min(3, s.length()));
        
		// DEBUG
		//System.out.println("Skip String [" + temp + "]");
        
        int skipValue = temp.charAt(0);
        
        if (temp.length() >= 2) {
        	skipValue += temp.charAt(1);
            
        	if (temp.length() >= 3) {
        		skipValue += temp.charAt(2);
        	}
        }
        
        // Add the values of the 3 characters together and mod by 30. Finally
        // add 1 so that a zero skip is never used.
		return ((skipValue % 30) + 1);
	}
    
	
    /**
     * Returns the load factor of the table.
     * 
     * @return
     * This method returns an integer value corresponding to the load factor
     * of the table.
     */
	private int load() {
        // Multiply by 100 so the load can be returned as an int. We aren't
		// making watches here. We just need a threshold to trigger the resize
		// before out performance dips in the table.
        return ((100 * used) / capacity);
	}
    
	
	private void resize() {
		// Resize the table and rehash the old values from the old array into
		// the new array.
        // TODO: (goldsy) FINISH ME.
	}
}
