package Crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;

/**
 * This program processes HTML files stored in the "saved_pages" folder.
 * It extracts readable text content using the Jsoup library and
 * generates corresponding plain text files inside the "text_pages" directory.
 */
public class HTMLtoTextConverter {

    // Folder containing the input HTML files
    private static final String HTML_DIR = "saved_pages";

    // Folder where the text output will be saved
    private static final String TEXT_DIR = "text_pages";

    public static void main(String[] args) {

        // Reference to the HTML input directory
        File htmlInputFolder = new File(HTML_DIR);

        // Filter only files ending with .html extension
        File[] htmlFiles = htmlInputFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".html"));

        // If no HTML files found, exit the program
        if (htmlFiles == null || htmlFiles.length == 0) {
            System.out.println("No HTML documents found in directory: " + HTML_DIR);
            return;
        }

        // Create the destination folder for text files if it doesn't already exist
        File textOutputFolder = new File(TEXT_DIR);
        if (!textOutputFolder.exists()) {
            textOutputFolder.mkdir();
        }

        // Loop through each HTML file for processing
        for (File htmlFile : htmlFiles) {
            try {
                // Load the HTML file using Jsoup with UTF-8 encoding
                Document doc = Jsoup.parse(htmlFile, "UTF-8");

                // Extract all the visible text content from the HTML structure
                String extractedText = doc.text();

                // Prepare the output file name by changing the extension to .txt
                String textFileName = htmlFile.getName().replace(".html", ".txt");
                File textFile = new File(textOutputFolder, textFileName);

                // Write the plain text content into the new file
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(textFile))) {
                    writer.write(extractedText);
                }

                // Log the output path to the console
                System.out.println("Text extracted and saved to: " + textFile.getAbsolutePath());

            } catch (IOException e) {
                System.err.println("Failed to convert " + htmlFile.getName() + ": " + e.getMessage());
            }
        }
    }
}
