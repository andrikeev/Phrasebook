package ru.vandrikeev.android.phrasebook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class SettingsTest {

    @Mock
    @NonNull
    private Context context;

    @Mock
    @NonNull
    private SharedPreferences preferences;

    @Mock
    @NonNull
    private SharedPreferences.Editor editor;

    @NonNull
    private Settings settings;

    @Before
    @SuppressLint("CommitPrefEdits")
    public void setUp() throws Exception {
        initMocks(this);

        when(context.getSharedPreferences("ru.vandrikeev.android.phrasebook.SETTINGS", Context.MODE_PRIVATE)).thenReturn(preferences);
        when(context.getString(R.string.translation_from_default)).thenReturn("auto");
        when(context.getString(R.string.translation_to_default)).thenReturn("ru");

        when(preferences.getString("ru.vandrikeev.android.phrasebook.LANGUAGE_FROM", "auto")).thenReturn("en");
        when(preferences.getString("ru.vandrikeev.android.phrasebook.LANGUAGE_TO", "ru")).thenReturn("ru");
        when(preferences.edit()).thenReturn(editor);

        when(editor.putString(eq("ru.vandrikeev.android.phrasebook.LANGUAGE_FROM"), anyString())).thenReturn(editor);
        when(editor.putString(eq("ru.vandrikeev.android.phrasebook.LANGUAGE_TO"), anyString())).thenReturn(editor);

        settings = new Settings(context);
    }

    @Test
    public void getLanguageFrom() throws Exception {
        reset(preferences);
        reset(context);
        final String languageCode = settings.getLanguageFrom();
        assertEquals("en", languageCode);
        verify(preferences, times(1)).getString(anyString(), anyString());
        verify(context, times(1)).getString(R.string.translation_from_default);
    }

    @Test
    @SuppressLint("CommitPrefEdits")
    public void setLanguageFrom() throws Exception {
        reset(preferences);
        reset(context);
        settings.setLanguageFrom("ru");
        verify(preferences, times(1)).edit();
        verify(editor, times(1)).putString(eq("ru.vandrikeev.android.phrasebook.LANGUAGE_FROM"), anyString());
        verify(editor, times(1)).apply();
    }

    @Test
    public void getLanguageTo() throws Exception {
        reset(preferences);
        reset(context);
        final String languageCode = settings.getLanguageFrom();
        assertEquals("ru", languageCode);
        verify(preferences, times(1)).getString(anyString(), anyString());
        verify(context, times(1)).getString(R.string.translation_from_default);
    }

    @Test
    @SuppressLint("CommitPrefEdits")
    public void setLanguageTo() throws Exception {
        reset(preferences);
        reset(context);
        settings.setLanguageTo("en");
        verify(preferences, times(1)).edit();
        verify(editor, times(1)).putString(eq("ru.vandrikeev.android.phrasebook.LANGUAGE_TO"), anyString());
        verify(editor, times(1)).apply();

    }

}