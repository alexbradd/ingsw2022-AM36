package it.polimi.ingsw;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Simple bean for passing parsed CLI options. All values are initialized to their default.
 *
 * @see Main
 */
public class ProgramOptions {
    /**
     * The mode the program has been launched in. Default is SERVER
     */
    private ProgramMode mode = ProgramMode.SERVER;
    /**
     * Port the server will listen on. Default is 9999
     */
    private int port = 9999;
    /**
     * The address the server will be located at. Default is localhost
     */
    private InetAddress address;

    /**
     * Default constructor.
     */
    public ProgramOptions() {
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new IllegalStateException("Wrong default address", e);
        }
    }

    /**
     * Returns the mode the program has been set to run in.
     *
     * @return the mode the program has been set to run in.
     */
    public ProgramMode getMode() {
        return mode;
    }

    /**
     * Sets the program launch mode
     *
     * @param mode the new program launch mode
     * @throws IllegalArgumentException if {@code mode} is null
     */
    public void setMode(ProgramMode mode) {
        if (mode == null) throw new IllegalStateException("mode shouldn't be null");
        this.mode = mode;
    }

    /**
     * Returns the port the server will listen on
     *
     * @return the port the server will liste on
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the port will liste on to the specified one
     *
     * @param port the new port
     * @throws IllegalArgumentException if the port is less than 0
     */
    public void setPort(int port) {
        if (port < 0) throw new IllegalArgumentException("port should be >= 0");
        this.port = port;
    }

    /**
     * Returns the {@link InetAddress} the server will listen on
     *
     * @return the {@link InetAddress} the server will listen on
     */
    public InetAddress getAddress() {
        return address;
    }

    /**
     * Sets the address the sever will listen on to the specified one
     *
     * @param address the new address
     * @throws UnknownHostException     if {@code address} is not a valid IP address
     * @throws IllegalArgumentException if {@code address} is null
     */
    public void setAddress(String address) throws UnknownHostException {
        if (address == null) throw new IllegalArgumentException("address shouldn't be null");
        this.address = InetAddress.getByName(address);
    }

    /**
     * Enum representing possible program launch modes
     */
    public enum ProgramMode {
        SERVER, CLIENT_CLI, CLIENT_GUI
    }

    @Override
    public String toString() {
        return "ProgramOptions{" +
                "mode=" + mode +
                ", port=" + port +
                ", address=" + address +
                '}';
    }
}
