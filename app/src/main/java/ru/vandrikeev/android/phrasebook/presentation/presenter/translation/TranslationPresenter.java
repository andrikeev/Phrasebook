package ru.vandrikeev.android.phrasebook.presentation.presenter.translation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import ru.vandrikeev.android.phrasebook.model.translations.HistoryTranslation;
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
    private LanguageRepository languageRepository;

    @Nullable
    private Translation lastTranslation;

    @Inject
    public TranslationPresenter(@NonNull YandexTranslateService service,
                                @NonNull TranslationRepository translationRepository,
                                @NonNull LanguageRepository languageRepository) {
        this.service = service;
        this.translationRepository = translationRepository;
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
                          @NonNull final Language from,
                          @NonNull Language to) {
        dispose();
        getViewState().setLoadingModel(text);
        getViewState().showLoading();
        getViewState().enableFavorite(false);
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
                                getViewState().setModel(translation);
                                getViewState().showContent();
                                saveToHistory(translation);
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(@NonNull Throwable e) throws Exception {
                                getViewState().showError(e);
                            }
                        });
    }

    public void clearView() {
        dispose();
        getViewState().clearView();
    }

    public void setTranslation(@NonNull Translation translation) {
        dispose();
        getViewState().setModel(translation);
        getViewState().showContent();
        saveToHistory(translation);
    }

    /**
     * Persists current translation.
     *
     * @param translation translation
     */
    private void saveToHistory(@NonNull final Translation translation) {
        lastTranslation = translation;
        disposable = translationRepository.saveToRecents(translation)
                .subscribe(
                        new Consumer<HistoryTranslation>() {
                            @Override
                            public void accept(@NonNull HistoryTranslation translation) throws Exception {
                                getViewState().enableFavorite(true);
                                getViewState().setFavorite(translation.isFavorite());
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(@NonNull Throwable e) throws Exception {
                                getViewState().showError(e);
                            }
                        }
                );
    }

    /**
     * Persists favorite state for last successful translation {@link #lastTranslation}
     *
     * @param favorite new favorite state
     */
    public void setFavorite(final boolean favorite) {
        if (lastTranslation != null) {
            getViewState().enableFavorite(false);
            disposable = translationRepository.setFavorite(lastTranslation, favorite)
                    .subscribe(
                            new Consumer<HistoryTranslation>() {
                                @Override
                                public void accept(@NonNull HistoryTranslation translation) throws Exception {
                                    getViewState().setFavorite(translation.isFavorite());
                                    getViewState().enableFavorite(true);
                                }
                            },
                            new Consumer<Throwable>() {
                                @Override
                                public void accept(@NonNull Throwable e) throws Exception {
                                    getViewState().showError(e);
                                }
                            }
                    );
        } else {
            getViewState().enableFavorite(false);
        }
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().clearView();
    }
}
