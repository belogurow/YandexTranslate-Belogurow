package com.alexbelogurow.yandextranslate.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;

import com.alexbelogurow.yandextranslate.R;
import com.alexbelogurow.yandextranslate.tabs.TranslationTab;

import java.util.ArrayList;

/**
 * Класс GetLanguageActivity формирует список языков на которые(с которых)
 * может осуществляться перевод. При нажатии на язык, его позиция в списке передается
 * предыдущему Activity.
 */

public class GetLanguageActivity extends AppCompatActivity {

    private ListView mListViewLanguages;
    private Toolbar mToolBar;
    private Switch mSwitchAutoLang;

    private int code;
    private String label;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_language);

        mListViewLanguages = (ListView) findViewById(R.id.listViewLanguages);
        mToolBar = (Toolbar) findViewById(R.id.toolbarInfo);
        mSwitchAutoLang = (Switch) findViewById(R.id.switchAutoLang);

        code = getIntent().getIntExtra("CODE", -1);

        if (code == 2) {
            mSwitchAutoLang.setVisibility(View.INVISIBLE);

            label = getResources().getString(R.string.lang_of_trtext);
        } else {
            if (TranslationTab.isAutoLang) {
                mSwitchAutoLang.setChecked(true);
            }

            label = getResources().getString(R.string.lang_of_text);
        }

        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(label);
        }

        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainMenu1 = new Intent();

                mainMenu1.putExtra("isAutoLang", mSwitchAutoLang.isChecked());
                setResult(RESULT_OK, mainMenu1);
                //onBackPressed();
                finish();
            }
        });


        ArrayList<String> listOfLangs = new ArrayList<>();

        for (int i = 0; i < TranslationTab.languages.size(); i++ ) {
            listOfLangs.add(TranslationTab.languages.valueAt(i));
        }

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listOfLangs);
        mListViewLanguages.setAdapter(arrayAdapter);


        mListViewLanguages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent mainMenu = new Intent();
                mainMenu.putExtra("numberOfKey", position);
                mainMenu.putExtra("isAutoLang", mSwitchAutoLang.isChecked());
                mainMenu.putExtra("code", code);
                setResult(RESULT_OK, mainMenu);

                finish();

            }
        });

    }
}
