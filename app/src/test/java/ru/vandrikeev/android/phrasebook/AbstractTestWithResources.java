package ru.vandrikeev.android.phrasebook;

import org.junit.Before;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import ru.vandrikeev.android.phrasebook.model.languages.Language;
import ru.vandrikeev.android.phrasebook.model.responses.DetectedLanguage;
import ru.vandrikeev.android.phrasebook.model.responses.SupportedLanguages;
import ru.vandrikeev.android.phrasebook.model.responses.TranslationResponse;

public abstract class AbstractTestWithResources {

    private ResourcesReader resourcesReader;

    protected Language russian = new Language("ru", "Русский");

    protected Language english = new Language("en", "Английский");

    protected Language apiErrorLanguageArg = new Language("ERROR", "ERROR");

    protected Language networkErrorLanguageArg = new Language("NETWORK_ERROR", "NETWORK_ERROR");

    protected Throwable networkError = new SocketTimeoutException();

    protected static List<Language> getLanguages() {
        final List<Language> languages = new ArrayList<>();
        languages.add(new Language("be", "Белорусский"));
        languages.add(new Language("de", "Немецкий"));
        languages.add(new Language("en", "Английский"));
        languages.add(new Language("es", "Испанский"));
        languages.add(new Language("fr", "Французский"));
        return languages;
    }

    protected static List<Language> getSupportedLanguages() {
        final List<Language> languages = new ArrayList<>();
        languages.add(new Language("be", "Белорусский"));
        languages.add(new Language("de", "Немецкий"));
        languages.add(new Language("en", "Английский"));
        languages.add(new Language("es", "Испанский"));
        languages.add(new Language("fr", "Французский"));
        languages.add(new Language("ru", "Русский"));
        languages.add(new Language("zh", "Китайский"));
        return languages;
    }

    @Before
    public void setUp() throws Exception {
        resourcesReader = new ResourcesReader();
    }

    protected SupportedLanguages getSupportedLanguagesOk() throws Exception {
        return resourcesReader.getSupportedLanguagesOk();
    }

    protected SupportedLanguages getSupportedLanguagesError() throws Exception {
        return resourcesReader.getSupportedLanguagesError();
    }

    protected SupportedLanguages getSupportedLanguagesEmpty() throws Exception {
        return resourcesReader.getSupportedLanguagesEmpty();
    }

    protected TranslationResponse getTranslationOk() throws Exception {
        return resourcesReader.getTranslationOk();
    }

    protected TranslationResponse getTranslationError() throws Exception {
        return resourcesReader.getTranslationError();
    }

    protected DetectedLanguage getDetectedLangOk() throws Exception {
        return resourcesReader.getDetectedLangOk();
    }

    protected DetectedLanguage getDetectedLangFailed() throws Exception {
        return resourcesReader.getDetectedLangFailed();
    }
}
