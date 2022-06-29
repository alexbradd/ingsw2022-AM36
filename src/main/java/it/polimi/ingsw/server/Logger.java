package it.polimi.ingsw.server;

import it.polimi.ingsw.ProgramOptions;

public class Logger {
    public static void log(String x) {
        if (ProgramOptions.isVerbose())
            System.out.println(x);
    }
}
