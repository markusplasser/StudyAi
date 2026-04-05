package KI_Satzerkennung;

import org.example.Fragen_Antworten;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FindAnswersAndQuestions {

    public Fragen_Antworten[] find(String AIAnswertxt, int anzFragen, int AntwortenProFrage){
        TrainWithTrainSet t = new TrainWithTrainSet();

        Fragen_Antworten[] ret = new Fragen_Antworten[anzFragen];
        ArrayList<String> zeilen = new ArrayList<>();
        String[] tmp = AIAnswertxt.split("\n");
        for(int i = 0; i< tmp.length;i++){
            if(!tmp[i].isEmpty()){
                zeilen.add(tmp[i]);
            }
        }
        System.out.println(zeilen);

        String start = zeilen.getFirst();
        int p = 1;
        while(!auswertung(t.runSentenceThrough(removeNumbering(start)),0.5).equals("Frage")){
            start = zeilen.get(p);
            p++;
        }
        p++;

        for(int i = 0; i < anzFragen; i++){
            Fragen_Antworten add = new Fragen_Antworten();
            String[] content = new String[AntwortenProFrage];
            for(int j = 0; j < AntwortenProFrage; j++){
                String antwort = removeLettering(zeilen.get(p));
                if(auswertung(t.runSentenceThrough(antwort),0.5).equals("Antwort")){
                    content[j] = antwort;
                }
                p++;
            }
            String richtig =  removeAnswerPrefix(zeilen.get(p));
            p++;
            if(auswertung(t.runSentenceThrough(richtig), 0.5).equals("Antwort")){
                List<String> l = Arrays.asList(content);
                String right = findMostSimilar(richtig, l);
                boolean[] loesung = new boolean[AntwortenProFrage];

                for(int j = 0; j < AntwortenProFrage; j++){
                    if(right.equals(content[j])){
                        loesung[j] = true;
                    }
                }
                add.setContent(content);
                add.setLoesung(loesung);
            }
            ret[i] = add;
            System.out.println("added one!");
        }


        return ret;
    }

    // 1) Entfernt: 1. 2. 3. ... 20.
    public static String removeNumbering(String s) {
        return s.replaceAll("^\\s*(\\d{1,2}\\.)\\s*", "");
    }

    // 2) Entfernt: a) b) c) A) B) C)
    public static String removeLettering(String s) {
        return s.replaceAll("^\\s*([a-zA-Z]\\))\\s*", "");
    }

    // 3) Entfernt: "Antwort:" und a) b) c) A) B) C) (a) (b) (c)
    public static String removeAnswerPrefix(String s) {
        return s.replaceAll("^\\s*(Antwort:\\s*)?([a-zA-Z]\\)|\\([a-zA-Z]\\))\\s*", "");
    }

    public String auswertung(double[] arr, double genauigkeit){
        if(arr.length != 2){
            System.out.println("Auswertung hat ein ungültiges arr bekommen: " + Arrays.toString(arr));
            return null;
        }
        double diff = Math.max(arr[0],arr[1]) - Math.min(arr[0],arr[1]);
        if(diff > genauigkeit){
            return arr[0] < arr[1] ? "Antwort" : "Frage";
        }else{
            return "Ungenau";
        }
    }

    /**
     * Schaut welche Antwort am ähnlichsten ist.
     * Levenshtein'sche methode:
     * wie viele Zeichen muss ich ändern/einfügen/löschen um von String A zu String B zu kommen
     * @param target
     * @param candidates
     * @return
     */
    public static String findMostSimilar(String target, List<String> candidates) {
        String best = null;
        int bestDistance = Integer.MAX_VALUE;

        for (String candidate : candidates) {
            int distance = levenshtein(target, candidate);
            if (distance < bestDistance) {
                bestDistance = distance;
                best = candidate;
            }
        }
        return best;
    }


    public static int levenshtein(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= b.length(); j++) dp[0][j] = j;

        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1],  // ersetzen
                            Math.min(dp[i - 1][j],        // löschen
                                    dp[i][j - 1]));      // einfügen
                }
            }
        }
        return dp[a.length()][b.length()];
    }

}
