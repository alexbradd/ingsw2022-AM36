package it.polimi.ingsw.server;

import it.polimi.ingsw.ProgramOptions;

public class Server {
    public static void exec(ProgramOptions opts) {
        System.out.println("Hello from server code");
        System.out.println(opts);
    }
}
