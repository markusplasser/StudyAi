package org.example;

import okio.Buffer;

import java.io.*;

public class Handle_Save {
    private Fragen_Antworten[] arr;
    private String filename;


    public Handle_Save(String filename, Fragen_Antworten[] content) {
        this.filename = filename;
        arr = content;
    }

    /**
     * übernimmt das Speichern von den einzelnen Fragen und Antworten
     * StudyAi\save\filename - Speicherort
     */
    public void save() {
        if (!create_default_saveFolder()) {
            System.out.println("Fehler beim erzeugen des save folders");
        }
        String path = Oberflaeche.path + "\\save\\" + filename + ".txt";

        try (BufferedWriter out = new BufferedWriter(new FileWriter(path))) {
            out.write(String.valueOf(arr.length));
            out.newLine();
            for (int i = 0; i < arr.length; i++) {
                out.write(arr[i].getFrage());
                out.newLine();
                out.write(arr[i].getAntwort());
                out.newLine();
            }
        } catch (IOException e) {
            System.out.println("Fehler beim erzeugen der txt Datei:" + path);
        }
    }

    public Fragen_Antworten[] read(String filename){
        if(!check_file_exist(filename)){
            System.out.println("Das File das eingelesen werden soll gibt es nicht");
        }

        String path = Oberflaeche.path + "\\save\\" + filename + ".txt";

        Fragen_Antworten[] ret = new Fragen_Antworten[0];
        try(BufferedReader red  = new BufferedReader(new FileReader(path))){
            String line = red.readLine();
            int len = Integer.parseInt(line);
            ret = new Fragen_Antworten[len];
            Fragen_Antworten f = new Fragen_Antworten();
            int max = len*2;
            int count = 0;
            for(int i = 1; i<=max;i++){
                 if(i%2 == 1){
                     f.setFrage(red.readLine());
                 }
                 else{
                     f.setAntwort(red.readLine());
                     ret[count] = f;
                     count++;
                 }
            }
        }
        catch(IOException e){
            System.out.println("Fehlerö beim einlesen aus der Datei:" + path);
        }
        return ret;
    }

    public boolean check_file_exist(String filname){
        String path = Oberflaeche.path + "\\save\\" + filname + ".txt";
        File f = new File(path);
        if(f.exists()){
            return true;
        }
        return false;
    }

    /**
     * Erzeugt einen save Folder in StudyAi
     * Der Folder heißt save
     * @return
     */
    private boolean create_default_saveFolder(){
        String path = Oberflaeche.path + "\\save";
        File f = new File(path);
        return f.mkdir();
    }

}
