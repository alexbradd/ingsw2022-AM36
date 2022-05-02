package it.polimi.ingsw.client;

import it.polimi.ingsw.ProgramOptions;

public class Client {
    public static void exec(ProgramOptions opts) {
        System.out.println("Hello from client code");
        System.out.println(opts);
    }
}
