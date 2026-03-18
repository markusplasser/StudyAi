package KI_Satzerkennung;

import java.util.*;
//AI: all
public class Tokenizer {
    private Map<String, Integer> wordToId = new HashMap<>();
    private List<String> idToWord = new ArrayList<>();
    private int maxLength;

    // Spezielle IDs für das Netzwerk
    public static final int PAD_ID = 0;  // Für leere Stellen
    public static final int UNK_ID = 1;  // Für unbekannte Wörter

    public Tokenizer(int maxLength) {
        this.maxLength = maxLength;
        // Initialisiere Standard-Tokens
        wordToId.put("<PAD>", PAD_ID);
        wordToId.put("<UNK>", UNK_ID);
        idToWord.add("<PAD>");
        idToWord.add("<UNK>");
    }

    /**
     * Trainiert das Vokabular mit einer Liste von Sätzen.
     */
    public void fit(List<String> sentences) {
        for (String s : sentences) {
            for (String token : simpleSplit(s)) {
                if (!wordToId.containsKey(token)) {
                    wordToId.put(token, wordToId.size());
                    idToWord.add(token);
                }
            }
        }
    }

    /**
     * Verwandelt einen Satz in ein fixes Zahlen-Array für das Netzwerk.
     */
    public int[] encode(String text) {
        int[] vector = new int[maxLength];
        String[] tokens = simpleSplit(text);

        for (int i = 0; i < maxLength; i++) {
            if (i < tokens.length) {
                // Hol die ID, oder UNK_ID wenn Wort neu ist
                vector[i] = wordToId.getOrDefault(tokens[i], UNK_ID);
            } else {
                vector[i] = PAD_ID;
            }
        }
        return vector;
    }

    /**
     * Hilfsfunktion zum sauberen Trennen von Wörtern und Satzzeichen.
     */
    private String[] simpleSplit(String text) {
        if (text == null) return new String[0];
        // Trenne bei Leerzeichen, behalte aber Satzzeichen wie ? ! . , als eigene Tokens
        return text.toLowerCase()
                .replaceAll("([?!.,])", " $1 ")
                .trim()
                .split("\\s+");
    }

    public int getVocabSize() {
        return wordToId.size();
    }
}
