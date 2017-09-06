package com.github.odinasen.durak.business;

import java.util.Observable;

/**
 * ExtendedObservable implementiert Methoden, die das Observable als geandert setzen und alle Observer updated.
 * <p/>
 * Author: Timm Herrmann
 * Date: 26.05.2017
 */
public class ExtendedObservable extends Observable {

    public void setChangedAndNotifyObservers(Object updateObject) {
        this.setChanged();
        this.notifyObservers(updateObject);
    }

    public void setChangedAndNotifyObservers() {
        this.setChangedAndNotifyObservers(null);
    }
}
