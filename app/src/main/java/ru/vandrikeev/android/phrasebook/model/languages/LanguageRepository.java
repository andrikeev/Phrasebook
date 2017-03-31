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
        final String[] codes = context.getResources().getStringArray(0);
        final String[] names = context.getResources().getStringArray(0);
        for (int i = 0; i < codes.length; i++) {
            final Language language = new Language(codes[i], names[i]);
            languages.add(language);
            languagesByCode.put(language.getCode(), language);
        }
        Collections.sort(languages, Language.COMPARATOR);
    }

    @NonNull
    public List<Language> getLanguages() {
        return languages;
    }

    @NonNull
    public List<Language> getLanguagesWithAutodetect() {
        final List<Language> result = new ArrayList<>(languages);
        result.add(getAutodetect());
        return result;
    }

    @Nullable
    public Language getLanguageByCode(@NonNull String code) {
        return languagesByCode.get(code);
    }

    @NonNull
    public Language getAutodetect() {
        return Language.getAutodetect(context);
    }
}
