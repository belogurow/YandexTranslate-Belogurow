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
 * Фрагмент HistoryTab отображает список истории переводов
 */

public class HistoryTab extends Fragment {
    private DBHandler db;
    private RecyclerView mRecyclerView;
    private TabAdapter mAdapter;

    private FloatingActionButton mFabUpdate;
    private FloatingActionButton mFabDelete;

    private List<Translation> mTranslationList;

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

        hideKeyboard();
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
                mAdapter.updateList(false);
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
     * Инициализация адаптера
     */
    private void initializeAdapter() {
        mTranslationList = db.getAllTranslations(false);
        mAdapter = new TabAdapter(mTranslationList, getContext());
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
            Log.i("visible", "true");
            mAdapter.updateList(false);

        }
    }

    /**
     * Показ AlertDialog для подтверждения всей истории
     */
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.history)
                .setMessage(R.string.alert_del_history)
                .setCancelable(true)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.deleteAllTranslations();
                        mAdapter.updateList(false);
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
     * При открытии данного окна после перевода необходимо убрать клавиатуру
     */
    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
