package ru.vandrikeev.android.phrasebook.model.translations;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;
import io.requery.Persistable;
import io.requery.android.sqlite.DatabaseSource;
import io.requery.reactivex.ReactiveEntityStore;
import io.requery.reactivex.ReactiveSupport;
import io.requery.sql.Configuration;
import io.requery.sql.ConfigurationBuilder;
import io.requery.sql.EntityDataStore;
import io.requery.sql.TableCreationMode;
import ru.vandrikeev.android.phrasebook.BuildConfig;

/**
 * Repository for saved both recents and favorites translations.
 */
@Singleton
public class TranslationRepository {

    @NonNull
    private ReactiveEntityStore<Persistable> dataStore;

    @Inject
    public TranslationRepository(@NonNull Context context) {
        DatabaseSource source = new DatabaseSource(context, Models.DEFAULT, 1) {
            @Override
            protected void onConfigure(ConfigurationBuilder builder) {
                super.onConfigure(builder);
                builder.setQuoteColumnNames(true);
            }
        };
        if (BuildConfig.DEBUG) {
            // in development mode drops and recreates the tables on every upgrade
            source.setTableCreationMode(TableCreationMode.DROP_CREATE);
        }
        Configuration configuration = source.getConfiguration();
        this.dataStore = ReactiveSupport.toReactiveStore(new EntityDataStore<Persistable>(configuration));
    }

    // region History

    public Single<Translation> saveToRecents(@NonNull final String from,
                                             @NonNull final String to,
                                             @NonNull final String text,
                                             @NonNull final String translation) {
        final Translation Translation = new Translation();
        Translation.setLanguageFrom(from);
        Translation.setLanguageTo(to);
        Translation.setText(text);
        Translation.setTranslation(translation);
        Translation.setDate(new Date());

        return dataStore.insert(Translation);
    }

    @NonNull
    public Single<List<Translation>> getRecents() {
        final Observable<Translation> translationObservable = dataStore.select(Translation.class)
                .orderBy(Translation.DATE.desc())
                .get()
                .observable()
                .subscribeOn(Schedulers.computation());
        return collect(translationObservable)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Integer> clearHistory() {
        return dataStore.delete(Translation.class)
                .get()
                .single()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // endregion

    // region Favorites

    public Single<Integer> setFavorite(long id, boolean favorite) {
        return dataStore.update(Translation.class)
                .set(Translation.FAVORITE, favorite)
                .where(Translation.ID.eq(id))
                .get()
                .single()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    public Single<List<Translation>> getFavorites() {
        final Observable<Translation> translationObservable = dataStore.select(Translation.class)
                .where(Translation.FAVORITE.eq(true))
                .orderBy(Translation.DATE.desc())
                .get()
                .observable()
                .subscribeOn(Schedulers.computation());
        return collect(translationObservable)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Integer> clearFavorites() {
        return dataStore.delete(Translation.class)
                .where(Translation.FAVORITE.eq(true))
                .get()
                .single()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // endregion

    private Single<List<Translation>> collect(@NonNull Observable<Translation> observable) {
        return observable.collect(new Callable<List<Translation>>() {
                                      @Override
                                      public List<Translation> call() throws Exception {
                                          return new ArrayList<>();
                                      }
                                  },
                new BiConsumer<List<Translation>, Translation>() {
                    @Override
                    @SuppressWarnings("NullableProblems")
                    public void accept(@NonNull List<Translation> translations,
                                       @NonNull Translation translation) throws Exception {
                        translations.add(translation);
                    }
                });
    }
}
