package ru.vandrikeev.android.phrasebook.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;
import com.transitionseverywhere.TransitionManager;
import ru.vandrikeev.android.phrasebook.R;
import ru.vandrikeev.android.phrasebook.model.network.ErrorUtils;
import ru.vandrikeev.android.phrasebook.model.translations.HistoryTranslation;
import ru.vandrikeev.android.phrasebook.presentation.presenter.RxPresenter;
import ru.vandrikeev.android.phrasebook.presentation.view.history.TranslationListView;
import ru.vandrikeev.android.phrasebook.ui.adapter.TranslationAdapter;

import java.util.List;

/**
 * Base fragment for list of translations.
 */
@SuppressWarnings("NullableProblems")
public abstract class TranslationListFragment
        extends BaseFragment<TranslationListView, RxPresenter<TranslationListView>> implements TranslationListView {

    private static final String TAG = TranslationListFragment.class.getSimpleName();

    // region Fields

    @NonNull
    private View loadingView;

    @NonNull
    protected RecyclerView contentView;

    @NonNull
    private TextView errorView;

    @NonNull
    private TextView emptyView;

    @NonNull
    protected TranslationAdapter adapter;

    // endregion

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_translation_list;
    }

    protected abstract int getEmptyLabelRes();

    protected abstract int getEmptyDrawableRes();

    protected abstract int getClearedMessageRes();

    protected abstract int getClearMessageRes();

    protected abstract void onClearOptionSelected();

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
        errorView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_error, 0, 0);

        emptyView = (TextView) view.findViewById(R.id.emptyView);
        if (emptyView == null) {
            throw new IllegalStateException("Empty view is null");
        }
        emptyView.setText(getEmptyLabelRes());
        emptyView.setCompoundDrawablesWithIntrinsicBounds(0, getEmptyDrawableRes(), 0, 0);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        contentView.setHasFixedSize(true);
        contentView.setLayoutManager(layoutManager);
        contentView.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));

        Log.d(TAG, "Fragment created");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.history_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.history_clear:
                new AlertDialog.Builder(getContext())
                        .setMessage(getClearMessageRes())
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onClearOptionSelected();
                            }
                        })
                        .create()
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // region TranslationListView

    @Override
    public void showLoading() {
        TransitionManager.beginDelayedTransition((ViewGroup) getView());
        loadingView.setVisibility(View.VISIBLE);
        contentView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        Log.d(TAG, "Loading");
    }

    @Override
    public void showContent() {
        TransitionManager.beginDelayedTransition((ViewGroup) getView());
        loadingView.setVisibility(View.GONE);
        contentView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        Log.d(TAG, "Show content");
    }

    @Override
    public void showEmpty() {
        TransitionManager.beginDelayedTransition((ViewGroup) getView());
        loadingView.setVisibility(View.GONE);
        contentView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);
        Log.d(TAG, "Empty list");
    }

    @Override
    public void showError(@NonNull Throwable e) {
        TransitionManager.beginDelayedTransition((ViewGroup) getView());
        loadingView.setVisibility(View.GONE);
        contentView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
        errorView.setText(ErrorUtils.getErrorMessage(e));
        Log.d(TAG, String.format("Error %s", e.getMessage()));
    }

    @Override
    public void scrollToTop() {
        contentView.scrollToPosition(0);
        Log.d(TAG, "Reset position");
    }

    @Override
    public void clearContent() {
        TransitionManager.beginDelayedTransition((ViewGroup) getView());
        adapter.clear();
        Toast.makeText(getContext(), getClearedMessageRes(), Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Clear list");
    }

    @Override
    public void setModel(@NonNull List<? extends HistoryTranslation> model) {
        adapter.addAll(model);
    }

    // endregion
}
