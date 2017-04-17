package com.alexbelogurow.yandextranslate.activity;

import android.content.Intent;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alexbelogurow.yandextranslate.asyncTask.DictionaryTask;
import com.alexbelogurow.yandextranslate.asyncTask.LanguageTask;
import com.alexbelogurow.yandextranslate.helper.Dictionary;
import com.alexbelogurow.yandextranslate.R;
import com.alexbelogurow.yandextranslate.asyncTask.TranslateTask;

import java.net.URLEncoder;

// url = https://translate.yandex.net/api/v1.5/tr.json/translate?key=trnsl.1.1.20170314T185242Z.999b1fe140aa0411.51001ae9e89efdf73f40ad571ba71b214cc62f02&text=<переводимый текст>&lang=en-ru
public class MainActivity extends AppCompatActivity {

    private static final String
            KEY_TRANSLATE = "trnsl.1.1.20170314T185242Z.999b1fe140aa0411.51001ae9e89efdf73f40ad571ba71b214cc62f02",
            KEY_DICTIONARY = "dict.1.1.20170314T202833Z.600119660f570864.0875e9d92a265c752e3c59235b85f83e29a01a1e";

    private final int REQUEST_CODE_LANG_FROM = 1,
            REQUEST_CODE_LANG_TO = 2;

    private EditText mEditTextInput;
    private TextView mTextViewTranslate;
    private TextView mTextViewDictionary;
    private ProgressBar mProgressBar;
    private Button mButtonDeleteInputText;

    private TextView mTextViewLangFrom;
    private TextView mTextViewLangTo;

    private String langFrom = "ru";
    private String langTo = "en";

    public static ArrayMap<String, String> languages = null;
    public static Dictionary dictOfTranslate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_translation);



        mEditTextInput = (EditText) findViewById(R.id.editTextInput);
        mTextViewTranslate = (TextView) findViewById(R.id.textViewTranslate);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mButtonDeleteInputText = (Button) findViewById(R.id.buttonDeleteInputText);
        mTextViewDictionary = (TextView) findViewById(R.id.textViewDictionary);

        mTextViewLangFrom = (TextView) findViewById(R.id.textViewLangFrom);
        mTextViewLangTo = (TextView) findViewById(R.id.textViewLangTo);

        languages = new ArrayMap<>();

        mTextViewLangFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getLanguage = new Intent(MainActivity.this, GetLanguageActivity.class);
                startActivityForResult(getLanguage, REQUEST_CODE_LANG_FROM);
            }
        });


        mTextViewLangTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getLanguage = new Intent(MainActivity.this, GetLanguageActivity.class);
                startActivityForResult(getLanguage, REQUEST_CODE_LANG_TO);
            }
        });

        mTextViewDictionary.setMovementMethod(new ScrollingMovementMethod());
        String s = "";
        for (int i = 0; i < 200; i++) {
            s += "textviewTEST ";
        }

        mTextViewDictionary.setText(s);
        // FIXME вылетает при нажатии кнопки назад
        new LanguageTask(new LanguageTask.DownloadResponse() {
            @Override
            public void processLangsFinish(ArrayMap<String, String> output) {
                languages = output;
            }
        }).execute("https://translate.yandex.net/api/v1.5/tr.json/getLangs?" +
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

                // метод getTranslate для получения перевода и словаря
                getTranslate(s.toString());

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

    // очистить поле ввода после нажатия на крестик "x"
    public void deleteInputText(View view) {
        mEditTextInput.setText("");
        view.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        int number = data.getIntExtra("numberOfKey", -1);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_LANG_FROM:
                    mTextViewLangFrom.setText(languages.valueAt(number));
                    langFrom = languages.keyAt(number);
                case REQUEST_CODE_LANG_TO:
                    mTextViewLangTo.setText(languages.valueAt(number));
                    langTo = languages.keyAt(number);
            }
        }

        mEditTextInput.setText(mEditTextInput.getText());
    }

    private void getTranslate(String s) {
        try {
            String text = URLEncoder.encode(s, "UTF-8");

            new TranslateTask(new TranslateTask.DownloadResponse() {
                @Override
                public void processTranslateFinish(String output) {

                    mTextViewTranslate.setText(output);
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);

                }
            }).execute("https://translate.yandex.net/api/v1.5/tr.json/translate?" +
                    "key=" + KEY_TRANSLATE +
                    "&text=" + text +
                    "&lang=" + langFrom + "-" + langTo);


            // TODO добавить проверку на наличие языка
            new DictionaryTask(new DictionaryTask.DownloadResponse() {
                @Override
                public void processDictionaryFinish(String output) {

                    mTextViewDictionary.setText(output);
                    //Log.i("dictOfTranslate", dictOfTranslate.toString());

                }
            }).execute("https://dictionary.yandex.net/api/v1/dicservice.json/lookup?" +
                            "key=" + KEY_DICTIONARY +
                            "&text=" + text +
                            "&lang=" + langFrom + "-" + langTo +
                            "&ui=ru");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
