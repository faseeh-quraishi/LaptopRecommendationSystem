package AssignmentCodes.WordCompletionTries;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * This class demonstrates word completion (autocomplete) functionality
 * by reading words from a CSV file and storing them in a Trie data structure.
 * It allows users to input a prefix and returns a list of matching words.
 */
public class wordcompletionTries {
    public static void main(String[] args) {

        // Create a new Trie instance to store the vocabulary
        Trie trie = new Trie();

        // Absolute path to the CSV file containing the vocabulary data
        String filePath = "C:\\Users\\rk133\\eclipse-workspace\\Assignment 2\\merged_laptops.csv";

        // Try-with-resources ensures the reader is closed automatically
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            // Read the first line (usually the header) and skip it
            String line = reader.readLine();
            if (line == null) {
                System.out.println("CSV file is empty.");
                return;
            }

            // Loop through each line in the file starting from the second line
            while ((line = reader.readLine()) != null) {
                // Split the current row into values based on commas
                String[] values = line.split(",");

                // Insert each non-empty word into the Trie
                for (String word : values) {
                    word = word.trim(); // Remove leading/trailing whitespace
                    if (!word.isEmpty()) {
                        trie.insert(word); // Add the cleaned word to the Trie
                    }
                }
            }

            // Create a Scanner object to get user input from the console
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter a prefix to search: ");
            String prefix = scanner.nextLine(); // Read the prefix input by the user

            // Search for all words in the Trie that begin with the given prefix
            List<String> completions = trie.searchPrefix(prefix);

            // Display the matching words to the user
            System.out.println("\nWords with prefix \"" + prefix + "\":");
            for (String word : completions) {
                System.out.println(word);
            }

        } catch (IOException e) {
            // Handle file read errors gracefully and print stack trace
            e.printStackTrace();
        }
    }
}
