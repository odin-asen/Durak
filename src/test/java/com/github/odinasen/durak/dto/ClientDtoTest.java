package com.github.odinasen.durak.dto;

import com.github.odinasen.test.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@Category(UnitTest.class)
public class ClientDtoTest {
    private String UUIDStr = "bla";
    private String testName = "Horst";
    private String spectatorName = "spectator";

    @Test
    public void copyEqualsOriginal() throws Exception {
        ClientDto original = new ClientDto(UUIDStr, testName);
        ClientDto copy = ClientDto.copy(original);
        assertCopyEqualsOriginalButNotIdentical(original, copy);

        original = createSpectator();
        copy = ClientDto.copy(original);
        assertCopyEqualsOriginalButNotIdentical(original, copy);
    }

    private void assertCopyEqualsOriginalButNotIdentical(ClientDto original, ClientDto copy) {
        assertEquals(original.getUuid(), copy.getUuid());
        assertEquals(original.getName(), copy.getName());
        assertEquals(original.isSpectator(), copy.isSpectator());
        assertFalse(original.equals(copy));
    }

    private ClientDto createSpectator() {
        ClientDto spectator = new ClientDto("abcde", spectatorName);
        spectator.setIsSpectator(true);
        return spectator;
    }
}