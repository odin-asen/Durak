package com.github.odinasen.business.network;

/**
 * Enthaehlt nur eine Callback-Methode. Implementierte Klassen werden vorzugsweise als
 * Remote-Objekte verwendet.
 * <p/>
 * Author: Timm Herrmann<br/>
 * Date: 23.06.14
 */
public interface Callable {
  /**
   * Ist eine Callback-Methode, die je nach Implementierung etwas anderes ausfuehrt.
   * @param parameter
   *    Ist ein Parameter-Objekt, der der Methode uebergeben werden kann.
   */
  public void callback(Object parameter);
}
