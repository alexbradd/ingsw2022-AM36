package it.polimi.ingsw;

import it.polimi.ingsw.client.Client;
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
        ProgramOptions options;
        try {
            options = fromCli(args);
        } catch (ParameterParsingException e) {
            System.out.println(e.getMessage());
            return;
        }

        if (options.getMode() == ProgramOptions.ProgramMode.SERVER) {
            Server.exec(options);
        } else {
            Client.exec(options);
        }
    }

    /**
     * Parse the parameters from CLI into a {@link ProgramOptions} object
     *
     * @param args the CLI parameters to parse
     * @return a new {@link ProgramOptions}
     * @throws ParameterParsingException if there is a syntax error
     */
    static ProgramOptions fromCli(String[] args) throws ParameterParsingException {
        ProgramOptions options = new ProgramOptions();
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--server":
                    options.setMode(ProgramOptions.ProgramMode.SERVER);
                    break;
                case "--client-gui":
                    options.setMode(ProgramOptions.ProgramMode.CLIENT_GUI);
                    break;
                case "--client-cli":
                    options.setMode(ProgramOptions.ProgramMode.CLIENT_CLI);
                    break;
                case "--port":
                    if (i + 1 < args.length)
                        try {
                            options.setPort(Integer.parseInt(args[i + 1]));
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
                            options.setAddress(args[i + 1]);
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
                            options.setPersistenceStore(new File(args[i + 1]));
                            i++;
                        } catch (IllegalArgumentException e) {
                            throw ParameterParsingException.invalidParameter(args[i + 1], args[i], e.getMessage());
                        }
                    else
                        throw ParameterParsingException.missingParameter(args[i]);
                    break;
                default:
                    throw ParameterParsingException.invalidOption(args[i]);
            }
        }
        return options;
    }
}