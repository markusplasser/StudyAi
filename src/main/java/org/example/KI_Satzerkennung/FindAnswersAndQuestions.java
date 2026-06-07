package org.example.KI_Satzerkennung;

import org.example.manage.Fragen_Antworten;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FindAnswersAndQuestions {

    /**
     * finds all components via trust that the AI responded the right way
     * @param AIAnswertext
     * @param anzFragen
     * @param antwortenProFragen
     * @return
     */
    public Fragen_Antworten[] findWithoutAI(String AIAnswertext, int anzFragen, int antwortenProFragen) {
        Fragen_Antworten[] answer = new Fragen_Antworten[anzFragen];

        Fragen_Antworten[] ret = new Fragen_Antworten[anzFragen];
        ArrayList<String> zeilen = new ArrayList<>();
        String[] tmp = AIAnswertext.split("\n");
        for (String s : tmp) {
            if (!s.isEmpty()) {
                zeilen.add(s);
            }
        }
        int counter = 0;

        for(int i = 0; i < anzFragen; i++){
            Fragen_Antworten f = new Fragen_Antworten();

            String question = zeilen.get(counter);
            counter++;

            String[] content = getContent(zeilen, antwortenProFragen, counter);
            counter = counter + antwortenProFragen;

            String right = zeilen.get(counter);
            counter++;
            boolean[] boolArr = new boolean[antwortenProFragen];
            String trueAns = findMostSimilar(right, List.of(content));
            for(int j= 0 ; j < content.length; j++){
                if(content[j].equals(trueAns)){
                    boolArr[j] = true;
                    break;
                }
            }

            f.setContent(content);
            f.setFrage(question);
            f.setLoesung(boolArr);

            answer[i] = f;
        }
        return answer;
    }



    private String[] getContent(ArrayList<String> zeilen, int antwortenProFrage, int counter){
        String[] ret = new String[antwortenProFrage];
        for(int i = 0; i < antwortenProFrage; i++){
            ret[i] = zeilen.get(counter+i);
        }
        return ret;
    }

    public Fragen_Antworten[] findWithAI(String AIAnswertxt, int anzFragen, int AntwortenProFrage) {
        TrainWithTrainSet t = null;
        try {
            t = new TrainWithTrainSet();
        } catch (Exception e) {
            return null;
        }

        Fragen_Antworten[] ret = new Fragen_Antworten[anzFragen];
        ArrayList<String> zeilen = new ArrayList<>();
        String[] tmp = AIAnswertxt.split("\n");
        for (String s : tmp) {
            if (!s.isEmpty()) {
                zeilen.add(s);
            }
        }



        double[][] arrRawAns = new double[zeilen.size()][];
        int counter = 0;
        for (int i = 0; i < zeilen.size(); i++) {
            String text = removeLettering(zeilen.get(i));
            arrRawAns[i] = t.runSentenceThrough(text);
        }


        for (int i = 0; i < anzFragen; i++) {
            Fragen_Antworten add = new Fragen_Antworten();

            counter = findQuestion(arrRawAns, counter);
            if (counter == -1) {
                System.out.println("Couldn't find the question");
                return null;
            }
            String question = zeilen.get(counter);
            counter++;


            String[] content = new String[AntwortenProFrage];
            int j = 0;
            while (content[AntwortenProFrage-1] == null) {
                counter = findContent(arrRawAns, counter);
                if (counter == -1) {
                    System.out.println("Couldn't find the content");
                    return null;
                }
                content[j] = zeilen.get(counter);
                counter++;
                j++;
            }


            String answer;
            counter = findContent(arrRawAns, counter);
            if (counter == -1) {
                System.out.println("Couldn't find the right answer");
            }
            answer = zeilen.get(counter);
            String rightAns = findMostSimilar(answer, List.of(content));

            boolean[] boolArr = {false, false, false};
            for (int p = 0; p < AntwortenProFrage; p++) {
                if (rightAns.equals(content[p])) {
                    boolArr[p] = true;
                    break;
                }
            }

            add.setFrage(question);
            add.setContent(content);
            add.setLoesung(boolArr);
            ret[i] = add;
        }
        return ret;
    }

    private int findContent(double[][] arrRawAns, int index) {
        for (int i = index; i < arrRawAns.length; i++) {
            if (auswertung(arrRawAns[i], 0.5).equals("Antwort")) {
                return i;
            }
        }

        return -1;
    }

    private int findQuestion(double[][] arrRawAns, int index) {
        for (int i = index; i < arrRawAns.length; i++) {
            if (auswertung(arrRawAns[i], 0.5).equals("Frage")) {
                return i;
            }
        }
        return -1;
    }

    // 2) Entfernt: a) b) c) A) B) C)
    public static String removeLettering(String s) {
        return s.replaceAll("^\\s*([a-zA-Z]\\))\\s*", "");
    }


    public String auswertung(double[] arr, double genauigkeit) {
        if (arr.length != 2) {
            System.out.println("Auswertung hat ein ungültiges arr bekommen: " + Arrays.toString(arr));
            return null;
        }
        double diff = Math.max(arr[0], arr[1]) - Math.min(arr[0], arr[1]);
        if (diff > genauigkeit) {
            return arr[0] < arr[1] ? "Antwort" : "Frage";
        } else {
            return "Ungenau";
        }
    }

    /**
     * Nicht selber gemacht
     * Schaut welche Antwort am ähnlichsten ist.
     * Levenshtein'sche methode:
     * wie viele Zeichen muss ich ändern/einfügen/löschen um von String A zu String B zu kommen
     *
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
