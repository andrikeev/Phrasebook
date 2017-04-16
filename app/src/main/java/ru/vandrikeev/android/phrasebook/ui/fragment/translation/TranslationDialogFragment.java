package ru.vandrikeev.android.phrasebook.ui.fragment.translation;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatDialogFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.transitionseverywhere.TransitionManager;

import ru.vandrikeev.android.phrasebook.App;
import ru.vandrikeev.android.phrasebook.R;
import ru.vandrikeev.android.phrasebook.model.languages.Language;
import ru.vandrikeev.android.phrasebook.model.translations.Translation;
import ru.vandrikeev.android.phrasebook.presentation.presenter.translation.TranslationDialogPresenter;
import ru.vandrikeev.android.phrasebook.presentation.view.translation.TranslationDialogView;

/**
 * Dialog with input text field that show translation of given text.
 */
@SuppressWarnings("NullableProblems")
public class TranslationDialogFragment extends MvpAppCompatDialogFragment implements TranslationDialogView {

    // region Constants

    private static final String TAG = TranslationDialogFragment.class.getSimpleName();
    private static final String LANGUAGE_FROM_KEY =
            "ru.vandrikeev.android.phrasebook.ui.fragment.translation.TranslationDialogFragment.LANGUAGE_FROM_KEY";
    private static final String LANGUAGE_TO_KEY =
            "ru.vandrikeev.android.phrasebook.ui.fragment.translation.TranslationDialogFragment.LANGUAGE_TO_KEY";
    private static final String TEXT_KEY =
            "ru.vandrikeev.android.phrasebook.ui.fragment.translation.TranslationDialogFragment.TEXT_KEY";

    // endregion

    // region Fields

    @NonNull
    private EditText inputTextEdit;

    @NonNull
    private TextView translationTextView;

    @NonNull
    private ImageButton actionButton;

    @NonNull
    private Language languageFrom;

    @NonNull
    private Language languageTo;

    @Nullable
    private OnTranslateButtonClickedListener listener;

    @InjectPresenter
    TranslationDialogPresenter presenter;

    // endregion

    @ProvidePresenter
    TranslationDialogPresenter providePresenter() {
        return ((App) getActivity().getApplication()).getDependencyGraph().getTranslationDialogPresenter();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        //noinspection ConstantConditions
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // force open keyboard
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup parent,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_translation_dialog, parent, false);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!getArguments().containsKey(LANGUAGE_FROM_KEY) ||
                !getArguments().containsKey(LANGUAGE_TO_KEY) ||
                !getArguments().containsKey(TEXT_KEY)) {
            throw new IllegalStateException("languageFromCode, languageToCode or text not found in fragment arguments");
        }
        this.languageFrom = (Language) getArguments().getSerializable(LANGUAGE_FROM_KEY);
        this.languageTo = (Language) getArguments().getSerializable(LANGUAGE_TO_KEY);
        final String text = getArguments().getString(TEXT_KEY);

        translationTextView = (TextView) view.findViewById(R.id.translation);

        actionButton = (ImageButton) view.findViewById(R.id.actionButton);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTranslationLoading();
                Log.d(TAG, "Translate button clicked");
            }
        });

        inputTextEdit = (EditText) view.findViewById(R.id.textInput);
        inputTextEdit.setHint(languageFrom.isAutodetect()
                ? getString(R.string.dialog_input_text_hint)
                : getString(R.string.dialog_input_text_hint_lang, languageFrom.getName()));
        final Handler handler = new Handler(Looper.getMainLooper());
        final ThreadLocal<Runnable> workRunnable = new ThreadLocal<>();
        inputTextEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                actionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onTranslationLoading();
                    }
                });
                if (TextUtils.isEmpty(s)) {
                    translationTextView.setText("...");
                    actionButton.setEnabled(false);
                } else {
                    actionButton.setEnabled(true);
                    handler.removeCallbacks(workRunnable.get());
                    workRunnable.set(new Runnable() {
                        @Override
                        public void run() {
                            presenter.translate(inputTextEdit.getText().toString(), languageFrom, languageTo);
                        }
                    });
                    handler.postDelayed(workRunnable.get(), 500);
                    Log.d(TAG, String.format("Text changed. New string '%s'.\n" +
                            "Waiting 0.5 seconds before request translation", s));
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        inputTextEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                switch (actionId) {
                    case R.id.textInputAction:
                    case EditorInfo.IME_NULL:
                        onTranslationLoading();
                        return true;
                    default:
                        return false;
                }
            }
        });
        inputTextEdit.setText(text);
        inputTextEdit.setSelection(text.length());

        Log.d(TAG, "Fragment created");
    }

    @Override
    public void onStart() {
        super.onStart();
        // set width of dialog to maximum
        // without handler soft input does not appear
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                //noinspection ConstantConditions
                getDialog().getWindow().setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        });
    }

    private void onTranslationLoading() {
        if (listener != null) {
            listener.onTranslationLoading(inputTextEdit.getText().toString());
        }
        getDialog().dismiss();
        Log.d(TAG, "Translate button clicked");
    }

    private void onTranslationLoaded(@NonNull Translation translation) {
        if (listener != null) {
            listener.onTranslationLoaded(translation);
        }
        getDialog().dismiss();
        Log.d(TAG, "Translate button clicked");
    }

    @Override
    public void showTranslation(@NonNull final Translation translation) {
        TransitionManager.beginDelayedTransition((ViewGroup) getView());
        translationTextView.setText(translation.getTranslation());
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTranslationLoaded(translation);
            }
        });
        inputTextEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                switch (actionId) {
                    case R.id.textInputAction:
                    case EditorInfo.IME_NULL:
                        onTranslationLoaded(translation);
                        return true;
                    default:
                        return false;
                }
            }
        });
        Log.d(TAG, String.format("Show translation: Translation {'%s' - '%s'; %s-%s}",
                translation.getText(),
                translation.getTranslation(),
                translation.getLanguageFrom().getCode(),
                translation.getLanguageTo().getCode()));
    }

    interface OnTranslateButtonClickedListener {
        void onTranslationLoaded(@NonNull Translation translation);

        void onTranslationLoading(@NonNull String text);
    }

    private void setListener(@NonNull OnTranslateButtonClickedListener listener) {
        this.listener = listener;
    }

    public static TranslationDialogFragment create(@NonNull Language languageFrom,
                                                   @NonNull Language languageTo,
                                                   @NonNull OnTranslateButtonClickedListener listener) {
        return create(languageFrom, languageTo, "", listener);
    }

    public static TranslationDialogFragment create(@NonNull Language languageFrom,
                                                   @NonNull Language languageTo,
                                                   @NonNull String text,
                                                   @NonNull OnTranslateButtonClickedListener listener) {
        final TranslationDialogFragment fragment = new TranslationDialogFragment();
        final Bundle args = new Bundle();
        args.putSerializable(LANGUAGE_FROM_KEY, languageFrom);
        args.putSerializable(LANGUAGE_TO_KEY, languageTo);
        args.putString(TEXT_KEY, text);
        fragment.setArguments(args);
        fragment.setListener(listener);
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        return fragment;
    }
}
