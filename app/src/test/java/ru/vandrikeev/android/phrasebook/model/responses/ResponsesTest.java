package ru.vandrikeev.android.phrasebook.model.responses;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ru.vandrikeev.android.phrasebook.model.languages.Language;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("Duplicates")
public class ResponsesTest {

    @NonNull
    private Gson gson;

    @NonNull
    private Type supportedLanguagesType;

    @NonNull
    private Type translationResponseType;

    private InputStreamReader readTestResource(String fileName) throws FileNotFoundException {
        return new InputStreamReader(this.getClass().getResourceAsStream(fileName));
    }

    @Before
    public void setUp() throws Exception {
        gson = new Gson();
        supportedLanguagesType = new TypeToken<SupportedLanguages>() {
        }.getType();
        translationResponseType = new TypeToken<TranslationResponse>() {
        }.getType();
    }

    @Test
    public void test_deserializeSupportedLanguages_Ok() throws Exception {
        final Object o1 = gson.fromJson(readTestResource("supported_langs_response_ok.json"), supportedLanguagesType);

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
        final Object o1 = gson.fromJson(readTestResource("supported_langs_response_error.json"), supportedLanguagesType);

        assertTrue(o1 instanceof SupportedLanguages);

        final SupportedLanguages supportedLanguages = (SupportedLanguages) o1;

        assertEquals(401, supportedLanguages.getCode());
        assertTrue(supportedLanguages.getSupportedLanguages().isEmpty());
    }

    @Test
    public void test_deserializeSupportedLanguages_Empty() throws Exception {
        final Object o1 = gson.fromJson(readTestResource("supported_langs_response_empty.json"), supportedLanguagesType);

        assertTrue(o1 instanceof SupportedLanguages);

        final SupportedLanguages supportedLanguages = (SupportedLanguages) o1;

        assertEquals(200, supportedLanguages.getCode());
        assertTrue(supportedLanguages.getSupportedLanguages().isEmpty());
    }

    @Test
    public void test_deserializeTranslationResponse_Ok() throws Exception {
        final Object o1 = gson.fromJson(readTestResource("translation_response_ok.json"), translationResponseType);

        assertTrue(o1 instanceof TranslationResponse);

        final TranslationResponse translationResponse = (TranslationResponse) o1;

        assertEquals(200, translationResponse.getCode());
        assertEquals("Привет Мир", translationResponse.getText());
        assertNotNull(translationResponse.getTranslationDirection());
        assertEquals("en-ru", translationResponse.getTranslationDirection());
    }

    @Test
    public void test_deserializeTranslationResponse_Error() throws Exception {
        final Object o1 = gson.fromJson(readTestResource("translation_response_error.json"), translationResponseType);

        assertTrue(o1 instanceof TranslationResponse);

        final TranslationResponse translationResponse = (TranslationResponse) o1;

        assertEquals(502, translationResponse.getCode());
        assertTrue(translationResponse.getText().isEmpty());
        assertNull(translationResponse.getTranslationDirection());
    }
}
