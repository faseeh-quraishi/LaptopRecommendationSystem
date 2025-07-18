package SearchFrequency;
import java.io.*;
import java.util.*;

public class SearchFreq {

    private class Node {
        String word;
        int count, height;
        Node left, right;

        Node(String word) {
            this.word = word;
            this.count = 1;
            this.height = 1;
        }
    }

    private Node root;
    private static final String FILE_NAME = "search_freq.txt";

    public SearchFreq() {
        loadFromFile();
    }

    // Public method to add/increment word count
    public Map<String, Integer> addSearchedWordCount(String word) {
        root = insert(root, word);
        saveToFile();
        int count = getCount(root, word);
        return Map.of(word, count);
    }

    // Public method to get top 5 searched words
    public List<Map.Entry<String, Integer>> getTop5SearchedWords() {
        List<Map.Entry<String, Integer>> entries = new ArrayList<>();
        inOrderCollect(root, entries);
        entries.sort((a, b) -> {
            int cmp = Integer.compare(b.getValue(), a.getValue());
            return cmp != 0 ? cmp : a.getKey().compareTo(b.getKey());
        });
        return entries.subList(0, Math.min(5, entries.size()));
    }

    // Insert or increment word
    private Node insert(Node node, String word) {
        if (node == null) return new Node(word);

        int cmp = word.compareTo(node.word);
        if (cmp < 0)
            node.left = insert(node.left, word);
        else if (cmp > 0)
            node.right = insert(node.right, word);
        else {
            node.count++;
            return node;
        }

        updateHeight(node);
        return balance(node);
    }

    // Get count of a word
    private int getCount(Node node, String word) {
        while (node != null) {
            int cmp = word.compareTo(node.word);
            if (cmp == 0) return node.count;
            node = (cmp < 0) ? node.left : node.right;
        }
        return 0;
    }

    // Traverse and collect word-counts
    private void inOrderCollect(Node node, List<Map.Entry<String, Integer>> list) {
        if (node != null) {
            inOrderCollect(node.left, list);
            list.add(new AbstractMap.SimpleEntry<>(node.word, node.count));
            inOrderCollect(node.right, list);
        }
    }

    // Height and balancing
    private int height(Node node) {
        return node == null ? 0 : node.height;
    }

    private void updateHeight(Node node) {
        node.height = 1 + Math.max(height(node.left), height(node.right));
    }

    private int getBalance(Node node) {
        return height(node.left) - height(node.right);
    }

    private Node balance(Node node) {
        int bal = getBalance(node);

        if (bal > 1) {
            if (getBalance(node.left) < 0)
                node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        if (bal < -1) {
            if (getBalance(node.right) > 0)
                node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    private Node rotateRight(Node y) {
        Node x = y.left;
        Node T = x.right;
        x.right = y;
        y.left = T;
        updateHeight(y);
        updateHeight(x);
        return x;
    }

    private Node rotateLeft(Node x) {
        Node y = x.right;
        Node T = y.left;
        y.left = x;
        x.right = T;
        updateHeight(x);
        updateHeight(y);
        return y;
    }

    // Load from file into AVL Tree
    private void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    String word = parts[0].trim();
                    int count = Integer.parseInt(parts[1].trim());
                    for (int i = 0; i < count; i++)
                        root = insert(root, word);
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    // Save AVL Tree to file
    private void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            List<Map.Entry<String, Integer>> entries = new ArrayList<>();
            inOrderCollect(root, entries);
            for (Map.Entry<String, Integer> entry : entries)
                bw.write(entry.getKey() + "=" + entry.getValue() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // For testing
    public static void main(String[] args) {
        SearchFreq sf = new SearchFreq();

        sf.addSearchedWordCount("java");
        sf.addSearchedWordCount("python");
        sf.addSearchedWordCount("java");
        sf.addSearchedWordCount("kotlin");
        sf.addSearchedWordCount("go");
        sf.addSearchedWordCount("java");

        System.out.println("Top 5 searched words:");
        for (Map.Entry<String, Integer> entry : sf.getTop5SearchedWords()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
