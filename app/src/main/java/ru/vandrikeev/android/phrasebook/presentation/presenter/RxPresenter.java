package ru.vandrikeev.android.phrasebook.presentation.presenter;

import android.support.annotation.Nullable;

import com.arellomobile.mvp.MvpPresenter;

import io.reactivex.disposables.Disposable;
import ru.vandrikeev.android.phrasebook.presentation.view.BaseView;

/**
 * Base presenter that uses Rx for data loading.
 */
public abstract class RxPresenter<V extends BaseView> extends MvpPresenter<V> {

    @Nullable
    protected Disposable disposable;

    @Override
    public void destroyView(V view) {
        dispose();
        super.destroyView(view);
    }

    protected void dispose() {
        if (disposable != null) {
            disposable.dispose();
            disposable = null;
        }
    }
}
