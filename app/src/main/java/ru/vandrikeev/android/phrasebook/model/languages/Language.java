package ru.vandrikeev.android.phrasebook.model.languages;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.Comparator;

import ru.vandrikeev.android.phrasebook.R;

/**
 * Language entity.
 */
public class Language {

    public static final Comparator<Language> COMPARATOR = new Comparator<Language>() {
        @Override
        public int compare(Language o1, Language o2) {
            return o1.name.compareTo(o2.name);
        }
    };
    private static final String AUTODETECT = "auto";
    @NonNull
    private String code;

    @NonNull
    private String name;

    public Language(@NonNull String code, @NonNull String name) {
        this.code = code;
        this.name = name;
    }

    static Language getAutodetect(Context context) {
        return new Language(AUTODETECT, context.getString(R.string.spinner_autodetect));
    }

    @NonNull
    public String getCode() {
        return code;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public boolean isAutodetect() {
        return AUTODETECT.equals(code);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Language that = (Language) o;

        return this.code.equals(that.code) && this.name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }
}
