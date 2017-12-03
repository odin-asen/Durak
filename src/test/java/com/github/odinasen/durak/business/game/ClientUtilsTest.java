package com.github.odinasen.durak.business.game;

import com.github.odinasen.durak.dto.ClientDto;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ClientUtilsTest {
    String testClientName = "Horst";

    @Test
    public void testListHasClientWithPlayerId() throws Exception {
        List<ClientDto> clients = createClientList(3);
        Player playerWithClientId = new Player(clients.get(2));

        boolean hasSameId = ClientUtils.listHasClientWithPlayerId(clients, playerWithClientId);
        assertTrue(hasSameId);

        Player playerWithRandomId = new Player(createClientDtoWithDefaultName());

        hasSameId = ClientUtils.listHasClientWithPlayerId(clients, playerWithRandomId);
        assertFalse(hasSameId);
    }

    private List<ClientDto> createClientList(int numberOfClients) {
        List<ClientDto> clients = new ArrayList<>(numberOfClients);
        for (int i = 0; i < numberOfClients; i++) {
            ClientDto clientDto = createClientDtoWithDefaultNameAndSuffix(Integer.toString(i));
            clients.add(clientDto);
        }

        return clients;
    }

    private ClientDto createClientDtoWithDefaultNameAndSuffix(String nameSuffix) {
        final String clientName;
        if (nameSuffix != null) {
            clientName = testClientName + nameSuffix;
        } else {
            clientName = testClientName;
        }

        return new ClientDto(UUID.randomUUID().toString(), clientName);
    }

    private ClientDto createClientDtoWithDefaultName() {
        return createClientDtoWithDefaultNameAndSuffix(null);
    }
}