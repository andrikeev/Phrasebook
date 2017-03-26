package ru.vandrikeev.android.phrasebook.ui.fragment.translation;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.transitionseverywhere.TransitionManager;

import ru.vandrikeev.android.phrasebook.App;
import ru.vandrikeev.android.phrasebook.R;
import ru.vandrikeev.android.phrasebook.model.Language;
import ru.vandrikeev.android.phrasebook.model.network.ErrorUtils;
import ru.vandrikeev.android.phrasebook.presentation.presenter.translation.TranslationPresenter;
import ru.vandrikeev.android.phrasebook.presentation.view.translation.TranslationView;
import ru.vandrikeev.android.phrasebook.ui.fragment.BaseFragment;
import ru.vandrikeev.android.phrasebook.ui.view.LanguageSelectionWidget;

import static ru.vandrikeev.android.phrasebook.R.id.translation;

/**
 * Fragment for translation tab.
 */
public class TranslationFragment
        extends BaseFragment<TranslationView, TranslationPresenter> implements TranslationView {

    private static final String TAG = TranslationFragment.class.getSimpleName();

    // region Fields
    @InjectPresenter
    TranslationPresenter presenter;
    @NonNull
    private LanguageSelectionWidget languageSelectionWidget;
    @NonNull
    private EditText inputTextEdit;
    @NonNull
    private TextView detectedLanguageLabel;
    @NonNull
    private ProgressBar loadingView;
    @NonNull
    private TextView translationTextView;
    @NonNull
    private ImageButton favoriteButton;

    // endregion

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_translate;
    }

    @Override
    @ProvidePresenter
    protected TranslationPresenter providePresenter() {
        return ((App) getActivity().getApplication()).getDependencyGraph().getTranslationPresenter();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inputTextEdit = (EditText) view.findViewById(R.id.textInput);
        final Handler handler = new Handler(Looper.getMainLooper());
        final ThreadLocal<Runnable> workRunnable = new ThreadLocal<>();
        inputTextEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(workRunnable.get());
                workRunnable.set(new Runnable() {
                    @Override
                    public void run() {
                        performTranslation();
                    }
                });
                handler.postDelayed(workRunnable.get(), 500);

                Log.d(TAG, String.format("Text changed. New string '%s'. " +
                        "Waiting 0.5 seconds to request translation", s));
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        loadingView = (ProgressBar) view.findViewById(R.id.translationLoadingView);
        translationTextView = (TextView) view.findViewById(translation);

        languageSelectionWidget = ((LanguageSelectionWidget) view.findViewById(R.id.languageSelector));
        languageSelectionWidget.init(getMvpDelegate());
        languageSelectionWidget.setOnLanguageFromSelectedListener(
                new LanguageSelectionWidget.OnLanguageFromSelectedListener() {
                    @Override
                    public void onSelected(@NonNull Language language) {
                        if (language != Language.auto) {
                            detectedLanguageLabel.setVisibility(View.INVISIBLE);
                        }
                    }
                });
        languageSelectionWidget.setOnLanguageToSelectedListener(
                new LanguageSelectionWidget.OnLanguageToSelectedListener() {
                    @Override
                    public void onSelected(@NonNull Language language) {
                        performTranslation();
                    }
                }
        );

        detectedLanguageLabel = (TextView) view.findViewById(R.id.detectedLanguageLabel);

        favoriteButton = (ImageButton) view.findViewById(R.id.favoriteButton);
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoriteButton.setImageResource(R.drawable.ic_favorite_on);
                final Language from = languageSelectionWidget.getLanguageFrom() == Language.auto
                        ? Language.valueOf(detectedLanguageLabel.getText().toString())
                        : languageSelectionWidget.getLanguageFrom();
                final Language to = languageSelectionWidget.getLanguageTo();
                final String text = inputTextEdit.getText().toString();
                final String translation = translationTextView.getText().toString();
                presenter.saveToFavorites(from, to, text, translation);
            }
        });
    }

    private void performTranslation() {
        Log.d(TAG, "Check if translation needed");
        final String text = inputTextEdit.getText().toString();
        if (!TextUtils.isEmpty(text)) {
            final Language from = languageSelectionWidget.getLanguageFrom();
            final Language to = languageSelectionWidget.getLanguageTo();
            presenter.translate(text, from, to);
            Log.d(TAG, String.format("Start translation of '%s' from '%s' to '%s'", text, from, to));
        } else {
            translationTextView.setText("");
        }
    }

    // region TranslationView

    @Override
    public void showLoading() {
        TransitionManager.beginDelayedTransition((ViewGroup) getView());
        loadingView.setVisibility(View.VISIBLE);
        favoriteButton.setImageResource(R.drawable.ic_favorite_off);
        Log.d(TAG, "Show loading translation");
    }

    @Override
    public void setModel(@NonNull String translation) {
        TransitionManager.beginDelayedTransition((ViewGroup) getView());
        translationTextView.setText(translation);
        Log.d(TAG, String.format("Translated '%s'", translation));
    }

    @Override
    public void setDetectedLanguage(@NonNull Language language) {
        detectedLanguageLabel.setText(language.name().toUpperCase());
        Log.d(TAG, String.format("Language detected '%s'", language));
    }

    @Override
    public void showContent() {
        loadingView.setVisibility(View.INVISIBLE);
        TransitionManager.beginDelayedTransition((ViewGroup) getView());
        Log.d(TAG, "Show translated text");
    }

    @Override
    public void showError(@Nullable Throwable e) {
        TransitionManager.beginDelayedTransition((ViewGroup) getView());
        loadingView.setVisibility(View.INVISIBLE);
        final String message = getString(ErrorUtils.getErrorMessage(e));
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Error while translating", e);
    }

    // endregion
}
