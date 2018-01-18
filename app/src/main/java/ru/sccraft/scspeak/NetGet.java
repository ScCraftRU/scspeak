package ru.sccraft.scspeak;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Создан пользователем alexandr 18.02.17 20:01, работающем в комманде ScCraft.
 */

public class NetGet {
    private static final String LOG_TAG = "NetGet";

    public static String getOneLine(String webAdress) {
        URL url;
        HttpURLConnection conn = null;
        BufferedReader rd;
        String line;
        String result = "";
        try {
            url = new URL(webAdress);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            Log.i(LOG_TAG, "Код ответа сервера (RESPONSE CODE) = " + responseCode);
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.disconnect();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static String[] getMultiLine(String webAdress) {
        URL url;
        HttpURLConnection conn = null;
        BufferedReader rd;
        String line;
        ArrayList<String> al = new ArrayList<>();
        String[] result;
        try {
            url = new URL(webAdress);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            Log.i(LOG_TAG, "Код ответа сервера (RESPONSE CODE) = " + responseCode);
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                if (!(line.equals(""))) {
                    al.add(line);
                }
            }
            rd.close();
            result = al.toArray(new String[al.size()]);
        } catch (Exception e) {
            e.printStackTrace();
            result = new String[1];
            result[0] = "Connection error";
        } finally {
            try {
                conn.disconnect();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static boolean getNetworkConnectionStatus(final Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        return false;
    }
}
