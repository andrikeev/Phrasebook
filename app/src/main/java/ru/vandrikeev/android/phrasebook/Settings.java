package ru.vandrikeev.android.phrasebook;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import ru.vandrikeev.android.phrasebook.model.Language;

/**
 * Application settings.
 */
public class Settings {

    private static final String SETTINGS = "ru.vandrikeev.android.phrasebook.SETTINGS";
    private static final String LANGUAGE_FROM_KEY = "ru.vandrikeev.android.phrasebook.LANGUAGE_FROM";
    private static final String LANGUAGE_TO_KEY = "ru.vandrikeev.android.phrasebook.LANGUAGE_TO";

    @NonNull
    private Context context;

    @NonNull
    private SharedPreferences preferences;

    @Inject
    public Settings(@NonNull Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
    }

    @NonNull
    public Language getLanguageFrom() {
        String name = preferences.getString(LANGUAGE_FROM_KEY, context.getString(R.string.translation_from_default));
        return Language.valueOf(name);
    }

    public void setLanguageFrom(Language language) {
        preferences.edit()
                .putString(LANGUAGE_FROM_KEY, language.name())
                .apply();
    }

    @NonNull
    public Language getLanguageTo() {
        String name = preferences.getString(LANGUAGE_TO_KEY, context.getString(R.string.translation_to_default));
        return Language.valueOf(name);
    }

    public void setLanguageTo(Language language) {
        preferences.edit()
                .putString(LANGUAGE_TO_KEY, language.name())
                .apply();
    }
}
