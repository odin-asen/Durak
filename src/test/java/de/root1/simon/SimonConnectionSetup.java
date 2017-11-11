package de.root1.simon;

import de.root1.simon.serverService.TestServerService;
import org.junit.After;
import org.junit.Before;

public class SimonConnectionSetup {
    protected int testPort;
    protected Registry serverRegistry;
    protected TestServerService serverService;

    @Before
    public void setUp() throws Exception {
        testPort = 10000;
        serverRegistry = Simon.createRegistry(testPort);
        serverService = new TestServerService();
        serverRegistry.start();
        serverRegistry.bind(getTestRegistryName(), serverService);
    }

    @After
    public void tearDown() throws Exception {
        serverRegistry.unbind(getTestRegistryName());
        serverRegistry.stop();
    }

    public String getTestRegistryName() {
        return "testRegistry";
    }
}
