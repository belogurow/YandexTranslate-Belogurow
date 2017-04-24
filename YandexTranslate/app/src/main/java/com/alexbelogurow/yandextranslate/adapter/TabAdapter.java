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
import com.alexbelogurow.yandextranslate.model.Translation;

import java.util.List;

/**
 * Класс TabAdapter наследуется от RecyclerView.Adapter<> и формирует прокручиваемый
 * список из CardView. В карточке содержится текст с его переводом, с какого и на какой
 * язык был выполнен перевод. TabAdapter используется для вкладок истории и избранного.
 */

public class TabAdapter extends RecyclerView.Adapter<TabAdapter.TabViewHolder> {
    private List<Translation> translationList;
    private Context context;


    public TabAdapter(List<Translation> translationList, Context context) {
        this.translationList = translationList;
        this.context = context;

    }

    public static class TabViewHolder extends RecyclerView.ViewHolder {
        private CardView mCardView;
        private TextView mTextViewText,
                mTextViewTrText,
                mTextViewFromToLang;
        private ImageButton mImageButtonFav;
        private boolean isFav;


        public TabViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.cardViewHistory);
            mTextViewText = (TextView) itemView.findViewById(R.id.textViewHistoryText);
            mTextViewTrText = (TextView) itemView.findViewById(R.id.textViewHistoryTrText);
            mTextViewFromToLang = (TextView) itemView.findViewById(R.id.textViewHistoryFromToLang);
            mImageButtonFav = (ImageButton) itemView.findViewById(R.id.imageButtonTabsFavorite);

        }

    }

    /**
     * Метод updateList вызывается после того, как в adapter произошли какие-либо изменения
     * @param isFav отвечает за то, будут ли в списке переводы, добавленные в избранное или нет
     */
    public void updateList(boolean isFav) {
        DBHandler dbHandler = new DBHandler(context);
        translationList.clear();
        translationList = dbHandler.getAllTranslations(isFav);
        notifyDataSetChanged();
    }


    @Override
    public TabViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.translation_item, parent, false);
        TabViewHolder historyViewHolder = new TabViewHolder(view);


        return historyViewHolder;
    }

    @Override
    public void onBindViewHolder(final TabViewHolder holder, final int position) {
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

        /*
         * Обновление нового значения избранного данной записи в БД
         */
        holder.mImageButtonFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (translationList.get(position).getFavourite() == 0) {
                    holder.mImageButtonFav.setImageResource(R.drawable.ic_fav_off);

                    DBHandler dbHandler = new DBHandler(context);

                    Translation translation = translationList.get(position);
                    translation.setFavourite(1);

                    dbHandler.addTranslation(translation);
                    dbHandler.close();

                    Toast.makeText(context, context.getString(R.string.add_in_fav),
                            Toast.LENGTH_SHORT).show();

                }
                else {
                    holder.mImageButtonFav.setImageResource(R.drawable.ic_fav_on);


                    DBHandler dbHandler = new DBHandler(context);

                    Translation translation = translationList.get(position);
                    translation.setFavourite(0);

                    dbHandler.addTranslation(translation);
                    dbHandler.close();

                    Toast.makeText(context, context.getString(R.string.remove_from_fav),
                            Toast.LENGTH_SHORT).show();

                }
            }
        });
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
