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
import ru.vandrikeev.android.phrasebook.R;
import ru.vandrikeev.android.phrasebook.Settings;
import ru.vandrikeev.android.phrasebook.di.AppComponent;
import ru.vandrikeev.android.phrasebook.model.Language;
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

    protected MvpDelegate parentDelegate;
    @NonNull
    @InjectPresenter
    LanguageSelectionPresenter presenter;

    // endregion

    // region Fields
    private MvpDelegate<LanguageSelectionWidget> mvpDelegate;
    @NonNull
    private Spinner languageFromSpinner;
    @NonNull
    private Spinner languageToSpinner;
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
    @NonNull
    private Settings settings;

    // endregion

    // region Constructor

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LanguageSelectionWidget(@NonNull Context context,
                                   @Nullable AttributeSet attrs,
                                   @AttrRes int defStyleAttr,
                                   @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        onCreateView(context);
    }

    // endregion


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

        //noinspection unchecked
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
        settings = getDependencyGraph().getSettings() /*new Settings(context)*/;

        languageFromSpinner = (Spinner) findViewById(R.id.languageFromSpinner);
        languageFromAdapter = new LanguageAdapter(getContext(), Language.getValues());
        languageFromSpinner.setAdapter(languageFromAdapter);
        languageFromSpinner.setSelection(languageFromAdapter.getItemPosition(settings.getLanguageFrom()));
        languageFromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, String.format("Language FROM selected: position %d; item %s", position, languageFromSpinner.getSelectedItem()));
                final Language language = getLanguageFrom();
                loadSupportedLanguages(language);
                swapLanguagesButton.setEnabled(language != Language.auto);
                settings.setLanguageFrom(language);
                if (languageFromSelectedListener != null) {
                    languageFromSelectedListener.onSelected(language);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        languageToSpinner = (Spinner) findViewById(R.id.languageToSpinner);
        languageToAdapter = new LanguageAdapter(getContext(), Language.getLanguageOnlyValues());
        languageToSpinner.setAdapter(languageToAdapter);
        languageToSpinner.setSelection(languageToAdapter.getItemPosition(settings.getLanguageTo()));
        languageToSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, String.format("Language TO selected: position %d; item %s", position, languageToSpinner.getSelectedItem()));
                final Language language = getLanguageTo();
                settings.setLanguageTo(language);
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

    // endregion

    // region LanguageSelectionView

    @Override
    public void showContent() {
        TransitionManager.beginDelayedTransition(this);
        languageToSpinner.setEnabled(true);
        swapLanguagesButton.setVisibility(VISIBLE);
        loadingView.setVisibility(INVISIBLE);
        Log.d(TAG, "Showing available languages");
    }

    @Override
    public void showError(@Nullable Throwable e) {
        TransitionManager.beginDelayedTransition(this);
        languageToSpinner.setEnabled(false);
        swapLanguagesButton.setVisibility(VISIBLE);
        loadingView.setVisibility(INVISIBLE);
        final String message = getContext().getString(ErrorUtils.getErrorMessage(e));
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
