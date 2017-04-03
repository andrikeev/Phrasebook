package ru.vandrikeev.android.phrasebook.model.languages;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import ru.vandrikeev.android.phrasebook.R;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class LanguageRepositoryTest {

    @Mock
    @NonNull
    private Context context;

    @Mock
    @NonNull
    private Resources resources;

    @NonNull
    private LanguageRepository languageRepository;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(context.getResources()).thenReturn(resources);
        when(context.getString(R.string.spinner_autodetect)).thenReturn("Autodetect");
        when(resources.getStringArray(R.array.language_codes)).thenReturn(new String[]{"ru", "en"});
        when(resources.getStringArray(R.array.language_values)).thenReturn(new String[]{"Russian", "English"});

        languageRepository = new LanguageRepository(context);

        verify(context, times(2)).getResources();
        verify(resources, times(1)).getStringArray(R.array.language_codes);
        verify(resources, times(1)).getStringArray(R.array.language_values);
        verify(context, times(1)).getString(R.string.spinner_autodetect);
    }

    @Test
    public void test_getLanguages() throws Exception {
        clearInvocations(context);
        final List<Language> expected = new ArrayList<>(2);
        expected.add(new Language("auto", "Autodetect"));
        expected.add(new Language("en", "English"));
        expected.add(new Language("ru", "Russian"));

        final List<Language> languages = languageRepository.getLanguages();
        assertEquals("Size doesn't match", expected.size(), languages.size());
        for (Language language : expected) {
            assertTrue(String.format("List does not contain required language %s", language),
                    languages.contains(language));
        }
    }

    @Test
    public void test_getLanguageByCode() throws Exception {
        final Language expected = new Language("en", "English");
        final Language language = languageRepository.getLanguageByCode("en");
        assertEquals(expected, language);
    }

    @Test
    public void test_getAutodetect() throws Exception {
        clearInvocations(context);
        final Language expected = new Language("auto", "Autodetect");
        final Language language = languageRepository.getAutodetect();
        assertEquals(expected, language);
        verify(context, times(1)).getString(R.string.spinner_autodetect);
    }
}
