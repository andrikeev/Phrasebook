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
import android.widget.Spinner;
import android.widget.Toast;

import com.arellomobile.mvp.MvpDelegate;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import java.util.List;

import ru.vandrikeev.android.phrasebook.App;
import ru.vandrikeev.android.phrasebook.BuildConfig;
import ru.vandrikeev.android.phrasebook.R;
import ru.vandrikeev.android.phrasebook.di.AppComponent;
import ru.vandrikeev.android.phrasebook.model.languages.Language;
import ru.vandrikeev.android.phrasebook.presentation.presenter.translation.LanguageSelectionPresenter;
import ru.vandrikeev.android.phrasebook.presentation.view.translation.LanguageSelectionView;
import ru.vandrikeev.android.phrasebook.ui.adapter.LanguageAdapter;

/**
 * Part of translation screen that response of selecting translation direction.
 */
@SuppressWarnings("NullableProblems")
public class LanguageSelectionWidget extends FrameLayout implements LanguageSelectionView {

    private static final String TAG = LanguageSelectionWidget.class.getSimpleName();

    // region MVP delegates

    private MvpDelegate parentDelegate;

    private MvpDelegate<LanguageSelectionWidget> mvpDelegate;

    // endregion

    // region Fields

    @NonNull
    private Spinner languageFromSpinner;

    @NonNull
    private Spinner languageToSpinner;

    @NonNull
    private ImageButton swapLanguagesButton;

    @NonNull
    private LanguageAdapter languageFromAdapter;

    @NonNull
    private LanguageAdapter languageToAdapter;

    @Nullable
    private OnLanguageSelectedListener onLanguageSelectedListener;

    @NonNull
    @InjectPresenter
    LanguageSelectionPresenter presenter;

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

    @SuppressWarnings("unused")
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

    public void init(@NonNull MvpDelegate parentDelegate) {
        this.parentDelegate = parentDelegate;
        getMvpDelegate().onCreate();
        getMvpDelegate().onAttach();
    }

    @NonNull
    private MvpDelegate getMvpDelegate() {
        if (mvpDelegate != null) {
            return mvpDelegate;
        }

        mvpDelegate = new MvpDelegate<>(this);
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

        languageFromAdapter = new LanguageAdapter();
        languageFromSpinner = (Spinner) findViewById(R.id.languageFromSpinner);
        languageFromSpinner.setAdapter(languageFromAdapter);

        languageToAdapter = new LanguageAdapter();
        languageToSpinner = (Spinner) findViewById(R.id.languageToSpinner);
        languageToSpinner.setAdapter(languageToAdapter);

        swapLanguagesButton = (ImageButton) findViewById(R.id.swapLanguagesButton);
        swapLanguagesButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Swap languages button clicked");
                final Language from = getLanguageFrom();
                final Language to = getLanguageTo();
                final int newFromPosition = languageFromAdapter.getItemPosition(to);
                final int newToPosition = languageToAdapter.getItemPosition(from);
                languageToSpinner.setSelection(newToPosition > 0 ? newToPosition : 0);
                languageFromSpinner.setSelection(newFromPosition > 0 ? newFromPosition : 0);
            }
        });
    }

    @NonNull
    public Language getLanguageFrom() {
        return (Language) languageFromSpinner.getSelectedItem();
    }

    @NonNull
    public Language getLanguageTo() {
        return (Language) languageToSpinner.getSelectedItem();
    }

    public void setLanguageFrom(@NonNull Language languageFrom) {
        final int position = languageFromAdapter.getItemPosition(languageFrom);
        languageFromSpinner.setSelection(position);
    }

    public void setLanguageTo(@NonNull Language languageTo) {
        final int position = languageToAdapter.getItemPosition(languageTo);
        languageToSpinner.setSelection(position);
    }

    // region LanguageSelectionView

    @Override
    public void setUpWidget(@NonNull List<Language> languages,
                            @NonNull Language languageFromPreference,
                            @NonNull Language languageToPreference) {
        Log.d(TAG, String.format("setUpWidget: %s, from %s, to %s",
                languages, languageFromPreference, languageToPreference));
        languageFromAdapter.addAll(languages);
        languageFromSpinner.setSelection(languageFromAdapter.getItemPosition(languageFromPreference));
        languageFromSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onSelected() {
                final Language language = getLanguageFrom();
                presenter.saveLanguageFromSelection(language);
                presenter.loadSupportedLanguages(language);
                swapLanguagesButton.setEnabled(!language.isAutodetect());
                Log.d(TAG, String.format("Language FROM selected: %s", language));
            }
        });

        languageToAdapter.addAll(languages.subList(1, languages.size()));
        languageToSpinner.setSelection(languageToAdapter.getItemPosition(languageToPreference));
        languageToSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onSelected() {
                final Language language = getLanguageTo();
                presenter.saveLanguageToSelection(getLanguageTo());
                if (onLanguageSelectedListener != null) {
                    onLanguageSelectedListener.onSelected();
                }
                Log.d(TAG, String.format("Language TO selected: %s", language));
            }
        });
    }

    @Override
    public void showLoading() {
        languageToSpinner.setEnabled(false);
        swapLanguagesButton.setEnabled(false);
        Log.d(TAG, "Loading available languages");
    }

    @Override
    public void setModel(@NonNull List<Language> model) {
        final Language oldLanguage = getLanguageTo();
        final int oldLanguageIndex = model.indexOf(oldLanguage);
        languageToAdapter.addAll(model);
        languageToSpinner.setSelection(oldLanguageIndex > 0 ? oldLanguageIndex : 0);
        if (onLanguageSelectedListener != null) {
            onLanguageSelectedListener.onSelected();
        }
        Log.d(TAG, String.format("Languages loaded! %s\nOld selected language: %s\nNew language: %s", model,
                oldLanguage, languageToSpinner.getSelectedItem()));
    }

    @Override
    public void showContent() {
        languageToSpinner.setEnabled(true);
        swapLanguagesButton.setEnabled(!getLanguageFrom().isAutodetect());
        Log.d(TAG, "Showing available languages");
    }

    @Override
    public void showError(@NonNull Throwable e) {
        languageToSpinner.setEnabled(true);
        swapLanguagesButton.setEnabled(!getLanguageFrom().isAutodetect());
        if (BuildConfig.DEBUG) {
            final String message = String.format("Error: %s; %s", e.getMessage(), e.toString());
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
        Log.e(TAG, "Error while loading languages", e);
    }

    // endregion

    // region Listeners

    public void setOnLanguageSelectedListener(@NonNull OnLanguageSelectedListener listener) {
        this.onLanguageSelectedListener = listener;
    }

    public void removeOnLanguageSelectedListener() {
        this.onLanguageSelectedListener = null;
    }

    private abstract class OnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public abstract void onSelected();

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            onSelected();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    public interface OnLanguageSelectedListener {
        void onSelected();
    }

    // endregion
}
