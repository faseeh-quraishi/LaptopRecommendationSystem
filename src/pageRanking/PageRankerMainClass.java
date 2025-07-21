package pageRanking;

import java.io.*;
import java.util.*;

import InvertedIndex.InvertedIndexCSV;

public class PageRankerMainClass {

    private static final String DATA_FILE = "all_laptops_data.csv";

    // Public method to be used from API or testing
    public static List<Laptop> pageRanking(String keyword, Set<Integer> rowsToRank) throws IOException {
        List<Laptop> rankedLaptops = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(DATA_FILE));
        String line;
        int lineNum = 1;

        // Skip the header
        br.readLine(); // header
        lineNum++;

        while ((line = br.readLine()) != null) {
            if (rowsToRank.contains(lineNum)) {
                int freq = countKeywordFrequency(line, keyword);
                if (freq > 0) {
                    String[] values = line.split(",", -1); // avoid ArrayIndexOutOfBounds

                    if (values.length >= 9) {
                        Laptop laptop = new Laptop(
                            values[0].trim(), // Brand Name
                            values[1].trim(), // Product
                            values[2].trim(), // Price
                            values[3].trim(), // Processor
                            values[4].trim(), // Memory
                            values[5].trim(), // Storage
                            values[6].trim(), // Graphics
                            values[7].trim(), // Display
                            values[8].trim(), // OS
                            values[9].trim()  // Image
                        );
                        rankedLaptops.add(laptop);
                    }
                }
            }
            lineNum++;
        }
        br.close();

        // Optionally sort by frequency if needed later
        return rankedLaptops;
    }

    // Utility method to count keyword frequency in a line
    private static int countKeywordFrequency(String line, String keyword) {
        int count = 0;
        String lowerLine = line.toLowerCase();
        String lowerKeyword = keyword.toLowerCase();

        int index = 0;
        while ((index = lowerLine.indexOf(lowerKeyword, index)) != -1) {
            count++;
            index += lowerKeyword.length();
        }
        return count;
    }

    // Optional main method for testing
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter keyword to rank pages: ");
        String keyword = scanner.nextLine();

        try {
            Set<Integer> rowsToRank = InvertedIndexCSV.InvertedIndexing(keyword, DATA_FILE);
            if (rowsToRank.isEmpty()) {
                System.out.println("No matching rows found for keyword: " + keyword);
            } else {
                List<Laptop> ranked = pageRanking(keyword, rowsToRank);
                System.out.println("Ranked results:");
                for (Laptop laptop : ranked) {
                    System.out.printf("Brand: %s | Product: %s | Price: %s%n",
                        laptop.brandName, laptop.product, laptop.price);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading data file: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}
