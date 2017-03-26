package ru.vandrikeev.android.phrasebook.presentation.view;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

/**
 * Base view for async loading some content from external source.
 */
@StateStrategyType(AddToEndSingleStrategy.class)
public interface LceView<M> extends BaseView {

    /**
     * Show loading process.
     */
    void showLoading();

    /**
     * Set up view with loaded content.
     *
     * @param model loaded content
     */
    void setModel(@NonNull M model);

    /**
     * Show loaded content.
     */
    void showContent();

    /**
     * Show error message to user.
     *
     * @param e error cause
     */
    void showError(@Nullable Throwable e);
}
