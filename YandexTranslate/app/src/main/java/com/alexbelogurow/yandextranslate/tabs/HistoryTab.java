package com.alexbelogurow.yandextranslate.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
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

public class HistoryTab extends Fragment {
    private DBHandler db;
    private RecyclerView mRecyclerView;
    public static CoordinatorLayout mCoordLayout;
    private HistoryTabAdapter adapter;

    private List<Translate> translationList;

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
        final View view = inflater.inflate(R.layout.fragment_history, container, false);
        // Inflate the layout for this fragment

        mCoordLayout = (CoordinatorLayout) view.findViewById(R.id.coordLayoutHistory);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewHistory);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        //initializeData();
        initializeAdapter();
    }

    private void initializeAdapter() {
        translationList = db.getAllTranslations(false);
        adapter = new HistoryTabAdapter(translationList, getContext());
        mRecyclerView.setAdapter(adapter);
        //adapter.setHasStableIds(true);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            Log.i("visible", "true");
            //initializeAdapter();
            //translationList = db.getAllTranslations(false);
            //mRecyclerView.setAdapter(adapter);
            //adapter.notifyDataSetChanged();
            adapter.updateList(false);

        }
    }
}
