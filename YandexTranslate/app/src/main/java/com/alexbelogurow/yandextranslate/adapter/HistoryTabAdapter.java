package com.alexbelogurow.yandextranslate.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alexbelogurow.yandextranslate.R;
import com.alexbelogurow.yandextranslate.helper.Translate;

import java.util.List;

/**
 * Created by alexbelogurow on 18.04.17.
 */

public class HistoryTabAdapter extends RecyclerView.Adapter<HistoryTabAdapter.HistoryViewHolder> {
    private List<Translate> translationList;

    public HistoryTabAdapter(List<Translate> translationList) {
        this.translationList = translationList;
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        private CardView mCardView;
        private TextView mTextViewText,
                mTextViewTrText,
                mTextViewFromLang,
                mTextViewToLang;


        public HistoryViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.cardViewHistory);
            mTextViewText = (TextView) itemView.findViewById(R.id.textViewHistoryText);
            mTextViewTrText = (TextView) itemView.findViewById(R.id.textViewHistoryTrText);
            mTextViewFromLang = (TextView) itemView.findViewById(R.id.textViewHistoryFromLang);
            mTextViewToLang = (TextView) itemView.findViewById(R.id.textViewHistoryToLang);
        }
    }
    @Override
    public HistoryTabAdapter.HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.translation_item, parent, false);
        HistoryViewHolder historyViewHolder = new HistoryViewHolder(view);

        return historyViewHolder;
    }

    @Override
    public void onBindViewHolder(HistoryTabAdapter.HistoryViewHolder holder, int position) {
        holder.mTextViewText.setText(translationList.get(position).getText());
        holder.mTextViewTrText.setText(translationList.get(position).getTranslatedText());
        holder.mTextViewFromLang.setText(translationList.get(position).getFrom());
        holder.mTextViewToLang.setText(translationList.get(position).getTo());

    }

    @Override
    public int getItemCount() {
        return translationList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
