package KI_Satzerkennung;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

public class Tokenizer {

    private Map<String, Integer> vocab;
    private Map<Integer, String> reverseVocab;
    private Map<String, Integer> merges;
    private int vocabSize;

    // Regex für Wort-Tokenisierung (ähnlich GPT-2)
    private static final Pattern TOKEN_PATTERN = Pattern.compile(
            "'s|'t|'re|'ve|'m|'ll|'d|[\\p{L}]+|[\\p{N}]+|[^\\s\\p{L}\\p{N}]+"
    );

    // Spezielle Tokens
    private static final String UNK_TOKEN = "<UNK>";
    private static final String PAD_TOKEN = "<PAD>";
    private static final String BOS_TOKEN = "<BOS>";
    private static final String EOS_TOKEN = "<EOS>";

    public Tokenizer() {
        this.vocab = new LinkedHashMap<>();
        this.reverseVocab = new HashMap<>();
        this.merges = new LinkedHashMap<>();
        initializeBaseVocab();
    }

    private void initializeBaseVocab() {
        // Spezielle Tokens
        addToVocab(PAD_TOKEN);
        addToVocab(UNK_TOKEN);
        addToVocab(BOS_TOKEN);
        addToVocab(EOS_TOKEN);

        // Alle druckbaren ASCII-Zeichen als Basis
        for (int i = 32; i < 127; i++) {
            addToVocab(String.valueOf((char) i));
        }

        // Deutsche Sonderzeichen
        for (char c : "äöüÄÖÜß€".toCharArray()) {
            addToVocab(String.valueOf(c));
        }
    }

    private void addToVocab(String token) {
        if (!vocab.containsKey(token)) {
            int id = vocab.size();
            vocab.put(token, id);
            reverseVocab.put(id, token);
        }
    }

    /**
     * Trainiert den Tokenizer auf einem Textkorpus mit BPE.
     *
     * @param corpus Der Trainingstext
     * @param targetVocabSize Zielgröße des Vokabulars
     * @param minFrequency Minimale Häufigkeit für Merges
     */
    public void train(String corpus, int targetVocabSize, int minFrequency) {
        this.vocabSize = targetVocabSize;

        // Text in Wörter aufteilen
        List<String> words = extractWords(corpus);

        // Wörter in Zeichen aufteilen mit Häufigkeiten
        Map<List<String>, Integer> wordFreqs = new HashMap<>();
        for (String word : words) {
            List<String> chars = word.chars()
                    .mapToObj(c -> String.valueOf((char) c))
                    .collect(Collectors.toList());
            chars.add("</w>"); // End-of-word marker
            wordFreqs.merge(chars, 1, Integer::sum);
        }

        // BPE-Training
        while (vocab.size() < targetVocabSize) {
            // Finde häufigstes Paar
            Map<String, Integer> pairFreqs = new HashMap<>();

            for (Map.Entry<List<String>, Integer> entry : wordFreqs.entrySet()) {
                List<String> tokens = entry.getKey();
                int freq = entry.getValue();

                for (int i = 0; i < tokens.size() - 1; i++) {
                    String pair = tokens.get(i) + " " + tokens.get(i + 1);
                    pairFreqs.merge(pair, freq, Integer::sum);
                }
            }

            if (pairFreqs.isEmpty()) break;

            // Bestes Paar finden
            String bestPair = Collections.max(pairFreqs.entrySet(),
                    Map.Entry.comparingByValue()).getKey();
            int bestFreq = pairFreqs.get(bestPair);

            if (bestFreq < minFrequency) break;

            String[] parts = bestPair.split(" ");
            String merged = parts[0] + parts[1];

            // Merge durchführen
            merges.put(bestPair, merges.size());
            addToVocab(merged);

            // Wörter aktualisieren
            Map<List<String>, Integer> newWordFreqs = new HashMap<>();
            for (Map.Entry<List<String>, Integer> entry : wordFreqs.entrySet()) {
                List<String> tokens = new ArrayList<>(entry.getKey());
                int i = 0;
                while (i < tokens.size() - 1) {
                    if (tokens.get(i).equals(parts[0]) && tokens.get(i + 1).equals(parts[1])) {
                        tokens.set(i, merged);
                        tokens.remove(i + 1);
                    } else {
                        i++;
                    }
                }
                newWordFreqs.merge(tokens, entry.getValue(), Integer::sum);
            }
            wordFreqs = newWordFreqs;
        }
    }
    public void saveTokenizer(String path) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("vocab", vocab);
        data.put("merges", merges);
        mapper.writeValue(new File(path), data);
    }

    public void loadTokenizer(String path) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File(path));

        Map<String, Integer> loadedVocab = new LinkedHashMap<>();
        root.get("vocab").fields().forEachRemaining(e ->
                loadedVocab.put(e.getKey(), e.getValue().asInt()));

        Map<String, Integer> loadedMerges = new LinkedHashMap<>();
        root.get("merges").fields().forEachRemaining(e ->
                loadedMerges.put(e.getKey(), e.getValue().asInt()));

        loadVocab(loadedVocab, loadedMerges);
    }

    /**
     * Tokenisiert einen String und gibt Token-IDs als double[] zurück.
     *
     * @param text Der zu tokenisierende Text
     * @return Array mit Token-IDs als doubles
     */
    public double[] tokenize(String text) {
        return tokenize(text, false, false);
    }

    /**
     * Tokenisiert mit optionalen BOS/EOS-Tokens.
     */
    public double[] tokenize(String text, boolean addBos, boolean addEos) {
        List<Integer> tokenIds = new ArrayList<>();

        if (addBos) {
            tokenIds.add(vocab.get(BOS_TOKEN));
        }

        List<String> words = extractWords(text);

        for (String word : words) {
            List<String> tokens = bpeEncode(word);
            for (String token : tokens) {
                tokenIds.add(vocab.getOrDefault(token, vocab.get(UNK_TOKEN)));
            }
        }

        if (addEos) {
            tokenIds.add(vocab.get(EOS_TOKEN));
        }

        return tokenIds.stream().mapToDouble(Integer::doubleValue).toArray();
    }

    /**
     * Tokenisiert und gibt normalisierte Werte zurück (0-1 Bereich).
     */
    public double[] tokenizeNormalized(String text) {
        double[] tokens = tokenize(text);
        double maxId = vocab.size() - 1;
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = tokens[i] / maxId;
        }
        return tokens;
    }

    public double[] normalize(double[] tokenIds) {
        double maxId = vocab.size() - 1;
        double[] normalized = new double[tokenIds.length];
        for (int i = 0; i < tokenIds.length; i++) {
            normalized[i] = tokenIds[i] / maxId;
        }
        return normalized;
    }

    /**
     * Tokenisiert zu einem Batch mit Padding.
     */
    public double[][] tokenizeBatch(String[] texts, int maxLength) {
        double[][] batch = new double[texts.length][maxLength];
        int padId = vocab.get(PAD_TOKEN);

        for (int i = 0; i < texts.length; i++) {
            double[] tokens = tokenize(texts[i], true, true);
            for (int j = 0; j < maxLength; j++) {
                if (j < tokens.length) {
                    batch[i][j] = tokens[j];
                } else {
                    batch[i][j] = padId;
                }
            }
        }
        return batch;
    }

    private List<String> bpeEncode(String word) {
        List<String> tokens = word.chars()
                .mapToObj(c -> String.valueOf((char) c))
                .collect(Collectors.toCollection(ArrayList::new));
        tokens.add("</w>");

        while (tokens.size() > 1) {
            // Finde das Paar mit der niedrigsten Merge-Priorität (= frühester Merge)
            int bestIdx = -1;
            int bestRank = Integer.MAX_VALUE;

            for (int i = 0; i < tokens.size() - 1; i++) {
                String pair = tokens.get(i) + " " + tokens.get(i + 1);
                Integer rank = merges.get(pair);
                if (rank != null && rank < bestRank) {
                    bestRank = rank;
                    bestIdx = i;
                }
            }

            if (bestIdx == -1) break;

            // Merge anwenden
            String merged = tokens.get(bestIdx) + tokens.get(bestIdx + 1);
            tokens.set(bestIdx, merged);
            tokens.remove(bestIdx + 1);
        }

        // End-of-word marker entfernen für finale Tokens
        List<String> result = new ArrayList<>();
        for (String token : tokens) {
            if (token.endsWith("</w>")) {
                token = token.substring(0, token.length() - 4);
                if (!token.isEmpty()) {
                    result.add(token);
                }
            } else {
                result.add(token);
            }
        }

        return result;
    }

    private List<String> extractWords(String text) {
        List<String> words = new ArrayList<>();
        Matcher matcher = TOKEN_PATTERN.matcher(text.toLowerCase());
        while (matcher.find()) {
            words.add(matcher.group());
        }
        return words;
    }

    /**
     * Dekodiert Token-IDs zurück zu Text.
     */
    public String decode(double[] tokenIds) {
        StringBuilder sb = new StringBuilder();
        for (double id : tokenIds) {
            int intId = (int) id;
            String token = reverseVocab.getOrDefault(intId, "");
            if (!token.equals(PAD_TOKEN) && !token.equals(BOS_TOKEN) && !token.equals(EOS_TOKEN)) {
                sb.append(token);
            }
        }
        return sb.toString().replace("</w>", " ").trim();
    }

    /**
     * Gibt die Vokabulargröße zurück.
     */
    public int getVocabSize() {
        return vocab.size();
    }

    /**
     * Gibt die Token-ID für ein Token zurück.
     */
    public int getTokenId(String token) {
        return vocab.getOrDefault(token, vocab.get(UNK_TOKEN));
    }

    /**
     * Speichert das Vokabular (für Persistenz).
     */
    public Map<String, Integer> getVocab() {
        return new LinkedHashMap<>(vocab);
    }

    /**
     * Lädt ein bestehendes Vokabular.
     */
    public void loadVocab(Map<String, Integer> loadedVocab, Map<String, Integer> loadedMerges) {
        this.vocab = new LinkedHashMap<>(loadedVocab);
        this.reverseVocab = new HashMap<>();
        for (Map.Entry<String, Integer> e : vocab.entrySet()) {
            reverseVocab.put(e.getValue(), e.getKey());
        }
        this.merges = new LinkedHashMap<>(loadedMerges);
    }

    // ============ Beispiel-Verwendung ============
    public static void main(String[] args) {
        Tokenizer tokenizer = new Tokenizer();

        // Training auf Beispieltext
        String corpus = """
            Die künstliche Intelligenz revolutioniert die Welt.
            Machine Learning und Deep Learning sind Teilbereiche der KI.
            Neuronale Netze lernen aus Daten und erkennen Muster.
            Der Tokenizer zerlegt Text in kleinere Einheiten.
            Diese Einheiten nennt man Tokens.
            """.repeat(100); // Wiederholung für besseres Training

        tokenizer.train(corpus, 500, 2);

        // Tokenisierung
        String testText = "Künstliche Intelligenz lernt aus Daten.";
        double[] tokens = tokenizer.tokenize(testText);

        System.out.println("Original: " + testText);
        System.out.println("Token-IDs: " + Arrays.toString(tokens));
        System.out.println("Dekodiert: " + tokenizer.decode(tokens));
        System.out.println("Vocab-Größe: " + tokenizer.getVocabSize());

        // Mit BOS/EOS
        double[] tokensWithSpecial = tokenizer.tokenize(testText, true, true);
        System.out.println("Mit BOS/EOS: " + Arrays.toString(tokensWithSpecial));

        // Batch-Tokenisierung
        String[] batch = {"Hallo Welt", "Machine Learning ist toll"};
        double[][] batchTokens = tokenizer.tokenizeBatch(batch, 20);
        System.out.println("Batch Shape: " + batchTokens.length + "x" + batchTokens[0].length);
    }
}
