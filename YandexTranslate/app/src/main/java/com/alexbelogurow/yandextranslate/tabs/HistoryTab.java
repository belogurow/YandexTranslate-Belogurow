package com.alexbelogurow.yandextranslate.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import com.alexbelogurow.yandextranslate.model.Translation;

import java.util.List;

/**
 * Created by alexbelogurow on 17.04.17.
 */

public class HistoryTab extends Fragment {
    private DBHandler db;
    private RecyclerView mRecyclerView;
    private HistoryTabAdapter adapter;

    private FloatingActionButton mFabUpdate;
    private FloatingActionButton mFabDelete;

    private List<Translation> translationList;

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

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewHistory);
        mFabUpdate = (FloatingActionButton) view.findViewById(R.id.historyFabUpdate);
        mFabDelete = (FloatingActionButton) view.findViewById(R.id.historyFabDelete);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mFabUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.updateList(false);
            }
        });

        mFabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteAllTranslations();
                adapter.updateList(true);
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    // Scroll Down
                    if (mFabUpdate.isShown()) {
                        mFabUpdate.hide();
                        mFabDelete.hide();
                    }
                } else if (dy < 0) {
                    // Scroll Up
                    if (!mFabUpdate.isShown()) {
                        mFabUpdate.show();
                        mFabDelete.show();
                    }
                }
            }
        });

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
