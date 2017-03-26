package ru.vandrikeev.android.phrasebook.model.responses;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Base API response model.
 */
public abstract class BaseApiResponse {

    /**
     * Response code.
     * <ul>
     * <li>200 - Operation completed successfully</li>
     * <li>401 - Invalid API key</li>
     * <li>402 - Blocked API key</li>
     * <li>404 - Exceeded the daily limit on the amount of translated text</li>
     * <li>413 - Exceeded the maximum text size</li>
     * <li>422 - The text cannot be translated</li>
     * <li>501 - The specified translation direction is not supported</li>
     * </ul>
     */
    @SerializedName("code")
    private int code = 200;

    /**
     * Response message.
     */
    @NonNull
    @SerializedName("message")
    private String message = "";

    public int getCode() {
        return code;
    }

    @NonNull
    public String getMessage() {
        return message;
    }
}
