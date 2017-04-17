package com.alexbelogurow.yandextranslate.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alexbelogurow.yandextranslate.R;
import com.alexbelogurow.yandextranslate.dataBase.DBHandler;
import com.alexbelogurow.yandextranslate.helper.Translate;

import java.util.List;

/**
 * Created by alexbelogurow on 17.04.17.
 */

public class HistoryTab extends Fragment {
    private DBHandler db;

    public HistoryTab() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new DBHandler(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("Insert: ", "Inserting ..");
        // Reading all contacts
        Log.d("Reading: ", "Reading all contacts..");
        List<Translate> translate = db.getAllTranslations();

        for (Translate tr : translate) {
            // Writing Contacts to log
            Log.d("Name: ", tr.toString());
        }
    }
}
