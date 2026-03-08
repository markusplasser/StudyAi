package org.example;

import javafx.scene.Scene;

public class Fragen_Antworten {
    private String[] content;
    private String frage;
    private boolean[] loesung;
    public Fragen_Antworten() {
    }

    public Fragen_Antworten(String frage,String[] content,boolean[] loesung) {
        if(content.length!=loesung.length) {
            throw new MyException("Antworten und lösungen sind nicht gleich Lang");
        }
        this.content = content;
        this.frage = frage;
        this.loesung = loesung;
    }

    public String[] getContent() {
        return content;
    }
    public void setContent(String[] content) {
        this.content = content;
    }
    public String getFrage() {
        return frage;
    }
    public void setFrage(String frage) {
        this.frage = frage;
    }
    public boolean[] getLoesung() {
        return loesung;
    }
}
