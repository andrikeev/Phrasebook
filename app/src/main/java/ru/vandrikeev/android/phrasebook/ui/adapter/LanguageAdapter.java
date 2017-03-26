package ru.vandrikeev.android.phrasebook.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ru.vandrikeev.android.phrasebook.R;
import ru.vandrikeev.android.phrasebook.model.Language;

/**
 * Adapter for any list of {@link Language}.
 */
public class LanguageAdapter extends BaseAdapter {

    private final Context context;
    private List<Language> items;

    public LanguageAdapter(@NonNull Context context, List<Language> items) {
        super();
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @NonNull
    @Override
    public Language getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, ViewGroup parent) {
        TextView textView = new TextView(context);
        textView.setText(items.get(position).getNameResId());
        return textView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, ViewGroup parent) {
        final Language language = getItem(position);
        final ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.view_spinner_dropdown_item, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(language.getLocalizedName(parent.getContext()));

        return convertView;
    }

    public int getItemPosition(@NonNull Language language) {
        return items.indexOf(language);
    }

    public void clear() {
        items.clear();
    }

    public void addAll(@NonNull List<Language> languages) {
        items.addAll(languages);
    }

    private static class ViewHolder {
        TextView name;
    }
}
