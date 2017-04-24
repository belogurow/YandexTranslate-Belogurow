package com.alexbelogurow.yandextranslate.asyncTask;

import android.os.AsyncTask;
import android.support.v4.util.ArrayMap;
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
 * Асинхронная задача для получения списка языков в виде JSON
 *
 * Решил не хранить список языков в strings.xml, так как список языков
 * может пополняться или изменяться в дальнейшем. Поэтому при каждом запуске
 * приложение будет показвать актуальные языки на которые и с которых
 * можно осуществлять перевод
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

    /**
     * Разбор JSON и их упаковка в виде (ключ, значение) в ArrayMap
     *
     * В качестве словаря используется ArrayMap, так как он является более
     * эффективным аналогом HashMap
     *
     * @param result JSON
     */
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject langs = jsonObject.getJSONObject("langs");
            Iterator<?> keys = langs.keys();

            ArrayMap<String, String> arrayOfLangs = new ArrayMap<>();

            while (keys.hasNext()) {
                String key = keys.next().toString();
                arrayOfLangs.put(key, langs.getString(key));
            }

            delegate.processLangsFinish(arrayOfLangs);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}