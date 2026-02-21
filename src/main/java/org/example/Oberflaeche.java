package org.example;

import javax.swing.*;
import java.io.File;

public class Oberflaeche
{
    public static String path;

    public void start(){
        init();
        create_deafault_Folder();
    }

    private boolean create_deafault_Folder(){
        String path = System.getProperty("user.home");
        path += "\\StudyAi";
        File f = new File(path);
        this.path = path;
        return f.mkdir();
    }

    public void init(){
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800,600);
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
