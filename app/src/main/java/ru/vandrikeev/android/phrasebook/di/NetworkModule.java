package ru.vandrikeev.android.phrasebook.di;

import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.vandrikeev.android.phrasebook.model.network.YandexTranslateApi;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Module for network dependencies.
 */
@Module
final public class NetworkModule {

    /**
     * Provides OkHttpClient for http requests with cache.
     *
     * @param cache filesystem cache storage
     * @return http client
     */
    @Provides
    @Singleton
    @NonNull
    public OkHttpClient provideOkHttpClient(@NonNull final Cache cache) {
        return new OkHttpClient.Builder()
                .cache(cache)
                .readTimeout(20L, SECONDS)
                .build();
    }

    /**
     * Provides filesystem cache of 10Mb in {@link Context#getCacheDir()}
     *
     * @param context application context
     * @return filesystem cache of 10Mb in {@link Context#getCacheDir()}
     */
    @Provides
    @Singleton
    @NonNull
    public Cache provideCache(@NonNull final Context context) {
        return new Cache(context.getCacheDir(), 10 * 1024 * 1024);
    }

    /**
     * Provides retrofit implementation of {@link YandexTranslateApi}
     *
     * @param okHttpClient http client
     * @return retrofit api implementation
     */
    @Provides
    @Singleton
    @NonNull
    public YandexTranslateApi provideApi(@NonNull final OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl("https://translate.yandex.net/api/v1.5/tr.json/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
                .create(YandexTranslateApi.class);
    }
}
