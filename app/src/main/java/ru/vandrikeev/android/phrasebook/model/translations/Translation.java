package ru.vandrikeev.android.phrasebook.model.translations;

import android.support.annotation.NonNull;

import java.io.Serializable;

import ru.vandrikeev.android.phrasebook.model.languages.Language;

/**
 * Abstract translation entity for requery annotation processor.
 * <p>
 * Used to generate {@link Translation} class.
 */
public class Translation implements Serializable {

    @NonNull
    private String text;

    @NonNull
    private String translation;

    @NonNull
    private Language languageFrom;

    @NonNull
    private Language realLanguageFrom;

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
