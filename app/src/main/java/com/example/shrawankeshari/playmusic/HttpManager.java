package com.example.shrawankeshari.playmusic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by shrawankeshari on 16/12/17.
 * Creating HttpUrlConnection for retreiving the data from the web
 */

public class HttpManager {

    public static String getData(String uri) {

        BufferedReader bf = null;

        try {
            URL url = new URL(uri);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            StringBuilder sb = new StringBuilder();
            bf = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String data = null;

            while ((data = bf.readLine()) != null) {
                sb.append(data).append("\n");
            }

            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (bf != null) {
                try {
                    bf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }
}
