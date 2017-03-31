package ru.vandrikeev.android.phrasebook.model.network;

import android.support.annotation.Nullable;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.HttpException;
import ru.vandrikeev.android.phrasebook.R;

/**
 * Utils for errors handling.
 */
public class ErrorUtils {

    /**
     * The resource identifier of the localized message associated with given error.
     *
     * @param e error
     * @return resource id of string message
     */
    public static int getErrorMessage(@Nullable Throwable e) {
        if (e instanceof YandexTranslateException) {
            switch (((YandexTranslateException) e).getCode()) {
                case 401:
                    return R.string.error_api_invalid_key;
                case 402:
                    return R.string.error_api_blocked_key;
                case 404:
                    return R.string.error_api_daily_limit;
                case 413:
                    return R.string.error_api_text_size;
                case 422:
                    return R.string.error_api_cannot_translate;
                case 501:
                    return R.string.error_api_not_supported;
                default:
                    return R.string.error_api_not_supported;
            }
        } else if (e instanceof SocketTimeoutException ||
                e instanceof SocketException ||
                e instanceof HttpException ||
                e instanceof UnknownHostException) {
            return R.string.error_network;
        } else {
            return R.string.error_unknown;
        }
    }
}
