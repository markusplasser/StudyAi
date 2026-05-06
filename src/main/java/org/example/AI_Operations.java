package org.example;

import dev.langchain4j.model.ollama.OllamaChatModel;

import java.time.Duration;

public class AI_Operations {

    private final OllamaChatModel chat;

    // Konstruktor nimmt das Modell entgegen
    public AI_Operations(OllamaChatModel chat) {
        this.chat = chat;
    }

    public OllamaChatModel getChat() {
        return chat;
    }

    private String input_Output(String input){
        return chat.generate(input);
    }

    public String anz_Fragen(String input,int anzQuestions, int anzAnswers){
        String ret = "Erstelle " + anzQuestions + " Fragen mit je "+ anzAnswers + " Antworten zu dem nachfolgendem Infotext :" + input + ". Die Fragen haben Antworten die mit a) b) c) gekennzeichnent sind wobei nur eine richtig. Diese wird mit 'Antwort: ...' angezeigt . Alle Fragen und Antworten auf Detsch.";
        return input_Output(ret);
    }

}
