package Crawler;

import java.io.*;
import java.util.regex.*;

/**
 * This program processes all text files in the "text_pages" directory
 * and uses regular expressions to detect phone numbers, email addresses,
 * and URLs present in each file.
 */
public class RegexExtractor {

    // Folder containing the text documents for analysis
    private static final String TEXT_DIR = "text_pages";

    // Regular expression to capture common phone number formats
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "\\b(\\+?\\d{1,2}[\\s-]?)?(\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4})\\b"
    );

    // Pattern to locate valid email addresses in the text
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,6}",
            Pattern.CASE_INSENSITIVE
        );

    // Pattern to identify HTTP and HTTPS links
    private static final Pattern URL_PATTERN = Pattern.compile(
        "https?://\\S+"
    );

    public static void main(String[] args) {

        File directory = new File(TEXT_DIR);

        // Filter only .txt files from the specified folder
        File[] textFiles = directory.listFiles((dir, name) -> name.endsWith(".txt"));

        if (textFiles == null || textFiles.length == 0) {
            System.out.println("No .txt files found in directory: " + TEXT_DIR);
            return;
        }

        // Loop through each text file to search for pattern matches
        for (File file : textFiles) {
            System.out.println("----- Analyzing File: " + file.getName() + " -----");

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;

                // Read file line by line
                while ((line = reader.readLine()) != null) {

                    // Check for phone number matches
                    Matcher phoneMatcher = PHONE_PATTERN.matcher(line);
                    while (phoneMatcher.find()) {
                        System.out.println("Phone Number Found: " + phoneMatcher.group());
                    }

                    // Check for email address matches
                    Matcher emailMatcher = EMAIL_PATTERN.matcher(line);
                    while (emailMatcher.find()) {
                        System.out.println("Email Address Found: " + emailMatcher.group());
                    }

                    // Check for URL matches
                    Matcher urlMatcher = URL_PATTERN.matcher(line);
                    while (urlMatcher.find()) {
                        System.out.println("URL Found: " + urlMatcher.group());
                    }
                }

            } catch (IOException e) {
                System.err.println("Unable to read file: " + file.getName() + " - " + e.getMessage());
            }
        }
    }
}
