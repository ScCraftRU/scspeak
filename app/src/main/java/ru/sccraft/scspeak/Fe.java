package ru.sccraft.scspeak;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by alexandr on 19.02.17.
 * Операции с файлами
 */

class Fe {
    private static final String LOG_TAG = "Fe";
    private FileInputStream fin;
    private FileOutputStream fos;
    ContextWrapper a;

    Fe(Context a) {
        this.a = new ContextWrapper(a.getApplicationContext());
    }

    void saveFile(String name, String content){

        try {
            fos = a.openFileOutput(name, MODE_PRIVATE);
            fos.write(content.getBytes());
            Log.d(LOG_TAG, "File saved");
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

    String getFile(String name){
        try {
            fin = a.openFileInput(name);
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);
            String text = new String (bytes);
            Log.d(LOG_TAG, "Из файла \"" + name + "\" получен текст: " + text);
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

    boolean haveFile (String имя_файла) {
        String[] file = a.fileList();
        for (String файл : file) {
            if (имя_файла.equals(файл)) return true;
        }
        return false;
    }
}