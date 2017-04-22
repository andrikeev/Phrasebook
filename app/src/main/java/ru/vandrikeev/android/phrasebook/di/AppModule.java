package ru.vandrikeev.android.phrasebook.di;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.requery.Persistable;
import io.requery.android.sqlite.DatabaseSource;
import io.requery.reactivex.ReactiveEntityStore;
import io.requery.reactivex.ReactiveSupport;
import io.requery.sql.ConfigurationBuilder;
import io.requery.sql.EntityDataStore;
import ru.vandrikeev.android.phrasebook.model.translations.Models;

/**
 * Module for application context dependencies.
 */
@Module
final public class AppModule {

    private static final String DB_NAME = "history";

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

    /**
     * Provides {@link ReactiveEntityStore} for accessing DB in a reactive way.
     *
     * @return Rx data store
     */
    @Provides
    @Singleton
    @NonNull
    public ReactiveEntityStore<Persistable> provideDataStore() {
        final DatabaseSource source = new DatabaseSource(application, Models.DEFAULT, DB_NAME, 1) {
            @Override
            protected void onConfigure(ConfigurationBuilder builder) {
                super.onConfigure(builder);
                builder.setQuoteColumnNames(true);
            }
        };
        return ReactiveSupport.toReactiveStore(new EntityDataStore<Persistable>(source.getConfiguration()));
    }
}
