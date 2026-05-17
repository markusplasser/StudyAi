package org.example.manage;

import org.example.GUI.Aplication;

import java.io.File;

public class Main2 {

    public static void main2(String[] args){
        Fragen_Antworten[] f = new Fragen_Antworten[1];
        f[0] = new Fragen_Antworten();
        f[0].setFrage("Das ist Test1");
        f[0].setContent(new String[]{"ja","nein","vieleicht"});
        f[0].setLoesung(new boolean[]{true,false,false});
        Handle_Save hs = new Handle_Save("StudyAi" + File.separator + "save");
        hs.setArr(f);
        hs.setFilename("Test1");
        //hs.save();
        Fragen_Antworten[] f2 = hs.read("Test1");
        System.out.println(f2[0].toString());

    }

    public static void main(String[] args){
        Aplication.main(args);
    }
}
