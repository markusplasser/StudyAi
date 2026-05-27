package org.example.manage;
import org.example.KI_Satzerkennung.FindAnswersAndQuestions;
import dev.langchain4j.model.ollama.OllamaChatModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class Connection {


    public static void main(String[] args) throws Exception {
        Properties p = new Properties();
        try (FileInputStream fis = new FileInputStream("C:\\Users\\marku\\StudyAi\\config.properties")) {
            p.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        var handleSave = new Handle_Save(p.getProperty("Project_Save_File"));
        Fragen_Antworten[] fr = handleSave.read("öfjdkls");
        System.out.println(fr[0].toString());
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
            aiAnswer = geminiService.getString(geminiService.ask(geminiService.buildPromt(inputtxt,anzFragen,anzProFrage),0));
        } catch (Exception e) {
            try {
                aiAnswer = geminiService.getString(geminiService.ask(geminiService.buildPromt(inputtxt,anzFragen,anzProFrage),1));
            } catch (Exception ex) {
                try {
                    aiAnswer = geminiService.getString(geminiService.ask(geminiService.buildPromt(inputtxt,anzFragen,anzProFrage),2));
                } catch (Exception exception) {
                    throw new RuntimeException(exception);
                }
            }
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

    public String returnFileNames() {
        String path = p.getProperty("Project_Save_File");
        File folder = new File(path);

        if (!folder.exists() || !folder.isDirectory()){
            System.out.println("Ordner existiert nicht!");
            return "";
        }

        File[] files = folder.listFiles();
        if (files == null) {return "";}

        StringBuilder sb = new StringBuilder();

        for (File file : files) {
            if (file.isFile()) {
                if (sb.length() > 0) {sb.append(";");}
                sb.append(file.getName());
            }
        }
        return sb.toString();
    }
}

