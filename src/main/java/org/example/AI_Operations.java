package org.example;

import dev.langchain4j.model.ollama.OllamaChatModel;

public class AI_Operations {
    OllamaChatModel chat;

    public AI_Operations() {
        chat = OllamaChatModel.builder().baseUrl("http://localhost:11434").modelName("llama3").build();
    }

    public String Input_Output(String input){
        return chat.generate(input);
    }
}
