package ru.sccraft.scspeak;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

public class WordEditActivity extends AppCompatActivity {

    Word w;
    EditText etEN, etMK, etRU, etEN_MK, etEN_RU, etMK_EN, etMK_RU, etRU_EN, etRU_MK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_edit);
        w = getIntent().getParcelableExtra("word");
        etEN = (EditText) findViewById(R.id.wordEdit_ENet);
        etMK = (EditText) findViewById(R.id.wordEdit_MKet);
        etRU = (EditText) findViewById(R.id.wordEdit_RUet);
        etEN_MK = (EditText) findViewById(R.id.WordEdit_enMKet);
        etEN_RU = (EditText) findViewById(R.id.WordEdit_enRUet);
        etMK_EN = (EditText) findViewById(R.id.WordEdit_mkENet);
        etMK_RU = (EditText) findViewById(R.id.WordEdit_mkRUet);
        etRU_EN = (EditText) findViewById(R.id.WordEdit_RUenet);
        etRU_MK = (EditText) findViewById(R.id.WordEdit_ruMKet);
        if (w == null) {
            setTitle(getString(R.string.newWord));
            w = new Word();
        }else{
            setTitle(getString(R.string.editWord));
            etEN.setText(w.en);
            etMK.setText(w.mk);
            etRU.setText(w.ru);
        }
    }
}
