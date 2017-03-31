package ru.vandrikeev.android.phrasebook.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.transitionseverywhere.TransitionManager;

import java.util.List;

import ru.vandrikeev.android.phrasebook.R;
import ru.vandrikeev.android.phrasebook.model.network.ErrorUtils;
import ru.vandrikeev.android.phrasebook.model.translations.AbstractTranslation;
import ru.vandrikeev.android.phrasebook.presentation.presenter.RxPresenter;
import ru.vandrikeev.android.phrasebook.presentation.view.history.TranslationListView;
import ru.vandrikeev.android.phrasebook.ui.adapter.TranslationAdapter;

/**
 * Base fragment for list of translations.
 */
public abstract class TranslationListFragment
        extends BaseFragment<TranslationListView, RxPresenter<TranslationListView>> implements TranslationListView {

    // region Fields

    @NonNull
    protected View loadingView;

    @NonNull
    protected RecyclerView contentView;

    @NonNull
    protected TextView errorView;

    @NonNull
    protected TextView emptyView;

    @NonNull
    protected TranslationAdapter adapter;

    // endregion

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_translation_list;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        loadingView = view.findViewById(R.id.loadingView);
        if (loadingView == null) {
            throw new IllegalStateException("Loading view is null");
        }

        contentView = (RecyclerView) view.findViewById(R.id.contentView);
        if (contentView == null) {
            throw new IllegalStateException("Content view is null");
        }

        errorView = (TextView) view.findViewById(R.id.errorView);
        if (errorView == null) {
            throw new IllegalStateException("Error view is null");
        }

        emptyView = (TextView) view.findViewById(R.id.emptyView);
        if (emptyView == null) {
            throw new IllegalStateException("Empty view is null");
        }

        adapter = new TranslationAdapter();
        contentView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.navigation, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.history_clear:
                onClearOptionSelected();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected abstract void onClearOptionSelected();

    // region TranslationListView

    @Override
    public void showLoading() {
        TransitionManager.beginDelayedTransition((ViewGroup) getView());
        loadingView.setVisibility(View.VISIBLE);
        contentView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
    }

    @Override
    public void showContent() {
        TransitionManager.beginDelayedTransition((ViewGroup) getView());
        loadingView.setVisibility(View.GONE);
        contentView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
    }

    @Override
    public void showEmpty() {
        TransitionManager.beginDelayedTransition((ViewGroup) getView());
        loadingView.setVisibility(View.GONE);
        contentView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);
    }

    @Override
    public void showError(@NonNull Throwable e) {
        TransitionManager.beginDelayedTransition((ViewGroup) getView());
        loadingView.setVisibility(View.GONE);
        contentView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
        errorView.setText(ErrorUtils.getErrorMessage(e));
    }

    @Override
    public void scrollToTop() {
        contentView.scrollToPosition(0);
    }

    @Override
    public void clearContent() {
        adapter.clear();
    }

    @Override
    public void setModel(@NonNull List<? extends AbstractTranslation> model) {
        adapter.addAll(model);
    }

    // endregion
}
