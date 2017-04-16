package ru.vandrikeev.android.phrasebook.presentation.view.history;

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

import ru.vandrikeev.android.phrasebook.model.translations.HistoryTranslation;
import ru.vandrikeev.android.phrasebook.presentation.view.LceView;

/**
 * Interface for screen with list of translations.
 */
@StateStrategyType(AddToEndSingleStrategy.class)
public interface TranslationListView extends LceView<List<? extends HistoryTranslation>> {

    void clearContent();

    void showEmpty();

    @StateStrategyType(SkipStrategy.class)
    void scrollToTop();
}
