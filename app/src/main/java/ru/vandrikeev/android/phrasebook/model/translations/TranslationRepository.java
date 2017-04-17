package ru.vandrikeev.android.phrasebook.model.translations;

import android.support.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Repository for saved both recent and favorite translations.
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

    /**
     * Saves translation to DB. First tries to load existed favorite translation, to prevent overriding of favorite
     * state. Then uses {@link ReactiveEntityStore#upsert} to save or update entity. Nonblocking.
     *
     * @param translation translation to be saved
     * @return {@link Single} with saved entity observable on Android main thread
     */
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
                .subscribeOn(Schedulers.io())
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
                        return dataStore.upsert(historyTranslation);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Asynchronously loads all recent translations ordered by timestamp descending. Nonblocking.
     *
     * @return {@link Single} with list of recent translation observable on Android main thread
     */
    @NonNull
    public Single<? extends List<? extends HistoryTranslation>> getRecents() {
        final Observable<? extends HistoryTranslation> translationObservable =
                dataStore.select(HistoryTranslationEntity.class)
                        .orderBy(HistoryTranslationEntity.TIMESTAMP.desc())
                        .get()
                        .observable()
                        .subscribeOn(Schedulers.io());
        return collect(translationObservable)
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Asynchronously deletes all saved translations from DB. Nonblocking.
     *
     * @return {@link Single} with result of deletion observable on Android main thread
     */
    public Single<Integer> clearHistory() {
        return dataStore.delete(HistoryTranslationEntity.class)
                .get()
                .single()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // endregion

    // region Favorites

    /**
     * Saves translation to DB with given favorite status. Uses {@link ReactiveEntityStore#upsert} to save or update
     * entity. Nonblocking.
     *
     * @param translation translation to be saved
     * @return {@link Single} with saved entity observable on Android main thread
     */
    public Single<? extends HistoryTranslation> setFavorite(@NonNull Translation translation, boolean favorite) {
        final HistoryTranslationEntity historyTranslation = new HistoryTranslationEntity(translation);
        historyTranslation.setFavorite(favorite);
        return dataStore.upsert(historyTranslation)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Asynchronously loads all favorite translations ordered by timestamp descending. Nonblocking.
     *
     * @return {@link Single} with list of favorite translation observable on Android main thread
     */
    @NonNull
    public Single<? extends List<? extends HistoryTranslation>> getFavorites() {
        final Observable<? extends HistoryTranslation> translationObservable =
                dataStore.select(HistoryTranslationEntity.class)
                        .where(HistoryTranslationEntity.FAVORITE.eq(true))
                        .orderBy(HistoryTranslationEntity.TIMESTAMP.desc())
                        .get()
                        .observable()
                        .subscribeOn(Schedulers.io());
        return collect(translationObservable)
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Asynchronously deletes all favorite translations from DB. Nonblocking.
     *
     * @return {@link Single} with result of deletion observable on Android main thread
     */
    public Single<Integer> clearFavorites() {
        return dataStore.delete(HistoryTranslationEntity.class)
                .where(HistoryTranslationEntity.FAVORITE.eq(true))
                .get()
                .single()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // endregion

    /**
     * Transform {@link Observable} items to {@link Single} with list of items.
     *
     * @param observable observable
     * @param <T>        item type
     * @return single
     */
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
