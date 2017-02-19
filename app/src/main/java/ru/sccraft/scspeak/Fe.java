package ru.sccraft.scspeak;

import android.content.ContextWrapper;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by alexandr on 19.02.17.
 * Операции с файлами
 */

public class Fe {
    FileInputStream fin;
    FileOutputStream fos;
    ContextWrapper a = null;

    public void saveText(String name, String content){

        try {
            fos = a.openFileOutput(name, MODE_PRIVATE);
            fos.write(content.getBytes());
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
        finally{
            try{
                if(fos!=null)
                    fos.close();
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
    }

    public String openText(String name){
        try {
            fin = a.openFileInput(name);
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);
            String text = new String (bytes);
            return text;
        }
        catch(IOException ex) {
            ex.printStackTrace();
            return "File read error";
        }
        finally{

            try{
                if(fin!=null)
                    fin.close();
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
    }
}
