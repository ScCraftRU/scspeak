package ru.sccraft.scspeak;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by alexandr on 18.02.17.
 */

public class Word implements Parcelable, Comparable<Word> {
    private static final String LOG_TAG = "Word";
    public String en;
    public String mk;
    public String ru;

    public String enTranscriptionToMK;
    public String enTranscriptionToRU;

    public String mkTranscriptionToEN;
    public String mkTranscriptionToRU;

    public String ruTranscriptionToEN;
    public String ruTranscriptionToMK;

    public Word() {

    }

    protected Word(Parcel in) {
        en = in.readString();
        mk = in.readString();
        ru = in.readString();
        enTranscriptionToMK = in.readString();
        enTranscriptionToRU = in.readString();
        mkTranscriptionToEN = in.readString();
        mkTranscriptionToRU = in.readString();
        ruTranscriptionToEN = in.readString();
        ruTranscriptionToMK = in.readString();
    }

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

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) return true;
        Word слово = (Word) obj;
        if(!(en.equals(слово.en))) return false;
        if(!(mk.equals(слово.mk))) return false;
        if(!(ru.equals(слово.ru))) return false;
        if(!(enTranscriptionToMK.equals(слово.enTranscriptionToMK))) return false;
        if(!(enTranscriptionToRU.equals(слово.enTranscriptionToRU))) return false;
        if(!(mkTranscriptionToEN.equals(слово.mkTranscriptionToEN))) return false;
        if(!(mkTranscriptionToRU.equals(слово.mkTranscriptionToRU))) return false;
        if(!(ruTranscriptionToEN.equals(слово.ruTranscriptionToEN))) return false;
        if(!(ruTranscriptionToMK.equals(слово.ruTranscriptionToMK))) return false;
        return true;
    }

    public boolean содержит(String word) {
        if (сравнить_строки_без_учёта_регистра(this.en, word)) return true;
        if (сравнить_строки_без_учёта_регистра(this.mk, word)) return true;
        if (сравнить_строки_без_учёта_регистра(this.ru, word)) return true;
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(en);
        dest.writeString(mk);
        dest.writeString(ru);
        dest.writeString(enTranscriptionToMK);
        dest.writeString(enTranscriptionToRU);
        dest.writeString(mkTranscriptionToEN);
        dest.writeString(mkTranscriptionToRU);
        dest.writeString(ruTranscriptionToEN);
        dest.writeString(ruTranscriptionToMK);
    }

    public static final Parcelable.Creator<Word> CREATOR = new Creator<Word>() {
        @Override
        public Word createFromParcel(Parcel source) {
            return new Word(source);
        }

        @Override
        public Word[] newArray(int size) {
            return new Word[size];
        }
    };

    @Override
    public int compareTo(@NonNull Word o) {
        switch (MainActivity.language) {
            case "en":
                return this.en.compareTo(o.en);
            case "mk":
                return this.mk.compareTo(o.mk);
            case "ru":
                return this.ru.compareTo(o.ru);
        }
        return 0;
    }

    public static boolean сравнить_строки_без_учёта_регистра(String строка_1, String строка_2)     {
        if(строка_1 == null || строка_2 == null) return false;

        final int length = строка_2.length();
        if (length == 0)
            return true;

        for (int i = строка_1.length() - length; i >= 0; i--) {
            if (строка_1.regionMatches(true, i, строка_2, 0, length))
                return true;
        }
        return false;
    }
}