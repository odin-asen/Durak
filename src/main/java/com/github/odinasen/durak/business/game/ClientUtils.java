package com.github.odinasen.durak.business.game;

import com.github.odinasen.durak.dto.ClientDto;

import java.util.List;

public class ClientUtils {
    public static boolean listHasClientWithPlayerId(List<ClientDto> clients, Player player) {
        return clients.stream().filter(player::hasSameIdAs).count() > 0;
    }
}
