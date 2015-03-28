package com.github.odinasen.durak.business.network.simon;

import com.github.odinasen.durak.business.game.GameAction;
import com.github.odinasen.durak.dto.ClientDto;

/**
 * Dieses Interface enthaehlt Methoden, die ein Client ausfuehren kann, wenn dieser sich ueber
 * das {@link ServerInterface} authentifiziert hat.
 * <p/>
 * Author: Timm Herrmann<br/>
 * Date: 22.06.14
 */
public interface SessionInterface {
  /**
   * Sendet eine Nachricht an alle angemeldeten Clients.
   * @param callable
   *    Ist das Remote-Objekt, das den Client referenziert.
   * @param message
   *    Ist die Nachricht, die gesendet wird.
   */
  public void sendChatMessage(Callable callable, String message);

  /**
   * Fuehrt eine Spielaktion aus. Darf diese Aktion ausgefuehrt werden, werden alle anderen
   * Clients informiert. Ansonsten wird nur der Client, der die Aktion ausfuehren wollte ueber
   * den Abweisungsgrund informiert.
   * @param callable
   *    Ist das Remote-Objekt, das den Client referenziert.
   * @param action
   *    Ist die Spielaktion, die ausgefuehrt wird.
   * @return
   *    True, wenn die Aktion ausgefuehrt wurde.
   *    False, wenn die Aktion gegen Regeln verstoesst und nicht ausgefuehrt wurde.
   */
  public boolean doAction(Callable callable, GameAction action);

  /**
   * Aktualisiert die Informationen eines Clients beim Server. Z.B. kann hiermit ein Name geaendert
   * werden.
   * @param callable
   *    Ist das Remote-Objekt, das den Client referenziert.
   * @param client
   *    Ist das Informationsobjekt des Clients.
   */
  public void updateClient(Callable callable, ClientDto client);
}
