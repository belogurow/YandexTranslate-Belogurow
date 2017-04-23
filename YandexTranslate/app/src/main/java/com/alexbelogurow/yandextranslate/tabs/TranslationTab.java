package com.alexbelogurow.yandextranslate.tabs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
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

    public static boolean isAutoLang = false;
    private String langFrom;
    private String langTo;

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

        loadState();

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        languages = new ArrayMap<>();
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
        currentTranslation = new Translation();

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mTextViewLangFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getLanguage = new Intent(getActivity(), GetLanguageActivity.class);
                //startActivity(getLanguage);
                //startActivityForResult(getLanguage, REQUEST_CODE_LANG_FROM);
                //getActivity().startActivityForResult(getLanguage, REQUEST_CODE_LANG_FROM);


                TranslationTab.this.startActivityForResult(getLanguage, REQUEST_CODE_LANG_FROM);
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
                mImageButtonTrFavourite.setImageResource(R.drawable.ic_fav_off);
                currentTranslation.setFavourite(0);

                // метод getTranslate для получения перевода и словаря
                if (s.toString().length() != 0) {
                    getTranslate(s.toString());
                    mButtonDeleteInputText.setVisibility(Button.VISIBLE);
                }
                else {
                    mTextViewTranslate.setText("");
                    mButtonDeleteInputText.setVisibility(Button.INVISIBLE);
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
                if (keyboardIsHidden()) {
                    addTranslationToDB();
                }
            }
        });




    }

    private boolean keyboardIsHidden() {
        Rect r = new Rect();
        mRootLayout.getWindowVisibleDisplayFrame(r);
        int screenHeight = mRootLayout.getRootView().getHeight();

        // r.bottom is the position above soft keypad or device button.
        // if keypad is shown, the r.bottom is smaller than that before.
        int keypadHeight = screenHeight - r.bottom;

        return keypadHeight <= screenHeight * 0.15;
    }

    // очистить поле ввода после нажатия на крестик "x"
    public void deleteInputText(View view) {
        mEditTextInput.setText("");
        view.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        Log.d(Log.DEBUG + "code", requestCode + "");

        if (resultCode == RESULT_OK) {
            int number = data.getIntExtra("numberOfKey", -1);
            isAutoLang = data.getBooleanExtra("isAutoLang", false);

            switch (requestCode) {
                case REQUEST_CODE_LANG_FROM:
                    Log.d(Log.DEBUG + "aresult", number + ", " + isAutoLang);
                    if (!isAutoLang) {
                        mTextViewLangFrom.setText(languages.valueAt(number));
                        langFrom = languages.keyAt(number);
                    } else {
                        if (number != -1) {
                            mTextViewLangFrom.setText(languages.valueAt(number));
                            langFrom = languages.keyAt(number);
                        }
                    }
                case REQUEST_CODE_LANG_TO:
                    if (number != -1) {
                        mTextViewLangTo.setText(languages.valueAt(number));
                        langTo = languages.keyAt(number);
                    }
            }
            if (mEditTextInput.getText().length() != 0) {
                getTranslate(mEditTextInput.getText().toString());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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

            String urlLangs;
            if (isAutoLang) {
                urlLangs = langTo;
            } else {
                urlLangs = langFrom + "-" + langTo;
            }
            new TranslateTask(new TranslateTask.DownloadResponse() {
                @Override
                public void processTranslateFinish(String output) {

                    mTextViewTranslate.setText(output);

                    currentTranslation.setText(mEditTextInput.getText().toString());
                    currentTranslation.setTranslatedText(mTextViewTranslate.getText().toString());
                    currentTranslation.setFrom(langFrom);
                    currentTranslation.setTo(langTo);

                    //
                    if (keyboardIsHidden()) {
                        addTranslationToDB();
                    }

                }
            }).execute("https://translate.yandex.net/api/v1.5/tr.json/translate?" +
                    "key=" + KEY_TRANSLATE +
                    "&text=" + text +
                    "&lang=" + urlLangs);


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
                    "&lang=" + urlLangs +
                    "&ui=ru");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addTranslationToDB() {
        Log.d(Log.DEBUG + "-current", currentTranslation.toString());
        if (currentTranslation.getText().length() != 0 && TranslationTab.this.isVisible()) {
            if (dbHandler.getTranslationsCount() > 0) {
                Translation lastTranslationFromDB = dbHandler.getLastTranslation();
                Log.d(Log.DEBUG + "-last", lastTranslationFromDB.toString());
                if (!Objects.equals(currentTranslation.getText(), lastTranslationFromDB.getText()) ||
                        !Objects.equals(currentTranslation.getFrom(), lastTranslationFromDB.getFrom()) ||
                        !Objects.equals(currentTranslation.getTo(), lastTranslationFromDB.getTo())) {
                    dbHandler.addTranslation(currentTranslation);
                }
            } else {
                dbHandler.addTranslation(currentTranslation);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        saveState();
    }

    private void saveState() {
        SharedPreferences statePref = this.getActivity().
                getSharedPreferences("lastTranslation", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = statePref.edit();
        editor.clear();
        editor.putString("text", currentTranslation.getText());
        editor.putString("trText", currentTranslation.getTranslatedText());
        editor.putString("langFrom", langFrom);
        editor.putString("langTo", langTo);

        //editor.put

        editor.apply();
    }

    private void loadState() {
        SharedPreferences statePref = this.getActivity().
                getSharedPreferences("lastTranslation", Context.MODE_PRIVATE);

        currentTranslation.setText(statePref.getString("text", ""));
        currentTranslation.setTranslatedText(statePref.getString("trText", ""));
        currentTranslation.setFrom(statePref.getString("langFrom", "ru"));
        currentTranslation.setTo(statePref.getString("langTo", "en"));

        mEditTextInput.setText(currentTranslation.getText());
        mTextViewTranslate.setText(currentTranslation.getTranslatedText());
        /*mTextViewLangFrom.setText(languages.valueAt(
                languages.indexOfKey(currentTranslation.getFrom())));
        mTextViewLangTo.setText(languages.valueAt(
                languages.indexOfKey(currentTranslation.getTo())));
        */
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("currentTranslation", currentTranslation);
        Log.i("statePref", "save");
    }
}
