package pageRanking;

import java.io.*;
import java.util.*;
import AssignmentCodes.InvertedIndexCSV;

public class PageRankerMainClass {

    private static final String DATA_FILE = "all_laptops_data.csv";

    // Represents a product or row with frequency score
    public static class RankedRow {
        public int rowNum;
        public String lineContent;
        public int frequency;

        public RankedRow(int rowNum, String lineContent, int frequency) {
            this.rowNum = rowNum;
            this.lineContent = lineContent;
            this.frequency = frequency;
        }
    }

    public static List<RankedRow> pageRanking(String keyword, Set<Integer> rowsToRank) throws IOException {
        List<RankedRow> rankedResults = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(DATA_FILE));
        String line;
        int lineNum = 1;

        while ((line = br.readLine()) != null) {
            if (rowsToRank.contains(lineNum)) {
                int freq = countKeywordFrequency(line, keyword);
                if (freq > 0) {
                    rankedResults.add(new RankedRow(lineNum, line, freq));
                }
            }
            lineNum++;
        }
        br.close();

        // Sort descending by frequency
        rankedResults.sort((a, b) -> Integer.compare(b.frequency, a.frequency));
        return rankedResults;
    }

    // Counts occurrences of keyword (case-insensitive) in the text line
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

    // For testing or running from main
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter keyword to rank pages: ");
        String keyword = scanner.nextLine();

        try {
            // Get relevant rows from Inverted Index
            Set<Integer> rowsToRank = InvertedIndexCSV.InvertedIndexing(keyword, DATA_FILE);
            if (rowsToRank.isEmpty()) {
                System.out.println("No matching rows found for keyword: " + keyword);
            } else {
                List<RankedRow> ranked = pageRanking(keyword, rowsToRank);
                System.out.println("Ranked results:");
                for (RankedRow r : ranked) {
                    System.out.printf("Row %d (Frequency: %d): %s%n", r.rowNum, r.frequency, r.lineContent);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading data file: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}
