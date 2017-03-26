package ru.vandrikeev.android.phrasebook.di;

import javax.inject.Singleton;

import dagger.Component;
import ru.vandrikeev.android.phrasebook.App;
import ru.vandrikeev.android.phrasebook.Settings;
import ru.vandrikeev.android.phrasebook.presentation.presenter.translation.LanguageSelectionPresenter;
import ru.vandrikeev.android.phrasebook.presentation.presenter.translation.TranslationPresenter;

/**
 * Component for dependencies injection.
 */
@Singleton
@Component(modules = {AppModule.class, NetworkModule.class})
public interface AppComponent {

    void inject(App application);

    Settings getSettings();

    LanguageSelectionPresenter getLanguageSelectionPresenter();

    TranslationPresenter getTranslationPresenter();

    //HistoryPresenter getHistoryPresenter();

    //FavoritesPresenter getFavoritesPresenter();
}
