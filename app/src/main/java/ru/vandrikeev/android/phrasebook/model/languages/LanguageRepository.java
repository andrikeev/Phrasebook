package ru.vandrikeev.android.phrasebook.model.languages;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import ru.vandrikeev.android.phrasebook.R;

/**
 * Repository for current available API languages.
 * <p>
 * Languages from <a href="https://tech.yandex.com/translate/doc/dg/concepts/api-overview-docpage/">API</a>
 * are stored in resources in two arrays: {@link R.array#language_codes} for language codes and
 * {@link R.array#language_values} for language names.
 * <p>
 * btw, why there's no 'getAvailableLangs' API method? ¯\_(>_<)_/¯
 */
@Singleton
public class LanguageRepository {

    private static final String SETTINGS = "ru.vandrikeev.android.phrasebook.SETTINGS";
    private static final String LANGUAGE_FROM_KEY = "ru.vandrikeev.android.phrasebook.LANGUAGE_FROM";
    private static final String LANGUAGE_TO_KEY = "ru.vandrikeev.android.phrasebook.LANGUAGE_TO";

    /**
     * Context for accessing resources.
     */
    @NonNull
    private Context context;

    /**
     * Preferences for soring language selections.
     */
    @NonNull
    private SharedPreferences preferences;

    /**
     * List of languages.
     */
    @NonNull
    private List<Language> languages;

    /**
     * Map of languages by their code.
     */
    @NonNull
    private Map<String, Language> languagesByCode;

    @Inject
    public LanguageRepository(@NonNull Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
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

        final Language autodetect = getAutodetect();
        languages.add(0, autodetect);
        languagesByCode.put(autodetect.getCode(), autodetect);
    }

    /**
     * Returns list of available languages with special {@link Language} object Language#createAutodetect(Context).
     *
     * @return list of languages
     */
    @NonNull
    public List<Language> getLanguages() {
        return languages;
    }

    /**
     * Returns {@link Language} with given code or null, if there's no such language.
     *
     * @param code code of language
     * @return language with given code or null
     */
    @NonNull
    public Language getLanguageByCode(@NonNull String code) {
        return languagesByCode.containsKey(code)
                ? languagesByCode.get(code)
                : new Language(code, code);
    }

    /**
     * The method tries to localize the given list of languages to the app language. The appropriate language is
     * selected from the repository by the language code or the localization is preserved if the language does not
     * contained in the repository.
     *
     * @param languages list of languages in possibly wrong localization
     * @return list of localized languages
     */
    @NonNull
    public List<Language> getLocalizedLanguages(@NonNull List<Language> languages) {
        final List<Language> result = new ArrayList<>(languages.size());
        for (Language language : languages) {
            if (languagesByCode.containsKey(language.getCode())) {
                result.add(languagesByCode.get(language.getCode()));
            } else {
                result.add(language);
                languagesByCode.put(language.getCode(), language);
            }
        }
        Collections.sort(result, Language.COMPARATOR);
        return result;
    }

    @NonNull
    public Pair<Language, Language> getDirection(@NonNull String direction) {
        int idx = direction.indexOf('-');
        final String fromCode = direction.substring(0, idx);
        final String toCode = direction.substring(idx + 1, direction.length());
        final Language languageFrom = getLanguageByCode(fromCode);
        final Language languageTo = getLanguageByCode(toCode);
        return new Pair<>(languageFrom, languageTo);
    }

    /**
     * Returns {@link Language} object that represents special case of translation when application should try to
     * automatically detect language of given text.
     *
     * @return 'autodetect' language
     * @see Language#AUTODETECT
     * @see Language#createAutodetect
     */
    @NonNull
    public Language getAutodetect() {
        return Language.createAutodetect(context);
    }

    /**
     * Saves last language user chose to translate from in shared preferences.
     *
     * @param language last language user chose from translate to
     */
    public void saveSelectedLanguageFrom(@NonNull Language language) {
        preferences.edit()
                .putString(LANGUAGE_FROM_KEY, language.getCode())
                .apply();
    }

    /**
     * Returns last language user chose to translate from or default language if there's no last chosen language.
     *
     * @return last language user chose to translate from or default language ('auto')
     * @see Language#AUTODETECT
     * @see Language#createAutodetect
     */
    @NonNull
    public Language getSavedLanguageFrom() {
        final String fromCode = preferences.getString(LANGUAGE_FROM_KEY, null);
        return languagesByCode.containsKey(fromCode)
                ? languagesByCode.get(fromCode)
                : languagesByCode.get(context.getString(R.string.translation_from_default));
    }

    /**
     * Saves last language user chose to translate to in shared preferences.
     *
     * @param language last language user chose to translate to
     */
    public void saveSelectedLanguageTo(@NonNull Language language) {
        preferences.edit()
                .putString(LANGUAGE_TO_KEY, language.getCode())
                .apply();
    }

    /**
     * Returns last language user chose to translate to or default language if there's no last chosen language.
     *
     * @return last language user chose to translate to or default language ('ru')
     */
    @NonNull
    public Language getSavedLanguageTo() {
        final String toCode = preferences.getString(LANGUAGE_TO_KEY, null);
        return languagesByCode.containsKey(toCode)
                ? languagesByCode.get(toCode)
                : languagesByCode.get(context.getString(R.string.translation_to_default));
    }
}
