package ru.vandrikeev.android.phrasebook.model.responses;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.vandrikeev.android.phrasebook.model.languages.Language;

/**
 * API response with list of languages supported for translation.
 */
@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
public final class SupportedLanguages extends BaseApiResponse {

    /**
     * Map of {@link Language#code} to {@link Language#name}.
     */
    @NonNull
    @SerializedName("langs")
    private Map<String, String> supportedLanguages = new HashMap<>();

    private SupportedLanguages() {
    }

    @NonNull
    public List<Language> getSupportedLanguages() {
        final List<Language> languages = new ArrayList<>(supportedLanguages.size());
        for (Map.Entry<String, String> entry : supportedLanguages.entrySet()) {
            languages.add(new Language(entry.getKey(), entry.getValue()));
        }
        return languages;
    }
}
