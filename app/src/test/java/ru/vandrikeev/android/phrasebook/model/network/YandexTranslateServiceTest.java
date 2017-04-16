package ru.vandrikeev.android.phrasebook.model.network;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import ru.vandrikeev.android.phrasebook.BuildConfig;
import ru.vandrikeev.android.phrasebook.model.languages.Language;
import ru.vandrikeev.android.phrasebook.model.responses.SupportedLanguages;
import ru.vandrikeev.android.phrasebook.model.responses.TranslationResponse;

import static io.reactivex.android.plugins.RxAndroidPlugins.setInitMainThreadSchedulerHandler;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@SuppressWarnings("NullableProblems")
public class YandexTranslateServiceTest {

    @Mock
    @NonNull
    private YandexTranslateApi api;

    @NonNull
    private YandexTranslateService service;

    private InputStreamReader readTestResource(String fileName) throws FileNotFoundException {
        return new InputStreamReader(this.getClass().getResourceAsStream(fileName));
    }

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        setInitMainThreadSchedulerHandler(new Function<Callable<Scheduler>, Scheduler>() {
            @Override
            public Scheduler apply(Callable<Scheduler> schedulerCallable) throws Exception {
                return Schedulers.trampoline();
            }
        });

        final Gson gson = new Gson();
        final Type supportedLanguagesType = new TypeToken<SupportedLanguages>() {
        }.getType();
        final Object o1 = gson.fromJson(readTestResource("supported_langs_response_ok.json"), supportedLanguagesType);
        final SupportedLanguages supportedLanguages = (SupportedLanguages) o1;
        final Type translationResponseType = new TypeToken<TranslationResponse>() {
        }.getType();
        final Object o2 = gson.fromJson(readTestResource("translation_response_ok.json"), translationResponseType);
        final TranslationResponse translationResponse = (TranslationResponse) o2;


        when(api.getSupportedLanguages(BuildConfig.API_KEY, "ru")).thenReturn(new Single<SupportedLanguages>() {
            @Override
            protected void subscribeActual(SingleObserver<? super SupportedLanguages> observer) {
                observer.onSuccess(supportedLanguages);
            }
        });
        when(api.translate(BuildConfig.API_KEY, "Hello World", "ru")).thenReturn(new Single<TranslationResponse>() {
            @Override
            protected void subscribeActual(SingleObserver<? super TranslationResponse> observer) {
                observer.onSuccess(translationResponse);
            }
        });

        service = new YandexTranslateService(api);
    }

    @Test
    public void test_getSupportedLanguages_Ok() throws Exception {
        final List<Language> expected = new ArrayList<>();
        expected.add(new Language("be", "Белорусский"));
        expected.add(new Language("de", "Немецкий"));
        expected.add(new Language("en", "Английский"));
        expected.add(new Language("es", "Испанский"));
        expected.add(new Language("fr", "Французский"));
        expected.add(new Language("ru", "Русский"));
        expected.add(new Language("zh", "Китайский"));

        service.getSupportedLanguages(new Language("ru", "Russian"))
                .subscribe(
                        new Consumer<SupportedLanguages>() {
                            @Override
                            public void accept(SupportedLanguages supportedLanguages) throws Exception {
                                assertNotNull(supportedLanguages);
                                assertEquals(200, supportedLanguages.getCode());
                                assertEquals(expected.size(), supportedLanguages.getSupportedLanguages().size());
                                for (Language language : expected) {
                                    assertTrue(supportedLanguages.getSupportedLanguages().contains(language));
                                }
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                throw new Exception("This should not be called");
                            }
                        });
    }
}
