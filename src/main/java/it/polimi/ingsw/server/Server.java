package it.polimi.ingsw.server;

import it.polimi.ingsw.Main;
import it.polimi.ingsw.ProgramOptions;
import it.polimi.ingsw.server.net.Dispatcher;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Static class that executes the main server code
 */
public class Server {
    /**
     * The servers chosen persistence store.
     */
    public static File persistenceStore = null;

    /**
     * Like {@link Main#main}, however it executes only if the program is run in server mode
     *
     * @param opts the options the server will use
     */
    public static void exec(ProgramOptions opts) {
        persistenceStore = opts.getPersistenceStore();
        if (!persistenceStore.exists() && !persistenceStore.mkdir())
            throw new IllegalStateException("Server was unable to create the persistence directory");
        ExecutorService threadPool = Executors.newCachedThreadPool();
        try (ServerSocket server = new ServerSocket(opts.getPort())) {
            while (!server.isClosed()) {
                Socket client = server.accept();
                System.out.println("Accepted connection, dispatching...");
                threadPool.submit(new Dispatcher(client));
            }
        } catch (IOException e) {
            System.out.println("Error while opening the socket: " + e);
        }
    }
}
