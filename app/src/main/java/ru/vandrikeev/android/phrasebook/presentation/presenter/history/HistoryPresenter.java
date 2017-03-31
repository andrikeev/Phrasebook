package ru.vandrikeev.android.phrasebook.presentation.presenter.history;

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
 * Presenter for history view.
 */
@InjectViewState
public class HistoryPresenter extends RxPresenter<TranslationListView> {

    @NonNull
    private TranslationRepository repository;

    @Inject
    public HistoryPresenter(@NonNull TranslationRepository repository) {
        this.repository = repository;
    }

    public void getTranslations() {
        getViewState().showLoading();
        dispose();

        disposable = repository.getRecents()
                .subscribe(
                        new Consumer<List<Translation>>() {
                            @Override
                            @SuppressWarnings("NullableProblems")
                            public void accept(@NonNull List<Translation> model) throws Exception {
                                getViewState().setModel(model);
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
        getViewState().showLoading();
        dispose();

        disposable = repository.clearHistory()
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
