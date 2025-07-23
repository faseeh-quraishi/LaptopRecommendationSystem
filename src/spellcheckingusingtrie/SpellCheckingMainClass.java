package spellcheckingusingtrie;

import java.util.*;

public class SpellCheckingMainClass {
    private final SpellChecker spellChecker;

    // Constructor: load vocabulary and build Trie
    public SpellCheckingMainClass(String csvFilePath) {
        List<String> vocabularyList;

        try {
            vocabularyList = CSVLoader.loadVocabularyFromCSV(csvFilePath);

            if (vocabularyList.isEmpty()) {
                // Do not log to console
                this.spellChecker = null;
                return;
            }

            this.spellChecker = new SpellChecker(vocabularyList);

        } catch (Exception e) {
            // Do not log to console
            throw new RuntimeException(e);
        }
    }

    /**
     * Spell-check the word and return suggestions (up to 5) using Trie.
     *
     * @param word Word to check
     * @return List of result messages (first: status, rest: suggestions)
     */
    public List<String> SpellCheckingUsingTrie(String word) {
        List<String> outputMessages = new ArrayList<>();

        if (spellChecker == null) {
            outputMessages.add("error: SpellChecker not initialized due to earlier error.");
            return outputMessages;
        }

        word = word.trim().toLowerCase();

        if (word.isEmpty()) {
            outputMessages.add("error: Please provide a non-empty word.");
            return outputMessages;
        }
        for(String unitWord : word.split(" ")) {
            if (spellChecker.isCorrect(unitWord)) {
            } else {
                List<String> suggestions = spellChecker.suggest(unitWord, 3);
                String tempSuggestions = unitWord+": ";
                if (suggestions.isEmpty()) {
                    outputMessages.add("error:  The word " + unitWord + " is not in the vocabulary.");
                    break;
                } else {
                    for (int i = 0; i < Math.min(5, suggestions.size()); i++) {
                        tempSuggestions += suggestions.get(i) + ",";
                    }
                }
                outputMessages.add(tempSuggestions.substring(0, tempSuggestions.length() - 1));
            }
        }
        return outputMessages;
    }
}
