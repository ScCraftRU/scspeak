package ru.sccraft.scspeak;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class DownloadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
    }

    class Get extends AsyncTask<Void, Integer, Boolean> {

        FileInputStream fin;
        FileOutputStream fos;

        @Override
        protected Boolean doInBackground(Void... params) {
            return null;
        }
    }

    public void saveText(String name, String content){

        try {
            fos = openFileOutput(name, MODE_PRIVATE);
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
