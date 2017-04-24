package com.alexbelogurow.yandextranslate.tabs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.alexbelogurow.yandextranslate.model.Translation;
import com.alexbelogurow.yandextranslate.utils.Constant;

import java.net.URLEncoder;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.alexbelogurow.yandextranslate.utils.Constant.KEY_TRANSLATE;


/**
 * Главный фрагмент TranslationTab, который отвечает за перевод введенного текста
 */

public class TranslationTab extends Fragment {

    private final int REQUEST_CODE_LANG_FROM = 1;
    private final int REQUEST_CODE_LANG_TO = 2;

    private EditText mEditTextInput;
    private TextView mTextViewTranslate;
    private TextView mTextViewDictionary;
    private Button mButtonDeleteInputText;
    private ImageButton mImageButtonTrFavourite;
    private TextView mTextViewSign;

    private TextView mTextViewLangFrom;
    private TextView mTextViewLangTo;

    private ConstraintLayout mRootLayout;

    public static boolean isAutoLang;
    private String langFrom;
    private String langTo;

    public static ArrayMap<String, String> languages = null;

    private DBHandler dbHandler;

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
        mTextViewSign = (TextView) view.findViewById(R.id.textViewSign);
        mImageButtonTrFavourite = (ImageButton) view.findViewById(R.id.imageButtonTrFavourite);
        mRootLayout = (ConstraintLayout) view.findViewById(R.id.layoutTranslationTab);

        loadState();

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Заполняем список languages языками, полученными из асинхронной задачи
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

        // currentTranslation используется для хранения значений текущего перевода
        currentTranslation = new Translation();

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /**
         * При нажатии на значок ⟷ меняется направление перевода
         */
        mTextViewSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String swap = langFrom;
                langFrom = langTo;
                langTo = swap;

                mTextViewLangFrom.setText(languages.valueAt(languages.indexOfKey(langFrom)));
                mTextViewLangTo.setText(languages.valueAt(languages.indexOfKey(langTo)));
                mEditTextInput.setText(currentTranslation.getTranslatedText());

                getTranslate();
            }
        });

        /**
         * Вызывается startActivityfForResult для получения номера выбранного ключа в списке языков
         */
        mTextViewLangTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline()) {
                    if (languages.isEmpty()) {
                        new LanguageTask(new LanguageTask.DownloadResponse() {
                            @Override
                            public void processLangsFinish(ArrayMap<String, String> output) {
                                languages = output;
                                Log.i("langs", languages.toString());

                                Intent getLanguage = new Intent(getActivity(), GetLanguageActivity.class);
                                getLanguage.putExtra("CODE", REQUEST_CODE_LANG_TO);
                                TranslationTab.this.startActivityForResult(getLanguage, REQUEST_CODE_LANG_TO);

                            }
                        }).execute("https://translate.yandex.net/api/v1.5/tr.json/getLangs?" +
                                "key=" + Constant.KEY_TRANSLATE +
                                "&ui=ru");
                    } else {
                        Intent getLanguage = new Intent(getActivity(), GetLanguageActivity.class);
                        getLanguage.putExtra("CODE", REQUEST_CODE_LANG_TO);
                        TranslationTab.this.startActivityForResult(getLanguage, REQUEST_CODE_LANG_TO);
                    }


                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                }
            }
        });

        /**
         * Аналогично предыдущему
         */
        mTextViewLangFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline()) {
                    if (languages.isEmpty()) {
                        new LanguageTask(new LanguageTask.DownloadResponse() {
                            @Override
                            public void processLangsFinish(ArrayMap<String, String> output) {
                                languages = output;
                                Log.i("langs", languages.toString());

                                Intent getLanguage = new Intent(getActivity(), GetLanguageActivity.class);
                                getLanguage.putExtra("CODE", REQUEST_CODE_LANG_FROM);
                                TranslationTab.this.startActivityForResult(getLanguage, REQUEST_CODE_LANG_FROM);

                            }
                        }).execute("https://translate.yandex.net/api/v1.5/tr.json/getLangs?" +
                                "key=" + Constant.KEY_TRANSLATE +
                                "&ui=ru");
                    } else {
                        Intent getLanguage = new Intent(getActivity(), GetLanguageActivity.class);
                        getLanguage.putExtra("CODE", REQUEST_CODE_LANG_FROM);
                        TranslationTab.this.startActivityForResult(getLanguage, REQUEST_CODE_LANG_FROM);
                    }


                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                }
            }
        });


        mTextViewDictionary.setMovementMethod(new ScrollingMovementMethod());

        /**
         * addTextChangedListener отслеживает изменение текста в mEditTextView,
         * и если оно произошло, отправляет запрос на получение json с переводом
         */
        mEditTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 0) {
                    mTextViewTranslate.setText("");
                    mTextViewDictionary.setText("");
                    mButtonDeleteInputText.setVisibility(Button.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                mImageButtonTrFavourite.setImageResource(R.drawable.ic_fav_off);
                currentTranslation.setFavourite(0);

                if (s.toString().length() != 0) {
                    getTranslate();
                    mButtonDeleteInputText.setVisibility(Button.VISIBLE);
                }
                else {
                    mTextViewTranslate.setText("");
                    mTextViewDictionary.setText("");
                    mButtonDeleteInputText.setVisibility(Button.INVISIBLE);
                }


            }
        });

        /**
         * При нажатии на крестик стирается поле ввода
         */
        mButtonDeleteInputText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditTextInput.setText("");
            }
        });


        /**
         * При нажатии ENTER скрывается наэкранная клавиатура
         */
        mEditTextInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                hideKeyboard();
                Log.d(Log.DEBUG + "", event + "");
                return true;
            }
        });


        /**
         * Кнопка добавления в избранное текущего перевода
         */
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


        /**
         * Если клавиатура не показывается, то добавить текущий перевод в историю (БД)
         */
        mRootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (keyboardIsHidden()) {
                    addTranslationToDB();
                }
            }
        });
    }

    /**
     * Показывает, закрыта ли клавиатура в данный момент
     * @return
     */
    private boolean keyboardIsHidden() {
        Rect r = new Rect();
        mRootLayout.getWindowVisibleDisplayFrame(r);
        int screenHeight = mRootLayout.getRootView().getHeight();

        // r.bottom is the position above soft keypad or device button.
        // if keypad is shown, the r.bottom is smaller than that before.
        int keypadHeight = screenHeight - r.bottom;

        return keypadHeight <= screenHeight * 0.15;
    }

    /**
     * Получаем данные из GetLanguageActivity для изменения параметров перевода такие как
     * langFrom, langTo, isAutoLang
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Log.d(Log.DEBUG + "data_code", requestCode+"");

        if (resultCode == RESULT_OK) {
            int number = data.getIntExtra("numberOfKey", -1);
            isAutoLang = data.getBooleanExtra("isAutoLang", false);
            int code = data.getIntExtra("code", -1);
            Log.d(Log.DEBUG + "data_code", code+"");

            //int code  = data.getIntExtra("code", -1);

            if (requestCode == REQUEST_CODE_LANG_FROM) {
                Log.d(Log.DEBUG + "data", REQUEST_CODE_LANG_FROM + "");
                if (!isAutoLang && number != -1) {
                    mTextViewLangFrom.setText(languages.valueAt(number));
                    langFrom = languages.keyAt(number);
                } else {
                    if (number != -1) {
                        mTextViewLangFrom.setText(languages.valueAt(number));
                        langFrom = languages.keyAt(number);
                    }
                }
            } else {
                Log.d(Log.DEBUG + "data", REQUEST_CODE_LANG_TO + "");
                if (number != -1) {
                    mTextViewLangTo.setText(languages.valueAt(number));
                    langTo = languages.keyAt(number);
                }
            }
            if (mEditTextInput.getText().length() != 0) {
                getTranslate();
            }
        }
    }

    /**
     * Метод для получения Перевода и Словаря с помощью асинхронной задачи
     */
    private void getTranslate() {
        if (isOnline()) {
            try {
                String text = URLEncoder.encode(mEditTextInput.getText().toString(), "UTF-8");

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
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Получение последнего перевода из БД, и если он не совпадает с текущим,
     * то добавляем текущий в БД
     */
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

    /**
     * Сохраняем состояние текущего перевода в sharedPreferences на случай закрытия приложения
     *
     * В данном случае отдал предпочтение sharedPreferences, так данных мало и
     * могут быть получены по (ключ, значение)
     */
    private void saveState() {
        SharedPreferences statePref = this.getActivity().
                getSharedPreferences("lastTranslation", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = statePref.edit();
        editor.clear();

        editor.putString("text", currentTranslation.getText());
        editor.putString("trText", currentTranslation.getTranslatedText());
        editor.putString("langFrom", langFrom);
        editor.putString("langTo", langTo);
        editor.putInt("isFav", currentTranslation.getFavourite());

        editor.putString("langFromDescribe", mTextViewLangFrom.getText().toString());
        editor.putString("langToDescribe", mTextViewLangTo.getText().toString());
        editor.putString("dict", mTextViewDictionary.getText().toString());
        editor.putBoolean("isAutoLang", isAutoLang);

        editor.apply();
    }

    /**
     * Загрузка сохраненного состояние
     */
    private void loadState() {
        Log.d("data", "load State");
        SharedPreferences statePref = this.getActivity().
                getSharedPreferences("lastTranslation", Context.MODE_PRIVATE);

        currentTranslation.setText(statePref.getString("text", ""));
        currentTranslation.setTranslatedText(statePref.getString("trText", ""));
        currentTranslation.setFrom(statePref.getString("langFrom", "ru"));
        currentTranslation.setTo(statePref.getString("langTo", "en"));
        currentTranslation.setFavourite(statePref.getInt("isFav", 0));

        mEditTextInput.setText(currentTranslation.getText());
        mTextViewTranslate.setText(currentTranslation.getTranslatedText());
        mTextViewDictionary.setText(statePref.getString("dict", ""));
        isAutoLang = statePref.getBoolean("isAutoLang", false);

        mTextViewLangFrom.setText(statePref.getString(
                "langFromDescribe",
                getContext().getResources().getString(R.string.ru)));

        mTextViewLangTo.setText(statePref.getString(
                "langToDescribe",
                getContext().getResources().getString(R.string.en)));

        if (currentTranslation.isFavourite()) {
            mImageButtonTrFavourite.setImageResource(R.drawable.ic_fav_on);
        }

        langFrom = currentTranslation.getFrom();
        langTo = currentTranslation.getTo();

    }

    /**
     * Проверка на подключение к интернету
     * @return
     */
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * Используется, чтобы убрать клавиатуру с текущего экрана
     */
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditTextInput.getWindowToken(), 0);
    }

}
