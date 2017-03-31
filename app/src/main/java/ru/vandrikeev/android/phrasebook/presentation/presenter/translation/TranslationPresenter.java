package ru.vandrikeev.android.phrasebook.presentation.presenter.translation;

import android.support.annotation.NonNull;
import android.util.Pair;

import com.arellomobile.mvp.InjectViewState;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import ru.vandrikeev.android.phrasebook.Settings;
import ru.vandrikeev.android.phrasebook.model.languages.Language;
import ru.vandrikeev.android.phrasebook.model.network.YandexTranslateException;
import ru.vandrikeev.android.phrasebook.model.network.YandexTranslateService;
import ru.vandrikeev.android.phrasebook.model.responses.TranslationResponse;
import ru.vandrikeev.android.phrasebook.model.translations.Translation;
import ru.vandrikeev.android.phrasebook.model.translations.TranslationRepository;
import ru.vandrikeev.android.phrasebook.presentation.presenter.RxPresenter;
import ru.vandrikeev.android.phrasebook.presentation.view.translation.TranslationView;

/**
 * Presenter for {@link TranslationView}.
 */
@InjectViewState
public class TranslationPresenter extends RxPresenter<TranslationView> {

    @NonNull
    private YandexTranslateService service;

    @NonNull
    private TranslationRepository translationRepository;

    @NonNull
    private Settings settings;

    private long currentTranslationId = -1;

    @Inject
    public TranslationPresenter(@NonNull YandexTranslateService service,
                                @NonNull TranslationRepository translationRepository,
                                @NonNull Settings settings) {
        this.service = service;
        this.translationRepository = translationRepository;
        this.settings = settings;
    }

    /**
     * Asynchronously translates text from one language to another.
     *
     * @param text text for translation
     * @param from any {@link Language}
     * @param to   any {@link Language} except {@link Language#AUTODETECT}
     */
    public void translate(@NonNull final String text,
                          @NonNull Language from,
                          @NonNull Language to) {
        dispose();
        getViewState().showLoading();
        getViewState().setFavorite(false);
        disposable = service.translate(from, to, text)
                .subscribe(
                        new Consumer<TranslationResponse>() {
                            @Override
                            @SuppressWarnings("NullableProblems")
                            public void accept(@NonNull TranslationResponse translation) throws Exception {
                                switch (translation.getCode()) {
                                    case 200:
                                        getViewState().setModel(translation.getText());
                                        Pair<String, String> translationDirection =
                                                translation.getTranslationDirection();
                                        if (translationDirection != null) {
                                            getViewState().setDetectedLanguage(translationDirection.first);
                                            saveToHistory(translationDirection.first,
                                                    translationDirection.second,
                                                    text,
                                                    translation.getText());
                                        }
                                        break;
                                    default:
                                        getViewState().showError(new YandexTranslateException(translation.getCode(),
                                                translation.getMessage()));
                                        break;
                                }
                                getViewState().showContent();
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            @SuppressWarnings("NullableProblems")
                            public void accept(@NonNull Throwable e) throws Exception {
                                getViewState().showError(e);
                            }
                        });
    }

    /**
     * Persists current translation.
     *
     * @param from        language translated from
     * @param to          language translated to
     * @param text        translated text
     * @param translation translation
     */
    private void saveToHistory(@NonNull String from,
                               @NonNull String to,
                               @NonNull String text,
                               @NonNull String translation) {
        settings.setLanguageFrom(from);
        settings.setLanguageTo(from);
        disposable = translationRepository.saveToRecents(from, to, text, translation)
                .subscribe(
                        new Consumer<Translation>() {
                            @Override
                            @SuppressWarnings("NullableProblems")
                            public void accept(@NonNull Translation model) throws Exception {
                                currentTranslationId = model.getId();
                            }
                        }
                );
    }

    /**
     * Persists favorite state for last successful translation with id {@link #currentTranslationId}
     *
     * @param favorite new favorite state
     */
    public void setFavorite(final boolean favorite) {
        disposable = translationRepository.setFavorite(currentTranslationId, favorite)
                .subscribe(
                        new Consumer<Integer>() {
                            @Override
                            @SuppressWarnings("NullableProblems")
                            public void accept(@NonNull Integer result) throws Exception {
                                getViewState().setFavorite(favorite);
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            @SuppressWarnings("NullableProblems")
                            public void accept(@NonNull Throwable e) throws Exception {
                                getViewState().showError(e);
                            }
                        }
                );
    }
}
