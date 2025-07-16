package AssignmentCodes.WordCompletionTries;

import java.util.HashMap;

/**
 * TrieNode represents a single node within the Trie data structure.
 * Each node contains a map of its child characters and a flag to indicate
 * whether it marks the end of a complete word.
 */
public class TrieNode {

    // Stores child nodes: each character points to its corresponding TrieNode
    public HashMap<Character, TrieNode> children;

    // This flag is set to true if the node marks the end of a valid word
    public boolean isEndOfWord;

    /**
     * Constructor to initialize a TrieNode.
     * By default, it creates an empty map for children and sets the end-of-word flag to false.
     */
    public TrieNode() {
        // Initialize the child map to store branches for each character
        children = new HashMap<>();

        // Initially, the node does not represent the end of any word
        isEndOfWord = false;
    }
}
