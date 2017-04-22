package com.alexbelogurow.yandextranslate.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alexbelogurow.yandextranslate.R;
import com.alexbelogurow.yandextranslate.dataBase.DBHandler;
import com.alexbelogurow.yandextranslate.model.Translate;

import java.util.List;

/**
 * Created by alexbelogurow on 18.04.17.
 */

public class HistoryTabAdapter extends RecyclerView.Adapter<HistoryTabAdapter.HistoryViewHolder> {
    private List<Translate> translationList;
    private Context context;


    public HistoryTabAdapter(List<Translate> translationList, Context context) {
        this.translationList = translationList;
        this.context = context;

    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        private CardView mCardView;
        private TextView mTextViewText,
                mTextViewTrText,
                mTextViewFromToLang;
        private ImageButton mImageButtonFav;
        private boolean isFav;


        public HistoryViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.cardViewHistory);
            mTextViewText = (TextView) itemView.findViewById(R.id.textViewHistoryText);
            mTextViewTrText = (TextView) itemView.findViewById(R.id.textViewHistoryTrText);
            mTextViewFromToLang = (TextView) itemView.findViewById(R.id.textViewHistoryFromToLang);
            mImageButtonFav = (ImageButton) itemView.findViewById(R.id.imageButtonTabsFavorite);

            //mImageButtonFav.setOnClickListener(this);
        }

        /*@Override
        public void onClick(View v) {
            if (isFav) {
                mImageButtonFav.setImageResource(R.drawable.ic_fav_on);
            }


        }  */
    }

    public void updateList(boolean isFav) {
        DBHandler dbHandler = new DBHandler(context);
        translationList.clear();
        translationList = dbHandler.getAllTranslations(isFav);
        notifyDataSetChanged();
    }

    @Override
    public HistoryTabAdapter.HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.translation_item, parent, false);
        HistoryViewHolder historyViewHolder = new HistoryViewHolder(view);


        return historyViewHolder;
    }

    @Override
    public void onBindViewHolder(final HistoryTabAdapter.HistoryViewHolder holder, final int position) {
        holder.mTextViewText.setText(translationList.get(position).getText());
        holder.mTextViewTrText.setText(translationList.get(position).getTranslatedText());
        holder.mTextViewFromToLang.setText((
                translationList.get(position).getFrom() + " - " +
                translationList.get(position).getTo())
                .toUpperCase());

        if (translationList.get(position).getFavourite() == 1) {
            holder.mImageButtonFav.setImageResource(R.drawable.ic_fav_on);
            holder.isFav = true;
        }
        else {
            holder.mImageButtonFav.setImageResource(R.drawable.ic_fav_off);
            holder.isFav = false;
        }

        holder.mImageButtonFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (translationList.get(position).getFavourite() == 0) {
                    holder.mImageButtonFav.setImageResource(R.drawable.ic_fav_off);

                    DBHandler dbHandler = new DBHandler(context);

                    Translate translation = translationList.get(position);
                    translation.setFavourite(1);

                    dbHandler.addTranslation(translation);
                    dbHandler.close();

                    Toast.makeText(context, "Added in favourite, update list", Toast.LENGTH_SHORT).show();

                }
                else {
                    holder.mImageButtonFav.setImageResource(R.drawable.ic_fav_on);


                    DBHandler dbHandler = new DBHandler(context);

                    Translate translation = translationList.get(position);
                    translation.setFavourite(0);

                    dbHandler.addTranslation(translation);
                    dbHandler.close();

                    Toast.makeText(context, "Removed from favourite, update list", Toast.LENGTH_SHORT).show();

                }
            }
        });
        //holder.mTextViewToLang.setText(favTranslationList.get(position).getTo());

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
