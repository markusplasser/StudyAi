package org.example;
import KI_Satzerkennung.FindAnswersAndQuestions;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Connection {
    FindAnswersAndQuestions FAAQ;
    Handle_Save hs;
    Properties p;
    AI_Operations ai;
    public Connection(){
        p = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            p.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }

        hs = new Handle_Save(p.getProperty("saveFolder"));
        FAAQ = new FindAnswersAndQuestions();
        ai = new AI_Operations();
    }
    public boolean saveConnection(String inputtxt, int anzFragen, int anzProFrage , String fileName){
        String aiAnswer = ai.anz_Fragen(inputtxt, anzFragen, anzProFrage);

        Fragen_Antworten[] save = FAAQ.find(aiAnswer,anzFragen,anzProFrage);

        //Saves the Questions in a File
        hs.setArr(save);
        hs.setFilename(fileName);
        hs.save();

        return true;
    }

    public Fragen_Antworten[] returnQuestions(String filename){
        Fragen_Antworten[] ret = hs.read(filename);
        if(ret == null){
            System.out.println("Corrupt file!");
            ret = new Fragen_Antworten[1];
            ret[0].setFirstQuestion("Fehler");
        }
        return ret;
    }
}
