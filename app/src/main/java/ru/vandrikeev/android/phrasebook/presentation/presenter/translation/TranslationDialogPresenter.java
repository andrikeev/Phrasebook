package ru.vandrikeev.android.phrasebook.presentation.presenter.translation;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.arellomobile.mvp.InjectViewState;

import javax.inject.Inject;

import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import ru.vandrikeev.android.phrasebook.model.languages.Language;
import ru.vandrikeev.android.phrasebook.model.languages.LanguageRepository;
import ru.vandrikeev.android.phrasebook.model.network.YandexTranslateException;
import ru.vandrikeev.android.phrasebook.model.network.YandexTranslateService;
import ru.vandrikeev.android.phrasebook.model.responses.DetectedLanguage;
import ru.vandrikeev.android.phrasebook.model.responses.TranslationResponse;
import ru.vandrikeev.android.phrasebook.model.translations.Translation;
import ru.vandrikeev.android.phrasebook.presentation.presenter.RxPresenter;
import ru.vandrikeev.android.phrasebook.presentation.view.translation.TranslationDialogView;

/**
 * Presenter for {@link TranslationDialogView}.
 */
@InjectViewState
public class TranslationDialogPresenter extends RxPresenter<TranslationDialogView> {

    @NonNull
    private YandexTranslateService service;

    @NonNull
    private LanguageRepository languageRepository;

    @Inject
    public TranslationDialogPresenter(@NonNull YandexTranslateService service,
                                      @NonNull LanguageRepository languageRepository) {
        this.service = service;
        this.languageRepository = languageRepository;
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
        disposable = service.translate(from, to, text)
                .zipWith(service.detectLanguage(text),
                        new BiFunction<TranslationResponse, DetectedLanguage, Translation>() {
                            @NonNull
                            @Override
                            public Translation apply(@NonNull TranslationResponse translationResponse,
                                                     @NonNull DetectedLanguage detectedLanguage) throws Exception {
                                switch (translationResponse.getCode()) {
                                    case 200:
                                        final Pair<Language, Language> translationDirection =
                                                languageRepository.getDirection(
                                                        translationResponse.getTranslationDirection());
                                        final Language realLanguage =
                                                languageRepository.getLanguageByCode(detectedLanguage.getLanguage());
                                        return new Translation(
                                                text,
                                                translationResponse.getText(),
                                                translationDirection.first,
                                                realLanguage,
                                                translationDirection.second
                                        );
                                    default:
                                        throw new YandexTranslateException(translationResponse.getCode(),
                                                translationResponse.getMessage());
                                }
                            }
                        })
                .subscribe(
                        new Consumer<Translation>() {
                            @Override
                            public void accept(@NonNull Translation translation) throws Exception {
                                getViewState().showTranslation(translation);
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            @SuppressWarnings("NullableProblems")
                            public void accept(@NonNull Throwable e) throws Exception {
                                // nothing
                            }
                        });
    }
}
