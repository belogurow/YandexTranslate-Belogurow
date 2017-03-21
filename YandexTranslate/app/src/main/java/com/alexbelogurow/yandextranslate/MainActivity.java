package com.alexbelogurow.yandextranslate;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URLEncoder;

// url = https://translate.yandex.net/api/v1.5/tr.json/translate?key=trnsl.1.1.20170314T185242Z.999b1fe140aa0411.51001ae9e89efdf73f40ad571ba71b214cc62f02&text=<переводимый текст>&lang=en-ru
public class MainActivity extends AppCompatActivity implements DownloadTask.DownloadResponse {

    private static final String KEY_TRANSLATE = "trnsl.1.1.20170314T185242Z.999b1fe140aa0411.51001ae9e89efdf73f40ad571ba71b214cc62f02";

    private EditText mEditTextInput;
    private TextView mTextViewTranslate;
    private ProgressBar mProgressBar;
    private Button mButtonDeleteInputText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mEditTextInput = (EditText) findViewById(R.id.editTextInput);
        mTextViewTranslate = (TextView) findViewById(R.id.textViewTranslate);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mButtonDeleteInputText = (Button) findViewById(R.id.buttonDeleteInputText);

        // addTextChangedListener отслеживает изменение текста в mEditTextView,
        // и если он произошел, отправляет запрос на получение json с переводом
        mEditTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(final Editable s) {
                mProgressBar.setVisibility(ProgressBar.VISIBLE);
                mButtonDeleteInputText.setVisibility(Button.VISIBLE);


                new DownloadTask(new DownloadTask.DownloadResponse() {
                    @Override
                    public void processFinish(String output) {
                        mTextViewTranslate.setText(output);
                        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                    }
                }).execute("https://translate.yandex.net/api/v1.5/tr.json/translate?" +
                        "key=" + KEY_TRANSLATE +
                        "&text=" + s.toString() +
                        "&lang=en-ru");
                // Timer для того, чтобы запросы не посылались при каждом разе,
                // когда пользователь добавлял или удалял букву,
                // а посылались с некоторой задержкой(500 миллисекунд)
                /*new CountDownTimer(500, 1000) {

                    public void onTick(long millisUntilFinished) { }

                    public void onFinish() {
                        new DownloadTask(new DownloadTask.DownloadResponse() {
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
