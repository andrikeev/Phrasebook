package ru.vandrikeev.android.phrasebook.presentation.view.translation;

import android.support.annotation.NonNull;

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import ru.vandrikeev.android.phrasebook.model.translations.Translation;
import ru.vandrikeev.android.phrasebook.presentation.view.LceView;

/**
 * Interface for part of translation screen with text controls.
 */
@StateStrategyType(AddToEndSingleStrategy.class)
public interface TranslationView extends LceView<Translation> {

    void clearView();

    void setLoadingModel(@NonNull String text);

    void enableFavorites(boolean enabled);

    void setFavorite(boolean favorite);
}
