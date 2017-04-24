package com.alexbelogurow.yandextranslate.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.alexbelogurow.yandextranslate.R;

/**
 * Класс InfoActivity отображает список сервисов с активными ссылками,
 * которые использует данное приложение: Яндекс.Переводчик, Яднекс.Словарь
 */

public class InfoActivity extends AppCompatActivity {

    private TextView mTextViewInfoTr,
            mTextViewInfoDict;

    private Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        mTextViewInfoTr = (TextView) findViewById(R.id.textViewInfoTr);
        mTextViewInfoTr.setMovementMethod(LinkMovementMethod.getInstance());

        mTextViewInfoDict = (TextView) findViewById(R.id.textViewInfoDict);
        mTextViewInfoDict.setMovementMethod(LinkMovementMethod.getInstance());

        mToolBar = (Toolbar) findViewById(R.id.toolbarInfo);

        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(getResources().getString(R.string.label_info));
        }

        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
    }


}
