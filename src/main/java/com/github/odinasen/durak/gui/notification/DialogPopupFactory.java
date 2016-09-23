package com.github.odinasen.durak.gui.notification;

import com.github.odinasen.durak.util.LoggingUtility;
import com.github.odinasen.durak.gui.notification.dialog.message.MessageDialog;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Klasse fuer statische Methoden, um Dialoge und Popups zu zeigen.
 * <p/>
 * Author: Timm Herrmann<br/>
 * Date: 06.01.14
 */
public class DialogPopupFactory {
    /**
     * Platziert ein Popup oder AbstractDialog oben links.
     */
    public static final int LOCATION_UP_LEFT = 0;
    /**
     * Platziert ein Popup oder AbstractDialog oben rechts.
     */
    public static final int LOCATION_UP_RIGHT = 1;
    /**
     * Platziert ein Popup oder AbstractDialog unten links.
     */
    public static final int LOCATION_DOWN_LEFT = 2;
    /**
     * Platziert ein Popup oder AbstractDialog unten rechts.
     */
    public static final int LOCATION_DOWN_RIGHT = 3;
    /**
     * Platziert ein Popup oder AbstractDialog mittig.
     */
    public static final int LOCATION_CENTRE = 4;
    private static final Logger LOGGER = LoggingUtility.getLogger(DialogPopupFactory.class.getName());
    /**
     * Offset zum Rand des Fensters in Pixel
     */
    private static final int POPUP_OFFSET = 10;
    /**
     * Singelton-Objekt
     */
    private static DialogPopupFactory FACTORY;


    private DialogPopupFactory() {

    }

    public static synchronized DialogPopupFactory getFactory() {
        if (FACTORY == null) {
            FACTORY = new DialogPopupFactory();
        }
        return FACTORY;
    }

    public Stage makeDialog(final Window owner, String message) {
        final Stage dialog;

        try {
            dialog = new MessageDialog(message);
            dialog.initOwner(owner);

            // Damit der AbstractDialog bewegt werden kann
            new DialogDraggableMaker(owner, dialog).makeDraggable();
        } catch (IOException ex) {
            LOGGER.severe("Ein Dialog konnte nicht angezeigt werden.");
            throw new RuntimeException(ex);
        }

        return dialog;
    }

    /**
     * Fuer gruene Popups.
     * Siehe {@link #showWarningPopup(javafx.stage.Window, String, int, double)}
     */
    public void showSuccessPopup(Window parentWindow, String message,
                                 int popupLocation, double openSeconds) {
        if (parentWindow != null) {
            final Tooltip tooltip = createStandardTooltip(message);
            tooltip.setStyle("-fx-background-color: linear-gradient(#00F600, #008800)");
            showTooltip(parentWindow, popupLocation, openSeconds, tooltip);
        }
    }

    /**
     * Fuer eierschalenweisse Popups.
     * Siehe {@link #showWarningPopup(javafx.stage.Window, String, int, double)}
     */
    public void showInfoPopup(Window parentWindow, String message,
                              int popupLocation, double openSeconds) {
        if (parentWindow != null) {
            final Tooltip tooltip = createStandardTooltip(message);

            tooltip.setStyle("-fx-background-color: linear-gradient(#F6E59C, #887A53)");
            showTooltip(parentWindow, popupLocation, openSeconds, tooltip);
        }
    }

    /**
     * Zeigt ein einfaches Popup an, das eine Textnachricht darstellt, gelb ist und
     * nach einer gewissen Zeit von alleine verschwindet.
     *
     * @param parentWindow  Window-Objekt, das das Popup enthaelt.
     * @param message       Dargestellte Nachricht.
     * @param popupLocation Eine der statischen LOCATION-Werten der Klasse.
     * @param openSeconds   Anzahl der Sekunden, die das Popup gezeigt wird.
     */
    public void showWarningPopup(Window parentWindow, String message,
                                 int popupLocation, double openSeconds) {
        if (parentWindow != null) {
            final Tooltip tooltip = createStandardTooltip(message);

            tooltip.setStyle("-fx-background-color: linear-gradient(#F1F600, #888900)");
            showTooltip(parentWindow, popupLocation, openSeconds, tooltip);
        }
    }

    /**
     * Fuer rote Popups.
     * Siehe {@link #showWarningPopup(javafx.stage.Window, String, int, double)}
     */
    public void showErrorPopup(Window parentWindow, String message,
                               int popupLocation, double openSeconds) {
        if (parentWindow != null) {
            final Tooltip tooltip = createStandardTooltip(message);

            tooltip.setStyle("-fx-background-color: linear-gradient(#F69999, #880000)");
            showTooltip(parentWindow, popupLocation, openSeconds, tooltip);
        }
    }

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
        new Thread(new TimedWindowCloser(tooltip, (long) (openSeconds * 1000L))).start();
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
                return ownerX + (owner.getWidth() - popup.getWidth()) / 2;
            default:
                return ownerX + (owner.getWidth() - popup.getWidth()) / 2;
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
                return ownerY + (owner.getHeight() - popup.getHeight()) / 2;
            default:
                return ownerY + (owner.getHeight() - popup.getHeight()) / 2;
        }
    }

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

    /**
     * Mit dieser Klasse kann ein AbstractDialog bewegbar gemacht werden.
     * Der Benutzer kann dann den AbstractDialog mit dem Mauszeiger bewegen.
     */
    private class DialogDraggableMaker {

        private Window owner;
        private Window dialog;

        private DialogDraggableMaker(final Window owner, final Window dialog) {
            this.owner = owner;
            this.dialog = dialog;
        }

        void makeDraggable() {
            final Node root = dialog.getScene().getRoot();
            final DragPoint dragPoint = new DragPoint();

            setOnMousePressedHandler(dialog, root, dragPoint);

            setOnMouseDraggedEvent(dialog, root, dragPoint);

            setWindowShowingHandler(owner, dialog);
            setWindowHidingHandler(owner, dialog);
        }

        private void setWindowHidingHandler(final Window owner, Window dialog) {
            dialog.addEventHandler(WindowEvent.WINDOW_HIDING, new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent windowEvent) {
                    owner.getScene().getRoot().setEffect(null);
                }
            });
        }

        private void setWindowShowingHandler(final Window owner, Window dialog) {
            dialog.addEventHandler(WindowEvent.WINDOW_SHOWING, new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent windowEvent) {
                    owner.getScene().getRoot().setEffect(new BoxBlur());
                }
            });
        }

        private void setOnMouseDraggedEvent(final Window dialog, Node root,
                                            final DragPoint dragPoint) {
            root.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    dialog.setX(mouseEvent.getScreenX() + dragPoint.x);
                    dialog.setY(mouseEvent.getScreenY() + dragPoint.y);
                }
            });
        }

        private void setOnMousePressedHandler(final Window dialog, Node root,
                                              final DragPoint dragPoint) {
            root.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
          /* Speicherung des Abstandes fuer die Mauszugoperation */
                    dragPoint.x = dialog.getX() - mouseEvent.getScreenX();
                    dragPoint.y = dialog.getY() - mouseEvent.getScreenY();
                }
            });
        }

        private class DragPoint {
            private double x, y;
        }
    }
}
