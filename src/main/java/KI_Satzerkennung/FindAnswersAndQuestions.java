package KI_Satzerkennung;

import org.example.Fragen_Antworten;

import java.util.ArrayList;
import java.util.Arrays;


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
            if(auswertung(t.runSentenceThrough(richtig),0.5).equals("")){

            }

        }


        return ret;
    }

//    public int checkRigthAnswer(String richtig, String[] content){
//
//    }
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

}
