package org.example;

import dev.langchain4j.model.ollama.OllamaChatModel;

public class AI_Operations {

    OllamaChatModel chat;

    public AI_Operations() {
        chat = OllamaChatModel.builder().baseUrl("http://localhost:11434").modelName("llama3").build();
    }

    public String input_Output(String input){
        return chat.generate(input);
    }

    public String anz_Fragen(int anz,String input){
        String ret = "Erstelle " + anz + " Fragen zu dem nachfolgendem Infotext :" + input;
        return input_Output(ret);
    }

}
