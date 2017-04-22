package ru.vandrikeev.android.phrasebook.model.network;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.vandrikeev.android.phrasebook.model.responses.DetectedLanguage;
import ru.vandrikeev.android.phrasebook.model.responses.SupportedLanguages;
import ru.vandrikeev.android.phrasebook.model.responses.TranslationResponse;

/**
 * Yandex.Translate API for Retrofit2.
 *
 * @see <a href="https://tech.yandex.com/translate/">https://tech.yandex.com/translate/</a>
 */
public interface YandexTranslateApi {

    @GET("getLangs")
    Single<SupportedLanguages> getSupportedLanguages(@Query("key") String apiKey, @Query("ui") String languageCode);

    @GET("translate")
    Single<TranslationResponse> translate(@Query("key") String apiKey,
                                          @Query("text") String text,
                                          @Query("lang") String translationDirection);

    @GET("detect")
    Single<DetectedLanguage> detectLanguage(@Query("key") String apiKey, @Query("text") String testText);
}
