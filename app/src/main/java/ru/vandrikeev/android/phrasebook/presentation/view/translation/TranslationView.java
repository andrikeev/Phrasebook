package ru.vandrikeev.android.phrasebook.presentation.view.translation;

import android.support.annotation.NonNull;

import ru.vandrikeev.android.phrasebook.presentation.view.LceView;

/**
 * Interface for part of translation screen with text controls.
 */
public interface TranslationView extends LceView<String> {

    void setDetectedLanguage(@NonNull String language);

    void setFavorite(boolean favorite);
}
