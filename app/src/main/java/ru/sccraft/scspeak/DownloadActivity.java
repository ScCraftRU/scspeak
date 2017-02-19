package ru.sccraft.scspeak;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class DownloadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
    }

    private void back() {
        super.onBackPressed();
    }

    class Get extends AsyncTask<Void, Integer, Boolean> {
        DownloadActivity a;
        Fe fe;
        String[] file;
        SharedPreferences sp;
        String server;
        String[] fileName;
        String[] pathOnServer;

        Get(DownloadActivity a) {
            this.a = a;
            this.execute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            fe = new Fe();
            file = fileList();
            sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            server = sp.getString("word_server", "http://sccraft.ru/android-app/scspeak").toString();
            String[] serverWoordList = NetGet.getMultiLine(server + "list.sccraft");
            fileName = new String[serverWoordList.length];
            pathOnServer = new String[serverWoordList.length];
            for (int i = 0; i < serverWoordList.length; i++) {
                String[] temp = serverWoordList[i].split(" ");
                fileName[i] = temp[0];
                pathOnServer[i] = temp[1];
            }
            for (int i = 0; i < fileName.length; i++) {
                boolean fl = true;
                for (int j = 0; j < file.length; j++) {
                    if (fileName[i].equals(file[j])) {
                        fl = false;
                        break;
                    }
                }
                if (fl) fe.saveText(fileName[i], NetGet.getOneLine(server + pathOnServer));
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                Toast.makeText(getApplicationContext(), a.getString(R.string.done), Toast.LENGTH_SHORT).show();
                a.back();
            }
        }
    }
}
