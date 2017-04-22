package ru.vandrikeev.android.phrasebook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

import ru.vandrikeev.android.phrasebook.model.responses.DetectedLanguage;
import ru.vandrikeev.android.phrasebook.model.responses.SupportedLanguages;
import ru.vandrikeev.android.phrasebook.model.responses.TranslationResponse;

public class ResourcesReader {

    private Gson gson;

    private Type supportedLanguagesType;

    private Type translationResponseType;

    private Type detectionResponseType;

    public ResourcesReader() {
        this.gson = new Gson();
        this.supportedLanguagesType = new TypeToken<SupportedLanguages>() {
        }.getType();
        this.translationResponseType = new TypeToken<TranslationResponse>() {
        }.getType();
        this.detectionResponseType = new TypeToken<DetectedLanguage>() {
        }.getType();
    }

    private InputStreamReader readTestResource(String fileName) throws FileNotFoundException {
        return new InputStreamReader(this.getClass().getResourceAsStream(fileName));
    }

    public SupportedLanguages getSupportedLanguagesOk() throws Exception {
        return gson.fromJson(readTestResource("supported_langs_response_ok.json"), supportedLanguagesType);
    }

    public SupportedLanguages getSupportedLanguagesError() throws Exception {
        return gson.fromJson(readTestResource("supported_langs_response_error.json"), supportedLanguagesType);
    }

    public SupportedLanguages getSupportedLanguagesEmpty() throws Exception {
        return gson.fromJson(readTestResource("supported_langs_response_empty.json"), supportedLanguagesType);
    }

    public TranslationResponse getTranslationOk() throws Exception {
        return gson.fromJson(readTestResource("translation_response_ok.json"), translationResponseType);
    }

    public TranslationResponse getTranslationError() throws Exception {
        return gson.fromJson(readTestResource("translation_response_error.json"), translationResponseType);
    }

    public DetectedLanguage getDetectedLangOk() throws Exception {
        return gson.fromJson(readTestResource("detection_response_ok.json"), detectionResponseType);
    }

    public DetectedLanguage getDetectedLangFailed() throws Exception {
        return gson.fromJson(readTestResource("detection_response_failed.json"), detectionResponseType);
    }
}
