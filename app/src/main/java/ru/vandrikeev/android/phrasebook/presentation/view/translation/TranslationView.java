package ru.vandrikeev.android.phrasebook.presentation.view.translation;

import android.support.annotation.NonNull;

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import ru.vandrikeev.android.phrasebook.model.translations.Translation;
import ru.vandrikeev.android.phrasebook.presentation.view.LceView;

/**
 * Interface for translation screen.
 */
@StateStrategyType(AddToEndSingleStrategy.class)
public interface TranslationView extends LceView<Translation> {

    /**
     * Reset screen to the initial state.
     */
    void clearView();

    /**
     * Set up view that shows text to be translated.
     *
     * @param text text to be translated
     */
    void setLoadingModel(@NonNull String text);

    /**
     * Enable or disable favorite button.
     *
     * @param enabled true if this button is enabled, false otherwise
     */
    void enableFavorite(boolean enabled);

    /**
     * Set favorite button state to show that translation is favorite.
     *
     * @param favorite true if translation is favorite, false otherwise
     */
    void setFavorite(boolean favorite);
}
