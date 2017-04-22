package com.alexbelogurow.yandextranslate.tabs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alexbelogurow.yandextranslate.R;
import com.alexbelogurow.yandextranslate.activity.GetLanguageActivity;
import com.alexbelogurow.yandextranslate.asyncTask.DictionaryTask;
import com.alexbelogurow.yandextranslate.asyncTask.LanguageTask;
import com.alexbelogurow.yandextranslate.asyncTask.TranslateTask;
import com.alexbelogurow.yandextranslate.dataBase.DBHandler;
import com.alexbelogurow.yandextranslate.model.Dictionary;
import com.alexbelogurow.yandextranslate.model.Translation;
import com.alexbelogurow.yandextranslate.utils.Constant;

import java.net.URLEncoder;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.alexbelogurow.yandextranslate.utils.Constant.KEY_TRANSLATE;


/**
 * Created by alexbelogurow on 17.04.17.
 */
// FIXME не работает крестик удаления

public class TranslationTab extends Fragment {

    private final int REQUEST_CODE_LANG_FROM = 1,
            REQUEST_CODE_LANG_TO = 2;

    private EditText mEditTextInput;
    private TextView mTextViewTranslate;
    private TextView mTextViewDictionary;
    private Button mButtonDeleteInputText;
    private ImageButton mImageButtonTrFavourite;

    private TextView mTextViewLangFrom;
    private TextView mTextViewLangTo;

    private ConstraintLayout mRootLayout;

    public static String langFrom = "ru";
    public static String langTo = "en";

    public static ArrayMap<String, String> languages = null;
    public static Dictionary dictOfTranslate = null;

    public static DBHandler dbHandler;

    private Translation currentTranslation;

    public TranslationTab() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_translation, container, false);

        mEditTextInput = (EditText) view.findViewById(R.id.editTextInput);
        mTextViewTranslate = (TextView) view.findViewById(R.id.textViewTranslate);
        mButtonDeleteInputText = (Button) view.findViewById(R.id.buttonDeleteInputText);
        mTextViewDictionary = (TextView) view.findViewById(R.id.textViewDictionary);
        mTextViewLangFrom = (TextView) view.findViewById(R.id.textViewLangFrom);
        mTextViewLangTo = (TextView) view.findViewById(R.id.textViewLangTo);
        mImageButtonTrFavourite = (ImageButton) view.findViewById(R.id.imageButtonTrFavourite);
        mRootLayout = (ConstraintLayout) view.findViewById(R.id.layoutTranslationTab);



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
                Log.i("langs", languages.toString());
            }
        }).execute("https://translate.yandex.net/api/v1.5/tr.json/getLangs?" +
                "key=" + Constant.KEY_TRANSLATE +
                "&ui=ru");



        dbHandler = new DBHandler(getContext());
        currentTranslation = new Translation("", "", langFrom, langTo);

        Log.d(Log.DEBUG + "", "onCreate");
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(Log.DEBUG + "", "onActivityCreated");
        Log.i("langs", languages.toString());


        mTextViewLangFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getLanguage = new Intent(getActivity(), GetLanguageActivity.class);
                startActivity(getLanguage);
                //startActivityForResult(getLanguage, REQUEST_CODE_LANG_FROM);
            }
        });

        mTextViewLangTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getLanguage = new Intent(getActivity(), GetLanguageActivity.class);
                //startActivity(getLanguage);
                TranslationTab.this.startActivityForResult(getLanguage, REQUEST_CODE_LANG_TO);
            }
        });


        mTextViewDictionary.setMovementMethod(new ScrollingMovementMethod());

        // addTextChangedListener отслеживает изменение текста в mEditTextView,
        // и если он произошел, отправляет запрос на получение json с переводом
        mEditTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                mButtonDeleteInputText.setVisibility(Button.VISIBLE);

                mImageButtonTrFavourite.setImageResource(R.drawable.ic_fav_off);
                currentTranslation.setFavourite(0);

                // метод getTranslate для получения перевода и словаря
                if (s.toString().length() != 0) {
                    getTranslate(s.toString());


                }


            }
        });


        mEditTextInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //if (actionId == EditorInfo.IME_ACTION_DONE) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mEditTextInput.getWindowToken(), 0);
                Log.d(Log.DEBUG + "", event + "");
                return true;
                //}
                //return false;

            }
        });


        // кнопка добавления в избранное
        mImageButtonTrFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentTranslation.getText().length() != 0 && TranslationTab.this.isVisible()) {
                    if (currentTranslation.isFavourite()) {
                        currentTranslation.setFavourite(0);
                        mImageButtonTrFavourite.setImageResource(R.drawable.ic_fav_off);

                        dbHandler.addTranslation(currentTranslation);
                    } else {
                        currentTranslation.setFavourite(1);
                        mImageButtonTrFavourite.setImageResource(R.drawable.ic_fav_on);

                        Toast.makeText(getContext(), "Added in favourite", Toast.LENGTH_SHORT).show();
                        dbHandler.addTranslation(currentTranslation);
                    }
                }
            }
        });


        mRootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                mRootLayout.getWindowVisibleDisplayFrame(r);
                int screenHeight = mRootLayout.getRootView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;


                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    Log.d("status", "not hidden");
                }
                else {
                    if (currentTranslation.getText().length() != 0 && TranslationTab.this.isVisible()) {
                        if (dbHandler.getTranslationsCount() > 0) {
                            Translation lastTranslationFromDB = dbHandler.getLastTranslation();
                            if (!Objects.equals(currentTranslation.getText(), lastTranslationFromDB.getText()) &&
                                    !Objects.equals(currentTranslation.getTranslatedText(), lastTranslationFromDB.getTranslatedText())) {
                                dbHandler.addTranslation(currentTranslation);
                            }
                        } else {
                            dbHandler.addTranslation(currentTranslation);
                        }
                    }
                }
                    /*
                    if (mEditTextInput.getText().length() != 0 && TranslationTab.this.isVisible()) {
                        if (dbHandler.getTranslationsCount() > 0) {
                            Translation lastTranslation = dbHandler.getLastTranslation();
                            if (!Objects.equals(lastTranslation.getText(), mEditTextInput.getText().toString()) &&
                                    !Objects.equals(lastTranslation.getTranslatedText(), mTextViewTranslate.getText().toString())) {

                                Translation translation = new Translation(
                                        mEditTextInput.getText().toString(),
                                        mTextViewTranslate.getText().toString(),
                                        langFrom,
                                        langTo, 0);
                                dbHandler.addTranslation(translation);
                            }
                        } else {
                            Translation translation = new Translation(
                                    mEditTextInput.getText().toString(),
                                    mTextViewTranslate.getText().toString(),
                                    langFrom,
                                    langTo, 0);
                            dbHandler.addTranslation(translation);
                        }

                    }
                    //mEditTextInput.setFocusable(false);
                }

                //Log.d("count", dbHandler.getTranslationsCount() + ""); */
            }
        });




    }

    // очистить поле ввода после нажатия на крестик "x"
    public void deleteInputText(View view) {
        mEditTextInput.setText("");
        view.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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

        if (mEditTextInput.getText().length() != 0) {
            getTranslate(mEditTextInput.getText().toString());
        }
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
    } */


    private void getTranslate(String s) {
        try {
            String text = URLEncoder.encode(s, "UTF-8");

            new TranslateTask(new TranslateTask.DownloadResponse() {
                @Override
                public void processTranslateFinish(String output) {

                    mTextViewTranslate.setText(output);

                    currentTranslation.setText(mEditTextInput.getText().toString());
                    currentTranslation.setTranslatedText(mTextViewTranslate.getText().toString());
                    currentTranslation.setFrom(langFrom);
                    currentTranslation.setTo(langTo);

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
                    "key=" + Constant.KEY_DICTIONARY +
                    "&text=" + text +
                    "&lang=" + langFrom + "-" + langTo +
                    "&ui=ru");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
