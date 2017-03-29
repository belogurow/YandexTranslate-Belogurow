package com.alexbelogurow.yandextranslate.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.alexbelogurow.yandextranslate.R;

import java.util.ArrayList;

public class GetLanguageActivity extends AppCompatActivity {

    private ListView mListViewLanguages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_language);

        mListViewLanguages = (ListView) findViewById(R.id.listViewLanguages);
        //mListViewLanguages.setSelector(R.color.selected);
        //mListViewLanguages.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        ArrayList<String> listOfLangs = new ArrayList<>();

        for (int i = 0; i < MainActivity.languages.size(); i++ ) {
            listOfLangs.add(MainActivity.languages.valueAt(i));
        }

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listOfLangs);
        mListViewLanguages.setAdapter(arrayAdapter);

        mListViewLanguages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String key = MainActivity.languages.keyAt(position);
                Toast.makeText(getApplicationContext(), key, Toast.LENGTH_LONG).show();

                //arrayAdapter.notifyDataSetChanged();

                Intent mainMenu = new Intent();
                mainMenu.putExtra("numberOfKey", position);
                setResult(RESULT_OK, mainMenu);
                finish();

            }
        });

    }
}
