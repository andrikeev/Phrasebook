package ru.vandrikeev.android.phrasebook.model.translations;

import android.support.annotation.NonNull;

import java.io.Serializable;

import ru.vandrikeev.android.phrasebook.model.languages.Language;

/**
 * Translation model.
 */
public class Translation implements Serializable {

    /**
     * Text to be translated.
     */
    @NonNull
    private String text;

    /**
     * Translations.
     */
    @NonNull
    private String translation;

    /**
     * Language to translate from.
     */
    @NonNull
    private Language languageFrom;

    /**
     * Real language of translated text.
     */
    @NonNull
    private Language realLanguageFrom;

    /**
     * Language to translate to.
     */
    @NonNull
    private Language languageTo;

    public Translation(@NonNull String text,
                       @NonNull String translation,
                       @NonNull Language languageFrom,
                       @NonNull Language realLanguageFrom,
                       @NonNull Language languageTo) {
        this.text = text;
        this.translation = translation;
        this.languageFrom = languageFrom;
        this.realLanguageFrom = realLanguageFrom;
        this.languageTo = languageTo;
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
    public Language getLanguageFrom() {
        return languageFrom;
    }

    @NonNull
    public Language getRealLanguageFrom() {
        return realLanguageFrom;
    }

    @NonNull
    public Language getLanguageTo() {
        return languageTo;
    }

    @Override
    public int hashCode() {
        int result = text.hashCode();
        result = 31 * result + translation.hashCode();
        result = 31 * result + languageFrom.getCode().hashCode();
        result = 31 * result + languageTo.getCode().hashCode();
        return Math.abs(result);
    }
}
