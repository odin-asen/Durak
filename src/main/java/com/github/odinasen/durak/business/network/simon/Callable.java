package com.github.odinasen.durak.business.network.simon;

import com.github.odinasen.durak.business.network.ClientMessageType;

/**
 * Enthaehlt nur Callback-Methoden. Implementierte Klassen werden vorzugsweise als
 * Remote-Objekte verwendet.
 * <p/>
 * Author: Timm Herrmann<br/>
 * Date: 23.06.14
 */
public interface Callable {
  /**
   * Ist eine Callback-Methode, mit der eine Nachricht fuer den Client uebertragen werden kann.
   * @param parameter
   *    Ist der Typ der Nachricht.
   */
  public void sendClientMessage(ClientMessageType parameter);
}
