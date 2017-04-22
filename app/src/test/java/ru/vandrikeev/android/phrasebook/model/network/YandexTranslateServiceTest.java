package ru.vandrikeev.android.phrasebook.model.network;

import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.concurrent.Callable;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import ru.vandrikeev.android.phrasebook.AbstractTestWithResources;
import ru.vandrikeev.android.phrasebook.BuildConfig;
import ru.vandrikeev.android.phrasebook.model.languages.Language;
import ru.vandrikeev.android.phrasebook.model.responses.SupportedLanguages;
import ru.vandrikeev.android.phrasebook.model.responses.TranslationResponse;

import static io.reactivex.android.plugins.RxAndroidPlugins.setInitMainThreadSchedulerHandler;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class YandexTranslateServiceTest extends AbstractTestWithResources {

    @Mock
    @NonNull
    private YandexTranslateApi api;

    @NonNull
    private YandexTranslateService service;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        initMocks(this);
        setInitMainThreadSchedulerHandler(new Function<Callable<Scheduler>, Scheduler>() {
            @Override
            public Scheduler apply(Callable<Scheduler> schedulerCallable) throws Exception {
                return Schedulers.trampoline();
            }
        });

        when(api.getSupportedLanguages(BuildConfig.API_KEY, russian.getCode()))
                .thenReturn(Single.just(getSupportedLanguagesOk()));
        when(api.getSupportedLanguages(BuildConfig.API_KEY, apiErrorLanguageArg.getCode()))
                .thenReturn(Single.just(getSupportedLanguagesError()));
        when(api.getSupportedLanguages(BuildConfig.API_KEY, networkErrorLanguageArg.getCode()))
                .thenReturn(Single.<SupportedLanguages>error(networkError));

        when(api.translate(BuildConfig.API_KEY, "Hello World", russian.getCode()))
                .thenReturn(Single.just(getTranslationOk()));
        when(api.translate(BuildConfig.API_KEY, "Hello World", apiErrorLanguageArg.getCode()))
                .thenReturn(Single.just(getTranslationError()));
        when(api.translate(BuildConfig.API_KEY, "Hello World", networkErrorLanguageArg.getCode()))
                .thenReturn(Single.<TranslationResponse>error(networkError));

        service = new YandexTranslateService(api);
    }

    @Test
    public void test_getSupportedLanguages_Ok() throws Exception {
        service.getSupportedLanguages(russian)
                .subscribe(
                        new Consumer<SupportedLanguages>() {
                            @Override
                            public void accept(SupportedLanguages supportedLanguages) throws Exception {
                                assertNotNull(supportedLanguages);
                                assertEquals(200, supportedLanguages.getCode());
                                assertEquals(getSupportedLanguages().size(), supportedLanguages.getSupportedLanguages().size());
                                for (Language language : getSupportedLanguages()) {
                                    assertTrue(supportedLanguages.getSupportedLanguages().contains(language));
                                }
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                fail("This should not be called");
                            }
                        });
    }

    @Test
    public void test_getSupportedLanguages_APIError() throws Exception {
        service.getSupportedLanguages(apiErrorLanguageArg)
                .subscribe(
                        new Consumer<SupportedLanguages>() {
                            @Override
                            public void accept(SupportedLanguages supportedLanguages) throws Exception {
                                assertNotNull(supportedLanguages);
                                assertEquals(401, supportedLanguages.getCode());
                                assertEquals(0, supportedLanguages.getSupportedLanguages().size());
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                fail("This should not be called");
                            }
                        });
    }

    @Test
    public void test_getSupportedLanguages_NetworkError() throws Exception {
        service.getSupportedLanguages(networkErrorLanguageArg)
                .subscribe(
                        new Consumer<SupportedLanguages>() {
                            @Override
                            public void accept(SupportedLanguages supportedLanguages) throws Exception {
                                fail("This should not be called");
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                assertEquals(networkError.getClass(), throwable.getClass());
                            }
                        });
    }

    @Test
    public void test_translate_Ok() throws Exception {
        final String text = "Hello World";
        final String translation = "Привет Мир";
        final String direction = "en-ru";

        service.translate(new Language("auto", "Detect language"), russian, text)
                .subscribe(
                        new Consumer<TranslationResponse>() {
                            @Override
                            public void accept(TranslationResponse translationResponse) throws Exception {
                                assertNotNull(translationResponse);
                                assertEquals(200, translationResponse.getCode());
                                assertEquals(translation, translationResponse.getText());
                                assertEquals(direction, translationResponse.getTranslationDirection());
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
    public void test_translate_APIError() throws Exception {
        service.translate(new Language("auto", "Detect language"), apiErrorLanguageArg, "Hello World")
                .subscribe(
                        new Consumer<TranslationResponse>() {
                            @Override
                            public void accept(TranslationResponse translationResponse) throws Exception {
                                assertNotNull(translationResponse);
                                assertEquals(502, translationResponse.getCode());
                                assertEquals("", translationResponse.getText());
                                assertEquals("", translationResponse.getTranslationDirection());
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
    public void test_translate_NetworkError() throws Exception {
        service.translate(new Language("auto", "Detect language"), networkErrorLanguageArg, "Hello World")
                .subscribe(
                        new Consumer<TranslationResponse>() {
                            @Override
                            public void accept(TranslationResponse translationResponse) throws Exception {
                                fail("This should not be called");
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                assertEquals(networkError.getClass(), throwable.getClass());
                            }
                        }
                );
    }
}
