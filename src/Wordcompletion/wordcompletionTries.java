package Wordcompletion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class wordcompletionTries {

    private Trie trie;

    public wordcompletionTries(String filePath) {
        trie = new Trie();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine(); // Skip header
            if (line == null) {
                System.out.println("CSV file is empty.");
            }
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                for (String word : values) {
                    word = word.trim();
                    if (!word.isEmpty()) {
                        trie.insert(word);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> wordCompletion(String prefix) {
        return trie.searchPrefix(prefix);
    }

//    public static void main(String[] args) {
//        String filePath = "all_laptops_data.csv";
//        wordcompletionTries wc = new wordcompletionTries(filePath);
//
//        // Example usage: you can call this method with any prefix
//        List<String> completions = wc.wordCompletion("");
//
//        // Display the results
//        for (String word : completions) {
//            System.out.println(word);
//        }
//    }
}
