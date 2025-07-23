package Crawler;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class RegexExtractor {

    private static final String TEXT_DIR = "text_pages";

    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "\\b(\\+?\\d{1,2}[\\s-]?)?(\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4})\\b"
    );

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
    );

    private static final Pattern URL_PATTERN = Pattern.compile(
        "https?://\\S+"
    );

    public static void main(String[] args) {
        Map<String, String> result = extractAllMatches();

        System.out.println("----- Summary of Matches Across All Files -----");
        for (Map.Entry<String, String> entry : result.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    public static Map<String, String> extractAllMatches() {
        Set<String> phones = new LinkedHashSet<>();
        Set<String> emails = new LinkedHashSet<>();
        Set<String> urls = new LinkedHashSet<>();

        File directory = new File(TEXT_DIR);
        File[] textFiles = directory.listFiles((dir, name) -> name.endsWith(".txt"));

        if (textFiles == null || textFiles.length == 0) {
            System.out.println("No .txt files found in directory: " + TEXT_DIR);
            return Collections.emptyMap();
        }

        for (File file : textFiles) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;

                while ((line = reader.readLine()) != null) {
                    Matcher phoneMatcher = PHONE_PATTERN.matcher(line);
                    while (phoneMatcher.find()) {
                        phones.add(phoneMatcher.group());
                    }

                    Matcher emailMatcher = EMAIL_PATTERN.matcher(line);
                    while (emailMatcher.find()) {
                        emails.add(emailMatcher.group());
                    }

                    Matcher urlMatcher = URL_PATTERN.matcher(line);
                    while (urlMatcher.find()) {
                        urls.add(urlMatcher.group());
                    }
                }
            } catch (IOException e) {
                System.err.println("Unable to read file: " + file.getName() + " - " + e.getMessage());
            }
        }

        Map<String, String> result = new LinkedHashMap<>();
        result.put("Phone", String.join(",", phones));
        result.put("Email", String.join(",", emails));
        result.put("URL", String.join(",", urls));

        return result;
    }
}
