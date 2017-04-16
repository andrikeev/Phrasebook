package ru.vandrikeev.android.phrasebook.ui.fragment.favorites;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import ru.vandrikeev.android.phrasebook.App;
import ru.vandrikeev.android.phrasebook.R;
import ru.vandrikeev.android.phrasebook.model.translations.HistoryTranslation;
import ru.vandrikeev.android.phrasebook.presentation.presenter.favorites.FavoritesPresenter;
import ru.vandrikeev.android.phrasebook.ui.adapter.TranslationAdapter;
import ru.vandrikeev.android.phrasebook.ui.fragment.TranslationListFragment;

/**
 * {@link TranslationListFragment} with favorites translations.
 */
@SuppressWarnings("NullableProblems")
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new TranslationAdapter(
                new TranslationAdapter.OnClickTranslationListener() {
                    @Override
                    public void onClick(@NonNull HistoryTranslation translation) {
//                        ((MainActivity) getActivity()).openTranslation(new Translation(translation));
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
