package com.github.odinasen.durak.gui.controller;

import com.github.odinasen.durak.resources.ResourceGetter;
import com.github.odinasen.durak.util.Assert;
import javafx.fxml.FXML;
import javafx.scene.Parent;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * <p/>
 * Author: Timm Herrmann<br/>
 * Date: 23.03.15
 */
public abstract class JavaFXController {
    /**
     * Ist der Name der FXML Datei
     */
    private String fxmlName;

    private ResourceBundle resourceBundle;

    //================================================================================================
    // Constructors

    /**
     * Initialisiert die Objektvariablen.
     *
     * @param fxmlName
     *         ist der {@link #fxmlName}. Darf nicht null sein.
     * @param resourceBundle
     *         ist das {@link #resourceBundle}
     */
    public JavaFXController(String fxmlName, ResourceBundle resourceBundle) {
        Assert.assertNotNull(fxmlName);

        this.resourceBundle = resourceBundle;
        this.fxmlName = fxmlName;
    }

    //================================================================================================
    // Methods

    /**
     * Laedt {@link #fxmlName} mit dem ResourceBundle.
     *
     * @return Den Wurzelknoten der fxml.
     */
    public Parent initContent() throws IOException {
    /* fxml laden, initialize() wird dadurch auch aufgerufen. */
        return ResourceGetter.loadFXMLPanel(this.fxmlName, this.resourceBundle);
    }

    /**
     * Prueft, ob alle Objektvariablen, die in der fxml-Datei definiert sind, geladen wurden.
     * Initialisiert die Oberflaechen-Komponenten.
     */
    @FXML
    void initialize() {
        this.assertNotNullComponents();
        this.initializePanel();
    }

    /**
     * Initialisiert alle Komponenten des Panels.
     */
    protected abstract void initializePanel();

    /**
     * Prueft alle Komponenten des Panels und wirft eine Assertation, wenn eine Variable nicht
     * korrekt initialisiert wurde.
     *
     * @see {@link #initialize()}
     */
    protected abstract void assertNotNullComponents();

    //================================================================================================
    // Getter and Setter

    /**
     * @return den {@link #fxmlName}
     */
    public String getFxmlName() {
        return this.fxmlName;
    }
    //================================================================================================
    // Inner classes
}
