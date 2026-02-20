package org.example;

import javax.swing.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Oberflaeche o = new Oberflaeche();

        AI_Operations a = new AI_Operations();
        System.out.println(a.Input_Output("Hallo wie geht es dir"));
    }
//
//1. RAG (Retrieval Augmented Generation) – "Wissen einspeisen"
//    Anstatt dass die KI nur Fragen aus einem Text erstellt, den du kopierst, baust du ein System, das ganze PDF-Bibliotheken oder lokale Ordner scannt.
//
//    Die Herausforderung: Du musst die Texte in "Vektoren" (Zahlenreihen) umwandeln und in einer Vektordatenbank (wie ChromaDB oder Milvus) speichern.
//
//    Technischer Anspruch: Implementierung von Chunking-Strategien (wie zerschneidet man Text sinnvoll?), Embedding-Modellen und einer semantischen Suche.
//
//            Nutzen: Dein Programm "liest" 50 Java-Lehrbücher und erstellt daraus gezielte Prüfungsfragen.

}