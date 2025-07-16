import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Advanced Search Analytics Engine
 * Implements a sophisticated frequency tracking system using adaptive tree structures
 */
public class SearchFrequencyTracker {

    // Core components
    private final AdvancedTreeStructure treeManager;
    private final QueryLogManager logManager;
    private final DataSourceProcessor dataProcessor;
    private final UserInterfaceController uiController;
    private final Map<String, Long> timestampCache;

    // Configuration constants
    private static final String DEFAULT_EXCEL_FILE = "Products_Final (3).xlsx";
    private static final String PRODUCT_COLUMN_HEADER = "ProductName";
    private static final int MAX_DISPLAY_ITEMS = 50;

    public SearchFrequencyTracker() {
        this.treeManager = new AdvancedTreeStructure();
        this.logManager = new QueryLogManager();
        this.dataProcessor = new DataSourceProcessor();
        this.uiController = new UserInterfaceController();
        this.timestampCache = new ConcurrentHashMap<>();
        initializeSystem();
    }

    /**
     * Initialize system components and load base data
     */
    private void initializeSystem() {
        System.out.println("Initializing Search Analytics Engine...");
        dataProcessor.loadFromExcelSource(DEFAULT_EXCEL_FILE);
        System.out.println("System ready for operation.");
    }

    /**
     * Enhanced tree node with metadata support
     */
    private static class EnhancedNode {
        private String dataValue;
        private int occurrenceCount;
        private EnhancedNode leftBranch;
        private EnhancedNode rightBranch;
        private int treeDepth;
        private long lastAccessTime;
        private double priorityScore;

        EnhancedNode(String value) {
            this.dataValue = value;
            this.occurrenceCount = 1;
            this.treeDepth = 1;
            this.lastAccessTime = System.currentTimeMillis();
            this.priorityScore = 1.0;
        }

        void incrementUsage() {
            this.occurrenceCount++;
            this.lastAccessTime = System.currentTimeMillis();
            this.priorityScore += Math.log(occurrenceCount) * 0.1;
        }
    }

    /**
     * Self-balancing tree implementation with advanced features
     */
    private class AdvancedTreeStructure {
        private EnhancedNode rootElement;
        private final Map<String, EnhancedNode> nodeRegistry;
        private int totalNodes;

        AdvancedTreeStructure() {
            this.nodeRegistry = new HashMap<>();
            this.totalNodes = 0;
        }

        /**
         * Calculate tree depth for balancing
         */
        private int determineDepth(EnhancedNode node) {
            return Optional.ofNullable(node).map(n -> n.treeDepth).orElse(0);
        }

        /**
         * Compute balance metric
         */
        private int computeBalance(EnhancedNode node) {
            if (node == null) return 0;
            return determineDepth(node.leftBranch) - determineDepth(node.rightBranch);
        }

        /**
         * Refresh node depth based on children
         */
        private void refreshDepth(EnhancedNode node) {
            if (node != null) {
                node.treeDepth = 1 + Math.max(
                        determineDepth(node.leftBranch),
                        determineDepth(node.rightBranch)
                );
            }
        }

        /**
         * Execute clockwise rotation
         */
        private EnhancedNode performClockwiseRotation(EnhancedNode pivot) {
            EnhancedNode newPivot = pivot.leftBranch;
            EnhancedNode transferredSubtree = newPivot.rightBranch;

            newPivot.rightBranch = pivot;
            pivot.leftBranch = transferredSubtree;

            refreshDepth(pivot);
            refreshDepth(newPivot);

            return newPivot;
        }

        /**
         * Execute counter-clockwise rotation
         */
        private EnhancedNode performCounterClockwiseRotation(EnhancedNode pivot) {
            EnhancedNode newPivot = pivot.rightBranch;
            EnhancedNode transferredSubtree = newPivot.leftBranch;

            newPivot.leftBranch = pivot;
            pivot.rightBranch = transferredSubtree;

            refreshDepth(pivot);
            refreshDepth(newPivot);

            return newPivot;
        }

        /**
         * Advanced insertion with automatic balancing
         */
        private EnhancedNode performInsertion(EnhancedNode current, String value) {
            // Base case: create new node
            if (current == null) {
                EnhancedNode newNode = new EnhancedNode(value);
                nodeRegistry.put(value.toLowerCase(), newNode);
                totalNodes++;
                return newNode;
            }

            int comparison = value.compareToIgnoreCase(current.dataValue);

            if (comparison < 0) {
                current.leftBranch = performInsertion(current.leftBranch, value);
            } else if (comparison > 0) {
                current.rightBranch = performInsertion(current.rightBranch, value);
            } else {
                // Value exists, update frequency
                current.incrementUsage();
                nodeRegistry.put(value.toLowerCase(), current);
                return current;
            }

            // Update depth and rebalance
            refreshDepth(current);
            return executeRebalancing(current, value);
        }

        /**
         * Sophisticated tree rebalancing algorithm
         */
        private EnhancedNode executeRebalancing(EnhancedNode node, String insertedValue) {
            int balanceFactor = computeBalance(node);

            // Handle left-heavy scenarios
            if (balanceFactor > 1) {
                if (insertedValue.compareToIgnoreCase(node.leftBranch.dataValue) < 0) {
                    return performClockwiseRotation(node);
                } else {
                    node.leftBranch = performCounterClockwiseRotation(node.leftBranch);
                    return performClockwiseRotation(node);
                }
            }

            // Handle right-heavy scenarios
            if (balanceFactor < -1) {
                if (insertedValue.compareToIgnoreCase(node.rightBranch.dataValue) > 0) {
                    return performCounterClockwiseRotation(node);
                } else {
                    node.rightBranch = performClockwiseRotation(node.rightBranch);
                    return performCounterClockwiseRotation(node);
                }
            }

            return node;
        }

        /**
         * Public interface for adding data
         */
        void addDataPoint(String value) {
            if (value != null && !value.trim().isEmpty()) {
                rootElement = performInsertion(rootElement, value.trim());
                timestampCache.put(value.toLowerCase(), System.currentTimeMillis());
            }
        }

        /**
         * Traverse tree and collect data with custom comparator
         */
        private void traverseAndCollect(EnhancedNode node, List<QueryResult> collector,
                                        BiFunction<EnhancedNode, EnhancedNode, Integer> comparator) {
            if (node != null) {
                traverseAndCollect(node.leftBranch, collector, comparator);
                collector.add(new QueryResult(node.dataValue, node.occurrenceCount,
                        node.lastAccessTime, node.priorityScore));
                traverseAndCollect(node.rightBranch, collector, comparator);
            }
        }

        /**
         * Retrieve all data points with sorting options
         */
        List<QueryResult> extractAllData(SortingStrategy strategy) {
            List<QueryResult> results = new ArrayList<>();
            traverseAndCollect(rootElement, results, null);

            switch (strategy) {
                case BY_FREQUENCY:
                    results.sort((a, b) -> Integer.compare(b.frequency, a.frequency));
                    break;
                case BY_RECENCY:
                    results.sort((a, b) -> Long.compare(b.timestamp, a.timestamp));
                    break;
                case BY_PRIORITY:
                    results.sort((a, b) -> Double.compare(b.priority, a.priority));
                    break;
                case ALPHABETICAL:
                    results.sort((a, b) -> a.term.compareToIgnoreCase(b.term));
                    break;
            }

            return results;
        }

        int getTotalNodes() {
            return totalNodes;
        }
    }

    /**
     * Query result data structure
     */
    private static class QueryResult {
        final String term;
        final int frequency;
        final long timestamp;
        final double priority;

        QueryResult(String term, int frequency, long timestamp, double priority) {
            this.term = term;
            this.frequency = frequency;
            this.timestamp = timestamp;
            this.priority = priority;
        }
    }

    /**
     * Sorting strategies for data presentation
     */
    private enum SortingStrategy {
        BY_FREQUENCY, BY_RECENCY, BY_PRIORITY, ALPHABETICAL
    }

    /**
     * Query log management system
     */
    private class QueryLogManager {
        private final Deque<String> queryHistory;
        private final Map<String, Integer> sessionStats;
        private int totalQueries;

        QueryLogManager() {
            this.queryHistory = new ArrayDeque<>();
            this.sessionStats = new HashMap<>();
            this.totalQueries = 0;
        }

        void recordQuery(String query) {
            if (query != null && !query.trim().isEmpty()) {
                String cleanQuery = query.trim();
                queryHistory.addLast(cleanQuery);
                sessionStats.merge(cleanQuery.toLowerCase(), 1, Integer::sum);
                totalQueries++;

                // Maintain reasonable history size
                if (queryHistory.size() > 1000) {
                    queryHistory.removeFirst();
                }
            }
        }

        List<String> getRecentQueries(int limit) {
            return queryHistory.stream()
                    .skip(Math.max(0, queryHistory.size() - limit))
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        }

        Map<String, Integer> getSessionStatistics() {
            return new HashMap<>(sessionStats);
        }

        int getTotalQueryCount() {
            return totalQueries;
        }

        void clearHistory() {
            queryHistory.clear();
            sessionStats.clear();
            totalQueries = 0;
        }
    }

    /**
     * Excel data processing component
     */
    private class DataSourceProcessor {

        void loadFromExcelSource(String filePath) {
            if (!Files.exists(Paths.get(filePath))) {
                System.err.println("Warning: Excel file not found at " + filePath);
                return;
            }

            try (FileInputStream inputStream = new FileInputStream(filePath);
                 Workbook workbook = new XSSFWorkbook(inputStream)) {

                Sheet primarySheet = workbook.getSheetAt(0);
                int productColumnIndex = locateProductColumn(primarySheet);

                if (productColumnIndex >= 0) {
                    processSpreadsheetData(primarySheet, productColumnIndex);
                    System.out.println("Successfully loaded " +
                            treeManager.getTotalNodes() + " products from database.");
                } else {
                    System.err.println("Error: Product column not found in spreadsheet.");
                }

            } catch (IOException e) {
                System.err.println("File processing error: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Unexpected processing error: " + e.getMessage());
            }
        }

        private int locateProductColumn(Sheet sheet) {
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) return -1;

            return Stream.iterate(0, i -> i < headerRow.getLastCellNum(), i -> i + 1)
                    .filter(i -> {
                        Cell cell = headerRow.getCell(i);
                        return cell != null &&
                                PRODUCT_COLUMN_HEADER.equalsIgnoreCase(cell.getStringCellValue().trim());
                    })
                    .findFirst()
                    .orElse(-1);
        }

        private void processSpreadsheetData(Sheet sheet, int columnIndex) {
            for (int rowIdx = 1; rowIdx <= sheet.getLastRowNum(); rowIdx++) {
                Row currentRow = sheet.getRow(rowIdx);
                if (currentRow != null) {
                    Cell dataCell = currentRow.getCell(columnIndex);
                    if (dataCell != null && dataCell.getCellType() == CellType.STRING) {
                        String productName = dataCell.getStringCellValue().trim();
                        if (!productName.isEmpty()) {
                            registerSearchTerm(productName);
                        }
                    }
                }
            }
        }
    }

    /**
     * User interface management
     */
    private class UserInterfaceController {
        private final Scanner inputReader;

        UserInterfaceController() {
            this.inputReader = new Scanner(System.in);
        }

        void displayWelcomeScreen() {
            System.out.println("\n" + "▓".repeat(60));
            System.out.println("▓" + " ".repeat(58) + "▓");
            System.out.println("▓" + centerText("SEARCH FREQUENCY TRACKER", 58) + "▓");
            System.out.println("▓" + centerText("Advanced Analytics Platform", 58) + "▓");
            System.out.println("▓" + " ".repeat(58) + "▓");
            System.out.println("▓".repeat(60));
        }

        private String centerText(String text, int width) {
            int padding = (width - text.length()) / 2;
            return " ".repeat(padding) + text + " ".repeat(width - text.length() - padding);
        }

        void showMainMenu() {
            System.out.println("\n┌" + "─".repeat(48) + "┐");
            System.out.println("│" + centerText("MAIN MENU OPTIONS", 48) + "│");
            System.out.println("├" + "─".repeat(48) + "┤");
            System.out.println("│ 1. Execute Product Search Query              │");
            System.out.println("│ 2. Display Popular Search Analytics         │");
            System.out.println("│ 3. Review Complete Query History            │");
            System.out.println("│ 4. Show Advanced Statistics                 │");
            System.out.println("│ 5. Clear All Data                           │");
            System.out.println("│ 6. Exit Application                         │");
            System.out.println("└" + "─".repeat(48) + "┘");
            System.out.print("\nSelect operation [1-6]: ");
        }

        int getUserSelection() {
            try {
                int choice = inputReader.nextInt();
                inputReader.nextLine(); // Clear buffer
                return choice;
            } catch (InputMismatchException e) {
                inputReader.nextLine();
                return -1;
            }
        }

        String getSearchQuery() {
            System.out.print("\n→ Enter search term: ");
            return inputReader.nextLine().trim();
        }

        void displaySearchResults(List<QueryResult> results, String title) {
            System.out.println("\n" + "═".repeat(70));
            System.out.println(centerText(title, 70));
            System.out.println("═".repeat(70));

            if (results.isEmpty()) {
                System.out.println(centerText("No data available", 70));
            } else {
                System.out.printf("%-4s %-40s %-12s %-10s%n", "Rank", "Search Term", "Frequency", "Priority");
                System.out.println("─".repeat(70));

                int displayCount = Math.min(results.size(), MAX_DISPLAY_ITEMS);
                for (int i = 0; i < displayCount; i++) {
                    QueryResult result = results.get(i);
                    System.out.printf("%-4d %-40s %-12d %.2f%n",
                            i + 1,
                            result.term.length() > 40 ? result.term.substring(0, 37) + "..." : result.term,
                            result.frequency,
                            result.priority);
                }

                if (results.size() > MAX_DISPLAY_ITEMS) {
                    System.out.println("... and " + (results.size() - MAX_DISPLAY_ITEMS) + " more entries");
                }
            }
            System.out.println("═".repeat(70));
        }

        void displayStatistics() {
            System.out.println("\n" + "═".repeat(50));
            System.out.println(centerText("SYSTEM STATISTICS", 50));
            System.out.println("═".repeat(50));
            System.out.println("Total unique terms: " + treeManager.getTotalNodes());
            System.out.println("Total queries processed: " + logManager.getTotalQueryCount());
            System.out.println("Session active time: " + getSessionDuration() + " minutes");
            System.out.println("═".repeat(50));
        }

        private long getSessionDuration() {
            return (System.currentTimeMillis() - sessionStartTime) / 60000;
        }

        void showSuccessMessage(String message) {
            System.out.println("✓ " + message);
        }

        void showErrorMessage(String message) {
            System.out.println("✗ " + message);
        }

        void closeInterface() {
            inputReader.close();
        }
    }

    // Session tracking
    private final long sessionStartTime = System.currentTimeMillis();

    /**
     * Register a search term in the system
     */
    private void registerSearchTerm(String term) {
        if (term != null && !term.trim().isEmpty()) {
            treeManager.addDataPoint(term);
            logManager.recordQuery(term);
        }
    }

    /**
     * Process user search request
     */
    private void handleSearchRequest() {
        String query = uiController.getSearchQuery();
        if (!query.isEmpty()) {
            registerSearchTerm(query);
            uiController.showSuccessMessage("Search term '" + query + "' processed successfully.");
        } else {
            uiController.showErrorMessage("Search term cannot be empty.");
        }
    }

    /**
     * Display analytics based on frequency
     */
    private void showFrequencyAnalytics() {
        List<QueryResult> results = treeManager.extractAllData(SortingStrategy.BY_FREQUENCY);
        uiController.displaySearchResults(results, "TOP SEARCHED TERMS BY FREQUENCY");
    }

    /**
     * Display complete query history
     */
    private void showQueryHistory() {
        List<String> recentQueries = logManager.getRecentQueries(100);
        System.out.println("\n" + "═".repeat(50));
        System.out.println(uiController.centerText("RECENT SEARCH HISTORY", 50));
        System.out.println("═".repeat(50));

        if (recentQueries.isEmpty()) {
            System.out.println("No search history available.");
        } else {
            Collections.reverse(recentQueries); // Show most recent first
            for (int i = 0; i < Math.min(recentQueries.size(), 20); i++) {
                System.out.printf("%3d. %s%n", i + 1, recentQueries.get(i));
            }
            if (recentQueries.size() > 20) {
                System.out.println("... and " + (recentQueries.size() - 20) + " more entries");
            }
        }
        System.out.println("═".repeat(50));
    }

    /**
     * Clear all system data
     */
    private void clearSystemData() {
        logManager.clearHistory();
        treeManager.rootElement = null;
        treeManager.nodeRegistry.clear();
        treeManager.totalNodes = 0;
        timestampCache.clear();
        uiController.showSuccessMessage("All data cleared successfully.");
    }

    /**
     * Main application execution loop
     */
    public void executeApplication() {
        uiController.displayWelcomeScreen();

        boolean continueExecution = true;
        while (continueExecution) {
            uiController.showMainMenu();
            int userChoice = uiController.getUserSelection();

            switch (userChoice) {
                case 1:
                    handleSearchRequest();
                    break;
                case 2:
                    showFrequencyAnalytics();
                    break;
                case 3:
                    showQueryHistory();
                    break;
                case 4:
                    uiController.displayStatistics();
                    break;
                case 5:
                    clearSystemData();
                    break;
                case 6:
                    continueExecution = false;
                    System.out.println("\n" + "═".repeat(40));
                    System.out.println("Thank you for using Search Frequency Tracker!");
                    System.out.println("Session terminated successfully.");
                    System.out.println("═".repeat(40));
                    break;
                default:
                    uiController.showErrorMessage("Invalid selection. Please choose 1-6.");
            }
        }

        uiController.closeInterface();
    }

    /**
     * Application entry point
     */
    public static void main(String[] args) {
        try {
            SearchFrequencyTracker tracker = new SearchFrequencyTracker();
            tracker.executeApplication();
        } catch (Exception e) {
            System.err.println("Application error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}