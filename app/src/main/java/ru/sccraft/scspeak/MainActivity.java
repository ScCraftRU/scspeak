package ru.sccraft.scspeak;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static String LOG_TAG = "MainActivity";
    Word[] w, sr;
    String[] s, vW; //Слова на текущем языке
    ListView lw;
    String[] file;
    SearchView searchView;
    String swResult = ""; //Текст из SearchView
    static String language;
    private Menu menu;
    private MenuItem searchItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        language = getString(R.string.getSystemLanguage);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        lw = (ListView) findViewById(R.id.lw);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WordEditActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        file = fileList();
        if (file.length < 2) {
            //Скрыть недоступные элементы GUI
            if (searchItem != null) {
                searchItem.setVisible(false);
                MenuItem exportItem = menu.findItem(R.id.action_exportAll);
                exportItem.setVisible(false);
            }
        } else {
            if (searchItem != null) {
                searchItem.setVisible(true);
                MenuItem exportItem = menu.findItem(R.id.action_exportAll);
                exportItem.setVisible(true);
            }
        }
        if (file.length > 1) {
            updateWordList();
            search(swResult);
        }else{
            String[] noWords = getResources().getStringArray(R.array.noWordsArray);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, noWords);

            lw.setAdapter(adapter);
            lw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (position) {
                        case 1:
                            Intent intent = new Intent(MainActivity.this, DownloadActivity.class);
                            startActivity(intent);
                            break;
                        case 3:
                            Uri address = Uri.parse("https://sites.google.com/view/scspeak/%D0%BF%D0%BE%D0%BB%D0%B8%D1%82%D0%B8%D0%BA%D0%B0-%D0%BA%D0%BE%D0%BD%D1%84%D0%B8%D0%B4%D0%B5%D0%BD%D1%86%D0%B8%D0%B0%D0%BB%D1%8C%D0%BD%D0%BE%D1%81%D1%82%D0%B8?authuser=0");
                            Intent openlink = new Intent(Intent.ACTION_VIEW, address);
                            startActivity(openlink);
                            break;
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(SEARCH_SERVICE);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    swResult = newText;
                    search(swResult);
                    return true;
                }
            });
        }

        this.menu = menu;
        if (file.length < 2) {
            //Скрыть недоступные элементы GUI
            if (searchItem != null) {
                searchItem.setVisible(false);
                MenuItem exportItem = menu.findItem(R.id.action_exportAll);
                exportItem.setVisible(false);
            }
        } else {
            if (searchItem != null) {
                searchItem.setVisible(true);
                MenuItem exportItem = menu.findItem(R.id.action_exportAll);
                exportItem.setVisible(true);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_exportAll:
                exportAllWords();
                break;
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_about:
                Intent intent1 = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent1);
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateWordList() {
        {
            Fe fe = new Fe(this);
            ArrayList<Word> al = new ArrayList<>();
            for (int i = 0; i < file.length; i++) {
                if (!((file[i].equals("instant-run"))||(file[i].equals("scspeak-ads")))) {
                    al.add(Word.fromJSON(fe.getFile(file[i])));
                }

            }
            w = al.toArray(new Word[al.size()]);
        }
        s = new String[w.length];
        for (int i = 0; i < w.length; i++) {
            switch (getString(R.string.getSystemLanguage)) {
                case "en":
                    s[i] = w[i].en;
                    break;
                case "mk":
                    s[i] = w[i].mk;
                    break;
                case "ru":
                    s[i] = w[i].ru;
                    break;
            }
        }
    }

    private void search(String st) {
        if (st == null) {
            swResult = "";
            search(swResult);
            return;
        }
        ArrayList<Word> searchResult = new ArrayList<>();
        SharedPreferences настройки = PreferenceManager.getDefaultSharedPreferences(this);
        String язык_поиска = настройки.getString("pref_searchLanguage", "");
        final String[] настройки_поиска = getResources().getStringArray(R.array.pref_search_array);
        if (язык_поиска.equals(настройки_поиска[0])) {
            for (int i = 0; i < w.length; i++) {
                if (s[i].contains(st)) {
                    searchResult.add(w[i]);
                }
            }
        } else if (язык_поиска.equals(настройки_поиска[1])) {
            for (int i = 0; i < w.length; i++) {
                if (w[i].contains(st)) {
                    searchResult.add(w[i]);
                }
            }
        } else {
            for (int i = 0; i < w.length; i++) {
                if (s[i].contains(st)) {
                    searchResult.add(w[i]);
                }
            }
        }
        sr = searchResult.toArray(new Word[searchResult.size()]);
        Arrays.sort(sr);
        vW = new String[sr.length];
        for(int i = 0; i < sr.length; i++) {
            switch (getString(R.string.getSystemLanguage)) {
                case "en":
                    vW[i] = sr[i].en;
                    break;
                case "mk":
                    vW[i] = sr[i].mk;
                    break;
                case "ru":
                    vW[i] = sr[i].ru;
                    break;
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, vW);

        lw.setAdapter(adapter);
        lw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < vW.length; i++) {
                    switch (getString(R.string.getSystemLanguage)) {
                        case "en":
                            if (sr[i].en.equals(vW[position])) info(sr[i]);
                            break;
                        case "mk":
                            if (sr[i].mk.equals(vW[position])) info(sr[i]);
                            break;
                        case "ru":
                            if (sr[i].ru.equals(vW[position])) info(sr[i]);
                            break;
                    }
                }
            }
        });
        lw.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < s.length; i++) {
                    switch (getString(R.string.getSystemLanguage)) {
                        case "en":
                            if (sr[i].en.equals(vW[position])) edit(sr[i]);
                            break;
                        case "mk":
                            if (sr[i].mk.equals(vW[position])) edit(sr[i]);
                            break;
                        case "ru":
                            if (sr[i].ru.equals(vW[position])) edit(sr[i]);
                            break;
                    }
                }
                return true;
            }
        });

    }

    private void info(Word word) {
        Intent intent = new Intent(MainActivity.this, WordInfoActivity.class);
        intent.putExtra("word", word);
        startActivity(intent);
    }

    private void edit(Word word) {
        Intent intent = new Intent(MainActivity.this, WordEditActivity.class);
        intent.putExtra("word", word);
        startActivity(intent);
    }

    private void exportAllWords() {
        Fe fe = new Fe(this);
        String tittle = "This data ONLY for ScSpeak server!\nMore information on http://sccraft.ru/publ/scspeak/guide/howtocreateserver/2-1-0-1";
        String rasdelitel = "=================================================================\n";
        String data = tittle + rasdelitel;
        for (int i = 0; i < file.length; i++) {
            if (!(file[i].equals("instant-run"))) {
                data = data + file[i] + "\n" + fe.getFile(file[i]) + "\n" + rasdelitel;
            }
        }
        data = data + "END OF SERVER DATA";

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, data);
        sendIntent.setType("text/plain");
        if (sendIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(sendIntent);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Log.d(LOG_TAG, swResult);
        outState.putString("search", swResult);
        Log.d(LOG_TAG, outState.toString());

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        swResult = savedInstanceState.getString("search");
    }
}
