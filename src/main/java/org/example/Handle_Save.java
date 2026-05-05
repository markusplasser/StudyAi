package org.example;

import okio.Buffer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Handle_Save {
    public final String path = System.getProperty("user.home");
    public static String savePath;
    private Fragen_Antworten[] arr;
    private String filename;


    public String getPath() {
        return path;
    }

    public Fragen_Antworten[] getArr() {
        return arr;
    }

    public void setArr(Fragen_Antworten[] arr) {
        this.arr = arr;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Handle_Save(String p){
        savePath = this.path + "\\" + p;
        if(!isSaveFolder(savePath)){
            createSaveFolder(savePath);
        }

    }
    /**
     * übernimmt das Speichern von ganzen Fargen_Antworten arrays
     * Speichert am Anfang die Anzahl der Fragen + die Anz der Antwortmöglichkeiten
     * StudyAi\save\filename - Speicherort
     */
    public void save() {

        String userpath =  path + "\\save\\" + filename + ".txt";

        try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(userpath)))) {
            dos.writeInt(arr.length); //wie viele Fragen Insgesamt
            dos.writeInt(arr[0].getContent().length); //wie viele Antwormöglichkeiten es pro frage gibt - kann sich nur von Datei zu Datei ändern
            for(int i=0;i<arr.length;i++) {
                dos.writeUTF(arr[i].getFrage());
                for(int j = 0; j<arr[i].getContent().length;j++) {
                    dos.writeUTF(arr[i].getContent()[j]);
                    dos.writeBoolean(arr[i].getLoesung()[j]);
                }
            }
        } catch (IOException e) {
            System.out.println("Fehler beim erzeugen der txt Datei:" + userpath);
        }
    }

    /**
     * Einlesen von ganzen Datein die um richtigen Vormat abgespeichert worden sind
     * Gibt den ganzen Inhalt der Datei in einem Fragen-Antworten array zurück
     * @param filename
     * @return
     */
    public Fragen_Antworten[] read(String filename){
        if(!check_file_exist(filename)){
            System.out.println("Das File das eingelesen werden soll gibt es nicht");
        }

        String userpath = savePath + filename + ".txt";

        ArrayList<Fragen_Antworten> ret = new ArrayList<>();

        try(DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(userpath)))) {
            int length = dis.readInt(); // wie viele Fragen es insgesamt zum einlesen gibt
            int anz_fragen = dis.readInt(); // wie viele Antwortmöglichkeiten es gibt - kann sich nur von Datei zu Datei ändern
            for(int i=0;i<length;i++) {
                String frage =  dis.readUTF();
                String[] content = new String[anz_fragen];
                boolean[] bool = new boolean[anz_fragen];
                for(int j = 0; j < anz_fragen;j++){
                    content[j] = dis.readUTF();
                    bool[j] = dis.readBoolean();
                }
                ret.add(new Fragen_Antworten(frage,content,bool));
            }
        }
        catch(IOException e){
            System.out.println("Fehlerö beim einlesen aus der Datei:" + userpath);
            return null;
        }
        return ret.toArray(new Fragen_Antworten[0]);
    }

    public boolean check_file_exist(String filname){
        String userpath = path + "\\save\\" + filname + ".txt";
        File f = new File(userpath);
        if(f.exists()){
            return true;
        }
        return false;
    }

    /**
     * Checks if the save folder exists
     * @return
     */
    public boolean isSaveFolder(String path){
        Path p = Paths.get(path);
        if(Files.isDirectory(p)){
            return true;
        }
        return false;
    }

    /**
     * Erzeugt den Path mit dem save Folder
     */
    private void createSaveFolder(String path){
        Path p = Paths.get(path);
        try{
            Files.createDirectories(p);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }



}
