package com.github.odinasen.durak.business.network.server;

import com.github.odinasen.durak.business.network.simon.Callable;
import com.github.odinasen.durak.util.StringUtils;

class LoginAuthenticator {
    String clientName;
    String serverPassword;
    Callable callable;

    LoginAuthenticator(String clientName, String serverPassword, Callable callable) {
        this.clientName = clientName;
        this.serverPassword = serverPassword;
        this.callable = callable;
    }

    boolean passwordIsCorrect(String password) {
        return hasValidCallable() && isPasswordCorrect(password);
    }

    boolean hasValidCallable() {
        return callable != null;
    }

    boolean isPasswordCorrect(String password) {
        return StringUtils.stringsAreSame(password, serverPassword);
    }

    boolean clientHasCallable(Callable otherCallable) {
        return hasValidCallable() && callable.equals(otherCallable);
    }
}