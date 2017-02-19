package ru.sccraft.scspeak;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by alexandr on 18.02.17.
 */

public class Word {
    static final String LOG_TAG = "Word";
    public String en;
    public String mk;
    public String ru;

    public String enTranscriptionToMK;
    public String enTranscriptionToRU;

    public String mkTranscriptionToEN;
    public String mkTranscriptionToRU;

    public String ruTranscriptionToEN;
    public String ruTranscriptionToMK;

    public String toJSON() {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(this);
    }

    static Word fromJSON(String JSON) {
        Log.i(LOG_TAG, "Входящий JSON " + JSON);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Word w = gson.fromJson(JSON, Word.class);
        return w;
    }
}
