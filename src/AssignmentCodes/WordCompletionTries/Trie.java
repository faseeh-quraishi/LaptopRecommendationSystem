package AssignmentCodes.WordCompletionTries;

import java.util.ArrayList;
import java.util.List;

/**
 * The Trie class implements a prefix tree used for efficient storage and retrieval
 * of strings, particularly useful for autocomplete or prefix-matching operations.
 */
public class Trie {

    // Root node of the Trie — it doesn't contain any character itself
    private TrieNode root;

    /**
     * Constructor initializes the Trie with an empty root node.
     */
    public Trie() {
        root = new TrieNode();
    }

    /**
     * Inserts a new word into the Trie.
     * Each character is added level-by-level, creating nodes as needed.
     *
     * @param word The word to be added into the Trie
     */
    public void insert(String word) {
        TrieNode node = root;

        // Convert word to lowercase to ensure case-insensitive matching
        for (char c : word.toLowerCase().toCharArray()) {
            // If the current character doesn't exist in the map, add a new node
            node.children.putIfAbsent(c, new TrieNode());

            // Move to the next node in the path
            node = node.children.get(c);
        }

        // After all characters are added, mark the end node as a complete word
        node.isEndOfWord = true;
    }

    /**
     * Searches the Trie for all words that begin with the specified prefix.
     *
     * @param prefix The starting substring to search for
     * @return A list of all complete words that begin with this prefix
     */
    public List<String> searchPrefix(String prefix) {
        TrieNode node = root;

        // Traverse down the Trie to find the node matching the last prefix character
        for (char c : prefix.toLowerCase().toCharArray()) {
            node = node.children.get(c);
            if (node == null) {
                // Prefix not found in Trie; return empty result list
                return new ArrayList<>();
            }
        }

        // If prefix exists, use DFS to collect all possible completions
        List<String> results = new ArrayList<>();
        dfs(node, prefix.toLowerCase(), results);
        return results;
    }

    /**
     * Helper method to perform Depth-First Search (DFS) starting from a given node.
     * Used to explore all paths and collect valid words that share a common prefix.
     *
     * @param node    Current TrieNode being visited
     * @param path    The accumulated string formed so far
     * @param results The list to collect matched words
     */
    private void dfs(TrieNode node, String path, List<String> results) {
        // If this node marks a valid word, add it to the result list
        if (node.isEndOfWord) {
            results.add(path);
        }

        // Explore all children recursively, appending each character to the path
        for (char c : node.children.keySet()) {
            dfs(node.children.get(c), path + c, results);
        }
    }
}
