package org.example;

public class Fragen_Antworten {
    private String frage;
    private String antwort;

    public Fragen_Antworten() {
    }

    public Fragen_Antworten(String frage, String antwort){
        this.frage = frage;
        this.antwort = antwort;
    }

    public String getAntwort() {
        return antwort;
    }

    public void setAntwort(String antwort) {
        this.antwort = antwort;
    }

    public String getFrage() {
        return frage;
    }

    public void setFrage(String frage) {
        this.frage = frage;
    }
}
