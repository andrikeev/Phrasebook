package ru.vandrikeev.android.phrasebook.presentation.presenter.translation;

import android.support.annotation.NonNull;
import com.arellomobile.mvp.InjectViewState;
import io.reactivex.functions.Consumer;
import ru.vandrikeev.android.phrasebook.model.languages.Language;
import ru.vandrikeev.android.phrasebook.model.languages.LanguageRepository;
import ru.vandrikeev.android.phrasebook.model.network.YandexTranslateException;
import ru.vandrikeev.android.phrasebook.model.network.YandexTranslateService;
import ru.vandrikeev.android.phrasebook.model.responses.SupportedLanguages;
import ru.vandrikeev.android.phrasebook.presentation.presenter.RxPresenter;
import ru.vandrikeev.android.phrasebook.presentation.view.translation.LanguageSelectionView;

import javax.inject.Inject;
import java.util.List;

/**
 * Presenter for {@link LanguageSelectionView}.
 */
@InjectViewState
public class LanguageSelectionPresenter extends RxPresenter<LanguageSelectionView> {

    @NonNull
    private YandexTranslateService service;

    @NonNull
    private LanguageRepository languageRepository;

    @Inject
    public LanguageSelectionPresenter(@NonNull YandexTranslateService service,
                                      @NonNull LanguageRepository languageRepository) {
        this.service = service;
        this.languageRepository = languageRepository;
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
                                            final List<Language> localizedLanguages =
                                                    languageRepository.getLocalizedLanguages(
                                                            supportedLanguages.getSupportedLanguages());
                                            getViewState().setModel(localizedLanguages);
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
     * Saves last selected language from.
     *
     * @param language selected language
     */
    public void saveLanguageFromSelection(@NonNull Language language) {
        languageRepository.saveSelectedLanguageFrom(language);
    }

    /**
     * Saves last selected language to.
     *
     * @param language selected language
     */
    public void saveLanguageToSelection(@NonNull Language language) {
        languageRepository.saveSelectedLanguageTo(language);
    }

    /**
     * Sets up view on first attach.
     */
    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().setUpWidget(
                languageRepository.getLanguages(),
                languageRepository.getSavedLanguageFrom(),
                languageRepository.getSavedLanguageTo());
    }
}
