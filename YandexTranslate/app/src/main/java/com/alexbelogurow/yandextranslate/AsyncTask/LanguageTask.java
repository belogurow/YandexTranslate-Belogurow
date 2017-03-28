package com.alexbelogurow.yandextranslate.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

/**
 * Created by alexbelogurow on 28.03.17.
 */

public class LanguageTask extends AsyncTask<String, Void, String> {

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

        try {
            JSONObject langs = new JSONObject(result);
            JSONObject langs2 = langs.getJSONObject("langs");
            Iterator<?> keys = langs2.keys();
            //Log.i("Array", jsonArray.toString());
            while (keys.hasNext()) {
                String key = keys.next().toString();
                Log.i("o", key + " : " + langs2.getString(key));
            }
            Log.i("Language", langs.getString("langs"));



        } catch (JSONException e) {
            e.printStackTrace();
        }


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