package org.example.KI_Satzerkennung;

import org.example.manage.Fragen_Antworten;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FindAnswersAndQuestions {

    public Fragen_Antworten[] findWithoutAI(String AIAnswertext, int anzFragen, int AntwortenProFragen){
        Fragen_Antworten[] answer = new Fragen_Antworten[anzFragen];

        Fragen_Antworten[] ret = new Fragen_Antworten[anzFragen];
        ArrayList<String> zeilen = new ArrayList<>();
        String[] tmp = AIAnswertext.split("\n");
        for(int i = 0; i< tmp.length;i++){
            if(!tmp[i].isEmpty()){
                zeilen.add(tmp[i]);
            }
        }
        int counter = 0;
        boolean[] found = {true,false,false};
        for(int i = 0; i < anzFragen; i++){
            Fragen_Antworten f = new Fragen_Antworten();
            f.setFrage(zeilen.get(counter));
            counter++;
            String[] content = new String[AntwortenProFragen];
            for(int j = 0; j < AntwortenProFragen; j++){
                content[j] = zeilen.get(counter);
                counter++;
            }
            f.setLoesung(found);
            f.setContent(content);
            counter++;
            answer[i] = f;
        }
        return answer;
    }

    public Fragen_Antworten[] findWithAI(String AIAnswertxt, int anzFragen, int AntwortenProFrage){
        TrainWithTrainSet t = null;
        try {
            t = new TrainWithTrainSet();
        } catch (Exception e) {
            return null;
        }

        Fragen_Antworten[] ret = new Fragen_Antworten[anzFragen];
        ArrayList<String> zeilen = new ArrayList<>();
        String[] tmp = AIAnswertxt.split("\n");
        for(int i = 0; i< tmp.length;i++){
            if(!tmp[i].isEmpty()){
                zeilen.add(tmp[i]);
            }
        }
        //System.out.println(zeilen);
        String start = zeilen.getFirst();


        double[][] arr= new double[anzFragen][];

        for(int i = 0; i < zeilen.size(); i++){
            String text = removeLettering(zeilen.get(i));
            arr[i] = t.runSentenceThrough(text);
        }

        //for loop to the first question
        for(int i = 0; i < arr.length; i++){

        }

        for(int i = 0; i < anzFragen; i++){
            Fragen_Antworten add = new Fragen_Antworten();
            ret[i] = add;
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
     * Nicht selber gemacht
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
