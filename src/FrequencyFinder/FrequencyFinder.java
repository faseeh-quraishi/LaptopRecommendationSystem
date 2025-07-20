package FrequencyFinder;
import java.io.*;
import java.util.*;

public class FrequencyFinder {

    // Structure for storing the line no. and position of a match
    public static class MatchRecord {
        public int lineNumber;
        public int position;

        public MatchRecord(int lineNumber, int position) {
            this.lineNumber = lineNumber;
            this.position = position;
        }
    }

    // Finds all matches of the given word in the provided CSV file.
    public static List<MatchRecord> findMatches(String word, String csvFile) {
        List<MatchRecord> matchesFound = new ArrayList<>();
        int lineCount = 1;

        try (BufferedReader fileReader = new BufferedReader(new FileReader(csvFile))) {
            String currentLine;
            int actualLine = 1; // Start after header

            while ((currentLine = fileReader.readLine()) != null) {
                if (lineCount++ == 1) continue; // Skipping header row

                // Convert current line and word to lowercase for case-insensitive match
                String lineToSearch = currentLine.toLowerCase();
                List<Integer> foundIndexes = locateOccurrences(lineToSearch, word.toLowerCase());

                // Record all found positions
                for (int index : foundIndexes) {
                    matchesFound.add(new MatchRecord(actualLine + 1, index)); // +1 to match real CSV indexing
                }

                actualLine++;
            }

        } catch (IOException e) {
            System.err.println("Unable to read the file: " + e.getMessage());
        }

        return matchesFound;
    }

    // Core Boyer-Moore matching logic
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

    // Builds bad character shift table
    private static Map<Character, Integer> buildBadCharacterTable(String pattern) {
        Map<Character, Integer> table = new HashMap<>();
        for (int i = 0; i < pattern.length(); i++) {
            table.put(pattern.charAt(i), i); // store the latest index of each character
        }
        return table;
    }
}
