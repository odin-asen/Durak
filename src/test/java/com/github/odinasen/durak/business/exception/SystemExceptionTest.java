package com.github.odinasen.durak.business.exception;

import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SystemExceptionTest {

    Exception baseException;
    String baseExceptionMessage = "Upper Exception";

    @Test(expected = AssertionError.class)
    public void wrapAssertsNullParameter() {
        throw SystemException.wrap(null);
    }

    @Test
    public void wrapWithoutProperties() throws Exception {
        SystemException testException =
                initialiseBaseExceptionAndCreateSystemException(baseExceptionMessage);
        Map<String, Object> properties = testException.getProperties();
        assertEquals(0, properties.size());
    }

    private SystemException initialiseBaseExceptionAndCreateSystemException(String baseExceptionMessage) {
        Exception lastCause = new RuntimeException();
        baseException = new IOException(baseExceptionMessage, lastCause);

        return SystemException.wrap(baseException);
    }

    @Test
    public void wrapWithProperties() throws Exception {
        SystemException testException =
                initialiseBaseExceptionAndCreateSystemException(baseExceptionMessage);

        ErrorCode testErrorCode = TestErrorCode.ERROR_ONE_OH_ONE;
        String propKeyOne = "first";
        String propValueOne = "Hallo";
        String propKeyTwo = "second";
        Integer propValueTwo = 7;
        testException.setErrorCode(testErrorCode)
                     .set(propKeyOne, propValueOne)
                     .set(propKeyTwo, propValueTwo);

        Map<String, Object> properties = testException.getProperties();
        assertEquals(2, properties.size());
        assertEquals(testException.get(propKeyOne), propValueOne);
        assertEquals(testException.get(propKeyTwo), propValueTwo);
        assertEquals(testException.getErrorCode(), testErrorCode);
    }

    @Test
    public void printLastCauseStackTrace() throws Exception {
        RuntimeException mockLastCause = Mockito.mock(RuntimeException.class);
        baseException = new IOException(baseExceptionMessage, mockLastCause);

        SystemException testException = SystemException.wrap(baseException);
        PrintStream printStream = System.out;
        testException.printLastCauseStackTrace(printStream);

        verify(mockLastCause, times(1)).printStackTrace(printStream);
    }

    @Test
    public void wrapSystemExceptionWithoutNewErrorCode() throws Exception {
        SystemException wrappedException = new SystemException(TestErrorCode.ERROR_ONE_OH_ONE);
        SystemException testException = SystemException.wrap(wrappedException);

        assertEquals(TestErrorCode.ERROR_ONE_OH_ONE, testException.getErrorCode());
        assertEquals(wrappedException, testException);
    }

    @Test
    public void wrapSystemExceptionWithNewErrorCode() throws Exception {
        SystemException wrappedException = new SystemException(TestErrorCode.ERROR_ONE_OH_ONE);
        SystemException testException =
                SystemException.wrap(wrappedException, TestErrorCode.NEW_ERROR_CODE);

        assertNotSame(wrappedException.getErrorCode(), testException.getErrorCode());
        assertNotSame(wrappedException, testException);
    }

    @Test
    public void wrapInheritsExceptionMessage() {
        SystemException testException =
                initialiseBaseExceptionAndCreateSystemException(baseExceptionMessage);
        assertEquals(baseExceptionMessage, testException.getMessage());
    }

}