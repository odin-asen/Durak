package com.github.odinasen.durak.business.network;

import com.github.odinasen.durak.business.game.GameAction;
import com.github.odinasen.durak.business.network.simon.Callable;
import com.github.odinasen.durak.business.network.simon.ServerInterface;
import com.github.odinasen.durak.business.network.simon.SessionInterface;
import com.github.odinasen.durak.dto.ClientDto;
import de.root1.simon.annotation.SimonRemote;

/**
 * Eine Klasse, die verschiedene Dienste fuer den Durak-Server bereitstellt.
 * <p/>
 * Author: Timm Herrmann<br/>
 * Date: 23.06.14
 */
@SimonRemote(value={ServerInterface.class, SessionInterface.class})
public class DurakServerService
    implements ServerInterface, SessionInterface {

  @Override
  public boolean login(Callable callable, ClientDto client, String password) {
    return false;
  }

  @Override
  public void logoff(Callable callable) {

  }

  @Override
  public void sendChatMessage(Callable callable, String message) {

  }

  @Override
  public boolean doAction(Callable callable, GameAction action) {
    return false;
  }

  @Override
  public void updateClient(Callable callable, ClientDto client) {

  }
}
