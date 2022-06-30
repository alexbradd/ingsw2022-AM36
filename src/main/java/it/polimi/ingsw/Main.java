package it.polimi.ingsw;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.server.Logger;
import it.polimi.ingsw.server.Server;

import java.io.File;
import java.net.UnknownHostException;

/**
 * Main program entry point. Parses arguments and executes the correct code.
 * <p>
 * CLI parameters:
 * <ul>
 *     <li>{@code --server} Start in server mode, this is the default if nothing has been specified</li>
 *     <li>{@code --client-cli} Start in client mode with a CLI interface</li>
 *     <li>{@code --client-gui} Start in client mode with a GUI interface</li>
 *     <li>{@code --port PORT} Specify the port on which the server is listening</li>
 *     <li>{@code --address ADDR} Specify the address on which the server is located (used only in client mode)</li>
 * </ul>
 *
 * @see Server
 * @see Client
 */
public class Main {
    /**
     * The application's main method
     *
     * @param args the parameters passed from CLI
     */
    public static void main(String[] args) {
        try {
            fromCli(args);
        } catch (ParameterParsingException e) {
            System.out.println(e.getMessage());
            return;
        }
        Logger.log(ProgramOptions.printOptions());

        if (ProgramOptions.getMode() == ProgramOptions.ProgramMode.SERVER) {
            Server.exec();
        } else {
            Client.exec();
        }
    }

    /**
     * Parse the parameters from CLI into a {@link ProgramOptions} object
     *
     * @param args the CLI parameters to parse
     * @throws ParameterParsingException if there is a syntax error
     */
    static void fromCli(String[] args) throws ParameterParsingException {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--server":
                    ProgramOptions.setMode(ProgramOptions.ProgramMode.SERVER);
                    break;
                case "--client-gui":
                    ProgramOptions.setMode(ProgramOptions.ProgramMode.CLIENT_GUI);
                    break;
                case "--client-cli":
                    ProgramOptions.setMode(ProgramOptions.ProgramMode.CLIENT_CLI);
                    break;
                case "--port":
                    if (i + 1 < args.length)
                        try {
                            ProgramOptions.setPort(Integer.parseInt(args[i + 1]));
                            i++;
                        } catch (IllegalArgumentException e) {
                            throw ParameterParsingException.invalidParameter(args[i + 1], args[i], e.getMessage());
                        }
                    else
                        throw ParameterParsingException.missingParameter(args[i]);
                    break;
                case "--address":
                    if (i + 1 < args.length)
                        try {
                            ProgramOptions.setAddress(args[i + 1]);
                            i++;
                        } catch (UnknownHostException e) {
                            throw ParameterParsingException.invalidParameter(args[i + 1], args[i], e.getMessage());
                        }
                    else
                        throw ParameterParsingException.missingParameter(args[i]);
                    break;
                case "--persistence-store":
                    if (i + 1 < args.length)
                        try {
                            ProgramOptions.setPersistenceStore(new File(args[i + 1]));
                            i++;
                        } catch (IllegalArgumentException e) {
                            throw ParameterParsingException.invalidParameter(args[i + 1], args[i], e.getMessage());
                        }
                    else
                        throw ParameterParsingException.missingParameter(args[i]);
                    break;
                case "--no-persistence":
                    ProgramOptions.setUsePersistence(false);
                    break;
                case "--no-ping":
                    ProgramOptions.setUsePing(false);
                    break;
                case "--max-ping":
                    if (i + 1 < args.length)
                        try {
                            ProgramOptions.setMaximumPing(Long.parseLong(args[i + 1]));
                            i++;
                        } catch (IllegalArgumentException e) {
                            throw ParameterParsingException.invalidParameter(args[i + 1], args[i], e.getMessage());
                        }
                    else
                        throw ParameterParsingException.missingParameter(args[i]);
                    break;
                case "--verbose":
                    ProgramOptions.setVerbose(true);
                    break;
                default:
                    throw ParameterParsingException.invalidOption(args[i]);
            }
        }
    }
}