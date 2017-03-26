package ru.vandrikeev.android.phrasebook.presentation.presenter.translation;

import android.support.annotation.NonNull;

import com.arellomobile.mvp.InjectViewState;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import ru.vandrikeev.android.phrasebook.model.Language;
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

    @Inject
    public LanguageSelectionPresenter(@NonNull YandexTranslateService service) {
        this.service = service;
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
        if (language == Language.auto) {
            getViewState().setModel(Language.getValues());
            getViewState().showContent();
        } else {
            disposable = service.getSupportedLanguages(language)
                    .subscribe(
                            new Consumer<SupportedLanguages>() {
                                @Override
                                public void accept(@NonNull SupportedLanguages supportedLanguages) throws Exception {
                                    switch (supportedLanguages.getCode()) {
                                        case 200:
                                            getViewState().setModel(supportedLanguages.getSupportedLanguages());
                                            getViewState().showContent();
                                            break;
                                        default:
                                            getViewState().showError(new YandexTranslateException(supportedLanguages.getCode(), supportedLanguages.getMessage()));
                                            break;
                                    }
                                }
                            },
                            new Consumer<Throwable>() {
                                @Override
                                public void accept(@NonNull Throwable throwable) throws Exception {
                                    getViewState().showError(throwable);
                                }
                            });
        }
    }
}
