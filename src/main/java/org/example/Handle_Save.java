package org.example;

import okio.Buffer;

import java.io.*;

public class Handle_Save {
    public final String path = System.getProperty("user.dir");
    private Fragen_Antworten[] arr;
    private String filename;


    public Handle_Save(Fragen_Antworten[] arr,String filename) {
        this.filename = filename;
        this.arr = arr;
    }

    /**
     * übernimmt das Speichern von ganzen Fargen_Antworten arrays
     * Speichert am Anfang die Anzahl der Fragen + die Anz der Antwortmöglichkeiten
     * StudyAi\save\filename - Speicherort
     */
    public void save() {
        if (!create_default_saveFolder()) {
            System.out.println("Fehler beim erzeugen des save folders");
        }
        String userpath =  path + "\\save\\" + filename + ".txt";

        try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(userpath)))) {
            dos.writeInt(arr.length);
            dos.writeInt(arr[0].getContent().length);
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

        String userpath = path + "\\save\\" + filename + ".txt";

        Fragen_Antworten[] ret = new Fragen_Antworten[0];
        try(DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(userpath)))) {
            int length = dis.readInt();
            int anz_fragen = dis.readInt();
            for(int i=0;i<length;i++) {
                String frage =  dis.readUTF();
                String[] content = new String[anz_fragen];
                boolean[] bool = new boolean[anz_fragen];
                for(int j = 0; j < anz_fragen;j++){
                    content[j] = dis.readUTF();
                    bool[j] = dis.readBoolean();
                }
                ret[i] = new Fragen_Antworten(frage,content,bool);
            }
        }
        catch(IOException e){
            System.out.println("Fehlerö beim einlesen aus der Datei:" + userpath);
        }
        return ret;
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
     * Erzeugt einen save Folder in StudyAi
     * Der Folder heißt "save"
     * @return
     */
    private boolean create_default_saveFolder(){
        String userpath = path + "\\save";
        File f = new File(userpath);
        return f.mkdir();
    }

}
