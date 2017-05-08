package ru.sccraft.scspeak;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.LinkedList;
import java.util.List;

public class WordInfoActivity extends AppCompatActivity {

    Word w;
    TextView thisLanguage, tvEN, tvMK, tvRU;
    LinearLayout llEN, llMK, llRU;
    Button bEN, bMK, bRU;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_info);
        setupActionBar();
        w = getIntent().getParcelableExtra("word");
        thisLanguage = (TextView) findViewById(R.id.wordInfo_thisLanguage);
        llEN = (LinearLayout) findViewById(R.id.wordInfo_layoutEN);
        llMK = (LinearLayout) findViewById(R.id.wordInfo_layoutMK);
        llRU = (LinearLayout) findViewById(R.id.wordInfo_layoutRU);
        bEN = (Button) findViewById(R.id.wordInfo_en);
        bMK = (Button) findViewById(R.id.wordInfo_mk);
        bRU = (Button) findViewById(R.id.wordInfo_ru);

        // Load an ad into the AdMob banner view.
        adView = (AdView) findViewById(R.id.adView);
        showAD();
        if (w == null) return;
        switch (getString(R.string.getSystemLanguage)) {
            case "en":
                llEN.setVisibility(View.GONE);
                thisLanguage.setText(w.en);
                break;
            case "mk":
                llMK.setVisibility(View.GONE);
                thisLanguage.setText(w.mk);
                break;
            case "ru":
                llRU.setVisibility(View.GONE);
                thisLanguage.setText(w.ru);
                break;
        }
        bEN.setText(w.en);
        bMK.setText(w.mk);
        bRU.setText(w.ru);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_word_info, menu);
        MenuItem экспорт = menu.findItem(R.id.action_toServer);
        SharedPreferences настройки = PreferenceManager.getDefaultSharedPreferences(this);
        boolean разрешить_экспорт;
        разрешить_экспорт = настройки.getBoolean("pref_export", false);
        экспорт.setVisible(разрешить_экспорт);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_share:
                share();
                return true;
            case R.id.action_toServer:
                toServer();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void toServer() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, w.toJSON());
        sendIntent.setType("text/plain");
        if (sendIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(sendIntent);
        }
    }

    private void share() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "EN: " + w.en + "\n" + "MK: " + w.mk + "\n" + "RU: " + w.ru + "\n" + "===== TRANSCRIPTIONS ====\n" + "EN ---> MK: " + w.enTranscriptionToMK + "\n" + "EN ---> RU: " + w.enTranscriptionToRU + "\n" + "MK ---> EN: " + w.mkTranscriptionToEN + "\n" + "MK ---> RU: " + w.mkTranscriptionToRU + "\n" + "RU ---> EN: " + w.ruTranscriptionToEN + "\n" + "RU ---> MK: " + w.ruTranscriptionToMK);
        sendIntent.setType("text/plain");
        if (sendIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(sendIntent);
        }
    }

    private void showTranscription(String language) {
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle(getString(R.string.transcription));  // заголовок
        switch (getString(R.string.getSystemLanguage)) {
            case "en":
                switch (language) {
                    case "en":
                        ad.setMessage("ERROR");
                        break;
                    case "mk":
                        ad.setMessage(w.mkTranscriptionToEN);
                        break;
                    case "ru":
                        ad.setMessage(w.ruTranscriptionToEN);
                        break;
                }
                break;
            case "mk":
                switch (language) {
                    case "en":
                        ad.setMessage(w.mkTranscriptionToEN);
                        break;
                    case "mk":
                        ad.setMessage("ERROR");
                        break;
                    case "ru":
                        ad.setMessage(w.ruTranscriptionToMK);
                        break;
                }
                break;
            case "ru":
                switch (language) {
                    case "en":
                        ad.setMessage(w.enTranscriptionToRU);
                        break;
                    case "mk":
                        ad.setMessage(w.mkTranscriptionToRU);
                        break;
                    case "ru":
                        ad.setMessage("ERROR");
                        break;
                }
                break;
        }
        ad.setPositiveButton(getString(R.string.close), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        ad.setCancelable(true);
        ad.show();
    }

    private void showAD() {
        adView.setVisibility(View.GONE);
        Fe fe = new Fe(this);
        String AD_DATA = fe.getFile("scspeak-ads");
        if (AD_DATA.contains("1")) return; //Для повышения вероятности работы покупки. Раньше использовался equals.
        if (getUsername().equals("sasha01945@gmail.com")) return;
        adView.setVisibility(View.VISIBLE);
        AdRequest adRequest = new AdRequest.Builder().setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);
    }

    public String getUsername() {
        AccountManager manager = AccountManager.get(this);
        SharedPreferences myPreference= PreferenceManager.getDefaultSharedPreferences(this);
        Boolean fl = myPreference.getBoolean("disableADsByEmail", false);
        if (!fl) return "a@b.c";
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            запросить_разрешение();
            return "a@b.c";
        }
        Account[] accounts = manager.getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<String>();

        for (Account account : accounts) {
            // TODO: Check possibleEmail against an email regex or treat
            // account.name as an email address only for certain account.type values.
            possibleEmails.add(account.name);
        }

        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
            String email = possibleEmails.get(0);
            return email;
        }
        return "a@b.c";
    }

    public void trancriptionEN(View view) {
        showTranscription("en");
    }

    public void transcriptionMK(View view) {
        showTranscription("mk");
    }

    public void transcriptionRU(View view) {
        showTranscription("ru");
    }

    public void fullscreenEN(View view) {
        Intent intent = new Intent(WordInfoActivity.this, WordFullscreenActivity.class);
        intent.putExtra("word_fullscreen", w.en);
        startActivity(intent);
    }

    public void fullscreenMK(View view) {
        Intent intent = new Intent(WordInfoActivity.this, WordFullscreenActivity.class);
        intent.putExtra("word_fullscreen", w.mk);
        startActivity(intent);
    }

    public void fullscreenRU(View view) {
        Intent intent = new Intent(WordInfoActivity.this, WordFullscreenActivity.class);
        intent.putExtra("word_fullscreen", w.en);
        startActivity(intent);
    }

    private void запросить_разрешение() {
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.GET_ACCOUNTS}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1 && grantResults.length == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showAD();
                Toast.makeText(getApplicationContext(), "All ADs on ScSpeak is disabled by FREE!\nВся реклама в ScSpeak успешно отключена!\nThank you for choosing e-mail " + getUsername(), Toast.LENGTH_LONG).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
