package ru.vandrikeev.android.phrasebook.presentation.presenter.translation;

import android.support.annotation.NonNull;
import android.util.Pair;

import com.arellomobile.mvp.InjectViewState;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import ru.vandrikeev.android.phrasebook.model.Language;
import ru.vandrikeev.android.phrasebook.model.network.YandexTranslateException;
import ru.vandrikeev.android.phrasebook.model.network.YandexTranslateService;
import ru.vandrikeev.android.phrasebook.model.responses.Translation;
import ru.vandrikeev.android.phrasebook.presentation.presenter.RxPresenter;
import ru.vandrikeev.android.phrasebook.presentation.view.translation.TranslationView;

/**
 * Presenter for {@link TranslationView}.
 */
@InjectViewState
public class TranslationPresenter extends RxPresenter<TranslationView> {

    @NonNull
    private YandexTranslateService service;

    @Inject
    public TranslationPresenter(@NonNull YandexTranslateService service) {
        this.service = service;
    }

    /**
     * Asynchronously translates text from one language to another.
     *
     * @param text text for translation
     * @param from any {@link Language} enum
     * @param to   any {@link Language} enum except {@link Language#auto}
     */
    public void translate(@NonNull final String text,
                          @NonNull Language from,
                          @NonNull Language to) {
        dispose();

        getViewState().showLoading();
        disposable = service.translate(from, to, text)
                .subscribe(
                        new Consumer<Translation>() {
                            @Override
                            public void accept(@NonNull Translation translation) throws Exception {
                                switch (translation.getCode()) {
                                    case 200:
                                        getViewState().setModel(translation.getText());
                                        Pair<Language, Language> translationDirection =
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
                                        final Throwable error = new YandexTranslateException(translation.getCode(),
                                                translation.getMessage());
                                        getViewState().showError(error);
                                        break;
                                }
                                getViewState().showContent();
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception {
                                getViewState().showError(throwable);
                            }
                        });
    }

    public void saveToFavorites(@NonNull Language from,
                                @NonNull Language to,
                                @NonNull String text,
                                @NonNull String translation) {
        // TODO: save to favorites
    }

    private void saveToHistory(@NonNull Language from,
                               @NonNull Language to,
                               @NonNull String text,
                               @NonNull String translation) {
        // TODO: save to history
    }
}
