package ru.sccraft.scspeak;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WordInfoActivity extends AppCompatActivity {

    Word w;
    TextView thisLanguage;
    LinearLayout llEN, llMK, llRU;
    Button bEN, bMK, bRU;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_info);
        setupActionBar();
        w = getIntent().getParcelableExtra("word");
        thisLanguage = findViewById(R.id.wordInfo_thisLanguage);
        llEN = findViewById(R.id.wordInfo_layoutEN);
        llMK = findViewById(R.id.wordInfo_layoutMK);
        llRU = findViewById(R.id.wordInfo_layoutRU);
        bEN = findViewById(R.id.wordInfo_en);
        bMK = findViewById(R.id.wordInfo_mk);
        bRU = findViewById(R.id.wordInfo_ru);

        // Load an ad into the AdMob banner view.
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

        bEN.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(WordInfoActivity.this, WordFullscreenActivity.class);
                intent.putExtra("word_fullscreen", w.en);
                startActivity(intent);
                return true;
            }
        });

        bMK.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(WordInfoActivity.this, WordFullscreenActivity.class);
                intent.putExtra("word_fullscreen", w.mk);
                startActivity(intent);
                return true;
            }
        });

        bRU.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(WordInfoActivity.this, WordFullscreenActivity.class);
                intent.putExtra("word_fullscreen", w.ru);
                startActivity(intent);
                return true;
            }
        });
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

        if (id == R.id.action_share) {
            поделиться();
            return true;
        } else if (id == R.id.action_toServer) {
            экспорт_на_сервер();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void экспорт_на_сервер() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, w.toJSON());
        sendIntent.setType("text/plain");
        if (sendIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(sendIntent);
        }
    }

    private void поделиться() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "EN: " + w.en + "\n" + "MK: " + w.mk + "\n" + "RU: " + w.ru + "\n" + "===== TRANSCRIPTIONS ====\n" + "EN ---> MK: " + w.enTranscriptionToMK + "\n" + "EN ---> RU: " + w.enTranscriptionToRU + "\n" + "MK ---> EN: " + w.mkTranscriptionToEN + "\n" + "MK ---> RU: " + w.mkTranscriptionToRU + "\n" + "RU ---> EN: " + w.ruTranscriptionToEN + "\n" + "RU ---> MK: " + w.ruTranscriptionToMK);
        sendIntent.setType("text/plain");
        if (sendIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(sendIntent);
        }
    }

    private void показать_произношение(String language) {
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

    public void trancriptionEN(View view) {
        показать_произношение("en");
    }

    public void transcriptionMK(View view) {
        показать_произношение("mk");
    }

    public void transcriptionRU(View view) {
        показать_произношение("ru");
    }
}
