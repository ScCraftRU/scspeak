package ru.sccraft.scspeak;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by alexandr on 18.02.17.
 */

public class Word implements Parcelable {
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

    public boolean contains(String word) {
        if (this.en.equals(word)) return true;
        if (this.mk.equals(word)) return true;
        if (this.ru.equals(word)) return true;
        return false;
    }
    public boolean contains(Word word) {
        if (this.en.equals(word.en)) return true;
        if (this.mk.equals(word.mk)) return true;
        if (this.ru.equals(word.ru)) return true;
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
}
