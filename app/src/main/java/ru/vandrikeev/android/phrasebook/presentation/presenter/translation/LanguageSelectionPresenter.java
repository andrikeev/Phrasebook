package ru.vandrikeev.android.phrasebook.presentation.presenter.translation;

import android.support.annotation.NonNull;

import com.arellomobile.mvp.InjectViewState;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import ru.vandrikeev.android.phrasebook.Settings;
import ru.vandrikeev.android.phrasebook.model.languages.Language;
import ru.vandrikeev.android.phrasebook.model.languages.LanguageRepository;
import ru.vandrikeev.android.phrasebook.model.network.YandexTranslateException;
import ru.vandrikeev.android.phrasebook.model.network.YandexTranslateService;
import ru.vandrikeev.android.phrasebook.model.responses.SupportedLanguages;
import ru.vandrikeev.android.phrasebook.presentation.presenter.RxPresenter;
import ru.vandrikeev.android.phrasebook.presentation.view.translation.LanguageSelectionView;

/**
 * Presenter for {@link LanguageSelectionView}.
 */
@InjectViewState
public class LanguageSelectionPresenter extends RxPresenter<LanguageSelectionView> {

    @NonNull
    private YandexTranslateService service;

    @NonNull
    private LanguageRepository languageRepository;

    @NonNull
    private Settings settings;

    @Inject
    public LanguageSelectionPresenter(@NonNull YandexTranslateService service,
                                      @NonNull LanguageRepository languageRepository,
                                      @NonNull Settings settings) {
        this.service = service;
        this.languageRepository = languageRepository;
        this.settings = settings;
    }

    /**
     * Loads languages available for translation from specified language. If no language specified will load all
     * languages. Uses {@link RxPresenter#disposable} to prevent memory leaks.
     *
     * @param language language to translate from or null if no language specified
     * @see RxPresenter
     */
    public void loadSupportedLanguages(@NonNull Language language) {
        dispose();

        getViewState().showLoading();
        if (language.isAutodetect()) {
            getViewState().setModel(languageRepository.getLanguages());
            getViewState().showContent();
        } else {
            disposable = service.getSupportedLanguages(language)
                    .subscribe(
                            new Consumer<SupportedLanguages>() {
                                @Override
                                @SuppressWarnings("NullableProblems")
                                public void accept(@NonNull SupportedLanguages supportedLanguages) throws Exception {
                                    switch (supportedLanguages.getCode()) {
                                        case 200:
                                            getViewState().setModel(supportedLanguages.getSupportedLanguages());
                                            getViewState().showContent();
                                            break;
                                        default:
                                            getViewState().showError(new YandexTranslateException(
                                                    supportedLanguages.getCode(), supportedLanguages.getMessage()));
                                            break;
                                    }
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
    }

    /**
     * Set up view on first attach.
     */
    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        Language languageFrom = languageRepository.getLanguageByCode(settings.getLanguageFrom());
        if (languageFrom == null) {
            languageFrom = languageRepository.getAutodetect();
        }
        Language languageTo = languageRepository.getLanguageByCode(settings.getLanguageTo());
        getViewState().setUpSpinners(languageRepository.getLanguagesWithAutodetect(), languageFrom, languageTo);
    }
}
