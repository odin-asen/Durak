package com.github.odinasen.durak.i18n;

import com.github.odinasen.test.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class I18nSupportTest {
    private String error1DeText = "Fehler, die Welt geht unter!";
    private String error1Text = "Error, the world goes down!";
    private String error2DeText = "Fehler, aber die Welt dreht sich noch.";
    private String error2Text = "Error, but the world is still turning.";

    private static String TEST_BUNDLE_NAME = "testValues.testvalues";

    @Test
    public void getExceptionReturnsMessageStringInGerman() throws Exception {
        Locale.setDefault(Locale.GERMAN);
        String message = I18nSupport.getException(ErrorCodeTestImpl.ARMAGEDDON);
        assertEquals(error1DeText, message);

        message = I18nSupport.getException(ErrorCodeTestImpl.RED_ALERT);
        assertEquals(error2DeText, message);
    }

    @Test
    public void getExceptionReturnsMessageStringInDefaultLanguage() throws Exception {
        Locale.setDefault(Locale.ENGLISH);
        String message = I18nSupport.getException(ErrorCodeTestImpl.ARMAGEDDON);
        assertEquals(error1Text, message);

        message = I18nSupport.getException(ErrorCodeTestImpl.RED_ALERT);
        assertEquals(error2Text, message);
    }

    @Test
    public void getExceptionReturnsExceptionMessageWithParameter() throws Exception {
        Locale.setDefault(Locale.GERMAN);
        String parameter1 = "Ratte";
        Object parameter2 = new Object();
        String message =
                I18nSupport.getException(ErrorCodeTestImpl.NOT_IN_MESSAGE_BUNDLE, parameter1,
                                         parameter2);
        String expectedMessage = "Exception: com.github.odinasen.durak.i18n.ErrorCodeTestImpl";
        assertTrue("Exception does not start with '" + expectedMessage + "'",
                   message.startsWith(expectedMessage));

        assertTrue("Exception message does not 'Parameter:'", message.contains("Parameter:"));

        String parameterMessage = 1 + " - " + parameter1;
        assertTrue(
                "Exception message does not contain formatted value of '" + parameter1 + "'",
                message.contains(parameterMessage));

        parameterMessage = 2 + " - " + parameter2;
        assertTrue(
                "Exception message does not contain formatted value of '" + parameter2 + "'",
                message.contains(parameterMessage));
    }

    @Test
    public void getExceptionReturnsExceptionMessage() throws Exception {
        Locale.setDefault(Locale.GERMAN);
        String message = I18nSupport.getException(ErrorCodeTestImpl.NOT_IN_MESSAGE_BUNDLE);
        assertEquals("Exception: com.github.odinasen.durak.i18n.ErrorCodeTestImpl", message);
    }

    @Test
    public void getValueInEnglish() throws Exception {
        Locale.setDefault(Locale.ENGLISH);
        I18nSupport i18nMapper = I18nSupport.getInstance();
        String value = i18nMapper.getBundleValue(TEST_BUNDLE_NAME, "test.value");
        assertEquals("test value", value);

        value = i18nMapper.getBundleValue(TEST_BUNDLE_NAME, "second.test.value");
        assertEquals("second value", value);
    }

    @Test
    public void getValueInGerman() throws Exception {
        Locale.setDefault(Locale.GERMAN);
        I18nSupport i18nMapper = I18nSupport.getInstance();
        String value = i18nMapper.getBundleValue(TEST_BUNDLE_NAME, "test.value");
        assertEquals("test wert", value);

        value = i18nMapper.getBundleValue(TEST_BUNDLE_NAME, "second.test.value");
        assertEquals("zweiter wert", value);
    }

    @Test
    public void getValueWithParametersInEnglish() throws Exception {
        Locale.setDefault(Locale.ENGLISH);
        I18nSupport i18nMapper = I18nSupport.getInstance();
        String parameter = "parameter";
        String value = i18nMapper.getBundleValue(TEST_BUNDLE_NAME, "value.with.0", parameter);
        assertEquals("Value with a " + parameter, value);
    }

    @Test
    public void getValueWithParametersInGerman() throws Exception {
        Locale.setDefault(Locale.GERMAN);
        I18nSupport i18nMapper = I18nSupport.getInstance();
        String parameter = "Parameter";
        String value = i18nMapper.getBundleValue(TEST_BUNDLE_NAME, "value.with.0", parameter);
        assertEquals("Wert mit einem " + parameter, value);
    }

    @Test
    public void getValueCannotFindValue() throws Exception {
        String expectedValue = "!" + TEST_BUNDLE_NAME + "/unknown.value!";
        I18nSupport i18nMapper = I18nSupport.getInstance();
        String value = i18nMapper.getBundleValue(TEST_BUNDLE_NAME, "unknown.value");
        assertEquals(expectedValue, value);
    }
}