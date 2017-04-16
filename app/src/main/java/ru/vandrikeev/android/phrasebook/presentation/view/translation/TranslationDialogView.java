package ru.vandrikeev.android.phrasebook.presentation.view.translation;

import android.support.annotation.NonNull;

import ru.vandrikeev.android.phrasebook.model.translations.Translation;
import ru.vandrikeev.android.phrasebook.presentation.view.BaseView;

/**
 * Interface for real time translation dialog.
 */
public interface TranslationDialogView extends BaseView {

    void showTranslation(@NonNull Translation translation);
}
