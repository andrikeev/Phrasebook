package ru.vandrikeev.android.phrasebook.ui.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.arellomobile.mvp.MvpDelegate;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.transitionseverywhere.TransitionManager;

import java.util.List;

import ru.vandrikeev.android.phrasebook.App;
import ru.vandrikeev.android.phrasebook.BuildConfig;
import ru.vandrikeev.android.phrasebook.R;
import ru.vandrikeev.android.phrasebook.di.AppComponent;
import ru.vandrikeev.android.phrasebook.model.languages.Language;
import ru.vandrikeev.android.phrasebook.model.network.ErrorUtils;
import ru.vandrikeev.android.phrasebook.presentation.presenter.translation.LanguageSelectionPresenter;
import ru.vandrikeev.android.phrasebook.presentation.view.translation.LanguageSelectionView;
import ru.vandrikeev.android.phrasebook.ui.adapter.LanguageAdapter;

/**
 * Part of translation screen that response of selecting translation direction.
 */
public class LanguageSelectionWidget extends FrameLayout implements LanguageSelectionView {

    private static final String TAG = LanguageSelectionWidget.class.getSimpleName();

    // region MVP delegates
    @NonNull
    @InjectPresenter
    LanguageSelectionPresenter presenter;
    private MvpDelegate parentDelegate;
    private MvpDelegate<LanguageSelectionWidget> mvpDelegate;
    @NonNull
    private Spinner languageFromSpinner;
    @NonNull
    private Spinner languageToSpinner;

    // endregion

    // region Fields
    @NonNull
    private ImageButton swapLanguagesButton;
    @NonNull
    private ProgressBar loadingView;
    @NonNull
    private LanguageAdapter languageFromAdapter;
    @NonNull
    private LanguageAdapter languageToAdapter;
    @Nullable
    private OnLanguageFromSelectedListener languageFromSelectedListener;
    @Nullable
    private OnLanguageToSelectedListener languageToSelectedListener;

    public LanguageSelectionWidget(@NonNull Context context) {
        super(context);
        onCreateView(context);
    }

    public LanguageSelectionWidget(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        onCreateView(context);
    }

    public LanguageSelectionWidget(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onCreateView(context);
    }

    // endregion

    // region Constructor

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LanguageSelectionWidget(@NonNull Context context,
                                   @Nullable AttributeSet attrs,
                                   @AttrRes int defStyleAttr,
                                   @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        onCreateView(context);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getMvpDelegate().onSaveInstanceState();
        getMvpDelegate().onDetach();
    }

    public LanguageSelectionWidget init(@NonNull MvpDelegate parentDelegate) {
        this.parentDelegate = parentDelegate;
        getMvpDelegate().onCreate();
        getMvpDelegate().onAttach();
        return this;
    }

    @NonNull
    private MvpDelegate getMvpDelegate() {
        if (mvpDelegate != null) {
            return mvpDelegate;
        }

        //noinspection unchecked
        mvpDelegate = new MvpDelegate(this);
        mvpDelegate.setParentDelegate(parentDelegate, String.valueOf(getId()));
        return mvpDelegate;
    }

    // endregion

    @NonNull
    private AppComponent getDependencyGraph() {
        return ((App) ((AppCompatActivity) getContext()).getApplication()).getDependencyGraph();
    }

    @ProvidePresenter
    LanguageSelectionPresenter providePresenter() {
        return getDependencyGraph().getLanguageSelectionPresenter();
    }

    private void onCreateView(@NonNull Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_language_selector, this, true);

        languageFromAdapter = new LanguageAdapter();
        languageFromSpinner = (Spinner) findViewById(R.id.languageFromSpinner);
        languageFromSpinner.setAdapter(languageFromAdapter);
        languageFromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, String.format("Language FROM selected: position %d; item %s",
                        position, languageFromSpinner.getSelectedItem()));
                final Language language = getLanguageFrom();
                loadSupportedLanguages(language);
                swapLanguagesButton.setEnabled(language.isAutodetect());
                if (languageFromSelectedListener != null) {
                    languageFromSelectedListener.onSelected(language);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        languageToAdapter = new LanguageAdapter();
        languageToSpinner = (Spinner) findViewById(R.id.languageToSpinner);
        languageToSpinner.setAdapter(languageToAdapter);
        languageToSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, String.format("Language TO selected: position %d; item %s",
                        position, languageToSpinner.getSelectedItem()));
                final Language language = getLanguageTo();
                if (languageToSelectedListener != null) {
                    languageToSelectedListener.onSelected(language);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        swapLanguagesButton = (ImageButton) findViewById(R.id.swapLanguagesButton);
        swapLanguagesButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Swap languages button clicked");
                final Language from = getLanguageFrom();
                final Language to = getLanguageTo();
                final int newFromPosition = languageFromAdapter.getItemPosition(to);
                final int newToPosition = languageToAdapter.getItemPosition(from);
                languageFromSpinner.setSelection(newFromPosition);
                if (newToPosition != -1) {
                    languageToSpinner.setSelection(newToPosition);
                }
                Toast.makeText(getContext(), "Swap clicked", Toast.LENGTH_SHORT).show();
            }
        });

        loadingView = (ProgressBar) findViewById(R.id.loadingView);
    }

    public void loadSupportedLanguages(@NonNull Language language) {
        presenter.loadSupportedLanguages(language);
    }

    @NonNull
    public Language getLanguageFrom() {
        return (Language) languageFromSpinner.getSelectedItem();
    }

    @NonNull
    public Language getLanguageTo() {
        return (Language) languageToSpinner.getSelectedItem();
    }

    // region Listeners

    public void setOnLanguageFromSelectedListener(@NonNull OnLanguageFromSelectedListener listener) {
        this.languageFromSelectedListener = listener;
    }

    public void setOnLanguageToSelectedListener(@NonNull OnLanguageToSelectedListener listener) {
        this.languageToSelectedListener = listener;
    }

    // endregion

    // region LanguageSelectionView

    @Override
    public void showLoading() {
        TransitionManager.beginDelayedTransition(this);
        languageToSpinner.setEnabled(false);
        swapLanguagesButton.setVisibility(INVISIBLE);
        loadingView.setVisibility(VISIBLE);
        Log.d(TAG, "Loading available languages");
    }

    @Override
    public void setModel(@NonNull List<Language> model) {
        final Language oldLanguage = getLanguageTo();
        final boolean isOldLanguageAvailable = model.contains(oldLanguage);
        languageToAdapter.clear();
        languageToAdapter.addAll(model);
        languageToAdapter.notifyDataSetChanged();
        languageToSpinner.setSelection(isOldLanguageAvailable ? model.indexOf(oldLanguage) : 0, true);
        if (languageToSelectedListener != null) {
            languageToSelectedListener.onSelected(getLanguageTo());
        }
        Log.d(TAG, String.format("Languages loaded! %s\nOld selected language - %s\nNew language - %s", model,
                oldLanguage, languageToSpinner.getSelectedItem()));
    }

    @Override
    public void setUpSpinners(@NonNull List<Language> languages,
                              @NonNull Language languageFromPreference,
                              @Nullable Language languageToPreference) {
        languageFromAdapter.clear();
        languageFromAdapter.addAll(languages);
        languageFromAdapter.notifyDataSetChanged();
        if (languageFromPreference.isAutodetect()) {
            languageToAdapter.addAll(languages.subList(1, languages.size()));
            languageToAdapter.notifyDataSetChanged();
            if (languageToPreference != null) {
                languageToSpinner.setSelection(languageToAdapter.getItemPosition(languageToPreference));
            }
        }
        languageFromSpinner.setSelection(languageFromAdapter.getItemPosition(languageFromPreference));
    }

    @Override
    public void showContent() {
        TransitionManager.beginDelayedTransition(this);
        languageToSpinner.setEnabled(true);
        swapLanguagesButton.setVisibility(VISIBLE);
        loadingView.setVisibility(INVISIBLE);
        Log.d(TAG, "Showing available languages");
    }

    @Override
    public void showError(@NonNull Throwable e) {
        TransitionManager.beginDelayedTransition(this);
        languageToSpinner.setEnabled(false);
        swapLanguagesButton.setVisibility(VISIBLE);
        loadingView.setVisibility(INVISIBLE);
        final String message = BuildConfig.DEBUG
                ? String.format("Error: %s; %s", e.getMessage(), e.toString())
                : getContext().getString(ErrorUtils.getErrorMessage(e));
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Error while loading languages", e);
    }

    public interface OnLanguageFromSelectedListener {
        void onSelected(@NonNull Language language);
    }

    public interface OnLanguageToSelectedListener {
        void onSelected(@NonNull Language language);
    }

    // endregion
}
