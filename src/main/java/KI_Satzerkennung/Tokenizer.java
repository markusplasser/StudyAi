package KI_Satzerkennung;

import java.util.*;

public class Tokenizer {

    public static final int PAD_ID = 0;
    public static final int UNK_ID = 1;
    public static final int BOS_ID = 2; // Begin of Sentence
    public static final int EOS_ID = 3; // End of Sentence

    private static final List<String> SPECIAL_TOKENS = List.of("<PAD>", "<UNK>", "<BOS>", "<EOS>");

    private final Map<String, Integer> wordToId = new LinkedHashMap<>();
    private final List<String>         idToWord = new ArrayList<>();
    private final int maxLength;

    public Tokenizer(int maxLength) {
        this.maxLength = maxLength;
        for (String t : SPECIAL_TOKENS) {
            wordToId.put(t, idToWord.size());
            idToWord.add(t);
        }
    }

    // ------------------------------------------------------------------ fit --

    /** Baut das Vokabular aus einer Liste von Sätzen auf. */
    public void fit(List<String> sentences) {
        for (String s : sentences) {
            for (String token : tokenize(s)) {
                if (!wordToId.containsKey(token)) {
                    wordToId.put(token, idToWord.size()); // Größe *vor* dem Hinzufügen = neuer Index
                    idToWord.add(token);
                }
            }
        }
    }

    // --------------------------------------------------------------- encode --

    /**
     * Kodiert einen Satz als fixes double[]-Array.
     * Format: [BOS, w1, w2, ..., EOS, PAD, PAD, ...]
     */
    public double[] encode(String text) {
        double[] vector = new double[maxLength];
        String[] tokens = tokenize(text);

        // Nutzbarer Raum: BOS + tokens + EOS
        int usable = maxLength - 2;
        int len = Math.min(tokens.length, usable);

        vector[0] = BOS_ID;
        for (int i = 0; i < len; i++) {
            vector[1 + i] = wordToId.getOrDefault(tokens[i], UNK_ID);
        }
        if (1 + len < maxLength) {
            vector[1 + len] = EOS_ID;
        }
        // Rest bleibt 0.0 = PAD_ID (Array-Default)
        return vector;
    }

    // ------------------------------------------------------------- tokenize --

    /**
     * Zerlegt Text in Tokens:
     * - Lowercase
     * - Umlaute normalisieren (ä→ae usw.)
     * - Satzzeichen als eigene Tokens
     * - Leere Tokens herausfiltern
     */
    public String[] tokenize(String text) {
        if (text == null || text.isBlank()) return new String[0];

        String normalized = text.toLowerCase()
                .replace("ä", "ae").replace("ö", "oe").replace("ü", "ue")
                .replace("ß", "ss")
                .replaceAll("([?!.,;:\"'()\\[\\]])", " $1 ")
                .trim();

        return Arrays.stream(normalized.split("\\s+"))
                .filter(t -> !t.isEmpty())
                .toArray(String[]::new);
    }

    // ------------------------------------------------------------ accessors --

    public int getVocabSize()            { return wordToId.size(); }
    public int getMaxLength()            { return maxLength; }
    public String decode(int id)         { return id < idToWord.size() ? idToWord.get(id) : "<UNK>"; }
    public Map<String, Integer> getVocab() { return Collections.unmodifiableMap(wordToId); }
}