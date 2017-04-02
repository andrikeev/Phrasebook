package ru.vandrikeev.android.phrasebook.presentation.presenter.favorites;

import android.support.annotation.NonNull;

import com.arellomobile.mvp.InjectViewState;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import ru.vandrikeev.android.phrasebook.model.translations.Translation;
import ru.vandrikeev.android.phrasebook.model.translations.TranslationRepository;
import ru.vandrikeev.android.phrasebook.presentation.presenter.RxPresenter;
import ru.vandrikeev.android.phrasebook.presentation.view.history.TranslationListView;

/**
 * Presenter for favorites view.
 */
@InjectViewState
public class FavoritesPresenter extends RxPresenter<TranslationListView> {

    @NonNull
    private TranslationRepository repository;

    @Inject
    public FavoritesPresenter(@NonNull TranslationRepository repository) {
        this.repository = repository;
    }

    public void getTranslations() {
        dispose();
        getViewState().showLoading();
        getViewState().clearContent();

        disposable = repository.getFavorites()
                .subscribe(
                        new Consumer<List<Translation>>() {
                            @Override
                            @SuppressWarnings("NullableProblems")
                            public void accept(@NonNull List<Translation> model) throws Exception {
                                if (model.isEmpty()) {
                                    getViewState().showEmpty();
                                } else {
                                    getViewState().setModel(model);
                                    getViewState().showContent();
                                }
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

    public void clear() {
        dispose();
        getViewState().showLoading();

        disposable = repository.clearFavorites()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    @SuppressWarnings("NullableProblems")
                    public void accept(@NonNull Integer deleted) throws Exception {
                        getViewState().clearContent();
                        getViewState().showEmpty();
                    }
                });
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getTranslations();
    }
}