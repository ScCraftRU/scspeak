package ru.sccraft.scspeak;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DownloadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
    }

    class Get extends AsyncTask<Void, Integer, Boolean> {
        Fe fe;

        @Override
        protected Boolean doInBackground(Void... params) {
            fe = new Fe();
            return null;
        }
    }
}
