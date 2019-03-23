package ru.sccraft.scspeak;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

public class DownloadActivity extends AppCompatActivity {

    ProgressBar pb;
    Get g;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        setTitle(getString(R.string.pleaseWait));
        pb = findViewById(R.id.pb_download);
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

    @Override
    public void onBackPressed() {
        //Действие запрещено!
    }

    private class Get extends AsyncTask<Void, Integer, Boolean> {
        static final String LOG_TAG = "DownloadActivity/Get";
        DownloadActivity a;
        Fe fe;
        String[] file;
        SharedPreferences sp;
        String server;
        String[] fileName;
        String[] pathOnServer;
        boolean pr = false;

        Get(DownloadActivity a) {
            this.a = a;
            this.execute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //if(!NetGet.getNetworkConnectionStatus(a)) return false;
            fe = new Fe(a);
            file = fileList();
            sp = PreferenceManager.getDefaultSharedPreferences(a);
            server = sp.getString("word_server", "http://sccraft.ru/android-app/scspeak/");
            Log.i(LOG_TAG, "Загрузка с сервера:" + server);
            String swl = server + "list.sccraft";
            Log.i(LOG_TAG, "Путь к файлу со списком слов: " + swl);
            String[] serverWoordList = NetGet.getMultiLine(swl);
            if (serverWoordList[0].equals("Connection error")) return false;
            Log.i(LOG_TAG, "Список слов на сервере:");
            for (int i = 0; i < serverWoordList.length; i++) {
                Log.i(LOG_TAG, serverWoordList[i]);
            }
            fileName = new String[serverWoordList.length];
            pathOnServer = new String[serverWoordList.length];
            for (int i = 0; i < serverWoordList.length; i++) {
                String[] temp = serverWoordList[i].split(" ");
                fileName[i] = temp[0] + ".json";
                pathOnServer[i] = temp[1];
            }
            for (int i = 0; i < fileName.length; i++) {
                if (fe.haveFile(fileName[i])) continue;
                String s = NetGet.getOneLine(server + "words/" + pathOnServer[i] + ".json");
                if (s.equals("Connection error")) return false;
                if (s.equals("")) continue; //Не сохранять пустые, или несуществующие файлы
                fe.saveFile(fileName[i], s);
                publishProgress(i);
            }
            if (a.fileList().length == 0) {
                for (int i = 0; i < fileName.length; i++)  {
                    String s = NetGet.getOneLine(server + "words/" + pathOnServer[i] + ".json");
                    if (s.equals("Connection error")) return false;
                    if (s.equals("")) continue;
                    fe.saveFile(fileName[i], s);
                    publishProgress(i);
                }
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
                a.back(false);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (!pr) {
                a.pb.setMax(fileName.length);
                pr = true;
            }
            a.pb.setProgress(values[0]);
        }

        void link(DownloadActivity a) {
            this.a = a;
        }
    }
}
