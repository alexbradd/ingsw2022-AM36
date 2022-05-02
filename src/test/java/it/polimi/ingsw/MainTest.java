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
    }

    /**
     * Checks that the execution mode is correctly set
     */
    @Test
    void setMode() throws ParameterParsingException {
        ProgramOptions opt = Main.fromCli(new String[]{"--server"});
        assertEquals(ProgramOptions.ProgramMode.SERVER, opt.getMode());

        opt = Main.fromCli(new String[]{"--client-cli"});
        assertEquals(ProgramOptions.ProgramMode.CLIENT_CLI, opt.getMode());

        opt = Main.fromCli(new String[]{"--client-gui"});
        assertEquals(ProgramOptions.ProgramMode.CLIENT_GUI, opt.getMode());
    }

    /**
     * Checks that port is correctly set
     */
    @Test
    void setPort() throws ParameterParsingException {
        ProgramOptions opt = Main.fromCli(new String[]{"--port", "1234"});
        assertEquals(1234, opt.getPort());
    }

    /**
     * Checks that the InetAddress is correctly set
     */
    @Test
    void setAddress() throws ParameterParsingException {
        ProgramOptions opt = Main.fromCli(new String[]{"--address", "localhost"});
        assertEquals("localhost", opt.getAddress().getHostName());
    }
}