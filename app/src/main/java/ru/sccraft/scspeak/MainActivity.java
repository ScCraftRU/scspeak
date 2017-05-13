package ru.sccraft.scspeak;

import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
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
    private static boolean показывать_диалог = true;

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
        updateMenu();
        if (file.length > 1) {
            updateWordList();
            search(swResult);
        }else{
            if (показывать_диалог) предложить_скачать_слова();
            String[] noWords = getResources().getStringArray(R.array.noWordsArray);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, noWords);

            lw.setAdapter(adapter);
            lw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (position) {
                        case 1:
                            скачать_слова();
                            break;
                        case 3:
                            Uri address = Uri.parse("http://sccraft.ru/android-app/scspeak/privacy/");
                            Intent openlink = new Intent(Intent.ACTION_VIEW, address);
                            startActivity(openlink);
                            break;
                        default:
                            предложить_скачать_слова();
                    }
                }
            });
        }
    }

    private void предложить_скачать_слова() {
        AlertDialog.Builder ad;
        String title = getResources().getStringArray(R.array.noWordsArray)[0];
        String message = getString(R.string.noWordsDialogContent);
        String button1String = getString(R.string.yes);
        String button2String = getString(R.string.no);

        ad = new AlertDialog.Builder(this);
        ad.setTitle(title);  // заголовок
        ad.setMessage(message); // сообщение
        ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                показывать_диалог = false;
                скачать_слова();
            }
        });
        ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                показывать_диалог = false;
            }
        });
        ad.setCancelable(true);
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                показывать_диалог = false;
            }
        });
        ad.show();
    }

    private void скачать_слова() {
        Intent intent = new Intent(MainActivity.this, DownloadActivity.class);
        startActivity(intent);
    }

    private void updateMenu() {
        SharedPreferences настройки = PreferenceManager.getDefaultSharedPreferences(this);
        boolean разрешить_экспорт;
        разрешить_экспорт = настройки.getBoolean("pref_export", false);
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
                exportItem.setVisible(разрешить_экспорт);
            }
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
        updateMenu();
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
            for (String aFile : file) {
                if (!((aFile.equals("instant-run")) || (aFile.equals("scspeak-ads")))) {
                    if (aFile.contains("rList-ru.sccraft.scspeak.")) continue; //устраняет сбой на Samsung GALAXY S6
                    al.add(Word.fromJSON(fe.getFile(aFile)));
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
            for (Word aW : w) {
                if (aW.contains(st)) {
                    searchResult.add(aW);
                }
            }
        } else if (язык_поиска.equals(настройки_поиска[2])){
            for (Word aW : w) {
                if (aW.en.contains(st)) {
                    searchResult.add(aW);
                }
            }
        } else if (язык_поиска.equals(настройки_поиска[3])) {
            for (Word aW : w) {
                if (aW.mk.contains(st)) {
                    searchResult.add(aW);
                }
            }
        } else if (язык_поиска.equals(настройки_поиска[4])) {
            for (Word aW : w) {
                if (aW.ru.contains(st)) {
                    searchResult.add(aW);
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
        intent.putExtra("word", word.toJSON());
        startActivity(intent);
    }

    private void exportAllWords() {
        class Поток extends AsyncTask<Void, Void, Intent> {

            @Override
            protected Intent doInBackground(Void... params) {
                Log.i(LOG_TAG, "запущен экспорт слов");
                Fe fe = new Fe(MainActivity.this);
                String tittle = "This data ONLY for ScSpeak server!\nMore information on http://sccraft.ru/index.php/guide/10-scspeak/2-scspeak-create-server\n";
                String rasdelitel = "=================================================================\n";
                String data = tittle + rasdelitel;
                for (String aFile : file) {
                    if (!(aFile.equals("instant-run"))) {
                        data = data + aFile + "\n" + fe.getFile(aFile) + "\n" + rasdelitel;
                    }
                }
                data = data + "END OF SERVER DATA";

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, data);
                sendIntent.setType("text/plain");
                Log.i(LOG_TAG, "Экспорт слов завершён! Пользователю необходимо выбрать приложение, в которое будет проведён экспорт.");
                return sendIntent;
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
