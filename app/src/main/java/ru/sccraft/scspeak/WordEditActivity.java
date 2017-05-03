package ru.sccraft.scspeak;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Random;

public class WordEditActivity extends AppCompatActivity {

    Word w;
    EditText etEN, etMK, etRU, etEN_MK, etEN_RU, etMK_EN, etMK_RU, etRU_EN, etRU_MK;
    boolean newW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_edit);
        setupActionBar();
        w = getIntent().getParcelableExtra("word");
        if (w == null) {newW = true;} else {newW = false;}
        etEN = (EditText) findViewById(R.id.wordEdit_ENet);
        etMK = (EditText) findViewById(R.id.wordEdit_MKet);
        etRU = (EditText) findViewById(R.id.wordEdit_RUet);
        etEN_MK = (EditText) findViewById(R.id.WordEdit_enMKet);
        etEN_RU = (EditText) findViewById(R.id.WordEdit_enRUet);
        etMK_EN = (EditText) findViewById(R.id.WordEdit_mkENet);
        etMK_RU = (EditText) findViewById(R.id.WordEdit_mkRUet);
        etRU_EN = (EditText) findViewById(R.id.WordEdit_RUenet);
        etRU_MK = (EditText) findViewById(R.id.WordEdit_ruMKet);
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
        switch (id) {
            case R.id.action_save:
                save();
                return true;
            case R.id.action_delete:
                delete();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void save() {
        Fe fe = new Fe(this);
        if (newW) {
            updateTempWordClass();
            fe.saveFile((new Random()).nextInt(Integer.MAX_VALUE) + ".json", w.toJSON());
            Toast.makeText(getApplicationContext(), getString(R.string.fileSaved), Toast.LENGTH_LONG).show();
            finish();
        }else{
            String[] file = fileList();
            String fileName = "";
            updateTempWordClass();
            Word original = getIntent().getParcelableExtra("word");
            for (int i = 0; i < file.length; i++) {
                if (!((file[i].equals("instant-run"))||(file[i].equals("scspeak-ads")))) {
                    String JSON = fe.getFile(file[i]);
                    Word слово = Word.fromJSON(JSON);
                    if (!слово.equals(original)) continue;
                    fileName = file[i];
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
        String fileName = "";
        updateTempWordClass();
        Word original = getIntent().getParcelableExtra("word");
        for (int i = 0; i < file.length; i++) {
            if (!(file[i].equals("instant-run"))) {
                String JSON = fe.getFile(file[i]);
                Word слово = Word.fromJSON(JSON);
                if (!(слово.equals(original))) continue;
                fileName = file[i];
                deleteFile(fileName);
                Toast.makeText(getApplicationContext(), getString(R.string.fileDeleted), Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
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
}
