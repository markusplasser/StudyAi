package org.example.manage;

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
        savePath = p;
        if(!isSaveFolderExisting(p)){
            createSaveFolder(p);
        }

    }
    public Handle_Save(String basePath, String subFolder) {
        savePath = basePath + File.separator + subFolder;
        if (!isSaveFolderExisting(savePath)) {
            createSaveFolder(savePath);
        }
    }
    /**
     * saves the current Fragen_Antworten[] arr
     */
    public void save() {

        String userpath =  savePath + File.separator + filename + ".bin";

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
            e.printStackTrace();
        }
    }

    /**
     * reads Fragen_Antworten[] from a file
     * only looking in the default save folder for the file
     * @param filename filename
     * @return filled Fragen_Antworten[]
     */
    public Fragen_Antworten[] read(String filename){
        if(!check_file_exist(filename)){
            return null;
        }

        String userpath = savePath + File.separator + filename;

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
            return null;
        }
        return ret.toArray(new Fragen_Antworten[0]);
    }

    public boolean check_file_exist(String filname){
        String userpath = savePath + File.separator + filname;
        File f = new File(userpath);
        return f.exists();
    }

    /**
     * Checks if the save folder exists
     * @return true if the folder exists
     */
    public boolean isSaveFolderExisting(String path){
        Path p = Paths.get(path);
        if(Files.isDirectory(p)){
            return true;
        }
        return false;
    }

    /**
     * creates a folder
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
