package ru.vandrikeev.android.phrasebook.ui.fragment.history;

import android.support.annotation.NonNull;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import ru.vandrikeev.android.phrasebook.App;
import ru.vandrikeev.android.phrasebook.presentation.presenter.history.HistoryPresenter;
import ru.vandrikeev.android.phrasebook.ui.fragment.TranslationListFragment;

/**
 * {@link TranslationListFragment} with recent translations.
 */
public class HistoryFragment extends TranslationListFragment {

    @NonNull
    @InjectPresenter
    HistoryPresenter presenter;

    @Override
    @ProvidePresenter
    protected HistoryPresenter providePresenter() {
        return ((App) getActivity().getApplication()).getDependencyGraph().getHistoryPresenter();
    }

    @Override
    protected void onClearOptionSelected() {
        presenter.clear();
    }
}
