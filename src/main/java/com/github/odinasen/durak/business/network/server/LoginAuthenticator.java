package com.github.odinasen.durak.business.network.server;

import com.github.odinasen.durak.business.network.simon.AuthenticationClient;
import com.github.odinasen.durak.business.network.simon.Callable;
import com.github.odinasen.durak.util.StringUtils;

class LoginAuthenticator {
    AuthenticationClient client;
    String serverPassword;

    LoginAuthenticator(AuthenticationClient client, String serverPassword) {
        this.client = client;
        this.serverPassword = serverPassword;
    }

    boolean isAuthenticated() {
        return hasValidCallable() && isServerPasswordCorrect();
    }

    boolean hasValidCallable() {
        return client.getCallable() != null;
    }

    boolean isServerPasswordCorrect() {
        return StringUtils.stringsAreSame(client.getPassword(), serverPassword);
    }

    boolean clientHasCallable(Callable callable) {
        return hasValidCallable() && client.getCallable().equals(callable);
    }
}