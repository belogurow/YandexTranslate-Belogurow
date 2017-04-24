package com.alexbelogurow.yandextranslate.tabs;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.alexbelogurow.yandextranslate.R;
import com.alexbelogurow.yandextranslate.adapter.TabAdapter;
import com.alexbelogurow.yandextranslate.dataBase.DBHandler;
import com.alexbelogurow.yandextranslate.model.Translation;

import java.util.List;

/**
 * Фрагмент FavoriteTab отображает список переводов, которые добавлены в избранное
 */

public class FavoriteTab extends Fragment {
    private DBHandler db;
    private RecyclerView mRecyclerView;
    private TabAdapter mAdapter;

    private FloatingActionButton mFabUpdate;
    private FloatingActionButton mFabDelete;

    private List<Translation> mFavTranslationList;

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
                mAdapter.updateList(true);
            }
        });

        mFabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });

        /**
         * Float Action Button будут скрываться про прокрутке списка
         */
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
        initializeAdapter();
    }

    /**
     * Показ AlertDialog для подтверждения удаления избранного
     */
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.favourite)
                .setMessage(R.string.alert_del_favourite)
                .setCancelable(true)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.deleteFavTranslations();
                        mAdapter.updateList(true);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        builder.create().show();
    }

    /**
     * Инициализация адаптера
     */
    private void initializeAdapter() {
        mFavTranslationList = db.getAllTranslations(true);
        mAdapter = new TabAdapter(mFavTranslationList, getContext());
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * Вызывается при показе пользователю на экране
     * @param isVisibleToUser true - если виден, false - иначе
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            if (mAdapter != null) {
                mAdapter.updateList(true);
            }
        }
    }
}
