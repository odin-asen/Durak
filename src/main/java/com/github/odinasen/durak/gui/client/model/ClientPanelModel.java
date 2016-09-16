package com.github.odinasen.durak.gui.client.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model fuer das Client-Panel
 * <p/>
 * Author: Timm Herrmann<br/>
 * Date: 05.09.2016
 */
public class ClientPanelModel {

	/** Ist der Standardport fuer die Verbindung zum Server. */
	private static final int DEFAULT_SERVER_PORT = 10000;

	/**
	 * Ist der Nickname im Spiel
	 */
	private StringProperty nickname;

	/**
	 * Ist die Serveradresse auf die sich der Client verbindet.
	 */
	private StringProperty serverAddress;

	/**
	 * Ist der Port auf dem sich der Client mit dem Server verbinden soll.
	 */
	private IntegerProperty port;

	/**
	 * Ist das Passwort, mit dem sich der Client am Server authentifiziert.
	 */
	private StringProperty password;

	/**
	 * Gibt an, ob der Client mit einem Server verbunden ist.
	 */
	private boolean connectedToServer;

	//==============================================================================================
	// Constructors
	/**
	 * Initialisiert alle Variablen
	 */
	public ClientPanelModel() {
		this.nickname =new SimpleStringProperty("");
		this.serverAddress = new SimpleStringProperty("localhost");
		this.password = new SimpleStringProperty("");
		this.port = new SimpleIntegerProperty(DEFAULT_SERVER_PORT);
	}
	//==============================================================================================
	// Methods

	//==============================================================================================
	// Getter and Setter

	/**
	 * @return das {@link #port}-Objekt
	 */
	public IntegerProperty getPort() {
		return port;
	}

	/**
	 * @return das {@link #nickname}-Objekt
	 */
	public StringProperty getNickname() {
		return nickname;
	}

	/**
	 * @return das {@link #password}-Objekt
	 */
	public StringProperty getPassword() {
		return password;
	}

	/**
	 * @return das {@link #serverAddress}-Objekt
	 */
	public StringProperty getServerAddress() {
		return serverAddress;
	}

	/**
	 * Setzt das {@link #connectedToServer}-Objekt.
	 *
	 * @param connectedToServer das {@link #connectedToServer}-Objekt
	 */
	public void setConnectedToServer(boolean connectedToServer) {
		this.connectedToServer = connectedToServer;
	}

	/**
	 * @return das {@link #connectedToServer}-Objekt
	 */
	public boolean isConnectedToServer() {
		return connectedToServer;
	}
}
