package ru.vandrikeev.android.phrasebook.model.responses;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import ru.vandrikeev.android.phrasebook.model.Language;

/**
 * API response with code of detected language.
 */
@SuppressWarnings("unused")
public final class DetectedLanguage extends BaseApiResponse {

    @NonNull
    @SerializedName("lang")
    private Language language;

    @NonNull
    public Language getLanguage() {
        return language;
    }
}
