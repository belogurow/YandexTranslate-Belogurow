package com.alexbelogurow.yandextranslate.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alexbelogurow.yandextranslate.R;
import com.alexbelogurow.yandextranslate.activity.GetLanguageActivity;
import com.alexbelogurow.yandextranslate.activity.MainActivity;
import com.alexbelogurow.yandextranslate.asyncTask.DictionaryTask;
import com.alexbelogurow.yandextranslate.asyncTask.LanguageTask;
import com.alexbelogurow.yandextranslate.asyncTask.TranslateTask;
import com.alexbelogurow.yandextranslate.helper.Dictionary;

import java.net.URLEncoder;


/**
 * Created by alexbelogurow on 17.04.17.
 */

public class TranslationTab extends Fragment {

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

    public TranslationTab() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_main, container, false);

        mEditTextInput = (EditText) view.findViewById(R.id.editTextInput);
        mTextViewTranslate = (TextView) view.findViewById(R.id.textViewTranslate);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mButtonDeleteInputText = (Button) view.findViewById(R.id.buttonDeleteInputText);
        mTextViewDictionary = (TextView) view.findViewById(R.id.textViewDictionary);
        mTextViewLangFrom = (TextView) view.findViewById(R.id.textViewLangFrom);
        mTextViewLangTo = (TextView) view.findViewById(R.id.textViewLangTo);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        languages = new ArrayMap<>();
        // FIXME вылетает при нажатии кнопки назад
        new LanguageTask(new LanguageTask.DownloadResponse() {
            @Override
            public void processLangsFinish(ArrayMap<String, String> output) {
                languages = output;
            }
        }).execute("https://translate.yandex.net/api/v1.5/tr.json/getLangs?" +
                "key=" + KEY_TRANSLATE +
                "&ui=ru");
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.i("langs", languages.toString());
        /*
        mTextViewLangFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getLanguage = new Intent(getActivity(), GetLanguageActivity.class);
                startActivityForResult(getLanguage, REQUEST_CODE_LANG_FROM);
            }
        });


        mTextViewLangTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getLanguage = new Intent(getActivity(), GetLanguageActivity.class);
                startActivityForResult(getLanguage, REQUEST_CODE_LANG_TO);
            }
        });
        */

        mTextViewDictionary.setMovementMethod(new ScrollingMovementMethod());
        String s = "";
        for (int i = 0; i < 200; i++) {
            s += "textviewTEST ";
        }

        mTextViewDictionary.setText(s);



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


            }
        });

    }

    // очистить поле ввода после нажатия на крестик "x"
    public void deleteInputText(View view) {
        mEditTextInput.setText("");
        view.setVisibility(View.INVISIBLE);
    }

    /*
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
    */

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
