package ru.vandrikeev.android.phrasebook.presentation.view.translation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import ru.vandrikeev.android.phrasebook.model.languages.Language;
import ru.vandrikeev.android.phrasebook.presentation.view.LceView;

/**
 * Interface for part of translation screen with language choosing controls.
 */
public interface LanguageSelectionView extends LceView<List<Language>> {

    void setUpSpinners(@NonNull List<Language> languages,
                       @NonNull Language languageFromPreference,
                       @Nullable Language languageToPreference);
}
