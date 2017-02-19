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
}
