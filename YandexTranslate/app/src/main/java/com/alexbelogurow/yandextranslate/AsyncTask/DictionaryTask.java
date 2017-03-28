package com.alexbelogurow.yandextranslate.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by alexbelogurow on 28.03.17.
 */

public class DictionaryTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... urls) {
        String result = "";
        URL url;
        HttpURLConnection urlConnection;

        try {
            url = new URL(urls[0]);
            urlConnection = (HttpURLConnection) url.openConnection();

            InputStream in = urlConnection.getInputStream();
            InputStreamReader streamReader = new InputStreamReader(in);

            int data = streamReader.read();

            while (data != -1) {
                result += (char) data;
                data = streamReader.read();
            }

            //Log.i("JSON", result);
            return result;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        Log.i("Dictionary", result);

        /*try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray text = jsonObject.getJSONArray("text");
            String translated = text.getString(0);
            Log.i("Dictionary", translated);
            //delegate.processFinish(translated);

        } catch (JSONException e) {
            e.printStackTrace();
        } */
    }
}
