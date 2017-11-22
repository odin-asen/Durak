package com.github.odinasen.durak.business;

import java.util.Observable;

/**
 * ObserverNotificator implementiert Methoden, die das Observable als geandert setzen und alle
 * Observer updated.
 */
public class ObserverNotificator
        extends Observable {

    public void setChangedAndNotifyObservers(Object updateObject) {
        this.setChanged();
        this.notifyObservers(updateObject);
    }

    public void setChangedAndNotifyObservers() {
        this.setChangedAndNotifyObservers(null);
    }
}
