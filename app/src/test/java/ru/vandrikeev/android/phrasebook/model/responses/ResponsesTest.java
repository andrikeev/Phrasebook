package ru.vandrikeev.android.phrasebook.model.responses;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import ru.vandrikeev.android.phrasebook.AbstractTestWithResources;
import ru.vandrikeev.android.phrasebook.model.languages.Language;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ResponsesTest extends AbstractTestWithResources {

    @Test
    public void test_deserializeSupportedLanguages_Ok() throws Exception {
        final Object o1 = getSupportedLanguagesOk();

        assertTrue(o1 instanceof SupportedLanguages);

        final SupportedLanguages supportedLanguages = (SupportedLanguages) o1;

        assertEquals(200, supportedLanguages.getCode());

        final List<Language> expected = new ArrayList<>();
        expected.add(new Language("be", "Белорусский"));
        expected.add(new Language("de", "Немецкий"));
        expected.add(new Language("en", "Английский"));
        expected.add(new Language("es", "Испанский"));
        expected.add(new Language("fr", "Французский"));
        expected.add(new Language("ru", "Русский"));
        expected.add(new Language("zh", "Китайский"));

        assertEquals(expected.size(), supportedLanguages.getSupportedLanguages().size());

        for (Language language : expected) {
            assertTrue(supportedLanguages.getSupportedLanguages().contains(language));
        }
    }

    @Test
    public void test_deserializeSupportedLanguages_Error() throws Exception {
        final Object o1 = getSupportedLanguagesError();

        assertTrue(o1 instanceof SupportedLanguages);

        final SupportedLanguages supportedLanguages = (SupportedLanguages) o1;

        assertEquals(401, supportedLanguages.getCode());
        assertTrue(supportedLanguages.getSupportedLanguages().isEmpty());
    }

    @Test
    public void test_deserializeSupportedLanguages_Empty() throws Exception {
        final Object o1 = getSupportedLanguagesEmpty();

        assertTrue(o1 instanceof SupportedLanguages);

        final SupportedLanguages supportedLanguages = (SupportedLanguages) o1;

        assertEquals(200, supportedLanguages.getCode());
        assertTrue(supportedLanguages.getSupportedLanguages().isEmpty());
    }

    @Test
    public void test_deserializeTranslationResponse_Ok() throws Exception {
        final Object o1 = getTranslationOk();

        assertTrue(o1 instanceof TranslationResponse);

        final TranslationResponse translationResponse = (TranslationResponse) o1;

        assertEquals(200, translationResponse.getCode());
        assertEquals("Привет Мир", translationResponse.getText());
        assertNotNull(translationResponse.getTranslationDirection());
        assertEquals("en-ru", translationResponse.getTranslationDirection());
    }

    @Test
    public void test_deserializeTranslationResponse_Error() throws Exception {
        final Object o1 = getTranslationError();

        assertTrue(o1 instanceof TranslationResponse);

        final TranslationResponse translationResponse = (TranslationResponse) o1;

        assertEquals(502, translationResponse.getCode());
        assertEquals("", translationResponse.getText());
        assertEquals("", translationResponse.getTranslationDirection());
    }
}
