package ru.vandrikeev.android.phrasebook.presentation.presenter.history;

import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import ru.vandrikeev.android.phrasebook.AbstractTestWithServicesAndResources;
import ru.vandrikeev.android.phrasebook.model.translations.HistoryTranslation;
import ru.vandrikeev.android.phrasebook.presentation.presenter.favorites.FavoritesPresenter;
import ru.vandrikeev.android.phrasebook.presentation.view.history.TranslationListView;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class FavoritesPresenterTest extends AbstractTestWithServicesAndResources {

    @NonNull
    private FavoritesPresenter presenter;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        presenter = new FavoritesPresenter(translationRepository);
    }

    @Test
    public void test_attachView() throws Exception {
        final TranslationListView testView = Mockito.mock(TranslationListView.class);

        presenter.attachView(testView);
        verify(testView, times(1)).showLoading();
        verify(testView, times(1)).setModel(Mockito.<HistoryTranslation>anyList());
        verify(testView, times(1)).showContent();
        verify(testView, times(0)).showError(Mockito.<Exception>any());
        verify(testView, times(0)).clearContent();
        verify(testView, times(0)).scrollToTop();
        verify(testView, times(0)).showEmpty();
    }

    @Test
    public void test_clear() throws Exception {
        final TranslationListView testView = Mockito.mock(TranslationListView.class);

        presenter.attachView(testView);
        presenter.clear();
        verify(testView, times(2)).showLoading();
        verify(testView, times(1)).setModel(Mockito.<HistoryTranslation>anyList());
        verify(testView, times(1)).showContent();
        verify(testView, times(0)).showError(Mockito.<Exception>any());
        verify(testView, times(1)).clearContent();
        verify(testView, times(0)).scrollToTop();
        verify(testView, times(1)).showEmpty();
    }
}