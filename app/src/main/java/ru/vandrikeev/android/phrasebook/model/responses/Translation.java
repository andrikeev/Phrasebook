package ru.vandrikeev.android.phrasebook.model.responses;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import ru.vandrikeev.android.phrasebook.model.Language;

/**
 * API response for translation request.
 */
@SuppressWarnings({"unused", "MismatchedQueryAndUpdateOfCollection"})
public final class Translation extends BaseApiResponse {

    /**
     * List of translations for strings passed in request.
     */
    @NonNull
    @SerializedName("text")
    private List<String> text = new ArrayList<>();

    /**
     * Translation direction.
     */
    @Nullable
    @SerializedName("lang")
    private String direction;

    @NonNull
    public String getText() {
        if (text.size() > 1) {
            final StringBuilder sb = new StringBuilder();
            for (String s : text) {
                sb.append(s);
                sb.append('\n');
            }
            return sb.toString();
        } else if (text.size() > 0) {
            return text.get(0);
        } else {
            return "";
        }
    }

    @Nullable
    public Pair<Language, Language> getTranslationDirection() {
        if (TextUtils.isEmpty(direction)) {
            return null;
        } else {
            int idx = direction.indexOf('-');
            final Language from = Language.valueOf(direction.substring(0, idx));
            final Language to = Language.valueOf(direction.substring(idx + 1, direction.length()));
            return new Pair<>(from, to);
        }
    }
}
