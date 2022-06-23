package it.polimi.ingsw.client.control.state;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.enums.AssistantType;
import it.polimi.ingsw.enums.Mage;
import javafx.beans.binding.ListBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Client's representation of the game's state.
 *
 * @author Mattia Busso
 */
public class GameState {

    /**
     * The current phase of the game.
     */
    private final SimpleStringProperty phase = new SimpleStringProperty();

    /**
     * The current player.
     */
    private final SimpleStringProperty currentPlayer = new SimpleStringProperty();

    /**
     * The players list.
     */
    private final SimpleListProperty<String> playerList = new SimpleListProperty<>();

    /**
     * The game's professors.
     */
    private final SimpleListProperty<Professor> professors = new SimpleListProperty<>();

    /**
     * The game's boards.
     */
    private final SimpleListProperty<Board> boards = new SimpleListProperty<>();

    /**
     * Mother-nature's position.
     */
    private final SimpleIntegerProperty motherNature = new SimpleIntegerProperty();

    /**
     * If a character has been used.
     */
    private final SimpleBooleanProperty usedCharacter = new SimpleBooleanProperty();

    /**
     * If the sack is empty.
     */
    private final SimpleBooleanProperty isSackEmpty = new SimpleBooleanProperty();

    /**
     * An array representation of the island groups.
     */
    private final SimpleListProperty<int[]> islandList = new SimpleListProperty<>();

    /**
     * The island groups.
     */
    private final SimpleListProperty<IslandGroup> islands = new SimpleListProperty<>();

    /**
     * The game's clouds.
     */
    private final SimpleListProperty<Cloud> clouds = new SimpleListProperty<>();

    /**
     * The game's characters.
     */
    private final SimpleListProperty<Character> characters = new SimpleListProperty<>();

    /**
     * A message that explains what event triggered the state update.
     */
    private final SimpleListProperty<String> causes = new SimpleListProperty<>();

    /**
     * The available mages in the game.
     */
    private final ListBinding<Mage> availableMages;

    public GameState() {
        availableMages = new ListBinding<>() {
            {
                super.bind(boards);
            }

            @Override
            protected ObservableList<Mage> computeValue() {
                List<Mage> usedMages = new ArrayList<>();
                for (Board b : boards) {
                    if (b.getMage() != null) usedMages.add(b.getMage());
                }
                return FXCollections.observableList(
                        Stream.of(Mage.values())
                                .filter(e -> !usedMages.contains(e))
                                .toList());
            }
        };
    }

    // Getters

    /**
     * Returns the current phase of the game.
     *
     * @return the phase of the game
     */
    public String getPhase() {
        return phase.get();
    }

    /**
     * Returns the current phase property.
     *
     * @return the current phase property
     */
    public SimpleStringProperty phaseProperty() {
        return phase;
    }

    /**
     * Returns the current player.
     *
     * @return the current player
     */
    public String getCurrentPlayer() {
        return currentPlayer.get();
    }

    /**
     * Returns the current player property.
     *
     * @return the current player property
     */
    public SimpleStringProperty currentPlayerProperty() {
        return currentPlayer;
    }

    /**
     * Returns the game's professors.
     *
     * @return the professors
     */
    public ObservableList<Professor> getProfessors() {
        return professors.get();
    }

    /**
     * Returns the professors' property.
     *
     * @return the professors' property
     */
    public SimpleListProperty<Professor> professorsProperty() {
        return professors;
    }

    /**
     * Returns the islands' list
     *
     * @return the islands' list
     */
    public ObservableList<int[]> getIslandList() {
        return islandList.get();
    }

    /**
     * Returns the islands' list properties
     *
     * @return the islands' list properties
     */
    public SimpleListProperty<int[]> islandListProperty() {
        return islandList;
    }

    /**
     * Returns the islands
     *
     * @return the islands
     */
    public ObservableList<IslandGroup> getIslands() {
        return islands.get();
    }

    /**
     * Returns the islands' property
     *
     * @return the islands' property
     */
    public SimpleListProperty<IslandGroup> islandsProperty() {
        return islands;
    }

    /**
     * Returns the game's clouds.
     *
     * @return the game's clouds
     */
    public Cloud[] getClouds() {
        return clouds.get().toArray(Cloud[]::new);
    }

    /**
     * Returns the clouds' property
     *
     * @return the clouds' property
     */
    public SimpleListProperty<Cloud> cloudsProperty() {
        return clouds;
    }

    /**
     * Returns the game's characters
     *
     * @return the game's characters
     */
    public Character[] getCharacters() {
        return characters.get().toArray(new Character[0]);
    }

    /**
     * Returns the characters' property
     *
     * @return the characters' property
     */
    public SimpleListProperty<Character> charactersProperty() {
        return characters;
    }

    /**
     * Returns the list of causes of the performed actions
     *
     * @return the causes
     */
    public ObservableList<String> getCauses() {
        return causes.get();
    }

    /**
     * Returns the causes' property
     *
     * @return the causes' property
     */
    public SimpleListProperty<String> causesProperty() {
        return causes;
    }

    /**
     * Returns the players list
     *
     * @return the players list
     */
    public ObservableList<String> getPlayerList() {
        return playerList.get();
    }

    /**
     * Returns the players list property
     *
     * @return the players list property
     */
    public SimpleListProperty<String> playerListProperty() {
        return playerList;
    }

    /**
     * Returns the boards of the game.
     *
     * @return the boards of the game
     */
    public Board[] getBoards() {
        return boards.get().toArray(new Board[0]);
    }

    /**
     * Returns the boards' property
     *
     * @return the boards' property
     */
    public SimpleListProperty<Board> boardsProperty() {
        return boards;
    }

    /**
     * Returns mother nature
     *
     * @return mother nature
     */
    public int getMotherNature() {
        return motherNature.get();
    }

    /**
     * Returns mother nature's property
     *
     * @return mother nature's property
     */
    public SimpleIntegerProperty motherNatureProperty() {
        return motherNature;
    }

    /**
     * Returns the assistants of the current player.
     *
     * @return the assistants of the current player
     */
    public AssistantType[] getPlayerAssistants() {
        AssistantType[] assistants = new AssistantType[]{};
        for (Board b : boards) {
            if (Objects.equals(b.getUsername(), currentPlayer.get())) assistants = b.getAssistants();
        }
        return assistants;
    }

    /**
     * Returns a list of available mages in the game.
     *
     * @return a list of available mages to be selected in the game
     */
    public List<Mage> getAvailableMages() {
        return availableMages.get();
    }

    /**
     * Returns the available mages' property
     *
     * @return the available mages' property
     */
    public ListBinding<Mage> availableMagesProperty() {
        return availableMages;
    }

    /**
     * Returns the character with the given character type.
     *
     * @param characterType the string corresponding to the character type
     * @throws IllegalStateException if the given character type is not valid
     * @return the character with the given character type
     */
    public Character getCharacter(String characterType) throws IllegalStateException {
        for (Character c : characters) {
            if (Objects.equals(c.getType().name(), characterType.toUpperCase())) return c;
        }
        throw new IllegalStateException("character not found");
    }

    /**
     * Returns true if a character has been used
     *
     * @return true if a character has been used
     */
    public boolean isUsedCharacter() {
        return usedCharacter.get();
    }

    /**
     * Returns the used character property
     *
     * @return the used character property
     */
    public SimpleBooleanProperty usedCharacterProperty() {
        return usedCharacter;
    }

    /**
     * Returns true if the sack is empty
     *
     * @return true if the sack is empty
     */
    public boolean isIsSackEmpty() {
        return isSackEmpty.get();
    }

    /**
     * Returns the sack empty property
     *
     * @return the sack empty property
     */
    public SimpleBooleanProperty isSackEmptyProperty() {
        return isSackEmpty;
    }

    /**
     * Returns the board of the current player.
     *
     * @return the board of the current player
     * @throws IllegalStateException if the board is not present
     */
    public Board getBoardOfCurrentPlayer() throws IllegalStateException {
        for (Board b : boards) {
            if (Objects.equals(b.getUsername(), currentPlayer.get())) return b;
        }
        throw new IllegalStateException("board not found");
    }

    /**
     * Returns the last cause
     *
     * @return the last cause
     */
    public String getCause() {
        return causes.isEmpty() ? null : causes.get(causes.size() - 1);
    }

    /**
     * Returns the last cause property
     *
     * @return the last cause property
     */
    public SimpleStringProperty causeProperty() {
        return new SimpleStringProperty(getCause());
    }

    // update

    /**
     * Updates the game's state, given a new update message from the server.
     *
     * @param o the {@code JsonObject} corresponding to the update message sent by the server
     */
    public void update(JsonObject o) {
        Gson gson = new Gson();
        for (String key : o.keySet()) {

            switch (key) {
                case "phase" -> phase.set(gson.fromJson(o.get(key), String.class));
                case "currentPlayer" -> currentPlayer.set(gson.fromJson(o.get(key), String.class));
                case "playerList" -> {
                    List<String> newPlayerList = Arrays.stream(gson.fromJson(o.get(key), String[].class)).collect(Collectors.toList());
                    playerList.set(FXCollections.observableList(newPlayerList));
                }
                case "boards" -> {
                    List<Board> newBoards = Arrays.stream(gson.fromJson(o.get(key), Board[].class)).collect(Collectors.toList());
                    if(boards.isNull().get() || boards.size() != newBoards.size()) {
                        boards.set(FXCollections.observableList(newBoards));
                    }
                    else {
                        updateBoards(newBoards);
                    }
                }
                case "professors" -> {
                    List<Professor> newProfessors = Arrays.stream(gson.fromJson(o.get(key), Professor[].class)).collect(Collectors.toList());
                    if(professors.isNull().get()) {
                        professors.set(FXCollections.observableList(newProfessors));
                    }
                    else {
                        updateProfessors(newProfessors);
                    }
                }
                case "motherNature" -> motherNature.set(gson.fromJson(o.get(key), int.class));
                case "usedCharacter" -> usedCharacter.set(gson.fromJson(o.get(key), boolean.class));
                case "isSackEmpty" -> isSackEmpty.set(gson.fromJson(o.get(key), boolean.class));
                case "islandList" -> {
                    List<int[]> newIslandList = Arrays.stream(gson.fromJson(o.get(key), int[][].class)).collect(Collectors.toList());
                    islandList.set(FXCollections.observableList(newIslandList));
                }
                case "islands" -> {
                    List<IslandGroup> newIslands = Arrays.stream(gson.fromJson(o.get(key), IslandGroup[].class)).collect(Collectors.toList());
                    if(islands.isNull().get() || islands.size() != newIslands.size()) {
                        islands.set(FXCollections.observableList(newIslands));
                    }
                    else {
                        updateIslands(newIslands);
                    }
                }
                case "clouds" -> {
                    List<Cloud> newClouds = Arrays.stream(gson.fromJson(o.get(key), Cloud[].class)).collect(Collectors.toList());
                    if(clouds.isNull().get()) {
                        clouds.set(FXCollections.observableList(newClouds));
                    }
                    else {
                        updateClouds(newClouds);
                    }
                }
                case "characters" -> {
                    List<Character> newCharacters = Arrays.stream(gson.fromJson(o.get(key), Character[].class)).collect(Collectors.toList());
                    if(characters.isNull().get()) {
                        characters.set(FXCollections.observableList(newCharacters));
                    }
                    else {
                        updateCharacters(newCharacters);
                    }
                }
                case "cause" -> {
                    if(causes.isNull().get()) {
                        causes.set(FXCollections.observableList(new ArrayList<>()));
                    }
                    causes.add(gson.fromJson(o.get(key), String.class));
                }
            }
        }
    }

    /**
     * Updates the boards.
     *
     * @param newBoards a list of updated boards
     */
    private void updateBoards(List<Board> newBoards) {
        for(Board b : newBoards) {
            for(int i = 0; i < boards.size(); i++) {
                if(boards.get(i).getUsername().equals(b.getUsername())) {
                    boards.set(i, b);
                }
            }
        }
    }

    /**
     * Updates the professors.
     *
     * @param newProfessors a list of updated professors
     */
    private void updateProfessors(List<Professor> newProfessors) {
        for(Professor p: newProfessors) {
            for(int i = 0; i < professors.size(); i++) {
                if(professors.get(i).getColor().equals(p.getColor())) {
                    professors.set(i, p);
                }
            }
        }
    }

    /**
     * Updates the islands.
     *
     * @param newIslands a list of updated islands
     */
    private void updateIslands(List<IslandGroup> newIslands) {
        for(IslandGroup i : newIslands) {
            for(int j = 0; j < islands.size(); j++) {
                if(Arrays.equals(islands.get(j).getIds(), i.getIds())) {
                    islands.set(j, i);
                }
            }
        }
    }

    /**
     * Updates the clouds.
     *
     * @param newClouds a list of updated clouds
     */
    private void updateClouds(List<Cloud> newClouds) {
        for(Cloud c : newClouds) {
            for(int i = 0; i < clouds.size(); i++) {
                if(clouds.get(i).getId() == c.getId()) {
                    clouds.set(i, c);
                }
            }
        }
    }

    /**
     * Updates the characters.
     *
     * @param newCharacters a list of updated characters
     */
    private void updateCharacters(List<Character>  newCharacters) {
        for(Character c : newCharacters) {
            for(int i = 0; i < characters.size(); i++) {
                if(characters.get(i).getType().equals(c.getType())) {
                    characters.set(i, c);
                }
            }
        }
    }

    // stringify

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(getCause() == null ? "" : getCause()).append("\n\n");
        s.append("** State of the Game **\n");
        s.append("* Current phase: ").append(phase.get()).append("\n");
        s.append("* Player list: ").append(playerList.get().toString()).append("\n");
        if(!phase.get().equals("LobbyPhase")) {
            s.append(currentPlayer.isNull().get() ? "" : "* Current player: " + currentPlayer + "\n");
            if (islands.isNotNull().get()) {
                s.append("* Islands: ").append(displayIslands()).append("\n");
                for (IslandGroup island : islands) s.append(island);
            }
            s.append(motherNature.isEqualTo(0).get() ? "" : "* Mother Nature is on island: " + motherNature + "\n");
            s.append(usedCharacter.get() ? "* A character has been used\n" : "");
            if (characters.isNotNull().get()) {
                s.append("* Characters:\n");
                for (Character character : characters) s.append(character);
            }
            if (clouds.isNotNull().get()) {
                s.append("* Clouds:\n");
                for (Cloud cloud : clouds) s.append(cloud);
            }
            if (professors.isNotNull().get()) {
                s.append("* Professors:\n");
                for (Professor professor : professors) s.append(professor);
            }
            if (boards.isNotNull().get()) {
                s.append("* Boards:\n");
                for (Board board : boards) s.append(board);
            }
            s.append(isSackEmpty.get() ? "* The sack is empty." : "");
        }

        return s.toString();
    }

    /**
     * Helper method that returns a formatted string representing the game's islands.
     *
     * @return the custom formatted string
     */
    private String displayIslands() {
        StringBuilder s = new StringBuilder();
        for(int i = 0; i < islandList.size(); i++) {
            for(int id : islandList.get(i)) {
                s.append(id);
            }
            s.append((i == islandList.size() - 1) ? "" : "-");
        }
        return s.toString();
    }

}
