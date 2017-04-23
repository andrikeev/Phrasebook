package ru.vandrikeev.android.phrasebook.ui.fragment.history;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import ru.vandrikeev.android.phrasebook.App;
import ru.vandrikeev.android.phrasebook.R;
import ru.vandrikeev.android.phrasebook.model.translations.HistoryTranslation;
import ru.vandrikeev.android.phrasebook.presentation.presenter.history.HistoryPresenter;
import ru.vandrikeev.android.phrasebook.ui.activity.MainActivity;
import ru.vandrikeev.android.phrasebook.ui.adapter.TranslationAdapter;
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
    protected int getEmptyLabelRes() {
        return R.string.empty_history;
    }

    @Override
    protected int getEmptyDrawableRes() {
        return R.drawable.ic_history_large;
    }

    @Override
    protected int getClearedMessageRes() {
        return R.string.history_cleared;
    }

    @Override
    protected int getClearMessageRes() {
        return R.string.clear_history_message;
    }

    @Override
    protected void onClearOptionSelected() {
        presenter.clear();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new TranslationAdapter(
                new TranslationAdapter.OnClickTranslationListener() {
                    @Override
                    public void onClick(@NonNull HistoryTranslation translation) {
                        ((MainActivity) getActivity()).openTranslation(translation);
                    }
                },
                new TranslationAdapter.OnClickFavoriteListener() {
                    @Override
                    public void onClick(@NonNull HistoryTranslation translation) {
//
                    }
                });
        contentView.setAdapter(adapter);
    }
}
