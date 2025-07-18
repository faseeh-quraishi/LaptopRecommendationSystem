package FrequencyFinder;
import java.io.*;
import java.util.*;

public class FrequencyFinder {

    // Structure for storing the line no. and position
    static class MatchRecord {
        int lineNumber;
        int position;

        MatchRecord(int lineNumber, int position) {
            this.lineNumber = lineNumber;
            this.position = position;
        }
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        // Asking the user for word and converting it into lowercase
        System.out.print("Enter the word to search for: ");
        String targetWord = input.nextLine().toLowerCase(); 

        String csvFile = "products_final.csv";
        int matchCount = 0;
        List<MatchRecord> matchesFound = new ArrayList<>();

        try (BufferedReader fileReader = new BufferedReader(new FileReader(csvFile))) {
            String currentLine;
            int lineInFile = 0;
            int actualDataLine = 1; // To skip the title line from csv(Product name, memory, storage,...)

            while ((currentLine = fileReader.readLine()) != null) {
                lineInFile++;

                if (lineInFile == 1) {
                    continue; // Skipping the first row as it is title row
                }

                // Converting all words to lowercase
                String lineToSearch = currentLine.toLowerCase();

                // Finding matching position of target word in the current line
                List<Integer> foundIndexes = locateOccurrences(lineToSearch, targetWord);

                // Storing the matches found
                for (int index : foundIndexes) {
                    // Adding one due to search started from line 2
                    matchesFound.add(new MatchRecord(actualDataLine + 1, index)); 
                    matchCount++;
                }

                actualDataLine++;
            }

        } catch (IOException e) {
            System.err.println("Unable to read the file: " + e.getMessage());
            return;
        }

        // Final output showing total and individual match locations
        System.out.println("\nThe word \"" + targetWord + "\" occurred " + matchCount + " times\n");
        System.out.println("Displaying locations...");
        for (MatchRecord record : matchesFound) {
            System.out.println("Found '" + targetWord + "' at index " + record.position + " on line " + record.lineNumber);
        }

        input.close();
    }

    // Boyer-Moore Implementation 
    private static List<Integer> locateOccurrences(String textLine, String wordPattern) {
        Map<Character, Integer> shiftTable = buildBadCharacterTable(wordPattern);
        List<Integer> occurrences = new ArrayList<>();
        int offset = 0;

        while (offset <= (textLine.length() - wordPattern.length())) {
            int compareIndex = wordPattern.length() - 1;

            // Start matching from the end of the pattern
            while (compareIndex >= 0 && wordPattern.charAt(compareIndex) == textLine.charAt(offset + compareIndex)) {
                compareIndex--;
            }

            if (compareIndex < 0) {
                // Full match found at current offset
                occurrences.add(offset);
                offset += (offset + wordPattern.length() < textLine.length()) ? wordPattern.length() : 1;
            } else {
                char mismatchChar = textLine.charAt(offset + compareIndex);
                int shift = shiftTable.getOrDefault(mismatchChar, -1);
                offset += Math.max(1, compareIndex - shift);
            }
        }

        return occurrences;
    }

    // Building bad character table to calculate the shift
    private static Map<Character, Integer> buildBadCharacterTable(String pattern) {
        Map<Character, Integer> table = new HashMap<>();
        for (int i = 0; i < pattern.length(); i++) {
            table.put(pattern.charAt(i), i); // store the latest index of each character
        }
        return table;
    }
}