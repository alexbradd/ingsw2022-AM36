package it.polimi.ingsw.client.control;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.polimi.ingsw.client.control.state.Character;
import it.polimi.ingsw.client.control.state.Cloud;
import it.polimi.ingsw.client.control.state.State;
import it.polimi.ingsw.enums.AssistantType;
import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.enums.Mage;
import it.polimi.ingsw.enums.PieceColor;
import it.polimi.ingsw.functional.Tuple;

import java.util.*;

/**
 * This class contains static methods used by the {@code CLI} to construct the message to be sent to the {@code Controller}
 * and then forwarded to the server.
 *
 * @author Mattia Busso
 * @see Controller
 * @see it.polimi.ingsw.client.view.cli.CLI
 */
public class CLIMessageBuilder {

    /**
     * Builds the user-message.
     *
     * @param stdin the input scanner
     * @param controller the application's controller
     * @return the user-message
     */
    public static Optional<JsonObject> buildMessage(Scanner stdin, Controller controller) {
        try {
            String inputLine = stdin.nextLine();
            if (inputLine.equalsIgnoreCase("quit")) {
                controller.setToEnd();
            } else if (!controller.isHasPendingUserMessages()) {
                State state = controller.getState();
                Controller.Status status = controller.getStatus();
                if (status == Controller.Status.INITIAL) {
                    if (inputLine.equalsIgnoreCase("fetch")) {
                        return buildFetchMsg();
                    } else if (inputLine.equalsIgnoreCase("create")) {
                        return buildCreateMsg(stdin, controller);
                    }
                } else if (status == Controller.Status.FETCHED_LOBBIES) {
                    if (isValidJoin(inputLine, state)) {
                        return buildJoinMsg(stdin, controller);
                    } else if (inputLine.equalsIgnoreCase("back")) {
                        controller.toMainMenu();
                    }
                } else if (status == Controller.Status.IN_GAME && !state.getGameState().isRejoining()) {
                    if (isValidLeave(inputLine, state)) {
                        return buildLeaveMsg(state);
                    } else if (isValidMage(state, inputLine)) {
                        return buildChooseMageMsg(state, inputLine);
                    } else if (isValidAssistant(state, inputLine)) {
                        return buildPlayAssistantMsg(state, inputLine);
                    } else if (isValidStudent(state, inputLine)) {
                        return buildMoveStudentMsg(stdin, controller, inputLine);
                    } else if (isValidNumSteps(state, inputLine)) {
                        return buildMoveMnMsg(state, Integer.parseInt(inputLine));
                    } else if (isValidCloudId(state, inputLine)) {
                        return buildPickCloudMsg(state, Integer.parseInt(inputLine));
                    } else if (isValidCharacter(state, inputLine)) {
                        return buildPlayCharacterMsg(stdin, controller, inputLine);
                    }
                } else if (status == Controller.Status.END) {
                    if (inputLine.equalsIgnoreCase("back")) {
                        controller.toMainMenu();
                    }
                }
                else if(status == Controller.Status.DISCONNECT) {
                    controller.setToEnd();
                }
            }
            return Optional.empty();
        }
        catch(NoSuchElementException e) {
            controller.setToEnd();
            return Optional.empty();
        }
    }

    /**
     * Builds the FETCH message.
     *
     * @return the FETCH message
     */
    private static Optional<JsonObject> buildFetchMsg() {
        JsonObject o = new JsonObject();
        o.addProperty("type", "FETCH");
        return Optional.of(o);
    }

    /**
     * Builds the CREATE message.
     *
     * @param stdin input scanner
     * @param controller the application's controller
     * @return the CREATE message
     */
    private static Optional<JsonObject> buildCreateMsg(Scanner stdin, Controller controller) {
        try {
            JsonObject o = new JsonObject();
            assignUsername(stdin, controller, o);
            assignNumPlayersAndExpertMode(stdin, controller, o);
            o.addProperty("type", "CREATE");
            return Optional.of(o);
        }
        catch(ApplicationQuitException e) {
            controller.setToEnd();
            return Optional.empty();
        }
    }

    /**
     * Builds the JOIN message.
     *
     * @param stdin the input scanner
     * @param controller the application's controller
     * @return the JOIN message
     */
    private static Optional<JsonObject> buildJoinMsg(Scanner stdin, Controller controller) {
        try {
            JsonObject o = new JsonObject();
            assignUsername(stdin, controller, o);
            assignLobby(stdin, controller, o);
            o.addProperty("type", "JOIN");
            return Optional.of(o);
        }
        catch(ApplicationQuitException e) {
            controller.setToEnd();
            return Optional.empty();
        }
    }

    /**
     * Helper method used to assign a username to the message.
     * Also used to update the game's info with the said username.
     *
     * @param stdin the input scanner
     * @param controller the application's controller
     * @param o the message to build
     * @throws ApplicationQuitException if the user wants to quit the application
     */
    private static void assignUsername(Scanner stdin, Controller controller, JsonObject o) throws ApplicationQuitException {
        State state = controller.getState();
        System.out.println("Please type your username");
        String inputLine = stdin.nextLine();
        while(inputLine.replaceAll(" ", "").equals("")) { //username
            System.out.println("Invalid username");
            inputLine = stdin.nextLine();
        }
        if(operationToQuit(controller, inputLine)) throw new ApplicationQuitException();
        o.addProperty("username", inputLine);
        state.updateGameInfo(inputLine);
    }

    /**
     * Helper method used to assign the number of players and the expert mode the message.
     * Also used to update the game's info with them.
     *
     * @param stdin the input scanner
     * @param controller the application's controller
     * @param o the message to build
     * @throws ApplicationQuitException if the user wants to quit the application
     */
    private static void assignNumPlayersAndExpertMode(Scanner stdin, Controller controller, JsonObject o) throws ApplicationQuitException {
        JsonArray args = new JsonArray();
        JsonObject argsObject = new JsonObject();
        State state = controller.getState();

        System.out.println("Please enter the number of players in the game (2/3)");
        String inputLine = stdin.nextLine();
        while(!Objects.equals(inputLine, "2") && !Objects.equals(inputLine, "3")) {
            if(operationToQuit(controller, inputLine)) throw new ApplicationQuitException();
            inputLine = stdin.nextLine();
        }
        argsObject.addProperty("nPlayers", inputLine);
        state.updateGameInfo(Integer.parseInt(inputLine));

        System.out.println("Expert mode? (Y/N)");
        inputLine = stdin.nextLine();
        while(!Objects.equals(inputLine.toLowerCase(), "y") && !Objects.equals(inputLine.toLowerCase(), "n")) {
            if(operationToQuit(controller, inputLine)) throw new ApplicationQuitException();
            inputLine = stdin.nextLine();
        }
        argsObject.addProperty("expert", inputLine.equalsIgnoreCase("y") ? "true" : "false");
        state.updateGameInfo(inputLine.equalsIgnoreCase("y"));

        args.add(argsObject);

        o.add("arguments", args);
    }

    /**
     * Helper method that used to assign the lobby to the message.
     *
     * @param stdin the input scanner
     * @param controller the application's controller
     * @param o the message to build
     * @throws ApplicationQuitException if the user wants to quit the application
     */
    private static void assignLobby(Scanner stdin, Controller controller, JsonObject o) throws ApplicationQuitException {
        State state = controller.getState();
        System.out.println("Please type the id of the lobby you want to join");
        String inputLine = stdin.nextLine();
        while(true) { //game id
            try {
                if(operationToQuit(controller, inputLine)) throw new ApplicationQuitException();
                int id = Integer.parseInt(inputLine);
                if(state.isValidLobby(id)) {
                    state.updateGameInfo(state.getLobby(id).isExpert());
                    state.updateGameInfo(state.getLobby(id).getNumPlayers());
                    break;
                }
                inputLine = stdin.nextLine();
            }
            catch(NumberFormatException e) {
                inputLine = stdin.nextLine();
            }
        }
        o.addProperty("gameId", inputLine);
    }

    /**
     * Builds the LEAVE message.
     *
     * @param state the game's state
     * @return the LEAVE message
     */
    private static Optional<JsonObject> buildLeaveMsg(State state) {
        JsonObject o = new JsonObject();
        o.addProperty("gameId", state.getGameInfo().getId());
        o.addProperty("username", state.getGameInfo().getUsername());
        o.addProperty("type", "LEAVE");
        return Optional.of(o);
    }

    /**
     * Builds the CHOOSE_MAGE message.
     *
     * @param state the game's state
     * @param mageType mage-type string
     * @return the CHOOSE_MAGE message
     */
    private static Optional<JsonObject> buildChooseMageMsg(State state, String mageType) {
        JsonObject o = new JsonObject();
        o.addProperty("gameId", state.getGameInfo().getId());
        o.addProperty("username", state.getGameInfo().getUsername());
        o.addProperty("type", "CHOOSE_MAGE");
        JsonArray args = new JsonArray();
        args.add(mageType.toUpperCase());
        o.add("arguments", args);
        return Optional.of(o);
    }

    /**
     * Builds the PLAY_ASSISTANT message.
     *
     * @param state the game's state
     * @param assistantType the assistant-type string
     * @return the PLAY_ASSISTANT message
     */
    private static Optional<JsonObject> buildPlayAssistantMsg(State state, String assistantType) {
        JsonObject o = new JsonObject();
        o.addProperty("gameId", state.getGameInfo().getId());
        o.addProperty("username", state.getGameInfo().getUsername());
        o.addProperty("type", "PLAY_ASSISTANTS");
        JsonArray args = new JsonArray();
        args.add(assistantType.toUpperCase());
        o.add("arguments", args);
        return Optional.of(o);
    }

    /**
     * Builds the MOVE_STUDENT message.
     *
     * @param stdin the input scanner
     * @param controller the application's controller
     * @param studentColor the student color
     * @return the MOVE_STUDENT message
     */
    private static Optional<JsonObject> buildMoveStudentMsg(Scanner stdin, Controller controller, String studentColor) {
        try {
            JsonObject o = new JsonObject();
            o.addProperty("gameId", controller.getState().getGameInfo().getId());
            o.addProperty("username", controller.getState().getGameInfo().getUsername());
            o.addProperty("type", "MOVE_STUDENT");
            assignStudent(stdin, studentColor, controller, o);
            return Optional.of(o);
        }
        catch(ApplicationQuitException e) {
            controller.setToEnd();
            return Optional.empty();
        }
    }

    /**
     * Helper method that assigns the student with its destination to the message.
     *
     * @param stdin the input scanner
     * @param studentColor the student color string
     * @param o the message to build
     * @throws ApplicationQuitException if the user wants to quit the application
     */
    private static void assignStudent(Scanner stdin, String studentColor, Controller controller, JsonObject o) throws ApplicationQuitException {
        JsonArray args = new JsonArray();
        JsonObject o1 = new JsonObject();
        o1.addProperty("color", studentColor.toUpperCase());
        System.out.println("Please type the destination you want to move your student to (HALL / ISLAND)");
        String inputLine = stdin.nextLine();
        while(!inputLine.equalsIgnoreCase("hall") && !inputLine.equalsIgnoreCase("island")) {
            if(operationToQuit(controller, inputLine)) throw new ApplicationQuitException();
            inputLine = stdin.nextLine();
        }
        o1.addProperty("destination", inputLine.toUpperCase());
        if(inputLine.equalsIgnoreCase("island")) {
            System.out.println("Please type the id of the island");
            inputLine = stdin.nextLine();
            while(true) {
                try {
                    if(inputLine.equalsIgnoreCase("quit")) throw new ApplicationQuitException();
                    else if((Integer.parseInt(inputLine) < 0 || Integer.parseInt(inputLine) >= 12)) {
                        inputLine = stdin.nextLine();
                    }
                    else {
                        break;
                    }
                }
                catch(NumberFormatException e) {
                    inputLine = stdin.nextLine();
                }
            }
            o1.addProperty("index", inputLine);
        }
        args.add(o1);
        o.add("arguments", args);
    }

    /**
     * Builds the MOVE_MN message.
     *
     * @param state the game's state
     * @param steps the number of steps to move mother nature
     * @return the MOVE_MN message
     */
    private static Optional<JsonObject> buildMoveMnMsg(State state, int steps) {
        JsonObject o = new JsonObject();
        o.addProperty("gameId", state.getGameInfo().getId());
        o.addProperty("username", state.getGameInfo().getUsername());
        o.addProperty("type", "MOVE_MN");
        JsonArray args = new JsonArray();
        args.add(steps);
        o.add("arguments", args);
        return Optional.of(o);
    }

    /**
     * Builds the PICK_CLOUD message.
     *
     * @param state the game's state
     * @param id the id of the cloud
     * @return the PICK_CLOUD message
     */
    private static Optional<JsonObject> buildPickCloudMsg(State state, int id) {
        JsonObject o = new JsonObject();
        o.addProperty("gameId", state.getGameInfo().getId());
        o.addProperty("username", state.getGameInfo().getUsername());
        o.addProperty("type", "PICK_CLOUD");
        JsonArray args = new JsonArray();
        args.add(id);
        o.add("arguments", args);
        return Optional.of(o);
    }

    /**
     * Builds the PLAY_CHARACTER message.
     *
     * @param stdin the input scanner
     * @param controller the application's controller
     * @param characterType the character type string
     * @return the PLAY_CHARACTER message
     */
    private static Optional<JsonObject> buildPlayCharacterMsg(Scanner stdin, Controller controller, String characterType) {
        JsonObject o = new JsonObject();
        o.addProperty("gameId", controller.getState().getGameInfo().getId());
        o.addProperty("username", controller.getState().getGameInfo().getUsername());
        o.addProperty("type", "PLAY_CHARACTER");
        JsonArray args = new JsonArray();
        JsonObject arg = new JsonObject();
        arg.addProperty("character", characterType.toUpperCase());
        JsonArray steps = new JsonArray();
        CharacterType character = CharacterType.valueOf(characterType.toUpperCase());
        for (int i = 0; i < character.getMaxSteps(); i++) {
            if(i > character.getMinSteps() - 1) {
                System.out.println("Type STOP if you don't want to add any more steps, simply press the enter key otherwise");
                String inputLine = stdin.nextLine();
                if(inputLine.equalsIgnoreCase("stop")) break;
                else if(operationToQuit(controller, inputLine)) {
                    controller.setToEnd();
                    return Optional.empty();
                }
            }
            JsonObject step = new JsonObject();
            for(Tuple<String, CharacterType.ParameterType> p : character.getStepParameters()) {
                switch (p.getFirst()) {
                    case "card" -> System.out.println("Choose a student from the card (type the student color):");
                    case "color" -> System.out.println("Choose a student color:");
                    case "island" -> System.out.println("Choose an island (type the island id):");
                    case "hall" -> System.out.println("Choose a student from the hall (type the student color):");
                    case "entrance" -> System.out.println("Choose a student from the entrance (type the student color):");
                }
                String inputLine = stdin.nextLine();
                if(operationToQuit(controller, inputLine)) {
                    controller.setToEnd();
                    return Optional.empty();
                }
                step.addProperty(p.getFirst(), inputLine.toUpperCase());
            }
            steps.add(step);
        }
        arg.add("steps", steps.getAsJsonArray());
        args.add(arg);
        o.add("arguments", args.getAsJsonArray());
        return Optional.of(o);
    }

    // Input validity checks

    /**
     * Checks if the given input is a valid join message.
     *
     * @param joinString the join input string
     * @param state the game's state
     * @return {@code true} if the given input is valid, {@code false} otherwise
     */
    private static boolean isValidJoin(String joinString, State state) {
        return joinString.equalsIgnoreCase("join")
                && state.areAvailableLobbiesPresent();
    }

    /**
     * Checks if the given input is a valid leave message.
     *
     * @param leaveString the leave input string
     * @param state the game's state
     * @return {@code true} if the given input is valid, {@code false} otherwise
     */
    private static boolean isValidLeave(String leaveString, State state) {
        return leaveString.equalsIgnoreCase("leave")
                && state.getGameState().getPhase().equals("LobbyPhase");
    }

    /**
     * Checks if the given input is a valid mage type.
     *
     * @param state the game's state
     * @param mageType the mage-type string
     * @return {@code true} if the given input is valid, {@code false} otherwise
     */
    private static boolean isValidMage(State state, String mageType) {
        try {
            return  state.getGameState().getPhase().equals("PreparePhase") &&
                    state.isPlayerTurn() &&
                    state.getGameState().getAvailableMages().stream().toList()
                    .contains(Mage.valueOf(mageType.toUpperCase()));
        }
        catch(IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Checks if the given input is a valid assistant type.
     *
     * @param state the game' state
     * @param assistantType the assistant type string
     * @return {@code true} if the given input is valid, {@code false} otherwise
     */
    private static boolean isValidAssistant(State state, String assistantType) {
        try {
            return  state.getGameState().getPhase().equals("PlanningPhase") &&
                    state.isPlayerTurn() &&
                    Arrays.stream(state.getGameState().getPlayerAssistants()).toList()
                    .contains(AssistantType.valueOf(assistantType.toUpperCase()));
        }
        catch(IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Checks if the given input is a valid student.
     *
     * @param state the game's state
     * @param studentColor the student color string
     * @return {@code true} if the given input is valid, {@code false} otherwise
     */
    private static boolean isValidStudent(State state, String studentColor) {
        try {
            return  state.getGameState().getPhase().equals("StudentMovePhase") &&
                    state.isPlayerTurn() &&
                    Arrays.stream(state.getGameState().getBoardOfCurrentPlayer().getEntrance()).toList()
                    .contains(PieceColor.valueOf(studentColor.toUpperCase()));
        }
        catch(IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Checks if the given input is a valid number of steps.
     *
     * @param state the game's state
     * @param numSteps the number of steps
     * @return {@code true} if the given input is valid, {@code false} otherwise
     */
    private static boolean isValidNumSteps(State state, String numSteps) {
        try {
            int steps = Integer.parseInt(numSteps);
            return  state.getGameState().getPhase().equals("MnMovePhase") &&
                    state.isPlayerTurn() &&
                    steps > 0 &&
                    steps <= state.getGameState().getBoardOfCurrentPlayer().getLastPlayedAssistant()
                            .getMNSteps();
        }
        catch(NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks if the given input is a valid cloud id.
     *
     * @param state the game's state
     * @param cloudId the cloud id string
     * @return {@code true} if the given input is valid, {@code false} otherwise
     */
    private static boolean isValidCloudId(State state, String cloudId) {
        try {
            int id = Integer.parseInt(cloudId);
            return  state.getGameState().getPhase().equals("CloudPickPhase") &&
                    state.isPlayerTurn() &&
                    Arrays.stream(
                    state.getGameState().getClouds())
                    .filter(c -> c.getStudents().length != 0)
                    .map(Cloud::getId).toList().contains(id);
        }
        catch(NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks if the given input is a valid character.
     *
     * @param state the game's state
     * @param characterType the character type string
     * @return {@code true} if the given input is valid, {@code false} otherwise
     */
    private static boolean isValidCharacter(State state, String characterType) {
        try {
            return  state.getGameInfo().isExpert() &&
                    !state.getGameState().getPhase().equals("LobbyPhase") &&
                    !state.getGameState().getPhase().equals("PreparePhase") &&
                    state.isPlayerTurn() &&
                    Arrays.stream(state.getGameState().getCharacters())
                    .map(Character::getType).toList()
                    .contains(CharacterType.valueOf(characterType.toUpperCase())) &&
                    state.getGameState().getBoardOfCurrentPlayer().getCoins() >=
                            state.getGameState().getCharacter(characterType).getPrice();
        }
        catch(IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Returns true if an event that requires to quit the operation has happened.
     *
     * @param controller the application's controller
     * @param inputLine the user input line
     * @return {@code true} if an event that requires to quit the operation has happened
     */
    private static boolean operationToQuit(Controller controller, String inputLine) {
        return inputLine.equalsIgnoreCase("quit") || controller.getStatus() == Controller.Status.DISCONNECT;
    }

    /**
     * Exception used internally to signal that the user wants to quit the application.
     */
    private static class ApplicationQuitException extends Exception {
    }

}
