package ru.vandrikeev.android.phrasebook.model.network;

import org.junit.Assert;
import org.junit.Test;
import ru.vandrikeev.android.phrasebook.R;

public class ErrorUtilsTest {

    @Test
    public void test_getErrorMessage_InvalidKey() throws Exception {
        int messageId = ErrorUtils.getErrorMessage(new YandexTranslateException(401, ""));
        Assert.assertEquals(R.string.error_api_invalid_key, messageId);
    }
}