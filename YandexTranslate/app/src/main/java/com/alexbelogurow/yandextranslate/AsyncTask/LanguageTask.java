package com.alexbelogurow.yandextranslate.asyncTask;

import android.os.AsyncTask;
import android.support.v4.util.ArrayMap;

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
    public interface DownloadResponse {
        void processLangsFinish(ArrayMap<String, String> output);
    }

    public LanguageTask.DownloadResponse delegate = null;

    public LanguageTask(LanguageTask.DownloadResponse delegate){
        this.delegate = delegate;
    }



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
            JSONObject jsonObject = new JSONObject(result);
            JSONObject langs = jsonObject.getJSONObject("langs");
            Iterator<?> keys = langs.keys();
            ArrayMap<String, String> arrayOfLangs = new ArrayMap<>();
            //Log.i("Array", jsonArray.toString());
            while (keys.hasNext()) {
                String key = keys.next().toString();
                //Log.i("o", key + " : " + langs.getString(key));
                arrayOfLangs.put(key, langs.getString(key));
                //TranslationTab.languages.put(key, langs.getString(key));
                //MainActivity.languages.put(key, langs.getString(key));
            }
            //Log.i("Language", langs.getString("langs"));


            //Log.i("HashMap2", MainActivity.languages.toString());
            delegate.processLangsFinish(arrayOfLangs);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}