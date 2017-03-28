package com.alexbelogurow.yandextranslate.Activity;

import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alexbelogurow.yandextranslate.AsyncTask.DictionaryTask;
import com.alexbelogurow.yandextranslate.AsyncTask.LanguageTask;
import com.alexbelogurow.yandextranslate.R;
import com.alexbelogurow.yandextranslate.AsyncTask.TranslateTask;

import java.net.URLEncoder;

// url = https://translate.yandex.net/api/v1.5/tr.json/translate?key=trnsl.1.1.20170314T185242Z.999b1fe140aa0411.51001ae9e89efdf73f40ad571ba71b214cc62f02&text=<переводимый текст>&lang=en-ru
public class MainActivity extends AppCompatActivity implements TranslateTask.DownloadResponse {

    private static final String
            KEY_TRANSLATE = "trnsl.1.1.20170314T185242Z.999b1fe140aa0411.51001ae9e89efdf73f40ad571ba71b214cc62f02",
            KEY_DICTIONARY = "dict.1.1.20170314T202833Z.600119660f570864.0875e9d92a265c752e3c59235b85f83e29a01a1e";

    private EditText mEditTextInput;
    private TextView mTextViewTranslate;
    private ProgressBar mProgressBar;
    private Button mButtonDeleteInputText;

    private TextView mTextViewLangFrom;
    private TextView mTextViewLangTo;

    public static ArrayMap<String, String> languages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mEditTextInput = (EditText) findViewById(R.id.editTextInput);
        mTextViewTranslate = (TextView) findViewById(R.id.textViewTranslate);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mButtonDeleteInputText = (Button) findViewById(R.id.buttonDeleteInputText);

        mTextViewLangFrom = (TextView) findViewById(R.id.textViewLangFrom);
        mTextViewLangTo = (TextView) findViewById(R.id.textViewLangTo);

        languages = new ArrayMap<>();
        /*languages.put("en", "Английский");
        languages.put("ru", "Русский");
        Log.i("HashMap", languages.toString());
        Log.i("HashMap", languages.keyAt(1) + " : " + languages.valueAt(1)); */

        new LanguageTask()
                .execute("https://translate.yandex.net/api/v1.5/tr.json/getLangs?" +
                        "key=" + KEY_TRANSLATE +
                        "&ui=ru");

        // addTextChangedListener отслеживает изменение текста в mEditTextView,
        // и если он произошел, отправляет запрос на получение json с переводом
        mEditTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                mProgressBar.setVisibility(ProgressBar.VISIBLE);
                mButtonDeleteInputText.setVisibility(Button.VISIBLE);

                try {
                    String text = URLEncoder.encode(s.toString(), "UTF-8");

                    new TranslateTask(new TranslateTask.DownloadResponse() {
                        @Override
                        public void processFinish(String output) {
                            mTextViewTranslate.setText(output);
                            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                        }
                    }).execute("https://translate.yandex.net/api/v1.5/tr.json/translate?" +
                            "key=" + KEY_TRANSLATE +
                            "&text=" + text +
                            "&lang=en-ru");


                    new DictionaryTask()
                            .execute("https://dictionary.yandex.net/api/v1/dicservice.json/lookup?" +
                                    "key=" + KEY_DICTIONARY +
                                    "&lang=en-ru" +
                                    "&text=" + text +
                                    "&ui=ru");


                } catch (Exception e) {
                    e.printStackTrace();
                }
                // Timer для того, чтобы запросы не посылались при каждом разе,
                // когда пользователь добавлял или удалял букву,
                // а посылались с некоторой задержкой(500 миллисекунд)
                /*new CountDownTimer(500, 1000) {

                    public void onTick(long millisUntilFinished) { }

                    public void onFinish() {
                        new TranslateTask(new TranslateTask.DownloadResponse() {
                            @Override
                            public void processFinish(String output) {
                                mTextViewTranslate.setText(output);
                                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                            }
                        }).execute("https://translate.yandex.net/api/v1.5/tr.json/translate?" +
                                "key=" + KEY_TRANSLATE +
                                "&text=" + s.toString() +
                                "&lang=en-ru");
                    }
                }.start(); */
            }
        });

    }

    @Override
    public void processFinish(String output) {

    }

    public void deleteInputText(View view) {
        mEditTextInput.setText("");
        view.setVisibility(View.INVISIBLE);
    }
}