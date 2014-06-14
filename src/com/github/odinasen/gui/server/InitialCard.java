package com.github.odinasen.gui.server;

import com.github.odinasen.i18n.BundleStrings;
import com.github.odinasen.i18n.I18nSupport;

/**
 * Diese Klasse repraesentiert die initiale Anzahl der Karten bei einem Spiel.
 * <p/>
 * Author: Timm Herrmann<br/>
 * Date: 06.01.14
 */
public enum InitialCard {
  THIRTY_SIX(36),
  FOURTY(40),
  FOURTY_FOUR(44),
  FOURTY_EIGHT(48),
  FIFTY_FIVE(52);

  private int mNumberCards;

  InitialCard(int numberCards) {
    mNumberCards = numberCards;
  }

  public int getNumberCards() {
    return mNumberCards;
  }

  @Override
  public String toString() {
    return mNumberCards + " " + I18nSupport.getValue(BundleStrings.GUI, "cards");
  }
}
