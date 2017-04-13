package com.alexbelogurow.yandextranslate.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.alexbelogurow.yandextranslate.Activity.MainActivity;
import com.alexbelogurow.yandextranslate.Helper.Dictionary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by alexbelogurow on 28.03.17.
 */

public class DictionaryTask extends AsyncTask<String, Void, String> {
    public interface DownloadResponse {
        void processDictionaryFinish(String output);
    }

    public DictionaryTask.DownloadResponse delegate = null;

    public DictionaryTask(DictionaryTask.DownloadResponse delegate) {
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

        Log.i("Dictionary", result);

        // добавить исключение: {"head":{},"def":[]}
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray def = jsonObject.getJSONArray("def");


            JSONObject text = new JSONObject(def.getString(0));

            Log.i("Definition", text.getString("text") + " " + text.getString("pos") + " " + text.getString("gen"));
            //delegate.processFinish(translated);
            delegate.processDictionaryFinish(text.getString("text") + " " + text.getString("pos") + " " + text.getString("gen"));
            MainActivity.dictOfTranslate = new Dictionary(text.getString("text"),
                    text.getString("pos"),
                    text.getString("gen"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
