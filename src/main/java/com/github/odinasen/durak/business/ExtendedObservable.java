package com.github.odinasen.durak.business;

import java.util.Observable;

/**
 * ExtendedObservable implementiert Methoden, die das Observable als geandert setzen und alle Observer updated.
 * <p/>
 * Author: Timm Herrmann
 * Date: 26.05.2017
 */
public class ExtendedObservable extends Observable {

    /** Benachrichtigt alle Observer und aendert den Status auf "changed" */
    public void setChangedAndUpdate(Object updateObject) {
        this.setChanged();
        this.notifyObservers(updateObject);
    }

    public void setChangedAndUpdate() {
        this.setChangedAndUpdate(null);
    }
}