package org.example.manage;
import org.example.KI_Satzerkennung.FindAnswersAndQuestions;
import dev.langchain4j.model.ollama.OllamaChatModel;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

public class Connection {


    public static void main(String[] args) throws Exception {
        Properties p = new Properties();
        try (FileInputStream fis = new FileInputStream("C:\\Users\\marku\\StudyAi\\properties.txt")) {
            p.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        GeminiService g = new GeminiService(p.getProperty("API_KEY"));
        System.out.println(g.getString(g.ask("Hallo wie geht es dir")));

    }



    FindAnswersAndQuestions FAAQ;
    Handle_Save hs;
    Properties p;
    GeminiService geminiService;
    public Connection(Properties p){
        this.p = p;

        hs = new Handle_Save(p.getProperty("Project_Save_File"));
        FAAQ = new FindAnswersAndQuestions();
        OllamaChatModel chat = OllamaChatModel.builder().baseUrl("http://localhost:8080").modelName("llama3").timeout(Duration.ofMinutes(5)).build();
        geminiService = new GeminiService(p.getProperty("API_KEY"));
    }
    public boolean saveConnection(String inputtxt, int anzFragen, int anzProFrage , String fileName){
        String aiAnswer = null;
        try {
            aiAnswer = geminiService.getString(geminiService.ask(geminiService.buildPromt(inputtxt,anzFragen,anzProFrage)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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
