package com.github.odinasen.durak;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Testklasse fuer Startparameter.
 * Created by tih on 18.09.2016.
 */
public class ApplicationStartParameterTest {

    private static final String SERVER_PORT_PARAM = "sPort";
    private static final String SERVER_PASSWORD_PARAM = "sPwd";
    private static final String SERVER_PASSWORD = "bla";

    private static final String CLIENT_PORT_PARAM = "cPort";
    private static final String CLIENT_PASSWORD_PARAM = "cPwd";
    private static final String CLIENT_NAME_PARAM = "cName";
    private static final String CLIENT_NAME = "testName";
    private static final String CLIENT_PASSWORD = "bla";


    /**
     * Testet getInstance-Methoden und dass die Werte der Singletonvariablen nicht durch erneute Parameter
     * veraendert werden.
     */
    @Test
    public void getInstance() throws Exception {
        ApplicationStartParameter startParameter = ApplicationStartParameter.getInstance();

        Assert.assertSame(0, startParameter.getServerPort());
        Assert.assertSame(null, startParameter.getServerPwd());

        // Neu mit Werten initialisieren und pruefen, ob Werten nicht ueberschrieben werden
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(SERVER_PORT_PARAM, "1");
        paramMap.put(SERVER_PASSWORD_PARAM, SERVER_PASSWORD);
        startParameter = ApplicationStartParameter.getInstance(paramMap, true);

        // getInstance(Map) veraendert nichts
        paramMap.put(SERVER_PORT_PARAM, "2");
        paramMap.put(SERVER_PASSWORD_PARAM, SERVER_PASSWORD + "2");
        ApplicationStartParameter startParameter2 = ApplicationStartParameter.getInstance(paramMap);

        Assert.assertSame(startParameter.getServerPort(), startParameter2.getServerPort());
        Assert.assertSame(startParameter.getServerPwd(), startParameter2.getServerPwd());

        //getInstance() veraendert nichts
        startParameter2 = ApplicationStartParameter.getInstance();
        Assert.assertSame(startParameter.getServerPort(), startParameter2.getServerPort());
        Assert.assertSame(startParameter.getServerPwd(), startParameter2.getServerPwd());

        //getInstance(Map,false) veraendert nichts
        paramMap.clear();
        paramMap.put(SERVER_PORT_PARAM, "4");
        paramMap.put(SERVER_PASSWORD_PARAM, SERVER_PASSWORD + "1");
        startParameter2 = ApplicationStartParameter.getInstance(paramMap, false);
        Assert.assertSame(startParameter.getServerPort(), startParameter2.getServerPort());
        Assert.assertSame(startParameter.getServerPwd(), startParameter2.getServerPwd());

    }

    // Der Serverport wird auf 0 gesetzt, wenn ein Wert nicht geparst werden kann
    @Test
    public void getNotParsableServerPort() throws Exception {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(SERVER_PORT_PARAM, "eintext");
        ApplicationStartParameter startParameter = ApplicationStartParameter.getInstance(paramMap, true);

        Assert.assertSame("String value should not be parsable and deliver zero.", startParameter.getServerPort(), 0);
    }

    // Der Clientport wird auf 0 gesetzt, wenn ein Wert nicht geparst werden kann
    @Test
    public void getNotParsableClientPort() throws Exception {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(CLIENT_PORT_PARAM, "eintext");
        ApplicationStartParameter startParameter = ApplicationStartParameter.getInstance(paramMap, true);

        Assert.assertSame("String value should not be parsable and deliver zero.", startParameter.getClientPort(), 0);
    }

    @Test
    public void getServerPort() throws Exception {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(SERVER_PORT_PARAM, "1000");
        ApplicationStartParameter startParameter = ApplicationStartParameter.getInstance(paramMap, true);

        Assert.assertEquals(1000, startParameter.getServerPort());
    }

    @Test
    public void getClientPort() throws Exception {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(CLIENT_PORT_PARAM, "1000");
        ApplicationStartParameter startParameter = ApplicationStartParameter.getInstance(paramMap, true);

        Assert.assertEquals(1000, startParameter.getClientPort());
    }

    @Test
    public void getServerPwd() throws Exception {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(SERVER_PASSWORD_PARAM, SERVER_PASSWORD);
        ApplicationStartParameter startParameter = ApplicationStartParameter.getInstance(paramMap, true);

        Assert.assertSame(SERVER_PASSWORD, startParameter.getServerPwd());
    }

    @Test
    public void getClientPwd() throws Exception {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(CLIENT_PASSWORD_PARAM, CLIENT_PASSWORD);
        ApplicationStartParameter startParameter = ApplicationStartParameter.getInstance(paramMap, true);

        Assert.assertSame(CLIENT_PASSWORD, startParameter.getClientPwd());
    }

    @Test
    public void getClientName() throws Exception {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(CLIENT_NAME_PARAM, CLIENT_NAME);
        ApplicationStartParameter startParameter = ApplicationStartParameter.getInstance(paramMap, true);

        Assert.assertSame(CLIENT_NAME, startParameter.getClientName());
    }

}