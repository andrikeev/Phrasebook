package ru.vandrikeev.android.phrasebook.presentation.presenter.translation;

import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.SocketTimeoutException;

import ru.vandrikeev.android.phrasebook.AbstractTestWithServicesAndResources;
import ru.vandrikeev.android.phrasebook.model.languages.Language;
import ru.vandrikeev.android.phrasebook.model.network.YandexTranslateException;
import ru.vandrikeev.android.phrasebook.presentation.view.translation.LanguageSelectionView;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class LanguageSelectionPresenterTest extends AbstractTestWithServicesAndResources {

    @NonNull
    private LanguageSelectionPresenter presenter;

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void test_presenter_SetUpWidget() throws Exception {
        final LanguageSelectionView testView = mock(LanguageSelectionView.class);

        presenter = new LanguageSelectionPresenter(service, languageRepository);
        presenter.attachView(testView);

        verify(testView, times(1)).setUpWidget(
                getLanguages(),
                new Language("ru", "Русский"),
                new Language("en", "Английский"));
    }

    @Test
    public void test_presenter_GetLanguages_AUTO() {
        final LanguageSelectionView testView = mock(LanguageSelectionView.class);

        presenter = new LanguageSelectionPresenter(service, languageRepository);
        presenter.attachView(testView);
        presenter.loadSupportedLanguages(new Language("auto", "Autodetect"));

        verify(testView, times(1)).setUpWidget(
                getLanguages(),
                new Language("ru", "Русский"),
                new Language("en", "Английский"));
        verify(testView, times(1)).showLoading();
        verify(testView, times(1)).setModel(getLanguages());
        verify(testView, times(1)).showContent();
        verify(testView, times(0)).showError(Mockito.<Throwable>any());
    }

    @Test
    public void test_presenter_GetLanguages_RU() {
        final LanguageSelectionView testView = mock(LanguageSelectionView.class);

        presenter = new LanguageSelectionPresenter(service, languageRepository);
        presenter.attachView(testView);
        presenter.loadSupportedLanguages(russian);

        verify(testView, times(1)).setUpWidget(
                getLanguages(),
                new Language("ru", "Русский"),
                new Language("en", "Английский"));
        verify(testView, times(1)).showLoading();
        verify(testView, times(1)).setModel(getSupportedLanguages());
        verify(testView, times(1)).showContent();
        verify(testView, times(0)).showError(Mockito.<Throwable>any());
    }

    @Test
    public void test_presenter_GetLanguages_APIError() {
        final LanguageSelectionView testView = mock(LanguageSelectionView.class);

        presenter = new LanguageSelectionPresenter(service, languageRepository);
        presenter.attachView(testView);
        presenter.loadSupportedLanguages(apiErrorLanguageArg);

        verify(testView, times(1)).setUpWidget(
                getLanguages(),
                new Language("ru", "Русский"),
                new Language("en", "Английский"));
        verify(testView, times(1)).showLoading();
        verify(testView, times(0)).setModel(getSupportedLanguages());
        verify(testView, times(0)).showContent();
        verify(testView, times(1)).showError(Mockito.<YandexTranslateException>any());
    }

    @Test
    public void test_presenter_GetLanguages_NetworkError() {
        final LanguageSelectionView testView = mock(LanguageSelectionView.class);

        presenter = new LanguageSelectionPresenter(service, languageRepository);
        presenter.attachView(testView);
        presenter.loadSupportedLanguages(networkErrorLanguageArg);

        verify(testView, times(1)).setUpWidget(
                getLanguages(),
                new Language("ru", "Русский"),
                new Language("en", "Английский"));
        verify(testView, times(1)).showLoading();
        verify(testView, times(0)).setModel(getSupportedLanguages());
        verify(testView, times(0)).showContent();
        verify(testView, times(1)).showError(Mockito.<SocketTimeoutException>any());
    }
}
