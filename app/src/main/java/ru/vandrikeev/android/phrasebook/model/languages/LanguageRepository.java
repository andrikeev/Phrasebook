package ru.vandrikeev.android.phrasebook.model.languages;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import ru.vandrikeev.android.phrasebook.R;

@Singleton
public class LanguageRepository {

    @NonNull
    private Context context;

    @NonNull
    private List<Language> languages;

    @NonNull
    private Map<String, Language> languagesByCode;

    @Inject
    public LanguageRepository(@NonNull Context context) {
        this.context = context;
        this.languages = new ArrayList<>();
        this.languagesByCode = new HashMap<>();
        final String[] codes = context.getResources().getStringArray(R.array.language_codes);
        final String[] names = context.getResources().getStringArray(R.array.language_values);
        for (int i = 0; i < codes.length; i++) {
            final Language language = new Language(codes[i], names[i]);
            languages.add(language);
            languagesByCode.put(language.getCode(), language);
        }
        Collections.sort(languages, Language.COMPARATOR);
        languages.add(0, getAutodetect());
    }

    @NonNull
    public List<Language> getLanguages() {
        return languages;
    }

    @Nullable
    public Language getLanguageByCode(@NonNull String code) {
        return languagesByCode.get(code);
    }

    @NonNull
    public List<Language> getLocalizedLanguages(@NonNull List<Language> languages) {
        final List<Language> result = new ArrayList<>(languages.size());
        for (Language language : languages) {
            if (getLanguageByCode(language.getCode()) != null) {
                result.add(getLanguageByCode(language.getCode()));
            } else {
                result.add(language);
            }
        }
        return result;
    }

    @NonNull
    public Language getAutodetect() {
        return Language.getAutodetect(context);
    }
}
