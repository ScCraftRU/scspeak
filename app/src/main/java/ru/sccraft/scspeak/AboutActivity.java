package ru.sccraft.scspeak;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AboutActivity extends AppCompatActivity {

    int номер_сборки = 0; //VersionCode
    String название_версии; //VersionName
    TextView vc, vn;
    private byte кликни_пять_раз_не_поворачивая_экран;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setupActionBar();
        setTitle(getString(R.string.about));
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
        try {
            название_версии = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        try {
            номер_сборки = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        vc = findViewById(R.id.aboutVersionCode);
        vn = findViewById(R.id.aboutVN);
        vc.setText("" + номер_сборки);
        vn.setText(название_версии);
        кликни_пять_раз_не_поворачивая_экран = 0;
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void rateAPP(View view) {
        showDialog(1);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder ad;
        if (id == 1) {
            String title = getString(R.string.rateApp);
            String message = getString(R.string.goToGooglePlayQestion);
            String button1String = getString(R.string.yes);
            String button2String = getString(R.string.no);

            ad = new AlertDialog.Builder(AboutActivity.this);
            ad.setTitle(title);  // заголовок
            ad.setMessage(message); // сообщение
            ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("market://details?id=ru.sccraft.scspeak"));
                        startActivity(intent);
                    }catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
            ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                }
            });
            ad.setCancelable(true);
            ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                }
            });
            return ad.create();
        }
        return super.onCreateDialog(id);
    }

    private String получить_лог_приложения() {
        try {
            Process процесс = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(процесс.getInputStream()));

            StringBuilder лог = new StringBuilder();
            String линия = "";
            while ((линия = bufferedReader.readLine()) != null) {
                лог.append(линия);
            }
            return лог.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void получить_LogCat(View view) {
        if (кликни_пять_раз_не_поворачивая_экран < 5) {
            кликни_пять_раз_не_поворачивая_экран++;
            toast.setText(R.string.onlyForDevelopers);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.show();
            if (кликни_пять_раз_не_поворачивая_экран != 5) return;
        }
        toast.cancel();
        class Поток extends AsyncTask<Void, Void, Intent> {

            @Override
            protected Intent doInBackground(Void... params) {
                Intent отправить_лог = new Intent();
                отправить_лог.setAction(Intent.ACTION_SEND);
                отправить_лог.putExtra(Intent.EXTRA_TEXT, получить_лог_приложения());
                отправить_лог.setType("text/plain");
                return отправить_лог;
            }

            @Override
            protected void onPostExecute(Intent intent) {
                super.onPostExecute(intent);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        }

        Поток поток = new Поток();
        поток.execute();

    }

    public void privacy(View view) {
        Uri ссылка_на_политику_конфиденциальности = Uri.parse("http://sccraft.ru/android-app/scspeak/privacy/");
        Intent открыть_политику_конфиденциальности_в_браузере = new Intent(Intent.ACTION_VIEW, ссылка_на_политику_конфиденциальности);
        startActivity(открыть_политику_конфиденциальности_в_браузере);
    }
}
