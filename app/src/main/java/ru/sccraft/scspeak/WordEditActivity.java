package ru.sccraft.scspeak;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class WordEditActivity extends AppCompatActivity implements TextWatcher {

    Word w;
    EditText etEN, etMK, etRU, etEN_MK, etEN_RU, etMK_EN, etMK_RU, etRU_EN, etRU_MK;
    TextView tvEN, tvMK, tvRU, tvEN_MK, tvEN_RU, tvMK_EN, tvMK_RU, tvRU_EN, tvRU_MK;
    boolean newW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_edit);
        setupActionBar();
        w = Word.fromJSON(getIntent().getStringExtra("word"));
        newW = w == null; //= if (w == null){newW = true;}else{newW = false}

        etEN = findViewById(R.id.wordEdit_ENet);
        etMK = findViewById(R.id.wordEdit_MKet);
        etRU = findViewById(R.id.wordEdit_RUet);
        etEN_MK = findViewById(R.id.WordEdit_enMKet);
        etEN_RU = findViewById(R.id.WordEdit_enRUet);
        etMK_EN = findViewById(R.id.WordEdit_mkENet);
        etMK_RU = findViewById(R.id.WordEdit_mkRUet);
        etRU_EN = findViewById(R.id.WordEdit_RUenet);
        etRU_MK = (EditText) findViewById(R.id.WordEdit_ruMKet);

        tvEN = findViewById(R.id.textView_editEN);
        tvMK = findViewById(R.id.textView_editMK);
        tvRU = findViewById(R.id.textView_editRU);
        tvEN_MK = findViewById(R.id.textView_editENtoMK);
        tvEN_RU = findViewById(R.id.textView_editENtoRU);
        tvMK_EN = findViewById(R.id.textView_editMKtoEN);
        tvMK_RU = findViewById(R.id.textView_editMKtoRU);
        tvRU_EN = findViewById(R.id.textView_editRUtoEN);
        tvRU_MK = findViewById(R.id.textView_editRUtoMK);

        etEN.addTextChangedListener(this);
        etMK.addTextChangedListener(this);
        etRU.addTextChangedListener(this);
        etEN_MK.addTextChangedListener(this);
        etEN_RU.addTextChangedListener(this);
        etMK_EN.addTextChangedListener(this);
        etMK_RU.addTextChangedListener(this);
        etRU_EN.addTextChangedListener(this);
        etRU_MK.addTextChangedListener(this);

        if (newW) {
            setTitle(getString(R.string.newWord));
            w = (Word) getLastCustomNonConfigurationInstance();
            if (w == null) {
                w = new Word();
            }
        }else{
            setTitle(getString(R.string.editWord));
            etEN.setText(w.en);
            etMK.setText(w.mk);
            etRU.setText(w.ru);
            etEN_MK.setText(w.enTranscriptionToMK);
            etEN_RU.setText(w.enTranscriptionToRU);
            etMK_EN.setText(w.mkTranscriptionToEN);
            etMK_RU.setText(w.mkTranscriptionToRU);
            etRU_EN.setText(w.ruTranscriptionToEN);
            etRU_MK.setText(w.ruTranscriptionToMK);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_wordedit, menu);
        MenuItem удалить = menu.findItem(R.id.action_delete);
        if (newW) удалить.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            save();
            return true;
        } else if (id == R.id.action_delete) {
            delete();
        }
        return super.onOptionsItemSelected(item);
    }

    private void save() {
        Fe fe = new Fe(this);
        if (newW) {
            updateTempWordClass();
            Random генератор = new Random();
            String fileName = генератор.nextInt() + ".json";
            while (fe.haveFile(fileName)) {
                fileName = генератор.nextInt() + ".json";
            }
            fe.saveFile(fileName, w.toJSON());
            Toast.makeText(getApplicationContext(), getString(R.string.fileSaved), Toast.LENGTH_LONG).show();
            finish();
        }else{
            String[] file = fileList();
            String fileName;
            updateTempWordClass();
            Word original = Word.fromJSON(getIntent().getStringExtra("word"));
            for (String aFile : file) {
                if (!((aFile.equals("instant-run")) || (aFile.equals("scspeak-ads")))) {
                    if (aFile.contains("rList-ru.sccraft.scspeak.")) continue; //устраняет сбой на Samsung GALAXY S6
                    String JSON = fe.getFile(aFile);
                    Word слово = Word.fromJSON(JSON);
                    if (слово == null) continue;
                    if (!слово.equals(original)) continue;
                    fileName = aFile;
                    fe.saveFile(fileName, w.toJSON());
                    Toast.makeText(getApplicationContext(), getString(R.string.fileSaved), Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
            }
            Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
        }
    }

    private void delete() {
        if (newW) return;
        String[] file = fileList();
        Fe fe = new Fe(this);
        String fileName;
        updateTempWordClass();
        Word original = Word.fromJSON(getIntent().getStringExtra("word"));
        for (String aFile : file) {
            if (!((aFile.equals("instant-run")) || (aFile.equals("scspeak-ads")))) {
                if (aFile.contains("rList-ru.sccraft.scspeak.")) continue; //устраняет сбой на Samsung GALAXY S6
                String JSON = fe.getFile(aFile);
                Word слово = Word.fromJSON(JSON);
                if (слово == null) continue;
                if (!(слово.equals(original))) continue;
                fileName = aFile;
                deleteFile(fileName);
                Toast.makeText(getApplicationContext(), getString(R.string.fileDeleted), Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void обновить_подсказки() {
        if (etEN.getText().toString().equals("")) tvEN.setVisibility(View.GONE); else tvEN.setVisibility(View.VISIBLE);
        if (etMK.getText().toString().equals("")) tvMK.setVisibility(View.GONE); else tvMK.setVisibility(View.VISIBLE);
        if (etRU.getText().toString().equals("")) tvRU.setVisibility(View.GONE); else tvRU.setVisibility(View.VISIBLE);
        if (etEN_MK.getText().toString().equals("")) tvEN_MK.setVisibility(View.GONE); else tvEN_MK.setVisibility(View.VISIBLE);
        if (etEN_RU.getText().toString().equals("")) tvEN_RU.setVisibility(View.GONE); else tvEN_RU.setVisibility(View.VISIBLE);
        if (etMK_EN.getText().toString().equals("")) tvMK_EN.setVisibility(View.GONE); else tvMK_EN.setVisibility(View.VISIBLE);
        if (etMK_RU.getText().toString().equals("")) tvMK_RU.setVisibility(View.GONE); else tvMK_RU.setVisibility(View.VISIBLE);
        if (etRU_EN.getText().toString().equals("")) tvRU_EN.setVisibility(View.GONE); else tvRU.setVisibility(View.VISIBLE);
        if (etRU_MK.getText().toString().equals("")) tvRU_MK.setVisibility(View.GONE); else tvRU_MK.setVisibility(View.VISIBLE);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        updateTempWordClass();
        return w;
    }

    private void updateTempWordClass() {
        w.en = etEN.getText().toString();
        w.mk = etMK.getText().toString();
        w.ru = etRU.getText().toString();
        w.enTranscriptionToMK = etEN_MK.getText().toString();
        w.enTranscriptionToRU = etEN_RU.getText().toString();
        w.mkTranscriptionToEN = etMK_EN.getText().toString();
        w.mkTranscriptionToRU = etMK_RU.getText().toString();
        w.ruTranscriptionToEN = etRU_EN.getText().toString();
        w.ruTranscriptionToMK = etRU_MK.getText().toString();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        обновить_подсказки();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        обновить_подсказки();
    }

    @Override
    public void afterTextChanged(Editable s) {
        обновить_подсказки();
    }
}