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

        // TODO добавить исключение: {"head":{},"def":[]} - добавлено
        // TODO можно проверять на наличие словаря в для данных языков в api
        // TODO можно проверять по возвращаемому коду 501
        try {
            JSONObject jsonObject = new JSONObject(result);

            String output = "";

            if (jsonObject.getJSONArray("def").length() == 0) {
                delegate.processDictionaryFinish(output);
            }
            else {
                JSONObject dictInfo = new JSONObject(jsonObject.getJSONArray("def").getString(0));

                output = dictInfo.getString("text");
                if (dictInfo.has("pos")) {
                    output += " " + dictInfo.getString("pos");
                }

                if (dictInfo.has("gen")) {
                    output += " " + dictInfo.getString("gen");
                }
                output += "\n";

                // массив переводов
                JSONArray arrayOfTransl = dictInfo.getJSONArray("tr");
                for (int k = 0; k < arrayOfTransl.length(); k++) {
                    output += k + 1;

                    JSONObject item = new JSONObject(arrayOfTransl.getString(k));

                    // массив из синонимов
                    output += " " + item.getString("text");
                    if (item.has("syn")) {
                        JSONArray arrayOfExamples = item.getJSONArray("syn");
                        for (int i = 0; i < arrayOfExamples.length(); i++) {
                            JSONObject jsonObjectI = arrayOfExamples.getJSONObject(i);
                            Log.i("array" + i, jsonObjectI.toString());
                            output += ", " + jsonObjectI.getString("text");
                        }
                    }
                    output += "\n";

                    // массив значений
                    if (item.has("mean")) {
                        output += "  (";
                        JSONArray arrayOfDefinition = item.getJSONArray("mean");
                        for (int i = 0; i < arrayOfDefinition.length(); i++) {
                            JSONObject jsonObjectI = arrayOfDefinition.getJSONObject(i);
                            Log.i("definition" + i, jsonObjectI.toString());
                            output += " " + jsonObjectI.getString("text");
                        }
                        output += ")\n";
                    }

                    // массив примеров
                    if (item.has("ex")) {
                        JSONArray arrayOfExamples = item.getJSONArray("ex");
                        for (int i = 0; i < arrayOfExamples.length(); i++) {
                            JSONObject jsonObjectI = arrayOfExamples.getJSONObject(i);
                            Log.i("example" + i, jsonObjectI.toString());
                            output += "\t\t" + jsonObjectI.getString("text");

                            // внутри надо вытащить перевод для примера
                            JSONArray transl = jsonObjectI.getJSONArray("tr");
                            output += " - " + transl.getJSONObject(0).getString("text") + "\n";
                        }
                        //output += "\n";
                    }

                }
                Log.i("Output", output);
                //Log.i("examples", examples.getString(0) + "\n" + examples.getString(1));
                //Log.i("item", firstItem.getString("syn"));
                //      text.getString("text") + " " + text.getString("pos") + " " + text.getString("gen"));
                //delegate.processFinish(translated);
                delegate.processDictionaryFinish(output);

            /*
            MainActivity.dictOfTranslate = new Dictionary(text.getString("text"),
                    text.getString("pos"),
                    text.getString("gen"));

            */
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

/*
JSONObject firstItem = new JSONObject(examples.getString(0));
Log.i("item", firstItem.getString("syn"));
[{"text":"machinery","pos":"существительное"},{"text":"apparatus","pos":"существительное"}]
 */
