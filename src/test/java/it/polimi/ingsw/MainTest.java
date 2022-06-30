package it.polimi.ingsw;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for Main's parameter parsing
 */
class MainTest {
    /**
     * Check that errors in cli syntax are handled correctly
     */
    @Test
    void boundCheck() {
        assertThrows(ParameterParsingException.class, () -> Main.fromCli(new String[]{"--port"}));
        assertThrows(ParameterParsingException.class, () -> Main.fromCli(new String[]{"--port", "-12"}));
        assertThrows(ParameterParsingException.class, () -> Main.fromCli(new String[]{"--port", "not a number"}));
        assertThrows(ParameterParsingException.class, () -> Main.fromCli(new String[]{"--address"}));
        assertThrows(ParameterParsingException.class, () -> Main.fromCli(new String[]{"--address", "0.1263.5...456"}));
        assertThrows(ParameterParsingException.class, () -> Main.fromCli(new String[]{"--persistence-store"}));
        assertThrows(ParameterParsingException.class, () -> Main.fromCli(new String[]{"--persistence-store", "./.gitignore"}));
        assertThrows(ParameterParsingException.class, () -> Main.fromCli(new String[]{"--persistence-store", "/"}));
    }

    /**
     * Checks that the execution mode is correctly set
     */
    @Test
    void setMode() throws ParameterParsingException {
        Main.fromCli(new String[]{"--server"});
        assertEquals(ProgramOptions.ProgramMode.SERVER, ProgramOptions.getMode());

        Main.fromCli(new String[]{"--client-cli"});
        assertEquals(ProgramOptions.ProgramMode.CLIENT_CLI, ProgramOptions.getMode());

        Main.fromCli(new String[]{"--client-gui"});
        assertEquals(ProgramOptions.ProgramMode.CLIENT_GUI, ProgramOptions.getMode());
    }

    /**
     * Checks that port is correctly set
     */
    @Test
    void setPort() throws ParameterParsingException {
        Main.fromCli(new String[]{"--port", "1234"});
        assertEquals(1234, ProgramOptions.getPort());
    }

    /**
     * Checks that the InetAddress is correctly set
     */
    @Test
    void setAddress() throws ParameterParsingException {
        Main.fromCli(new String[]{"--address", "localhost"});
        assertEquals("localhost", ProgramOptions.getAddress().getHostName());
    }

    /**
     * Checks that the persistence store is correctly set.
     */
    @Test
    void setPersistenceStore() throws ParameterParsingException {
        Main.fromCli(new String[]{"--persistence-store", "./deliveries"});
        assertEquals("deliveries", ProgramOptions.getPersistenceStore().getName());
        Main.fromCli(new String[]{"--persistence-store", "eryantis-store"});
        assertEquals("eryantis-store", ProgramOptions.getPersistenceStore().getName());
        Main.fromCli(new String[]{"--persistence-store", "./eryantis-store"});
        assertEquals("eryantis-store", ProgramOptions.getPersistenceStore().getName());
    }
}