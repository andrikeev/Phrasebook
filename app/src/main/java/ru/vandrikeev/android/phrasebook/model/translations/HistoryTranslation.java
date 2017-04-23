package ru.vandrikeev.android.phrasebook.model.translations;

import android.support.annotation.NonNull;

import java.util.Date;

import io.requery.Entity;
import io.requery.Key;
import io.requery.Table;

/**
 * Abstract translation entity for requery annotation processor.
 * <p>
 * Used to generate {@link HistoryTranslationEntity} class.
 */
@Entity
@Table(name = "history")
@SuppressWarnings("NullableProblems")
public class HistoryTranslation {

    @Key
    @NonNull
    protected String text;

    @Key
    @NonNull
    protected String translation;

    @Key
    @NonNull
    protected String languageFromCode;

    @Key
    @NonNull
    protected String languageToCode;

    @NonNull
    protected long timestamp;

    protected boolean favorite = false;

    public HistoryTranslation() {
    }

    public HistoryTranslation(@NonNull Translation translation) {
        this.text = translation.getText();
        this.translation = translation.getTranslation();
        this.languageFromCode = translation.getRealLanguageFrom().getCode();
        this.languageToCode = translation.getLanguageTo().getCode();
        this.timestamp = new Date().getTime();
    }

    public HistoryTranslation(@NonNull HistoryTranslation translation) {
        this.text = translation.getText();
        this.translation = translation.getTranslation();
        this.languageFromCode = translation.getLanguageFrom();
        this.languageToCode = translation.getLanguageTo();
        this.timestamp = new Date().getTime();
    }

    @NonNull
    public String getText() {
        return text;
    }

    @NonNull
    public String getTranslation() {
        return translation;
    }

    @NonNull
    public String getLanguageFrom() {
        return languageFromCode;
    }

    @NonNull
    public String getLanguageTo() {
        return languageToCode;
    }

    public boolean isFavorite() {
        return favorite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        final HistoryTranslation that = (HistoryTranslation) o;

        return text.equals(that.text) &&
                translation.equals(that.translation) &&
                languageFromCode.equals(that.languageFromCode) &&
                languageToCode.equals(that.languageToCode);
    }

    @Override
    public int hashCode() {
        int result = text.hashCode();
        result = 31 * result + translation.hashCode();
        result = 31 * result + languageFromCode.hashCode();
        result = 31 * result + languageToCode.hashCode();
        return result;
    }
}
