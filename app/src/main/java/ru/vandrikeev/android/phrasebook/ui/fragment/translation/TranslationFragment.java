package ru.vandrikeev.android.phrasebook.ui.fragment.translation;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import ru.vandrikeev.android.phrasebook.App;
import ru.vandrikeev.android.phrasebook.R;
import ru.vandrikeev.android.phrasebook.model.network.ErrorUtils;
import ru.vandrikeev.android.phrasebook.model.translations.Translation;
import ru.vandrikeev.android.phrasebook.presentation.presenter.translation.TranslationPresenter;
import ru.vandrikeev.android.phrasebook.presentation.view.translation.TranslationView;
import ru.vandrikeev.android.phrasebook.ui.fragment.BaseFragment;
import ru.vandrikeev.android.phrasebook.ui.view.LanguageSelectionWidget;

import static android.content.Context.CLIPBOARD_SERVICE;
import static ru.vandrikeev.android.phrasebook.R.id.translation;

/**
 * Fragment for translation tab.
 */
@SuppressWarnings("NullableProblems")
public class TranslationFragment
        extends BaseFragment<TranslationView, TranslationPresenter> implements TranslationView {

    private static final String TAG = TranslationFragment.class.getSimpleName();

    // region Fields

    @NonNull
    private LanguageSelectionWidget languageSelectionWidget;

    @NonNull
    private EditText inputTextEdit;

    @NonNull
    private TextView inputTextView;

    @NonNull
    private ProgressBar loadingView;

    @NonNull
    private TextView languageFromLabel;

    @NonNull
    private TextView realLanguageFromLabel;

    @NonNull
    private TextView languageToLabel;

    @NonNull
    private TextView translationTextView;

    @NonNull
    private CardView translationCard;

    @NonNull
    private CardView textInputCard;

    @NonNull
    private CardView textViewCard;

    @NonNull
    private CardView errorCard;

    @NonNull
    private TextView errorMessageView;

    @NonNull
    private ImageButton favoriteButton;

    @NonNull
    private TextView yandexReference;

    private boolean isFavorite = false;

    @NonNull
    @InjectPresenter
    TranslationPresenter presenter;

    // endregion

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_translation;
    }

    @Override
    @ProvidePresenter
    protected TranslationPresenter providePresenter() {
        return ((App) getActivity().getApplication()).getDependencyGraph().getTranslationPresenter();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // region Language selection widget
        languageSelectionWidget = ((LanguageSelectionWidget) view.findViewById(R.id.languageSelector));
        languageSelectionWidget.init(getMvpDelegate());
        // endregion

        final TranslationDialogFragment.OnTranslateButtonClickedListener listener =
                new TranslationDialogFragment.OnTranslateButtonClickedListener() {
                    @Override
                    public void onTranslationLoaded(@NonNull Translation translation) {
                        presenter.setTranslation(translation);
                    }

                    @Override
                    public void onTranslationLoading(@NonNull String text) {
                        presenter.translate(text, languageSelectionWidget.getLanguageFrom(),
                                languageSelectionWidget.getLanguageTo());
                    }
                };

        // region Text input card
        textInputCard = (CardView) view.findViewById(R.id.textInputCard);
        inputTextEdit = (EditText) view.findViewById(R.id.textInput);
        inputTextEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TranslationDialogFragment fragment =
                        TranslationDialogFragment.create(
                                languageSelectionWidget.getLanguageFrom(),
                                languageSelectionWidget.getLanguageTo(),
                                inputTextEdit.getText().toString(),
                                listener);
                fragment.show(getFragmentManager(), fragment.getClass().getSimpleName());
            }
        });
        // endregion

        // region Text view card
        textViewCard = (CardView) view.findViewById(R.id.textViewCard);
        loadingView = (ProgressBar) view.findViewById(R.id.loadingView);
        languageFromLabel = (TextView) view.findViewById(R.id.languageFromLabel);
        inputTextView = (TextView) view.findViewById(R.id.text);
        inputTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TranslationDialogFragment fragment =
                        TranslationDialogFragment.create(
                                languageSelectionWidget.getLanguageFrom(),
                                languageSelectionWidget.getLanguageTo(),
                                inputTextView.getText().toString(),
                                listener);
                fragment.show(getFragmentManager(), fragment.getClass().getSimpleName());
            }
        });
        final ImageButton clearButton = (ImageButton) view.findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.clearView();
            }
        });
        // endregion

        // region Translation card
        translationCard = (CardView) view.findViewById(R.id.translationCard);
        realLanguageFromLabel = (TextView) view.findViewById(R.id.realLanguageFromLabel);
        languageToLabel = (TextView) view.findViewById(R.id.languageToLabel);
        translationTextView = (TextView) view.findViewById(translation);
        favoriteButton = (ImageButton) view.findViewById(R.id.favoriteButton);
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.setFavorite(!isFavorite);
            }
        });
        final ImageButton copyButton = (ImageButton) view.findViewById(R.id.copyButton);
        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String translation = translationTextView.getText().toString();
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                final ClipData clipData =
                        ClipData.newPlainText(getString(R.string.clipboard_translation_title), translation);
                clipboard.setPrimaryClip(clipData);
                Toast.makeText(getContext(), R.string.translation_copied_to_clipboard, Toast.LENGTH_SHORT).show();
            }
        });
        // endregion

        // region Error card
        errorCard = (CardView) view.findViewById(R.id.errorCard);
        errorMessageView = (TextView) view.findViewById(R.id.errorMessageView);
        Button reloadButton = (Button) view.findViewById(R.id.reloadButton);
        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.clearView();
            }
        });
        // endregion

        yandexReference = (TextView) view.findViewById(R.id.yandexReference);

        Log.d(TAG, "Fragment created");
    }

    // region TranslationView

    @Override
    public void clearView() {
        yandexReference.setVisibility(View.GONE);
        errorCard.setVisibility(View.GONE);
        translationCard.setVisibility(View.GONE);
        textViewCard.setVisibility(View.GONE);
        textInputCard.setVisibility(View.VISIBLE);
        languageSelectionWidget.removeOnLanguageSelectedListener();
    }

    @Override
    public void showLoading() {
        yandexReference.setVisibility(View.GONE);
        errorCard.setVisibility(View.GONE);
        translationCard.setVisibility(View.GONE);
        textInputCard.setVisibility(View.GONE);
        textViewCard.setVisibility(View.VISIBLE);
        languageFromLabel.setVisibility(View.INVISIBLE);
        loadingView.setVisibility(View.VISIBLE);
        Log.d(TAG, "Loading");
    }

    @Override
    public void setLoadingModel(@NonNull String text) {
        inputTextView.setText(text);
    }

    @Override
    public void setModel(@NonNull Translation translation) {
        languageFromLabel.setText(translation.getLanguageFrom().getName());
        inputTextView.setText(translation.getText());
        if (!translation.getLanguageFrom().equals(translation.getRealLanguageFrom())) {
            realLanguageFromLabel.setText(getString(R.string.translation_from_label, translation.getRealLanguageFrom().getName()));
            realLanguageFromLabel.setVisibility(View.VISIBLE);
        } else {
            realLanguageFromLabel.setText("");
            realLanguageFromLabel.setVisibility(View.GONE);
        }
        languageToLabel.setText(languageSelectionWidget.getLanguageTo().getName());
        translationTextView.setText(translation.getTranslation());
        languageSelectionWidget.setOnLanguageSelectedListener(
                new LanguageSelectionWidget.OnLanguageSelectedListener() {
                    @Override
                    public void onSelected() {
                        presenter.translate(
                                inputTextView.getText().toString(),
                                languageSelectionWidget.getLanguageFrom(),
                                languageSelectionWidget.getLanguageTo());
                    }
                });
        Log.d(TAG, String.format("Translated: Translation {'%s' - '%s'; %s(%s)-%s}",
                translation.getText(),
                translation.getTranslation(),
                translation.getLanguageFrom().getCode(),
                translation.getRealLanguageFrom().getCode(),
                translation.getLanguageTo().getCode()));
    }

    @Override
    public void showContent() {
        errorCard.setVisibility(View.GONE);
        textInputCard.setVisibility(View.GONE);
        textViewCard.setVisibility(View.VISIBLE);
        translationCard.setVisibility(View.VISIBLE);
        yandexReference.setVisibility(View.VISIBLE);
        languageFromLabel.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.GONE);
        yandexReference.setVisibility(View.VISIBLE);
        Log.d(TAG, "Show content");
    }

    @Override
    public void showError(@NonNull Throwable e) {
        yandexReference.setVisibility(View.GONE);
        translationCard.setVisibility(View.GONE);
        textInputCard.setVisibility(View.GONE);
        textViewCard.setVisibility(View.GONE);
        errorCard.setVisibility(View.VISIBLE);
        errorMessageView.setText(ErrorUtils.getErrorMessage(e));
        Log.d(TAG, String.format("Error: %s", e.getMessage()));
    }

    @Override
    public void enableFavorite(boolean enabled) {
        favoriteButton.setEnabled(enabled);
        Log.d(TAG, String.format("Favorite button enabled state '%s'", enabled));
    }

    @Override
    public void setFavorite(boolean favorite) {
        favoriteButton.setImageResource(favorite ? R.drawable.ic_favorite_on : R.drawable.ic_favorite_off);
        isFavorite = favorite;
        Log.d(TAG, String.format("Translation favorite state '%s'", favorite));
    }

    // endregion
}
