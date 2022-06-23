package it.polimi.ingsw;

import java.io.File;
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
    private static ProgramMode mode = ProgramMode.SERVER;
    /**
     * Port the server will listen on. Default is 9999
     */
    private static int port = 9999;
    /**
     * The address the server will be located at. Default is localhost
     */
    private static InetAddress address;
    /**
     * Whether to send {@code PING} messages to clients (only if SERVER mode). Default is true
     */
    private static boolean usePing = true;
    /**
     * Whether to store changes to ongoing matches on disk, and restoring these matches at the following start-up, if
     * the program crashes (only if SERVER mode). Default is true
     */
    private static boolean usePersistence = true;
    /**
     * The maximum time in milliseconds to wait for clients to respond to {@code PING} messages, before assuming the
     * connection has been lost (only if SERVER mode). Default is true
     */
    private static long maximumPing = 1000;
    /**
     * Whether to have verbose output or not.
     */
    private static boolean verbose = false;
    /**
     * The directory in which to save the match states.
     */
    private static File persistenceStore;

    /**
     * This static block initializes the {@link #address} to the localhost address.
     */
    static {
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
    public static ProgramMode getMode() {
        return mode;
    }

    /**
     * Sets the program launch mode
     *
     * @param mode the new program launch mode
     * @throws IllegalArgumentException if {@code mode} is null
     */
    public static void setMode(ProgramMode mode) {
        if (mode == null) throw new IllegalStateException("mode shouldn't be null");
        ProgramOptions.mode = mode;
    }

    /**
     * Returns the port the server will listen on
     *
     * @return the port the server will liste on
     */
    public static int getPort() {
        return port;
    }

    /**
     * Sets the port will liste on to the specified one
     *
     * @param port the new port
     * @throws IllegalArgumentException if the port is less than 0
     */
    public static void setPort(int port) {
        if (port < 0) throw new IllegalArgumentException("port should be >= 0");
        ProgramOptions.port = port;
    }

    /**
     * Returns the {@link InetAddress} the server will listen on
     *
     * @return the {@link InetAddress} the server will listen on
     */
    public static InetAddress getAddress() {
        return address;
    }

    /**
     * Sets the address the sever will listen on to the specified one
     *
     * @param address the new address
     * @throws UnknownHostException     if {@code address} is not a valid IP address
     * @throws IllegalArgumentException if {@code address} is null
     */
    public static void setAddress(String address) throws UnknownHostException {
        if (address == null) throw new IllegalArgumentException("address shouldn't be null");
        ProgramOptions.address = InetAddress.getByName(address);
    }

    /**
     * Returns whether the server will use persistence or not.
     *
     * @return whether the server will use persistence or not
     */
    public static boolean usesPersistence() {
        return usePersistence;
    }

    /**
     * Returns whether the server will send ping messages or not.
     *
     * @return whether the server will send ping messages or not
     */
    public static boolean usesPing() {
        return usePing;
    }

    /**
     * Sets the value of {@link #usePersistence}.
     *
     * @param usePersistence whether to use persistence or not
     */
    public static void setUsePersistence(boolean usePersistence) {
        ProgramOptions.usePersistence = usePersistence;
    }

    /**
     * Sets the value of {@link #usePing}.
     *
     * @param usePing whether to send pings or not
     */
    public static void setUsePing(boolean usePing) {
        ProgramOptions.usePing = usePing;
    }

    /**
     * Returns the maximum ping time acceptable by the server.
     *
     * @return the maximum ping time acceptable by the server
     */
    public static long getMaximumPing() {
        return maximumPing;
    }

    /**
     * Sets the value of {@link #maximumPing}.
     *
     * @param maximumPing the maximum ping time acceptable by the server
     */
    public static void setMaximumPing(long maximumPing) {
        ProgramOptions.maximumPing = maximumPing;
    }

    /**
     * Returns whether the program should have verbose output or not.
     *
     * @return whether the program should have verbose output or not
     */
    public static boolean isVerbose() {
        return verbose;
    }

    /**
     * Sets the value of {@link #verbose}.
     *
     * @param verbose whether the program should have verbose output or not
     */
    public static void setVerbose(boolean verbose) {
        ProgramOptions.verbose = verbose;
    }

    /**
     * Returns the {@link File} representing the directory the server will save its persistence data in.
     *
     * @return the {@link File} the server will save its persistence data in.
     */
    public static File getPersistenceStore() {
        return persistenceStore;
    }

    /**
     * Sets the {@link File} representing the directory the server will save its persistence data in.
     *
     * @param persistenceStore the new location
     * @throws IllegalArgumentException if {@code persistenceStore} does not meet the requirements
     */
    public static void setPersistenceStore(File persistenceStore) {
        if (persistenceStore == null) throw new IllegalArgumentException("persistenceStore shouldn't be null");
        if (persistenceStore.exists()) {
            if (!persistenceStore.isDirectory())
                throw new IllegalArgumentException("persistenceStore should be a directory");
            if (!persistenceStore.canRead() || !persistenceStore.canWrite())
                throw new IllegalArgumentException("the process doesn't have the necessary permission to modify the directory");
        } else {
            File parent = persistenceStore.getAbsoluteFile().getParentFile();
            if (parent == null) throw new IllegalStateException("persistenceStore does not have a parent directory");
            if (!parent.canRead() && !parent.canWrite())
                throw new IllegalArgumentException("the process doesn't have the necessary permission to modify the directory");
        }
        ProgramOptions.persistenceStore = persistenceStore;

        if (!persistenceStore.exists())
            if (!persistenceStore.mkdir())
                throw new IllegalStateException("Something went wrong in the creation of the directory " + persistenceStore);
    }

    /**
     * Enum representing possible program launch modes
     */
    public enum ProgramMode {
        SERVER, CLIENT_CLI, CLIENT_GUI
    }

    public static String staticToString() {
        return "ProgramOptions{" +
                "mode=" + mode +
                ", port=" + port +
                ", address=" + address +
                ", persistence-store=" + persistenceStore +
                ", use-persistence=" + usePersistence +
                ", use-ping=" + usePing +
                ", max-ping=" + maximumPing +
                ", verbose=" + verbose +
                '}';
    }
}
