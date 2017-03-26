package ru.vandrikeev.android.phrasebook.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.MvpPresenter;
import com.arellomobile.mvp.MvpView;

import ru.vandrikeev.android.phrasebook.presentation.view.BaseView;

/**
 * Base MVP fragment.
 */
public abstract class BaseFragment<V extends MvpView, P extends MvpPresenter<V>>
        extends MvpAppCompatFragment implements BaseView {

    protected abstract int getLayoutRes();

    protected abstract P providePresenter();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutRes(), container, false);
    }
}
