package InvertedIndex;
import java.io.*;
import java.util.*;

public class InvertedIndexCSV {

    // Trie node definition
    static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEndOfWord = false;
        Set<Integer> rows = new HashSet<>();
    }

    static TrieNode root = new TrieNode();

    // Insert a word and associate it with a row number
    public static void insert(String word, int rowNum) {
        TrieNode current = root;
        for (char ch : word.toLowerCase().toCharArray()) {
            current = current.children.computeIfAbsent(ch, k -> new TrieNode());
        }
        current.isEndOfWord = true;
        current.rows.add(rowNum);
    }

    // Build the inverted index from the CSV file
    public static void buildIndex(String csvPath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(csvPath));
        String line;
        int row = 1; // Starting from 1 for human-friendly indexing
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(","); // CSV columns
            for (String field : parts) {
                String[] words = field.split("[^a-zA-Z0-9]+"); // Remove punctuation, split by non-alphanumeric
                for (String word : words) {
                    if (!word.isEmpty()) {
                        insert(word, row);
                    }
                }
            }
            row++;
        }
        br.close();
    }

    // Search for a word and return matching rows
    public static Set<Integer> search(String word) {
        TrieNode current = root;
        for (char ch : word.toLowerCase().toCharArray()) {
            current = current.children.get(ch);
            if (current == null)
                return Collections.emptySet();
        }
        return current.isEndOfWord ? current.rows : Collections.emptySet();
    }

    // Main method
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String[] fileName = { "all_laptops_data.csv" };
        try {
            System.out.print("Enter a word to search: ");
            String query = scanner.nextLine();
            for(String file : fileName) {
                //Reseting the TrieNode for next file
                root = new TrieNode();
                System.out.println("Building index from " + file +" ...");
                buildIndex(file);
                Set<Integer> result = search(query);
                if (result.isEmpty()) {
                    System.out.println("Word not found in any row.");
                } else {
                    System.out.println("Word found in the following rows:");
                    for (int row : result) {
                        System.out.println("- Row " + row + "  File: " + file);
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    public static Set<Integer> InvertedIndexing(String query, String fileName) {
        try {
            root = new TrieNode();
            System.out.println("Building index from " + fileName + " ...");
            buildIndex(fileName);

            // Split query into individual words (e.g., "Apple Macbook" → ["Apple",
            // "Macbook"])
            String[] words = query.split("[^a-zA-Z0-9]+");

            Set<Integer> result = null;
            for (String word : words) {
                if (word.isEmpty())
                    continue;
                Set<Integer> wordRows = search(word);
                if (result == null) {
                    result = new HashSet<>(wordRows);
                } else {
                    result.retainAll(wordRows); // Intersection: only keep rows containing all words
                }

                // Early exit if no rows are common
                if (result.isEmpty()) {
                    break;
                }
            }
            return result != null ? result : Collections.emptySet();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            return Collections.emptySet();
        }
    }
}
