package ru.vandrikeev.android.phrasebook.presentation.view.history;

import android.support.annotation.NonNull;

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.AddToEndStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

import ru.vandrikeev.android.phrasebook.model.translations.AbstractTranslation;
import ru.vandrikeev.android.phrasebook.presentation.view.LceView;

/**
 * Interface for screen with list of translations.
 */
@StateStrategyType(AddToEndSingleStrategy.class)
public interface TranslationListView extends LceView<List<? extends AbstractTranslation>> {

    @Override
    @StateStrategyType(AddToEndStrategy.class)
    void setModel(@NonNull List<? extends AbstractTranslation> model);

    void clearContent();

    void showEmpty();

    @StateStrategyType(SkipStrategy.class)
    void scrollToTop();
}
