package ru.sccraft.scspeak;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by alexandr on 18.02.17.
 */

public class NetGet {
    public static String getOneLine(String webAdress) {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        String result = "";
        try {
            url = new URL(webAdress);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String[] getMultiLine(String webAdress) {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        ArrayList<String> al = new ArrayList<>();
        String[] result;
        try {
            url = new URL(webAdress);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                al.add(line);
            }
            rd.close();
            result = al.toArray(new String[al.size()]);
        } catch (Exception e) {
            e.printStackTrace();
            result = new String[1];
            result[0] = "Connection error";
        }
        return result;
    }
}