package ru.vandrikeev.android.phrasebook.model.network;

/**
 * Yandex.Translate API error.
 *
 * @see ru.vandrikeev.android.phrasebook.model.responses.BaseApiResponse#code
 */
public class YandexTranslateException extends Throwable {

    private int code;

    public YandexTranslateException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
