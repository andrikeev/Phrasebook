package ru.vandrikeev.android.phrasebook.model.responses;

import android.support.annotation.NonNull;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * API response for translation request.
 */
@SuppressWarnings("NullableProblems")
public final class TranslationResponse extends BaseApiResponse {

    /**
     * List of translations for strings passed in request.
     */
    @NonNull
    @SerializedName("text")
    private List<String> text = new ArrayList<>();

    /**
     * Translation direction in 'from-to' format.
     */
    @NonNull
    @SerializedName("lang")
    private String direction;

    private TranslationResponse() {
    }

    @NonNull
    public String getText() {
        final StringBuilder sb = new StringBuilder();
        for (String s : text) {
            sb.append(s);
            if (text.size() > 1) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }

    @NonNull
    public String getTranslationDirection() {
        return direction;
    }
}
