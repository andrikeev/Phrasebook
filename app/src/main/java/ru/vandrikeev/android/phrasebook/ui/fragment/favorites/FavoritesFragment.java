package ru.vandrikeev.android.phrasebook.ui.fragment.favorites;

import android.support.annotation.NonNull;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import ru.vandrikeev.android.phrasebook.App;
import ru.vandrikeev.android.phrasebook.R;
import ru.vandrikeev.android.phrasebook.presentation.presenter.favorites.FavoritesPresenter;
import ru.vandrikeev.android.phrasebook.ui.fragment.TranslationListFragment;

/**
 * {@link TranslationListFragment} with favorites translations.
 */
public class FavoritesFragment extends TranslationListFragment {

    @NonNull
    @InjectPresenter
    FavoritesPresenter presenter;

    @Override
    @ProvidePresenter
    protected FavoritesPresenter providePresenter() {
        return ((App) getActivity().getApplication()).getDependencyGraph().getFavoritesPresenter();
    }

    @Override
    protected int getEmptyLabelRes() {
        return R.string.empty_favorites;
    }

    @Override
    protected int getEmptyDrawableRes() {
        return R.drawable.ic_favorites_large;
    }

    @Override
    protected void onClearOptionSelected() {
        presenter.clear();
    }
}
