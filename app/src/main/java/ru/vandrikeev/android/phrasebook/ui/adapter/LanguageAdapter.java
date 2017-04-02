package ru.vandrikeev.android.phrasebook.ui.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.vandrikeev.android.phrasebook.R;
import ru.vandrikeev.android.phrasebook.model.languages.Language;

/**
 * Adapter for list of {@link Language}.
 */
public class LanguageAdapter extends BaseAdapter {

    @NonNull
    private List<Language> items = new ArrayList<>();

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
        return getView(position, convertView, parent, R.layout.view_spinner_selected_item);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, ViewGroup parent) {
        return getView(position, convertView, parent, R.layout.view_spinner_dropdown_item);
    }

    private View getView(int position, @Nullable View convertView, ViewGroup parent, int resId) {
        final Language language = getItem(position);
        final ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(resId, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(language.getName());

        return convertView;
    }

    public int getItemPosition(@NonNull Language language) {
        return items.indexOf(language);
    }

    public void addAll(@NonNull List<Language> languages) {
        items.clear();
        items.addAll(languages);
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        private TextView name;
    }
}
