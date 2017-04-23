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
import android.widget.Toast;

import com.alexbelogurow.yandextranslate.R;
import com.alexbelogurow.yandextranslate.model.Translation;
import com.alexbelogurow.yandextranslate.tabs.TranslationTab;

import java.util.ArrayList;

public class GetLanguageActivity extends AppCompatActivity {

    private ListView mListViewLanguages;
    private Toolbar mToolBar;
    private Switch mSwitchAutoLang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_language);

        mListViewLanguages = (ListView) findViewById(R.id.listViewLanguages);
        mToolBar = (Toolbar) findViewById(R.id.toolbarGetLangs);
        mSwitchAutoLang = (Switch) findViewById(R.id.switchAutoLang);

        if (TranslationTab.isAutoLang) {
            mSwitchAutoLang.setChecked(true);
        }

        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainMenu = new Intent();

                mainMenu.putExtra("isAutoLang", mSwitchAutoLang.isChecked());
                setResult(RESULT_OK, mainMenu);
                //setResult(RESULT_CANCELED, mainMenu);
                onBackPressed();
                finish();
            }
        });


        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //mListViewLanguages.setSelector(R.color.selected);
        //mListViewLanguages.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

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
                setResult(RESULT_OK, mainMenu);

                onBackPressed();
                finish();

            }
        });

    }
}
