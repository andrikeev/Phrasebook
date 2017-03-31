package ru.vandrikeev.android.phrasebook.model.translations;

import android.support.annotation.NonNull;

import java.util.Date;

import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;

/**
 * Abstract translation entity for requery annotation processor.
 * <p>
 * Used to generate {@link Translation} class.
 */
@Entity
public abstract class AbstractTranslation {

    @Key
    @Generated
    long id;

    @NonNull
    String languageFrom;

    @NonNull
    String languageTo;

    @NonNull
    String text;

    @NonNull
    String translation;

    boolean favorite;

    @NonNull
    Date date;

    @NonNull
    public String getLanguageFrom() {
        return languageFrom;
    }

    @NonNull
    public String getLanguageTo() {
        return languageTo;
    }

    @NonNull
    public String getText() {
        return text;
    }

    @NonNull
    public String getTranslation() {
        return translation;
    }

    public boolean isFavorite() {
        return favorite;
    }
}
