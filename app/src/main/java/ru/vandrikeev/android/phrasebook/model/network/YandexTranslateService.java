package ru.vandrikeev.android.phrasebook.model.network;

import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.vandrikeev.android.phrasebook.BuildConfig;
import ru.vandrikeev.android.phrasebook.model.Language;
import ru.vandrikeev.android.phrasebook.model.responses.DetectedLanguage;
import ru.vandrikeev.android.phrasebook.model.responses.SupportedLanguages;
import ru.vandrikeev.android.phrasebook.model.responses.Translation;

/**
 * The service for async loading data from Yandex.Translate API.
 */
@Singleton
public class YandexTranslateService {

    @NonNull
    private YandexTranslateApi api;

    @Inject
    public YandexTranslateService(@NonNull YandexTranslateApi api) {
        this.api = api;
    }

    /**
     * Asynchronously loads list of supported to translation languages.
     *
     * @param language language to translate from
     * @return {@link Single} of {@link SupportedLanguages} response observable on Android main thread
     */
    @NonNull
    public Single<SupportedLanguages> getSupportedLanguages(@NonNull Language language) {
        return api.getSupportedLanguages(BuildConfig.API_KEY, language.name())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Asynchronously detects language for given text.
     *
     * @param text text to detect language
     * @return {@link Single} of {@link DetectedLanguage} response observable on Android main thread
     */
    @NonNull
    public Single<DetectedLanguage> detectLanguage(@NonNull String text) {
        return api.detectLanguage(BuildConfig.API_KEY, text)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Asynchronously translates given text between given languages.
     *
     * @param from language to translate from
     * @param to   language to translate to
     * @param text text to translate
     * @return {@link Single} of {@link Translation} response observable on Android main thread
     */
    @NonNull
    public Single<Translation> translate(@NonNull Language from, @NonNull Language to, @NonNull String text) {
        final String direction = from != Language.auto
                ? String.format("%s-%s", from.name(), to.name())
                : to.name();

        return api.translate(BuildConfig.API_KEY, text, direction)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
