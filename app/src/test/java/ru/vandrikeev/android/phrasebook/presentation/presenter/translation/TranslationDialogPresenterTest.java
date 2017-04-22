package ru.vandrikeev.android.phrasebook.presentation.presenter.translation;

import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import ru.vandrikeev.android.phrasebook.AbstractTestWithServicesAndResources;
import ru.vandrikeev.android.phrasebook.model.translations.Translation;
import ru.vandrikeev.android.phrasebook.presentation.view.translation.TranslationDialogView;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TranslationDialogPresenterTest extends AbstractTestWithServicesAndResources {

    @NonNull
    private TranslationDialogPresenter presenter;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        presenter = new TranslationDialogPresenter(service, languageRepository);
    }

    @Test
    public void test_translate_Ok() {
        final TranslationDialogView testView = mock(TranslationDialogView.class);

        presenter.attachView(testView);
        presenter.translate("Hello world", english, russian);

        verify(testView, Mockito.times(1)).showTranslation(any(Translation.class));
    }

    @Test
    public void test_translate_FailedTranslation() {
        final TranslationDialogView testView = mock(TranslationDialogView.class);

        presenter.attachView(testView);
        presenter.translate("%#@", english, apiErrorLanguageArg);

        verify(testView, Mockito.times(0)).showTranslation(any(Translation.class));
    }

    @Test
    public void test_translate_FailedDetection() {
        final TranslationDialogView testView = mock(TranslationDialogView.class);

        presenter.attachView(testView);
        presenter.translate("%#@", english, russian);

        verify(testView, Mockito.times(1)).showTranslation(any(Translation.class));
    }
}
