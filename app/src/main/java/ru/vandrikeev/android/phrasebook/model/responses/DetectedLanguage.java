package ru.vandrikeev.android.phrasebook.model.responses;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * API response with code of detected language.
 */
@SuppressWarnings("NullableProblems")
public final class DetectedLanguage extends BaseApiResponse {

    @NonNull
    @SerializedName("lang")
    private String language;

    @NonNull
    public String getLanguage() {
        return language;
    }
}
