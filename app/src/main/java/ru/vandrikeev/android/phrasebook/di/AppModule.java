package ru.vandrikeev.android.phrasebook.di;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Module for application context dependencies.
 */
@Module
final public class AppModule {

    @NonNull
    private Application application;

    public AppModule(@NonNull final Application application) {
        this.application = application;
    }

    /**
     * Provides application context.
     *
     * @return application context
     */
    @Provides
    @Singleton
    @NonNull
    public Context provideContext() {
        return application.getApplicationContext();
    }
}
