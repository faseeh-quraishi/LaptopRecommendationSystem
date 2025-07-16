package laptoprecommendation;

import java.util.List;

import spellcheckingusingtrie.SpellCheckingMainClass;

public class Features {

    public static void main(String[] args) {
//        List<String> results = SpellCheck("lepotop");
//
//        System.out.println("🔍 Suggestions:");
//        for (String word : results) {
//            System.out.println(word);
//        }
    }

    public static List<String> SpellCheck(String word) {
        // Initialize spell checker class (assumes constructor handles setup)
        SpellCheckingMainClass SPC = new SpellCheckingMainClass("all_laptops_data.csv");

        // Get and return suggestions
        return SPC.SpellCheckingUsingTrie(word);
    }
}
