package Crawler;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class RegexExtractor {

    private static final String TEXT_BASE_DIR = "text_pages";

    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "\\b(\\+?\\d{1,2}[\\s-]?)?(\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4})\\b"
    );

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,6}",
        Pattern.CASE_INSENSITIVE
    );

    private static final Pattern URL_PATTERN = Pattern.compile("https?://\\S+");

    /**
     * Extracts phone numbers, emails, and URLs from all .txt files under text_pages/{domain}/
     *
     * @param domain The domain to extract from (e.g., "hp.com")
     * @return A map containing matched phone numbers, emails, and URLs
     */
    public static Map<String, String> extractMatchesForDomain(String domain) {
        File domainDir = new File(TEXT_BASE_DIR, domain);

        if (!domainDir.exists() || !domainDir.isDirectory()) {
            System.out.println("❌ No text folder found for domain: " + domain);
            return Collections.emptyMap();
        }

        File[] textFiles = domainDir.listFiles((dir, name) -> name.endsWith(".txt"));

        if (textFiles == null || textFiles.length == 0) {
            System.out.println("⚠️ No text files found in: " + domainDir.getAbsolutePath());
            return Collections.emptyMap();
        }

        System.out.println("🔍 Extracting patterns from: " + domainDir.getAbsolutePath());

        Set<String> phones = new LinkedHashSet<>();
        Set<String> emails = new LinkedHashSet<>();
        Set<String> urls = new LinkedHashSet<>();

        for (File file : textFiles) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Matcher phoneMatcher = PHONE_PATTERN.matcher(line);
                    while (phoneMatcher.find()) phones.add(phoneMatcher.group());

                    Matcher emailMatcher = EMAIL_PATTERN.matcher(line);
                    while (emailMatcher.find()) emails.add(emailMatcher.group());

                    Matcher urlMatcher = URL_PATTERN.matcher(line);
                    while (urlMatcher.find()) urls.add(urlMatcher.group());
                }
            } catch (IOException e) {
                System.err.println("⚠️ Failed to read: " + file.getName() + " - " + e.getMessage());
            }
        }

        Map<String, String> result = new LinkedHashMap<>();
        result.put("Phone", String.join(",", phones));
        result.put("Email", String.join(",", emails));
        result.put("URL", String.join(",", urls));

        return result;
    }
}
