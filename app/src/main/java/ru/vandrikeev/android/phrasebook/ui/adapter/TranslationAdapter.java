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
import ru.vandrikeev.android.phrasebook.model.translations.AbstractTranslation;

/**
 * Recycler view adapter for list of {@link AbstractTranslation}.
 */
public class TranslationAdapter extends RecyclerView.Adapter<TranslationAdapter.TranslationHolder> {

    @NonNull
    private List<AbstractTranslation> items = new ArrayList<>();

    public TranslationAdapter() {
        super();
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
        final AbstractTranslation item = items.get(position);
        holder.icon.setImageResource(item.isFavorite() ? R.drawable.ic_favorite_on : R.drawable.ic_favorite_off);
        holder.languageFrom.setText(item.getLanguageFrom());
        holder.languageTo.setText(item.getLanguageTo());
        holder.text.setText(item.getText());
        holder.translation.setText(item.getTranslation());
    }

    public void addAll(@NonNull List<? extends AbstractTranslation> items) {
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    class TranslationHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView text;
        private TextView translation;
        private TextView languageFrom;
        private TextView languageTo;


        TranslationHolder(View view) {
            super(view);
            icon = (ImageView) view.findViewById(R.id.icon);
            text = (TextView) view.findViewById(R.id.text);
            translation = (TextView) view.findViewById(R.id.translation);
            languageFrom = (TextView) view.findViewById(R.id.languageFrom);
            languageTo = (TextView) view.findViewById(R.id.languageTo);
        }
    }
}
