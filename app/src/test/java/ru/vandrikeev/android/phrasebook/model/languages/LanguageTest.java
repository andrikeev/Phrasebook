package ru.vandrikeev.android.phrasebook.model.languages;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import ru.vandrikeev.android.phrasebook.R;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LanguageTest {

    @Mock
    @NonNull
    private Context context;

    @Mock
    @NonNull
    private Resources resources;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(context.getResources()).thenReturn(resources);
        when(context.getString(R.string.spinner_autodetect)).thenReturn("Autodetect");
    }

    @Test
    public void test_getAutodetect() throws Exception {
        final Language language = Language.getAutodetect(context);
        assertTrue(language.isAutodetect());
        assertEquals(language, new Language("auto", "Autodetect"));
    }
}