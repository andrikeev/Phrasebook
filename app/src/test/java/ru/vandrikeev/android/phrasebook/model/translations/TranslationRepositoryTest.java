package ru.vandrikeev.android.phrasebook.model.translations;

import android.database.sqlite.SQLiteException;
import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.requery.Persistable;
import io.requery.query.Condition;
import io.requery.query.Deletion;
import io.requery.query.Expression;
import io.requery.query.Limit;
import io.requery.query.Selection;
import io.requery.query.WhereAndOr;
import io.requery.reactivex.ReactiveEntityStore;
import io.requery.reactivex.ReactiveResult;
import io.requery.reactivex.ReactiveScalar;
import ru.vandrikeev.android.phrasebook.model.languages.Language;

import static io.reactivex.android.plugins.RxAndroidPlugins.setInitMainThreadSchedulerHandler;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TranslationRepositoryTest {


    @Mock
    @NonNull
    private ReactiveEntityStore<Persistable> dataStore;

    @Mock
    @NonNull
    private ReactiveResult<HistoryTranslationEntity> result;

    @NonNull
    private TranslationRepository repository;

    @NonNull
    private final Translation translation = new Translation(
            "hello",
            "привет",
            new Language("en", "English"),
            new Language("en", "English"),
            new Language("ru", "Russian"));

    @NonNull
    private final Translation favoriteTranslation = new Translation(
            "world",
            "мир",
            new Language("en", "English"),
            new Language("en", "English"),
            new Language("ru", "Russian"));

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        Schedulers.trampoline();
        setInitMainThreadSchedulerHandler(new Function<Callable<Scheduler>, Scheduler>() {
            @Override
            public Scheduler apply(Callable<Scheduler> schedulerCallable) throws Exception {
                return Schedulers.trampoline();
            }
        });

        final Selection<ReactiveResult<HistoryTranslationEntity>> selection = mock(Selection.class);
        final WhereAndOr<ReactiveResult<HistoryTranslationEntity>> whereAndOr = mock(WhereAndOr.class);
        final Limit<ReactiveResult<HistoryTranslationEntity>> limit = mock(Limit.class);

        //TODO: mock data store
        when(dataStore.select(HistoryTranslationEntity.class)).thenReturn(selection);
        when(selection.where(Mockito.<Condition<?, ?>>any())).thenReturn(whereAndOr);
        when(whereAndOr.get()).thenReturn(result);
        when(whereAndOr.orderBy(Mockito.<Expression<?>>any())).thenReturn(limit);
        when(selection.orderBy(Mockito.<Expression<?>>any())).thenReturn(limit);
        when(limit.get()).thenReturn(result);

        repository = new TranslationRepository(dataStore);
    }

    @Test
    public void test_saveToRecents_NotFavorite() throws Exception {
        when(result.observable()).thenReturn(Observable.<HistoryTranslationEntity>empty());

        repository.saveToRecents(translation)
                .subscribe(
                        new Consumer<HistoryTranslation>() {
                            @Override
                            public void accept(HistoryTranslation historyTranslation) throws Exception {
                                assertNotNull(historyTranslation);
                                assertEquals(translation.getText(), historyTranslation.getText());
                                assertEquals(translation.getTranslation(), historyTranslation.getTranslation());
                                assertEquals(translation.getLanguageFrom().getCode(), historyTranslation.getLanguageFrom());
                                assertEquals(translation.getLanguageTo().getCode(), historyTranslation.getLanguageTo());
                                assertFalse(historyTranslation.favorite);
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                fail("This should not be called");
                            }
                        }
                );
    }

    @Test
    public void test_saveToRecents_Favorite() throws Exception {
        final HistoryTranslationEntity translationEntity = new HistoryTranslationEntity(favoriteTranslation);
        when(result.observable()).thenReturn(Observable.just(translationEntity));

        repository.saveToRecents(favoriteTranslation)
                .subscribe(
                        new Consumer<HistoryTranslation>() {
                            @Override
                            public void accept(HistoryTranslation historyTranslation) throws Exception {
                                assertNotNull(historyTranslation);
                                assertEquals(favoriteTranslation.getText(), historyTranslation.getText());
                                assertEquals(favoriteTranslation.getTranslation(), historyTranslation.getTranslation());
                                assertEquals(favoriteTranslation.getLanguageFrom().getCode(), historyTranslation.getLanguageFrom());
                                assertEquals(favoriteTranslation.getLanguageTo().getCode(), historyTranslation.getLanguageTo());
                                assertTrue(historyTranslation.favorite);
                                assertTrue(historyTranslation.timestamp > translationEntity.timestamp);
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                fail("This should not be called");
                            }
                        }
                );
    }

    @Test
    public void test_saveToRecents_Error() throws Exception {
        when(result.observable()).thenReturn(Observable.<HistoryTranslationEntity>error(new SQLiteException()));

        repository.saveToRecents(translation)
                .subscribe(
                        new Consumer<HistoryTranslation>() {
                            @Override
                            public void accept(HistoryTranslation historyTranslation) throws Exception {
                                fail("This should not be called");
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                assertEquals(SQLiteException.class, throwable.getClass());
                            }
                        }
                );
    }

    @Test
    public void test_getRecents() throws Exception {
        final HistoryTranslationEntity[] translations = new HistoryTranslationEntity[]{
                new HistoryTranslationEntity(translation),
                new HistoryTranslationEntity(favoriteTranslation)

        };
        when(result.observable()).thenReturn(Observable.fromArray(translations));

        repository.getRecents()
                .subscribe(
                        new Consumer<List<? extends HistoryTranslation>>() {
                            @Override
                            public void accept(List<? extends HistoryTranslation> historyTranslations) throws Exception {
                                assertNotNull(historyTranslations);
                                assertEquals(translations.length, historyTranslations.size());
                                for (HistoryTranslationEntity translationEntity : translations) {
                                    historyTranslations.contains(translationEntity);
                                }
                            }
                        },

                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                fail("This should not be called");
                            }
                        }
                );
    }

    @Test
    public void test_getRecents_Empty() throws Exception {
        when(result.observable()).thenReturn(Observable.<HistoryTranslationEntity>empty());

        repository.getRecents()
                .subscribe(
                        new Consumer<List<? extends HistoryTranslation>>() {
                            @Override
                            public void accept(List<? extends HistoryTranslation> historyTranslations) throws Exception {
                                assertNotNull(historyTranslations);
                                assertEquals(0, historyTranslations.size());
                            }
                        },

                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                fail("This should not be called");
                            }
                        }
                );
    }

    @Test
    public void test_getRecents_Error() throws Exception {
        when(result.observable()).thenReturn(Observable.<HistoryTranslationEntity>error(new SQLiteException()));

        repository.getRecents()
                .subscribe(
                        new Consumer<List<? extends HistoryTranslation>>() {
                            @Override
                            public void accept(List<? extends HistoryTranslation> historyTranslations) throws Exception {
                                fail("This should not be called");
                            }
                        },

                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                assertEquals(SQLiteException.class, throwable.getClass());
                            }
                        }
                );
    }

    @Test
    public void test_clearHistory() throws Exception {
        final Deletion<ReactiveScalar<Integer>> deletion = mock(Deletion.class);
        final ReactiveScalar<Integer> scalar = mock(ReactiveScalar.class);

        when(dataStore.delete(HistoryTranslationEntity.class)).thenReturn(deletion);
        when(deletion.get()).thenReturn(scalar);
        when(scalar.single()).thenReturn(Single.just(1));

        repository.clearHistory()
                .subscribe(
                        new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) throws Exception {
                                assertEquals(Integer.valueOf(1), integer);
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                fail("This should not be called");
                            }
                        }
                );
    }

    @Test
    public void test_setFavorite_Success() throws Exception {
        when(dataStore.upsert(any(HistoryTranslationEntity.class)))
                .thenReturn(Single.just(new HistoryTranslationEntity(translation)));

        repository.setFavorite(translation, true)
                .subscribe(
                        new Consumer<HistoryTranslation>() {
                            @Override
                            public void accept(HistoryTranslation historyTranslation) throws Exception {
                                assertNotNull(historyTranslation);
                                assertEquals(translation.getText(), historyTranslation.getText());
                                assertEquals(translation.getTranslation(), historyTranslation.getTranslation());
                                assertEquals(translation.getLanguageFrom().getCode(), historyTranslation.getLanguageFrom());
                                assertEquals(translation.getLanguageTo().getCode(), historyTranslation.getLanguageTo());
                                assertTrue(historyTranslation.favorite);
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                fail("This should not be called");
                            }
                        }
                );
    }

    @Test
    public void test_setFavorite_Error() throws Exception {
        when(dataStore.upsert(any(HistoryTranslationEntity.class)))
                .thenReturn(Single.<HistoryTranslationEntity>error(new SQLiteException()));

        repository.setFavorite(translation, true)
                .subscribe(
                        new Consumer<HistoryTranslation>() {
                            @Override
                            public void accept(HistoryTranslation historyTranslation) throws Exception {
                                fail("This should not be called");
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                assertEquals(SQLiteException.class, throwable.getClass());
                            }
                        }
                );
    }

    @Test
    public void test_getFavorites() throws Exception {
        final HistoryTranslationEntity[] translations = new HistoryTranslationEntity[]{
                new HistoryTranslationEntity(favoriteTranslation)

        };
        when(result.observable()).thenReturn(Observable.fromArray(translations));

        repository.getFavorites()
                .subscribe(
                        new Consumer<List<? extends HistoryTranslation>>() {
                            @Override
                            public void accept(List<? extends HistoryTranslation> historyTranslations) throws Exception {
                                assertNotNull(historyTranslations);
                                assertEquals(translations.length, historyTranslations.size());
                                for (HistoryTranslationEntity translationEntity : translations) {
                                    historyTranslations.contains(translationEntity);
                                }
                            }
                        },

                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                fail("This should not be called");
                            }
                        }
                );
    }

    @Test
    public void test_getFavorites_Empty() throws Exception {
        when(result.observable()).thenReturn(Observable.<HistoryTranslationEntity>empty());

        repository.getFavorites()
                .subscribe(
                        new Consumer<List<? extends HistoryTranslation>>() {
                            @Override
                            public void accept(List<? extends HistoryTranslation> historyTranslations) throws Exception {
                                assertNotNull(historyTranslations);
                                assertEquals(0, historyTranslations.size());
                            }
                        },

                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                fail("This should not be called");
                            }
                        }
                );
    }

    @Test
    public void test_getFavorites_Error() throws Exception {
        when(result.observable()).thenReturn(Observable.<HistoryTranslationEntity>error(new SQLiteException()));

        repository.getFavorites()
                .subscribe(
                        new Consumer<List<? extends HistoryTranslation>>() {
                            @Override
                            public void accept(List<? extends HistoryTranslation> historyTranslations) throws Exception {
                                fail("This should not be called");
                            }
                        },

                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                assertEquals(SQLiteException.class, throwable.getClass());
                            }
                        }
                );
    }

    @Test
    public void test_clearFavorites() throws Exception {
        final Deletion<ReactiveScalar<Integer>> deletion = mock(Deletion.class);
        final WhereAndOr<ReactiveScalar<Integer>> whereAndOr = mock(WhereAndOr.class);
        final ReactiveScalar<Integer> scalar = mock(ReactiveScalar.class);

        when(dataStore.delete(HistoryTranslationEntity.class)).thenReturn(deletion);
        when(deletion.where(Mockito.<Condition<?, ?>>any())).thenReturn(whereAndOr);
        when(whereAndOr.get()).thenReturn(scalar);
        when(scalar.single()).thenReturn(Single.just(1));

        repository.clearFavorites()
                .subscribe(
                        new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) throws Exception {
                                assertEquals(Integer.valueOf(1), integer);
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                fail("This should not be called");
                            }
                        }
                );
    }
}