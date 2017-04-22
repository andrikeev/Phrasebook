package ru.vandrikeev.android.phrasebook.model.languages;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import ru.vandrikeev.android.phrasebook.R;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
        for (int i = 0; i < expected.size(); i++) {
            assertEquals("Item doesn't match", expected.get(i), languages.get(i));
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
