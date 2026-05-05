package org.example;

import dev.langchain4j.model.ollama.OllamaChatModel;

import java.time.Duration;

public class AI_Operations {

    OllamaChatModel chat;

    public AI_Operations() {
        chat = OllamaChatModel.builder().baseUrl("http://localhost:11434").modelName("llama3").timeout(Duration.ofMinutes(5)).build();
    }

    public String input_Output(String input){
        return chat.generate(input);
    }

    public String anz_Fragen(String input,int anzQuestions, int anzAnswers){
        String ret = "Erstelle " + anzQuestions + " Fragen mit je "+ anzAnswers + " Antworten zu dem nachfolgendem Infotext :" + input + ". Die Fragen haben Antworten die mit a) b) c) gekennzeichnent sind wobei nur eine richtig. Diese wird mit 'Antwort: ...' angezeigt . Alle Fragen und Antworten auf Detsch.";
        return input_Output(ret);
    }

}
