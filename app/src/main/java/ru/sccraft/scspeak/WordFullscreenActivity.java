package ru.sccraft.scspeak;

import android.app.PictureInPictureParams;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class WordFullscreenActivity extends AppCompatActivity {

    private TextView слово;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_word_fullscreen);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.hide(); //Кнопка "НАЗАД" в ActionBar работала некорректно
        }

        слово = findViewById(R.id.word_fullscreen);
        if (слово == null) return;
        слово.setText(getIntent().getStringExtra("word_fullscreen"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void закрыть(View view) {
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (слово == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (слово.length() <= 3) {
                слово.setTextSize(50);
            } else if (слово.length() <= 5) {
                слово.setTextSize(42);
            } else {
                слово.setTextSize(16);
            }
            PictureInPictureParams.Builder настройки_режима_КВК = new PictureInPictureParams.Builder();
            enterPictureInPictureMode(настройки_режима_КВК.build());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (слово == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            слово.setTextSize(36);
        }
    }
}
