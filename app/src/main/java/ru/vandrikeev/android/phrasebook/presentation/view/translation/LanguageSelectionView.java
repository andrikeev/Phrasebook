package ru.vandrikeev.android.phrasebook.presentation.view.translation;

import android.support.annotation.NonNull;

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

import ru.vandrikeev.android.phrasebook.model.languages.Language;
import ru.vandrikeev.android.phrasebook.presentation.view.LceView;

/**
 * Interface for part of translation screen with language choosing controls.
 */
@StateStrategyType(AddToEndSingleStrategy.class)
public interface LanguageSelectionView extends LceView<List<Language>> {

    /**
     * Set up initial state of controls.
     *
     * @param languages              list of ALL available languages including {@link Language#AUTODETECT}
     * @param languageFromPreference last selected language from or default
     * @param languageToPreference   last selected language to or default
     * @see ru.vandrikeev.android.phrasebook.model.languages.LanguageRepository#getSavedLanguageFrom()
     * @see ru.vandrikeev.android.phrasebook.model.languages.LanguageRepository#getSavedLanguageFrom()
     */
    void setUpWidget(@NonNull List<Language> languages,
                     @NonNull Language languageFromPreference,
                     @NonNull Language languageToPreference);
}
