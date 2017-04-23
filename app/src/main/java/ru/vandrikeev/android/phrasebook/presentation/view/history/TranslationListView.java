package ru.vandrikeev.android.phrasebook.presentation.view.history;

import android.support.annotation.NonNull;

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

    /**
     * Clear list of translations.
     */
    void clearContent();

    /**
     * Show message to user that the list is empty.
     */
    void showEmpty();

    /**
     * Scroll list to the top.
     */
    @StateStrategyType(SkipStrategy.class)
    void scrollToTop();

    /**
     * Update translation item.
     */
    @StateStrategyType(SkipStrategy.class)
    void updateTranslation(@NonNull HistoryTranslation translation);
}
