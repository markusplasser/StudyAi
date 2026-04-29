package org.example;
import KI_Satzerkennung.FindAnswersAndQuestions;

public class Connection {
    FindAnswersAndQuestions find = new FindAnswersAndQuestions();
    Handle_Save hs = new Handle_Save();
    public boolean connection(String inputtxt, int anzFragen, int anzAntproFrage){
        Fragen_Antworten[] save = find.find(inputtxt,anzFragen,anzAntproFrage);
        hs.set

    }
}
