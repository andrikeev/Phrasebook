package ru.vandrikeev.android.phrasebook.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.vandrikeev.android.phrasebook.R;
import ru.vandrikeev.android.phrasebook.model.translations.HistoryTranslation;
import ru.vandrikeev.android.phrasebook.model.translations.Translation;

/**
 * Recycler view adapter for list of {@link Translation}.
 */
public class TranslationAdapter extends RecyclerView.Adapter<TranslationAdapter.TranslationHolder> {

    @NonNull
    private List<HistoryTranslation> items = new ArrayList<>();

    @NonNull
    private OnClickTranslationListener onClickTranslationListener;

    @NonNull
    private OnClickFavoriteListener onClickFavoriteListener;

    public TranslationAdapter(@NonNull OnClickTranslationListener onClickTranslationListener,
                              @NonNull OnClickFavoriteListener onClickFavoriteListener) {
        super();
        this.onClickTranslationListener = onClickTranslationListener;
        this.onClickFavoriteListener = onClickFavoriteListener;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public TranslationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_translation_list_item, parent, false);
        return new TranslationHolder(view);
    }

    @Override
    public void onBindViewHolder(TranslationHolder holder, int position) {
        final HistoryTranslation item = items.get(position);
        holder.languageFrom.setText(item.getLanguageFrom());
        holder.languageTo.setText(item.getLanguageTo());
        holder.text.setText(item.getText());
        holder.translation.setText(item.getTranslation());
        holder.icon.setImageResource(item.isFavorite() ? R.drawable.ic_favorite_on : R.drawable.ic_favorite_off);
        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFavoriteListener.onClick(item);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickTranslationListener.onClick(item);
            }
        });
    }

    public void addAll(@NonNull List<? extends HistoryTranslation> items) {
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public void update(@NonNull HistoryTranslation translation) {
        int position = this.items.indexOf(translation);
        if (position != -1) {
            this.items.remove(position);
            this.items.add(position, translation);
            notifyItemChanged(position);
        }
    }

    public void remove(@NonNull HistoryTranslation translation) {
        int position = this.items.indexOf(translation);
        if (this.items.remove(translation)) {
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    class TranslationHolder extends RecyclerView.ViewHolder {
        private View itemView;

        private ImageView icon;
        private TextView text;
        private TextView translation;
        private TextView languageFrom;
        private TextView languageTo;


        TranslationHolder(View view) {
            super(view);
            itemView = view;
            icon = (ImageView) view.findViewById(R.id.icon);
            text = (TextView) view.findViewById(R.id.text);
            translation = (TextView) view.findViewById(R.id.translation);
            languageFrom = (TextView) view.findViewById(R.id.languageFromCode);
            languageTo = (TextView) view.findViewById(R.id.languageToCode);
        }
    }

    public interface OnClickTranslationListener {
        void onClick(@NonNull HistoryTranslation translation);
    }

    public interface OnClickFavoriteListener {
        void onClick(@NonNull HistoryTranslation translation);
    }
}
