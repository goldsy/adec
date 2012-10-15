
/**
 * This class implements a hash-based existence table.
 * 
 * @author Jeff Goldsworthy
 *
 */
public class StringTable {
    private String[] words = null;
    
    // Initial capacity. It's wicked big so that resize is unlikely.
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
     * This ctor is used during a resize.  It will create a new object with
     * the specified size.
     * 
     * @param initialCapacity
     * Specifies the initial capacity of the table.
     */
	private StringTable(int initialCapacity) {
        capacity = initialCapacity;
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

                // If capacity number of buckets were checked then do a resize
        		// on the array and retry the insert. This will only happen
        		// in cases when the capacity isn't a prime number and therefore
        		// a the double hash skip won't hit all of the buckets in the
        		// array.
                resize();
                insert(s);
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
            // String was found.
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
            		// There is nothing in the target bucket, the word does 
            		// not exist.
            		return false;
            	}
                
            	if (words[targetIndex].equals(s)) {
                    // The string was found in another bucket.
            		return true;
            	}
            }
            
            // The array is full and the value was not found.
            return false;
        }
	}
    
    
    /**
     * This method returns the target bucket index for the specified string.
     * 
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
        
        // Absolute this value because the hash code may be negative. Negatives
		// don't work so well as indexes into arrays.
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
		// making watches here. The nearest percent will do. We just need a 
		// threshold to trigger the resize before our performance dips in the 
		// table.
        return ((100 * used) / capacity);
	}
    
    
	/**
	 * This method gets the underlying array in the string table.
     * 
	 * @return
     * This method returns a reference to the string array.
	 */
    private String[] getArray() {
    	return words;
    }
    
	
    /**
     * This method resizes the array of the hash table by roughly doubling its
     * size.
     */
	private void resize() {
		// Resize the table and rehash the old values from the old array into
		// the new array. This likely won't be a prime, but I set the initial
		// value of the array to be wicked big, and I'm concerned about 
		// performance not necessarily space.
		int newCapacity = (2 * capacity);
        
		//System.out.println("WE ARE RESIZING THE STRING TABLE!!!!");
        
        // Create a new StringTable with the new capacity. Use the new object
		// to initialize the array, then take ownership of the array and leave
		// the temporary StringTable for the GC to cleanup. 
        StringTable resizedTable = new StringTable(newCapacity);
        
        for (int i = 0; i < words.length; ++i) {
        	// Traverse the old array and rehash those values into the new
        	// array.
        	if (words[i] != null) {
        		resizedTable.insert(words[i]);
        	}
        }
        
        // After the old values have been rehashed into the new larger table
        // set this table's array to the larger array.
        words = resizedTable.getArray();
        capacity = newCapacity;
	}
}
