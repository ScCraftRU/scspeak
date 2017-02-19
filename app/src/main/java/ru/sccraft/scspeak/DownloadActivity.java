package ru.sccraft.scspeak;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.Toast;

public class DownloadActivity extends AppCompatActivity {

    ProgressBar pb;
    Get g;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        pb = (ProgressBar) findViewById(R.id.pb_download);
        g = (Get) getLastCustomNonConfigurationInstance();
        if (g == null) {
            g = new Get(this);
        }else{
            g.link(this);
        }
    }

    private void back(boolean b) {
        super.onBackPressed();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return g;
    }

    class Get extends AsyncTask<Void, Integer, Boolean> {
        DownloadActivity a;
        Fe fe;
        String[] file;
        SharedPreferences sp;
        String server;
        String[] fileName;
        String[] pathOnServer;
        boolean pr = false;
        int p;

        Get(DownloadActivity a) {
            this.a = a;
            this.execute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            fe = new Fe();
            file = fileList();
            sp = PreferenceManager.getDefaultSharedPreferences(a);
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
                a.back(true);
            }else{
                Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (!pr) {
                a.pb.setMax(fileName.length);
                pr = true;
            }
            p++;
            a.pb.setProgress(p);
        }

        void link(DownloadActivity a) {
            this.a = a;
        }
    }
}
