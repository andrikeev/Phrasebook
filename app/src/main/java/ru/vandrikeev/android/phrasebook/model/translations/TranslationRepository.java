package ru.vandrikeev.android.phrasebook.model.translations;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

/**
 * Repository for saved both recents and favorites translations.
 */
@Singleton
public class TranslationRepository {

    @NonNull
    private ReactiveEntityStore<Persistable> dataStore;

    @Inject
    public TranslationRepository(@NonNull ReactiveEntityStore<Persistable> dataStore) {
        this.dataStore = dataStore;
    }

    // region History

    public Single<? extends HistoryTranslation> saveToRecents(@NonNull final Translation translation) {
        return dataStore.select(HistoryTranslationEntity.class)
                .where(HistoryTranslationEntity.TEXT.eq(translation.getText()).and(
                        HistoryTranslationEntity.TRANSLATION.eq(translation.getTranslation())).and(
                        HistoryTranslationEntity.LANGUAGE_FROM_CODE.eq(translation.getLanguageFrom().getCode())).and(
                        HistoryTranslationEntity.LANGUAGE_TO_CODE.eq(translation.getLanguageTo().getCode())).and(
                        HistoryTranslationEntity.FAVORITE.eq(true)
                ))
                .get()
                .observable()
                .singleOrError()
                .onErrorReturn(new Function<Throwable, HistoryTranslationEntity>() {
                    @Override
                    public HistoryTranslationEntity apply(@NonNull Throwable throwable) throws Exception {
                        return new HistoryTranslationEntity(translation);
                    }
                })
                .flatMap(new Function<HistoryTranslationEntity, SingleSource<? extends HistoryTranslation>>() {
                    @Override
                    public Single<? extends HistoryTranslation> apply(
                            @NonNull HistoryTranslationEntity historyTranslation) throws Exception {
                        historyTranslation.setTimestamp(new Date().getTime());
                        return dataStore.upsert(historyTranslation)
                                .subscribeOn(Schedulers.computation())
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                });
    }

    @NonNull
    public Single<? extends List<? extends HistoryTranslation>> getRecents() {
        final Observable<? extends HistoryTranslation> translationObservable =
                dataStore.select(HistoryTranslationEntity.class)
                        .orderBy(HistoryTranslationEntity.TIMESTAMP.desc())
                        .get()
                        .observable()
                        .subscribeOn(Schedulers.computation());
        return collect(translationObservable)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Integer> clearHistory() {
        return dataStore.delete(HistoryTranslationEntity.class)
                .get()
                .single()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // endregion

    // region Favorites

    public Single<? extends HistoryTranslation> setFavorite(@NonNull Translation translation, boolean favorite) {
        final HistoryTranslationEntity historyTranslation = new HistoryTranslationEntity(translation);
        historyTranslation.setFavorite(favorite);
        return dataStore.upsert(historyTranslation)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    public Single<? extends List<? extends HistoryTranslation>> getFavorites() {
        final Observable<? extends HistoryTranslation> translationObservable =
                dataStore.select(HistoryTranslationEntity.class)
                        .where(HistoryTranslationEntity.FAVORITE.eq(true))
                        .orderBy(HistoryTranslationEntity.TIMESTAMP.desc())
                        .get()
                        .observable()
                        .subscribeOn(Schedulers.computation());
        return collect(translationObservable)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Integer> clearFavorites() {
        return dataStore.delete(HistoryTranslationEntity.class)
                .where(HistoryTranslationEntity.FAVORITE.eq(true))
                .get()
                .single()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // endregion

    private <T> Single<List<T>> collect(@NonNull Observable<T> observable) {
        return observable.collect(
                new Callable<List<T>>() {
                    @Override
                    public List<T> call() throws Exception {
                        return new ArrayList<>();
                    }
                },
                new BiConsumer<List<T>, T>() {
                    @Override
                    @SuppressWarnings("NullableProblems")
                    public void accept(@NonNull List<T> items,
                                       @NonNull T item) throws Exception {
                        items.add(item);
                    }
                });
    }
}
