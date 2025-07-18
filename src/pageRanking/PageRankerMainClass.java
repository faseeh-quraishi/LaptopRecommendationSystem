package pageRanking;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class PageRankerMainClass {

    // Class representing a product
    static class Product {
        String brand;
        String name;
        String price;
        String processor;
        String connectivity;
        String storage;
        String memory;
        String display;
        String os;

        int frequency;  // keyword frequency for ranking

        public Product(String[] columns, int frequency) {
            this.brand = columns[0];
            this.name = columns[1];
            this.price = columns[2];
            this.processor = columns[3];
            this.connectivity = columns[4];
            this.storage = columns[5];
            this.memory = columns[6];
            this.display = columns[7];
            this.os = columns.length > 8 ? columns[8] : "N/A";
            this.frequency = frequency;
        }

        public String toString() {
            return "{ \"brand\": \"" + brand + "\", \"name\": \"" + name + "\", \"price\": \"" + price +
                   "\", \"processor\": \"" + processor + "\", \"storage\": \"" + storage +
                   "\", \"memory\": \"" + memory + "\", \"display\": \"" + display + "\", \"os\": \"" + os +
                   "\", \"frequency\": " + frequency + " }";
        }
    }

    public List<String> pageRanking(String keyword, Set<Integer> rowsToRank) {
        List<String> result = new ArrayList<>();
        List<Product> products = new ArrayList<>();
        String filePath = "all_laptops_data.csv";

        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);

            for (int i = 0; i < lines.size(); i++) {
                if (!rowsToRank.contains(i)) continue;  // Skip rows not in inverted index output

                String line = lines.get(i);
                String[] columns = line.split(",", -1);  // Preserve empty columns

                String rowText = line.toLowerCase();
                int freq = countOccurrences(rowText, keyword.toLowerCase());

                if (freq > 0) {
                    products.add(new Product(columns, freq));
                }
            }

            // Sort products by frequency in descending order
            products.sort((a, b) -> Integer.compare(b.frequency, a.frequency));

            for (Product p : products) {
                result.add(p.toString());
            }

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

        return result;
    }

    private int countOccurrences(String text, String keyword) {
        int count = 0;
        int index = text.indexOf(keyword);
        while (index != -1) {
            count++;
            index = text.indexOf(keyword, index + keyword.length());
        }
        return count;
    }
}
