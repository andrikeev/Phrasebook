package ru.vandrikeev.android.phrasebook.model.responses;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.vandrikeev.android.phrasebook.model.Language;

/**
 * API response with list of languages supported for translation.
 */
public final class SupportedLanguages extends BaseApiResponse {

    /**
     * List of supported languages.
     */
    @NonNull
    @SerializedName("langs")
    private Map<Language, String> supportedLanguages = new HashMap<>();

    private SupportedLanguages() {
    }

    @NonNull
    public List<Language> getSupportedLanguages() {
        final List<Language> languages = new ArrayList<>(supportedLanguages.keySet().size());
        for (Language language : supportedLanguages.keySet()) {
            languages.add(language);
        }
        return languages;
    }
}
