package com.github.odinasen.durak;

import com.github.odinasen.durak.business.network.client.GameClient;
import com.github.odinasen.durak.business.network.server.GameServer;
import com.github.odinasen.durak.gui.FXMLNames;
import com.github.odinasen.durak.i18n.BundleStrings;
import com.github.odinasen.durak.resources.ResourceGetter;
import com.github.odinasen.durak.util.LoggingUtility;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.awt.*;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class DurakApplication extends Application implements Observer {
    private static final String TITLE = "Durak";

    private static final Logger LOGGER = LoggingUtility.getLogger(DurakApplication.class.getName());

    private GUIMode guiMode;

	/**
	 * @param args Array mit Parametern. Benamte Parameter, die mit -- beginnen, werden in die
	 *             Oberflaeche als Startparameter uebergeben.
	 */
    public static void main(String[] args) {
    /* init logging class */
        LoggingUtility.setFirstTimeLoggingFile(System.getProperty("user.dir")
                + System.getProperty("file.separator") + "clientLog.txt");

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // WICHTIG: Die Zeile muss vor Erstellung von allem anderen aufgerufen werden.
        // Zuerst die Anwendung initalisieren und dann das Fenster anzeigen
        ApplicationStartParameter.getInstance(this.getParameters().getNamed());

        // Jetzt gehts richtig los
        ResourceBundle bundle = ResourceBundle.getBundle(BundleStrings.JAVAFX_BUNDLE_NAME,
                Locale.getDefault());
        Parent root = ResourceGetter.loadFXMLPanel(FXMLNames.MAIN_PANEL, bundle);
        primaryStage.setTitle(TITLE);

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dim = toolkit.getScreenSize();

        Scene scene = new Scene(root, getWindowDimension(dim.getWidth()), getWindowDimension(dim.getHeight()));
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();

        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                try {
                    /* Client zuerst stoppen */
                    GameClient.getInstance().disconnect();
                } catch (Exception ex) {
                    LOGGER.info("Exception on closing connection to a server");
                }

                try {
                    /* Dann Server stoppen */
                    GameServer.getInstance().stopServer();
                } catch (Exception ex) {
                    LOGGER.info("Exception on stopping server");
                }

                /* JavaFX Anwendung beenden */
                Platform.exit();
            }
        });
    }

    /**
     * Liefert einen faktorisierten Wert (< 1) fuer eine Fenstergroesse zurueck. Der Methode kann also Breite und Hoehe
     * uebergeben werden.
     */
    private double getWindowDimension(double size) {
        return 0.75*size;
    }

    @Override
    public void update(Observable o, Object arg) {

    }

    private void refreshHand() {

    }

    private void refreshOpponents() {

    }

    private void refreshTable() {

    }

    /**
     * Setzt den Modus fuer die Oberflache.
     * Moegliche Werte sind statische Felder dieser Klasse.
     */
    public void setMode(GUIMode mode) {
        this.guiMode = mode;
    }

    public enum GUIMode {
        /**
         * Oberflaeche ignoriert keine Netzwerk-Events.
         */
        START,
        /**
         * Oberflaeche ist im Spielmodus.
         */
        PLAYER,
        /**
         * Oberflaeche ist im Beobachtungsmodus.
         */
        OBSERVER
    }
}