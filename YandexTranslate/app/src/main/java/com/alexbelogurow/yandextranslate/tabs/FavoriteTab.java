package com.alexbelogurow.yandextranslate.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alexbelogurow.yandextranslate.R;
import com.alexbelogurow.yandextranslate.adapter.HistoryTabAdapter;
import com.alexbelogurow.yandextranslate.dataBase.DBHandler;
import com.alexbelogurow.yandextranslate.helper.Translate;

import java.util.List;

/**
 * Created by alexbelogurow on 17.04.17.
 */

public class FavoriteTab extends Fragment {
    private DBHandler db;
    private RecyclerView recyclerView;
    private HistoryTabAdapter adapter;

    public static List<Translate> favTranslationList;

    public FavoriteTab() {
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
        final View view = inflater.inflate(R.layout.fragment_history, container, false);
        // Inflate the layout for this fragment

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewHistory);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        //initializeData();
        initializeAdapter();
    }

    private void initializeAdapter() {
        favTranslationList = db.getAllTranslations(true);
        adapter = new HistoryTabAdapter(favTranslationList, getContext());
        recyclerView.setAdapter(adapter);
        //adapter.setHasStableIds(true);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            Log.i("visible", "true");
            initializeAdapter();
            //recyclerView.setAdapter(adapter);
            //adapter.notifyDataSetChanged();
        }
    }
}
