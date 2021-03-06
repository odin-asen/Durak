package com.github.odinasen.durak.business.network;

import com.github.odinasen.test.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class NetworkMessageTest {

    ClientMessageType testMessageType = ClientMessageType.CLIENT_REMOVED_BY_SERVER;

    @SuppressWarnings("unchecked")
    @Test(expected = AssertionError.class)
    public void passingTwoNullObjectsThrowsAssertion() {
        new NetworkMessage(null, null);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = AssertionError.class)
    public void passingFirstNullObjectThrowsAssertion() {
        new NetworkMessage(null, testMessageType);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = AssertionError.class)
    public void passingSecondNullObjectThrowsAssertion() {
        new NetworkMessage(5, null);
    }
}