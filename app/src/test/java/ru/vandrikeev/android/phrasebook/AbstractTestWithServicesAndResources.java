package ru.vandrikeev.android.phrasebook;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import org.junit.Before;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import ru.vandrikeev.android.phrasebook.model.languages.Language;
import ru.vandrikeev.android.phrasebook.model.languages.LanguageRepository;
import ru.vandrikeev.android.phrasebook.model.network.YandexTranslateService;
import ru.vandrikeev.android.phrasebook.model.responses.SupportedLanguages;
import ru.vandrikeev.android.phrasebook.model.responses.TranslationResponse;
import ru.vandrikeev.android.phrasebook.model.translations.HistoryTranslationEntity;
import ru.vandrikeev.android.phrasebook.model.translations.Translation;
import ru.vandrikeev.android.phrasebook.model.translations.TranslationRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public abstract class AbstractTestWithServicesAndResources extends AbstractTestWithResources {

    @Mock
    @NonNull
    protected YandexTranslateService service;

    @Mock
    @NonNull
    protected LanguageRepository languageRepository;

    @Mock
    @NonNull
    protected TranslationRepository translationRepository;

    @NonNull
    protected final Translation translation = new Translation(
            "hello",
            "привет",
            new Language("en", "English"),
            new Language("en", "English"),
            new Language("ru", "Russian"));

    @NonNull
    protected final Translation favoriteTranslation = new Translation(
            "world",
            "мир",
            new Language("en", "English"),
            new Language("en", "English"),
            new Language("ru", "Russian"));

    @Before
    public void setUp() throws Exception {
        super.setUp();

        initMocks(this);

        when(service.getSupportedLanguages(russian))
                .thenReturn(Single.just(getSupportedLanguagesOk()));
        when(service.getSupportedLanguages(apiErrorLanguageArg))
                .thenReturn(Single.just(getSupportedLanguagesError()));
        when(service.getSupportedLanguages(networkErrorLanguageArg))
                .thenReturn(Single.<SupportedLanguages>error(new SocketTimeoutException()));

        when(service.translate(any(Language.class), ArgumentMatchers.eq(russian), anyString()))
                .thenReturn(Single.just(getTranslationOk()));
        when(service.translate(any(Language.class), ArgumentMatchers.eq(apiErrorLanguageArg), anyString()))
                .thenReturn(Single.just(getTranslationError()));
        when(service.translate(any(Language.class), ArgumentMatchers.eq(networkErrorLanguageArg), anyString()))
                .thenReturn(Single.<TranslationResponse>error(new SocketTimeoutException()));

        when(service.detectLanguage("Hello world"))
                .thenReturn(Single.just(getDetectedLangOk()));
        when(service.detectLanguage("%#@"))
                .thenReturn(Single.just(getDetectedLangFailed()));

        when(languageRepository.getLanguages()).thenReturn(getLanguages());
        when(languageRepository.getLanguageByCode("en")).thenReturn(english);
        when(languageRepository.getDirection("en-ru")).thenReturn(new Pair<>(english, russian));
        when(languageRepository.getLocalizedLanguages(getSupportedLanguages())).thenReturn(getSupportedLanguages());
        when(languageRepository.getSavedLanguageFrom()).thenReturn(russian);
        when(languageRepository.getSavedLanguageTo()).thenReturn(english);

        when(translationRepository.clearHistory()).thenReturn(Single.just(2));
        when(translationRepository.clearFavorites()).thenReturn(Single.just(1));

        final List<HistoryTranslationEntity> recents = new ArrayList<>();
        recents.add(new HistoryTranslationEntity(translation));
        recents.add(new HistoryTranslationEntity(favoriteTranslation));

        final List<HistoryTranslationEntity> favorites = new ArrayList<>();
        favorites.add(new HistoryTranslationEntity(favoriteTranslation));

        // cannot use when-then because of wildcards in return types
        doReturn(Single.just(new HistoryTranslationEntity(translation)))
                .when(translationRepository).saveToRecents(translation);
        doReturn(Single.just(recents))
                .when(translationRepository).getRecents();
        doReturn(Single.just(new HistoryTranslationEntity(translation)))
                .when(translationRepository).setFavorite(translation, true);
        doReturn(Single.just(favorites))
                .when(translationRepository).getFavorites();
    }
}
