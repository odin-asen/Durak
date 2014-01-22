package com.github.odinasen.gui;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;

/**
 * Klasse fuer statische Methoden, um Dialoge und Popups zu zeigen.
 * <p/>
 * Author: Timm Herrmann<br/>
 * Date: 06.01.14
 */
public class DialogPopupFactory {
  /** Platziert ein Popup oder Dialog oben links. */
  public static final int LOCATION_UP_LEFT = 0;
  /** Platziert ein Popup oder Dialog oben rechts. */
  public static final int LOCATION_UP_RIGHT = 1;
  /** Platziert ein Popup oder Dialog unten links. */
  public static final int LOCATION_DOWN_LEFT = 2;
  /** Platziert ein Popup oder Dialog unten rechts. */
  public static final int LOCATION_DOWN_RIGHT = 3;
  /** Platziert ein Popup oder Dialog mittig. */
  public static final int LOCATION_CENTRE = 4;

  private static final int POPUP_OFFSET = 10;

  private static DialogPopupFactory FACTORY;

  /****************/
  /* Constructors */

  private DialogPopupFactory() {

  }

  /*     End      */
  /****************/

  /***********/
  /* Methods */

  /**
   * Fuer gruene Popups.
   * Siehe {@link #showWarningPopup(javafx.stage.Window, String, int, double)}
   */
  public void showSuccessPopup(Window parentWindow, String message,
                               int popupLocation, double openSeconds) {
    final Tooltip tooltip = createStandardTooltip(message);

    tooltip.setStyle("-fx-background-color: linear-gradient(#00F600, #008800)");
    showTooltip(parentWindow, popupLocation, openSeconds, tooltip);
  }

  /**
   * Fuer eierschalenweisse Popups.
   * Siehe {@link #showWarningPopup(javafx.stage.Window, String, int, double)}
   */
  public void showInfoPopup(Window parentWindow, String message,
                            int popupLocation, double openSeconds) {
    final Tooltip tooltip = createStandardTooltip(message);

    tooltip.setStyle("-fx-background-color: linear-gradient(#F6E59C, #887A53)");
    showTooltip(parentWindow, popupLocation, openSeconds, tooltip);
  }

  /**
   * Zeigt ein einfaches Popup an, das eine Textnachricht darstellt, gelb ist und
   * nach einer gewissen Zeit von alleine verschwindet.
   * @param parentWindow Window-Objekt, das das Popup enthaelt.
   * @param message Dargestellte Nachricht.
   * @param popupLocation Eine der statischen LOCATION-Werten der Klasse.
   * @param openSeconds Anzahl der Sekunden, die das Popup gezeigt wird.
   */
  public void showWarningPopup(Window parentWindow, String message,
                               int popupLocation, double openSeconds) {
    final Tooltip tooltip = createStandardTooltip(message);

    tooltip.setStyle("-fx-background-color: linear-gradient(#F1F600, #888900)");
    showTooltip(parentWindow, popupLocation, openSeconds, tooltip);
  }

  /**
   * Fuer rote Popups.
   * Siehe {@link #showWarningPopup(javafx.stage.Window, String, int, double)}
   */
  public void showErrorPopup(Window parentWindow, String message,
                             int popupLocation, double openSeconds) {
    final Tooltip tooltip = createStandardTooltip(message);

    tooltip.setStyle("-fx-background-color: linear-gradient(#F69999, #880000)");
    showTooltip(parentWindow, popupLocation, openSeconds, tooltip);
  }

  /*   End   */
  /***********/

  /*******************/
  /* Private Methods */

  private Tooltip createStandardTooltip(String message) {
    final Tooltip tooltip = new Tooltip(message);
    tooltip.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent mouseEvent) {
        tooltip.hide();
      }
    });
    return tooltip;
  }

  private void showTooltip(Window parentWindow, int popupLocation, double openSeconds, Tooltip tooltip) {
    tooltip.show(parentWindow);
    tooltip.setX(computeXLocation(parentWindow, tooltip, popupLocation));
    tooltip.setY(computeYLocation(parentWindow, tooltip, popupLocation));
    new Thread(new TimedWindowCloser(tooltip, (long) (openSeconds*1000L))).start();
  }

  private void pause(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private double computeXLocation(Window owner, Window popup, int popupLocation) {
    final double ownerX = owner.getX();
    switch (popupLocation) {
      case LOCATION_DOWN_LEFT:
        return ownerX + POPUP_OFFSET;
      case LOCATION_DOWN_RIGHT:
        return ownerX + owner.getWidth() - popup.getWidth() - POPUP_OFFSET;
      case LOCATION_UP_LEFT:
        return ownerX + POPUP_OFFSET;
      case LOCATION_UP_RIGHT:
        return ownerX + owner.getWidth() - popup.getWidth() - POPUP_OFFSET;
      case LOCATION_CENTRE:
        return ownerX + (owner.getWidth()-popup.getWidth())/2;
      default: return ownerX + (owner.getWidth()-popup.getWidth())/2;
    }
  }

  private double computeYLocation(Window owner, Window popup, int popupLocation) {
    final double ownerY = owner.getY();
    switch (popupLocation) {
      case LOCATION_DOWN_LEFT:
        return ownerY + owner.getHeight() - popup.getHeight() - POPUP_OFFSET;
      case LOCATION_DOWN_RIGHT:
        return ownerY + owner.getHeight() - popup.getHeight() - POPUP_OFFSET;
      case LOCATION_UP_LEFT:
        return ownerY + POPUP_OFFSET;
      case LOCATION_UP_RIGHT:
        return ownerY + POPUP_OFFSET;
      case LOCATION_CENTRE:
        return ownerY + (owner.getHeight() - popup.getHeight())/2;
      default: return ownerY + (owner.getHeight() - popup.getHeight())/2;
    }
  }

  /*       End       */
  /*******************/

  /*********************/
  /* Getter and Setter */

  public static synchronized DialogPopupFactory getFactory() {
    if(FACTORY == null) {
      FACTORY = new DialogPopupFactory();
    }
    return FACTORY;
  }

  /*        End        */
  /*********************/

  /*****************/
  /* Inner classes */

  private class TimedWindowCloser implements Runnable {
    private Window window;
    private long timeInMillis;

    TimedWindowCloser(Window window, long timeInMillis) {
      this.window = window;
      this.timeInMillis = timeInMillis;
    }

    @Override
    public void run() {
      pause(timeInMillis);
      Platform.runLater(new Runnable() {
        @Override
        public void run() {
          if (window.isShowing())
            window.hide();
        }
      });
    }
  }
  /*      End      */
  /*****************/
}
