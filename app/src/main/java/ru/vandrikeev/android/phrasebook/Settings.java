package ru.vandrikeev.android.phrasebook;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import javax.inject.Inject;

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
    public String getLanguageFrom() {
        return preferences.getString(LANGUAGE_FROM_KEY, context.getString(R.string.translation_from_default));
    }

    public void setLanguageFrom(String language) {
        preferences.edit()
                .putString(LANGUAGE_FROM_KEY, language)
                .apply();
    }

    @NonNull
    public String getLanguageTo() {
        return preferences.getString(LANGUAGE_TO_KEY, context.getString(R.string.translation_to_default));
    }

    public void setLanguageTo(String language) {
        preferences.edit()
                .putString(LANGUAGE_TO_KEY, language)
                .apply();
    }
}
