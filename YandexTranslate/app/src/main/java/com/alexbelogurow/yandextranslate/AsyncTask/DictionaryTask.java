package com.alexbelogurow.yandextranslate.asyncTask;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Асинхронная задача для получения словаря в виде JSON
 *
 * Почему использую AsyncTask, а не Thread?
 * В данном случае приложению не требуется загружать большие данные из интернета,
 * асинхронная задача представляет собой ресурсоемкий запрос и по времени выигрывает
 * у тредов.
 *
 * Данное суждение справедливо для свех AsyncTask, используемых в этом в
 * приложении. :)
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

    /**
     * В данном методе приосходит разбор JSON, полученного от словаря.
     * @param result JSON
     */

    //Код получился довольно сложным, поэтому далее все расписано подробно
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        try {
            JSONObject jsonObject = new JSONObject(result);

            Log.i("JSON-dict", jsonObject.toString());
            String output = "";

            // Если получен пустой JSON, то ответ в виде пустой строки
            if (jsonObject.getJSONArray("def").length() == 0) {
                delegate.processDictionaryFinish(output);
            }
            else {
                JSONObject dictInfo = new JSONObject(jsonObject.getJSONArray("def").getString(0));

                output = dictInfo.getString("text");

                // Если в словаре имеется информацияо о части речи переводимого слова,
                // то добавляем его в общий ответ
                if (dictInfo.has("pos")) {
                    output += " " + dictInfo.getString("pos");
                }

                // Аналогично иформация о роде
                if (dictInfo.has("gen")) {
                    output += " " + dictInfo.getString("gen");
                }
                output += "\n";

                // Разбор массивов переводов
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
                            //Log.i("array" + i, jsonObjectI.toString());
                            output += ", " + jsonObjectI.getString("text");
                        }
                    }
                    output += "\n";

                    // Разбор массива значений
                    if (item.has("mean")) {
                        output += "\t(";
                        JSONArray arrayOfDefinition = item.getJSONArray("mean");
                        for (int i = 0; i < arrayOfDefinition.length(); i++) {
                            JSONObject jsonObjectI = arrayOfDefinition.getJSONObject(i);
                            //Log.i("definition" + i, jsonObjectI.toString());
                            if (i != 0) {
                                output += ", ";
                            }
                            output += jsonObjectI.getString("text");
                        }
                        output +=")\n";
                    }

                    // Разбор массивов примеров
                    if (item.has("ex")) {
                        JSONArray arrayOfExamples = item.getJSONArray("ex");
                        for (int i = 0; i < arrayOfExamples.length(); i++) {
                            JSONObject jsonObjectI = arrayOfExamples.getJSONObject(i);
                            //Log.i("example" + i, jsonObjectI.toString());
                            output += "\t\t" + jsonObjectI.getString("text");

                            // внутри надо вытащить перевод для примера
                            JSONArray transl = jsonObjectI.getJSONArray("tr");
                            output += " - " + transl.getJSONObject(0).getString("text") + "\n";
                        }
                        //output += "\n";
                    }

                }
                // В делегат отправляем получившийся ответ
                delegate.processDictionaryFinish(output);

            }

        } catch (JSONException e) {
            e.printStackTrace();
            delegate.processDictionaryFinish("");
        }
    }
}
