package ru.vandrikeev.android.phrasebook.presentation.presenter.translation;

import android.support.annotation.NonNull;

import org.junit.Before;

import ru.vandrikeev.android.phrasebook.AbstractTestWithServicesAndResources;

public class TranslationPresenterTest extends AbstractTestWithServicesAndResources {

    @NonNull
    private TranslationPresenter presenter;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        presenter = new TranslationPresenter(service, translationRepository, languageRepository);
    }
}
