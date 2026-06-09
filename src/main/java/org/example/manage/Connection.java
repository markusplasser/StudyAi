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
        try (FileInputStream fis = new FileInputStream("C:\\Users\\Spieler\\StudyAi\\config.properties")) {
            p.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        var handleSave = new Handle_Save(p.getProperty("Project_Save_File"));
        Fragen_Antworten[] fr = handleSave.read("FürAlex2.bin");
        for (Fragen_Antworten fragenAntworten : fr) {
            System.out.println(fragenAntworten.toString());
        }
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

    /**
     * creates a Fragen_Antworten[] from the inputtxt and saves it.
     * @param inputtxt Input text
     * @param anzFragen amount of question to generate
     * @param anzProFrage amount of different answers
     * @param fileName name of save file
     * @return return true if the generation and saving was successful
     */
    public boolean saveConnection(String inputtxt, int anzFragen, int anzProFrage , String fileName){
        String aiAnswer = null;
        int model = -1;
        for(int models = 0; models < 3; models++){
            aiAnswer = geminiService.ask(geminiService.buildPromt(inputtxt,anzFragen,anzProFrage), models);
            if(aiAnswer != null){
                model = models;
                break;
            }
        }
        aiAnswer = geminiService.getString(model,aiAnswer);

        if(aiAnswer == null){
            return false;
        }

        Fragen_Antworten[] save;
        save = FAAQ.findWithoutAI(aiAnswer,anzFragen,anzProFrage);

        for(Fragen_Antworten f : save){
            System.out.println(f.toString());
        }
        //Saves the Questions in a File
        hs.setArr(save);
        hs.setFilename(fileName);
        hs.save();

        return true;
    }

    /**
     * Reads Fragen_Antworten from the file
     * @param filename filename
     * @return Fragen_Antworten[] with the content
     */
    public Fragen_Antworten[] returnQuestions(String filename){
        Fragen_Antworten[] ret = hs.read(filename);
        if(ret == null){
            System.out.println("Corrupt file!");
            ret = new Fragen_Antworten[1];
            ret[0].setFirstQuestion("Fehler");
        }
        return ret;
    }

    /**
     * reads the names of files in the default save folder
     * @return list of filenames
     */
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
                if (!sb.isEmpty()) {sb.append(";");}
                sb.append(file.getName());
            }
        }
        return sb.toString();
    }
}

