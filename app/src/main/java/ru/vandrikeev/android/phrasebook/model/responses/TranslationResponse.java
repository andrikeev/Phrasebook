package ru.vandrikeev.android.phrasebook.model.responses;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * API response for translation request.
 */
@SuppressWarnings({"unused", "MismatchedQueryAndUpdateOfCollection"})
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
    @Nullable
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

    @Nullable
    public String getTranslationDirection() {
        return direction;
    }
}
