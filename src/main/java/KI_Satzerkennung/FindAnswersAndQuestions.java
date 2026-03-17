package KI_Satzerkennung;

import org.example.Fragen_Antworten;

public class FindAnswersAndQuestions {

    Network network;
    public Fragen_Antworten[] find(String AIAnswertxt, int anzFragen, int AntwortenProFrage){
        try(){
            network = Network.loadNetwork("res/save.txt");
        }
        catch (Exception e){
            System.out.println("Fehler beim Laden des Netzes");
            e.printStackTrace();
        }


        Fragen_Antworten[] f = new Fragen_Antworten[anzFragen];

        String[] lines = AIAnswertxt.toLowerCase().split("\n");
        if(anzFragen+anzFragen*AntwortenProFrage > lines.length){
            System.out.println("es gibt zu wenige lines und zufiele Antwortmöglichkeiten");
        }

        for(int i = 0;i<anzFragen;i++){


            // Get Antwortmöglichkeiten

            // Get RichtigeAntwort

            // Erkenne welche die richitge ist

            // setzten der richtigen Lösung
        }
        return f;
    }

    public double[] runthroughNetwork(String txt){

    }
}
