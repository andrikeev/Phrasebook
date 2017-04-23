package ru.vandrikeev.android.phrasebook.model.languages;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Comparator;

import ru.vandrikeev.android.phrasebook.R;

/**
 * Language entity.
 */
public class Language implements Serializable {

    private static final String AUTODETECT = "auto";

    public static final Comparator<Language> COMPARATOR = new Comparator<Language>() {
        @Override
        public int compare(Language o1, Language o2) {
            return o1.name.compareTo(o2.name);
        }
    };

    /**
     * Language code.
     */
    @NonNull
    private String code;

    /**
     * Language name.
     */
    @NonNull
    private String name;

    public Language(@NonNull String code, @NonNull String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * Special language object that represents special case of translation when application should try to automatically
     * detect language of given text.
     */
    static Language createAutodetect(@NonNull Context context) {
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
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        final Language that = (Language) o;

        return this.code.equals(that.code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s [%s]", name, code);
    }
}
