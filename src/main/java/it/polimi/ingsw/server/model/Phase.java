package it.polimi.ingsw.server.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import it.polimi.ingsw.enums.*;
import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;
import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;
import it.polimi.ingsw.server.model.exceptions.InvalidPlayerException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * The Phase class represents a single state of the game, and it is a facade facing the controller via the Command
 * pattern, showing the available operations (its methods), that interact with the internal state of all model entities.
 * <p>
 * The class is completely immutable. Each modifier creates a new object; for more details see subclasses.
 *
 * @author Leonardo Bianconi, Alexandru Gabriel Bradatan
 * @see LobbyPhase
 * @see PreparePhase
 * @see PlanningPhase
 * @see ActionPhase
 * @see EndgamePhase
 */
public abstract class Phase {
    private final static String notSupportedErrMsg = "Action not supported in this game phase.";
    /**
     * The game's parameters.
     */
    protected final GameParameters parameters;

    /**
     * {@link GameParameters} constructor.
     *
     * @param parameters the game's parameters
     * @throws IllegalArgumentException if {@code parameters == null}
     */
    Phase(GameParameters parameters) throws IllegalArgumentException {
        if (parameters == null) {
            throw new IllegalArgumentException("parameters shouldn't be null");
        }
        this.parameters = parameters;
    }

    /**
     * Copy constructor: copies this Phase's GameParameters
     *
     * @param old the phase to copy
     * @throws IllegalArgumentException if {@code old} is null
     */
    Phase(Phase old) {
        if (old == null) throw new IllegalArgumentException("old shouldn't be null");
        this.parameters = old.parameters;
    }

    /**
     * Getter for this Phase's GameParameters
     *
     * @return this Phase's GameParameters
     */
    GameParameters getParameters() {
        return parameters;
    }

    /**
     * Returns the name of this Phase
     *
     * @return the name of this Phase
     */
    String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * Getter for this Phase's {@link Table}.
     *
     * @return this Phase's {@link Table}.
     * @throws UnsupportedOperationException if this operation is not supported by the current phase of the game
     */
    Table getTable() {
        throw new UnsupportedOperationException(notSupportedErrMsg);
    }

    /**
     * Returns a list containing all the usernames that this phase has knowledge of.
     *
     * @return a list containing all the usernames that this phase has knowledge of.
     * @throws UnsupportedOperationException if this operation is not supported by the current phase of the game
     */
    public List<String> getPlayerUsernames() {
        return getTable()
                .getPlayers().stream()
                .map(Player::getUsername)
                .toList();
    }

    /**
     * Getter for this Phase's current player. Default implementation throws exception if the phase's has not got a
     * player.
     *
     * @return this phase's current player.
     */
    Player getCurrentPlayer() {
        throw new UnsupportedOperationException(notSupportedErrMsg);
    }

    /**
     * Checks if the player with the given username exists and has permission to modify the game state.
     *
     * @param username the username of the player to authorize
     * @return a {@link Player} object
     * @throws IllegalArgumentException      if {@code username} is null
     * @throws UnsupportedOperationException if this operation is not supported by the current phase of the game
     * @throws InvalidPlayerException        either if the specified player username is invalid or it is not the
     *                                       specified player's turn
     */
    public Player authorizePlayer(String username) throws InvalidPlayerException {
        throw new UnsupportedOperationException(notSupportedErrMsg);
    }

    /**
     * Add the given Student to the student store on the {@link Island} with the given index.
     *
     * @param player  the Player who will move the Student
     * @param index   the index of the {@link Island}
     * @param student the Student to add
     * @return a new Phase containing the update
     * @throws IllegalArgumentException    if any parameter is null
     * @throws InvalidPhaseUpdateException if the index is out of bounds
     */
    public Phase addToIsland(Player player, int index, Student student) throws InvalidPhaseUpdateException {
        throw new UnsupportedOperationException(notSupportedErrMsg);
    }

    /**
     * Retrieve a Student from the specified Player's entrance
     *
     * @param player the Player of whom board to modify
     * @param color  the color to get from the entrance
     * @return a {@link Tuple} containing a Phase with the changes applied and the Student extracted
     * @throws InvalidPhaseUpdateException if the player's entrance doesn't have enough students of the specified color
     * @throws IllegalArgumentException    if any parameter is null
     */
    public Tuple<? extends Phase, Student> getFromEntrance(Player player, PieceColor color) throws InvalidPhaseUpdateException {
        throw new UnsupportedOperationException(notSupportedErrMsg);
    }

    /**
     * Add the given Student to the player's hall.
     *
     * @param player  the Player whose Hall will be updated
     * @param student the Student to add
     * @return a new Phase containing the update
     * @throws IllegalArgumentException    if any parameter is null
     * @throws InvalidPhaseUpdateException if the Hall cannot contain any more students of that color
     */
    public Phase addToHall(Player player, Student student) throws InvalidPhaseUpdateException {
        throw new UnsupportedOperationException(notSupportedErrMsg);
    }

    /**
     * Marks that the given {@link Player} has done one of his allowed movements.
     *
     * @param player the {@link Player} that has done the movements
     * @return a new Phase containing the update
     * @throws IllegalArgumentException if any parameter is null
     */
    public Phase markStudentMove(Player player) throws InvalidPhaseUpdateException {
        throw new UnsupportedOperationException(notSupportedErrMsg);
    }

    /**
     * This method lets the given Player choose the mage deck corresponding to the given enum.
     *
     * @param player a reference to a Player as returned by {@code authorizePlayer}
     * @param mage   a {@link Mage} value
     * @throws UnsupportedOperationException if this operation is not supported by the current phase of the game
     * @throws IllegalArgumentException      any parameter is null
     * @throws InvalidPhaseUpdateException   if the mage has already been chosen by another player
     */
    public Phase chooseMageDeck(Player player, Mage mage) throws InvalidPhaseUpdateException {
        throw new UnsupportedOperationException(notSupportedErrMsg);
    }

    /**
     * This method lets the given Player play the assistant card of his deck corresponding the AssistantType passed.
     *
     * @param player    a reference to a Player as returned by {@code authorizePlayer}
     * @param assistant the AssistantType to play
     * @throws UnsupportedOperationException if this operation is not supported by the current phase of the game
     * @throws IllegalArgumentException      if any parameter is null
     * @throws InvalidPhaseUpdateException   if this assistant has already been played by another player in this round
     * @throws InvalidPhaseUpdateException   if this assistant has already been played by the player
     */
    public Phase playAssistant(Player player, AssistantType assistant) throws InvalidPhaseUpdateException {
        throw new UnsupportedOperationException(notSupportedErrMsg);
    }

    /**
     * This method lets the given Player a movement of Mother Nature across the islands, for a total number of steps
     * specified via parameter. The movement is performed clockwise on the islands (see game rules).
     *
     * @param player a reference to a Player as returned by {@code authorizePlayer}
     * @param steps  the number of steps of the movement
     * @throws UnsupportedOperationException if this operation is not supported by the current phase of the game
     * @throws IllegalArgumentException      if any parameter is null
     * @throws InvalidPhaseUpdateException   if the number of steps is invalid (see game rules)
     */
    public Phase moveMn(Player player, int steps) throws InvalidPhaseUpdateException {
        throw new UnsupportedOperationException(notSupportedErrMsg);
    }

    /**
     * This method lets the given Player play a character card. The {@link CharacterType} is
     * passed via parameter, along with all the needed additional information as a series of {@link CharacterStep}.
     *
     * @param player    a reference to a Player as returned by {@code authorizePlayer}
     * @param character the {@link CharacterType} to play
     * @param steps     additional arguments that specify the behaviour of the card (see specific card for details)
     * @throws UnsupportedOperationException      if this operation is not supported by the current phase of the game
     * @throws IllegalArgumentException           if any parameter is null
     * @throws InvalidCharacterParameterException if the additional information "args" is either missing or incorrect
     * @throws InvalidPhaseUpdateException        if the number of coins of the player is less than the amount required
     *                                            to play the character card (see game rules)
     */
    public Phase playCharacter(Player player, CharacterType character, CharacterStep... steps) throws InvalidPhaseUpdateException, InvalidCharacterParameterException {
        throw new UnsupportedOperationException(notSupportedErrMsg);
    }

    /**
     * Returns true if the current {@link Player} has already played a character during this phase. Default
     * implementation returns false.
     *
     * @return true if the current {@link Player} has already played a character during this phase.
     */
    boolean hasPlayedCharacter() {
        return false;
    }

    /**
     * This method lets the given Player pick a cloud from the playing area and retrieve all the students placed on it
     * (see game rules).
     *
     * @param id     the id of the id
     * @param player a reference to a Player as returned by {@code authorizePlayer}
     * @throws UnsupportedOperationException if this operation is not supported by the current phase of the game
     * @throws IllegalArgumentException      if any parameter is null
     * @throws IndexOutOfBoundsException     if the id does not represent a valid id
     * @throws InvalidPhaseUpdateException   if the specified id has already been picked this round
     */
    public Phase drainCloud(Player player, int id) throws InvalidPhaseUpdateException {
        throw new UnsupportedOperationException(notSupportedErrMsg);
    }

    /**
     * This method lets a client join the game. It requires a username, that is from now on identifying the player
     * inside the game. Therefore, it must not be an already chosen username for this game.
     *
     * @param username the username of the player who wants to join
     * @throws UnsupportedOperationException if this operation is not supported by the current phase of the game
     * @throws IllegalArgumentException      if the given username is null
     * @throws InvalidPhaseUpdateException   if a player with the same username is already taking part in this game
     * @throws InvalidPhaseUpdateException   if the maximum number of players has already joined the game
     */
    public Phase addPlayer(String username) throws InvalidPhaseUpdateException {
        throw new UnsupportedOperationException(notSupportedErrMsg);
    }

    /**
     * This method lets a player peacefully disconnect from the game lobby. From now on, the server will no longer keep
     * track of this player.
     *
     * @param username the username of the player
     * @throws UnsupportedOperationException if this operation is not supported by the current phase of the game
     * @throws IllegalArgumentException      if the given username is null
     * @throws InvalidPhaseUpdateException   if the specified player is not taking part in this game
     */
    public Phase removePlayer(String username) throws InvalidPhaseUpdateException {
        throw new UnsupportedOperationException(notSupportedErrMsg);
    }

    /**
     * Returns true if this Phase corresponds to a "game ended" scenario.
     *
     * @return true if this Phase corresponds to a "game ended" scenario
     */
    boolean isFinal() {
        return false;
    }

    /**
     * Returns a list with all the Players that meet the winner criteria. If the list is of length greater than 1, we
     * are in a tie situation. If the list is empty, that means that this phase isn't a final phase. To see the cases in
     * which a Player is a winner, check the game rules.
     *
     * @return a list with all the Players that meet the winner criteria
     * @see Phase#isFinal()
     */
    List<Player> getWinners() {
        return List.of();
    }

    /**
     * Dumps all this Phase's information into a {@link PhaseDiff}. It is conceptually equivalent to diffing with an
     * empty Phase.
     *
     * @return a new {@link PhaseDiff}
     */
    PhaseDiff dump() {
        PhaseDiff diff = new PhaseDiff();

        addAttributeToDiff(diff, new JsonPrimitive(this.getName()), DiffKeys.PHASE);
        addEntityUpdateListToDiff(diff, getTable().getPlayers(), DiffKeys.PLAYER_LIST);
        addEntityUpdateListToDiff(diff, getTable().getProfessors(), DiffKeys.PROFESSORS);
        addEntityUpdateListToDiff(diff, getTable().getBoards(), DiffKeys.BOARDS);
        addEntityUpdateListToDiff(diff, createIslandIdArray(getTable().getIslandList()), DiffKeys.ISLAND_LIST);
        addEntityUpdateListToDiff(diff, getTable().getIslandList(), DiffKeys.ISLANDS);
        addAttributeToDiff(diff, getTable().getMotherNature().toJson(), DiffKeys.MOTHER_NATURE);
        addAttributeToDiff(diff, new JsonPrimitive(hasPlayedCharacter()), DiffKeys.HAS_PLAYED_CHARACTER);
        addEntityUpdateListToDiff(diff, getTable().getCharacters(), DiffKeys.CHARACTERS);
        addAttributeToDiff(diff, new JsonPrimitive(getTable().getSack().size() == 0), DiffKeys.IS_SACK_EMPTY);

        ArrayList<Jsonable> clouds = new ArrayList<>();
        for (int i = 0; i < getTable().getClouds().size(); i++)
            addCloudToArrayList(getTable().getClouds().get(i), clouds, i);
        addEntityUpdateListToDiff(diff, clouds, DiffKeys.CLOUDS);

        return diff;
    }

    /**
     * Helper method that adds a copy of the given list of {@link Jsonable} to a {@link PhaseDiff} with the given key
     * as entity updates.
     *
     * @param diff the {@link PhaseDiff}
     * @param list the {@link List} of {@link Jsonable}
     * @param key  a value from {@link DiffKeys}
     */
    private void addEntityUpdateListToDiff(PhaseDiff diff, List<? extends Jsonable> list, DiffKeys key) {
        if (list.size() > 0)
            diff.addEntityUpdate(key.toString(), new ArrayList<>(list));
    }

    /**
     * Helper method that adds the given {@link JsonPrimitive} to a {@link PhaseDiff} with the given key
     * as an attribute.
     *
     * @param diff      the {@link PhaseDiff}
     * @param primitive the {@link JsonPrimitive}
     * @param key       a value from {@link DiffKeys}
     */
    private void addAttributeToDiff(PhaseDiff diff, JsonPrimitive primitive, DiffKeys key) {
        diff.addAttribute(key.toString(), primitive);
    }

    /**
     * Calculates a {@link PhaseDiff} from this Phase and the given one. If any differences are found, the data from
     * the argument instance is saved into the diff.
     * <p>
     * Note: The phase's name will always be included, event if the two objects have the same one.
     *
     * @param other the Phase to compare against
     * @return a new {@link PhaseDiff}
     * @throws IllegalArgumentException if any argument is null
     */
    PhaseDiff compare(Phase other) {
        if (other == null) throw new IllegalArgumentException("other shouldn't be null");
        PhaseDiff diff = new PhaseDiff();

        addAttributeToDiff(diff, new JsonPrimitive(other.getName()), DiffKeys.PHASE);

        calculatePlayerListDiff(other, diff);
        calculateCurrentPlayerDiff(other, diff);
        calculateProfessorDiff(other, diff);
        calculateBoardsDiff(other, diff);
        calculateIslandListDiff(other, diff);
        calculateMotherNatureDiff(other, diff);
        calculateCharactersDiff(other, diff);
        calculateSackDiff(other, diff);
        calculateCloudsDiff(other, diff);

        return diff;
    }

    /**
     * Calculates the difference between the player lists and saves it inside the PhaseDiff
     *
     * @param other the Phase to compare to
     * @param acc   the PhaseDiff into which to put the difference, if there is one
     */
    private void calculatePlayerListDiff(Phase other, PhaseDiff acc) {
        List<Player> thisPlayers = this.getTable().getPlayers(),
                otherPlayers = other.getTable().getPlayers();
        if (!Objects.deepEquals(thisPlayers, otherPlayers))
            addEntityUpdateListToDiff(acc, otherPlayers, DiffKeys.PLAYER_LIST);
    }

    /**
     * Calculates the difference between the current players and saves it inside the PhaseDiff. The current player is
     * added into the diff if:
     *
     * <ul>
     *     <li>{@code other} has a current player but {@code this} has not</li>
     *     <li>both instances have a current player an it is different</li>
     * </ul>
     *
     * @param other the Phase to compare to
     * @param acc   the PhaseDiff into which to put the difference, if there is one
     */
    private void calculateCurrentPlayerDiff(Phase other, PhaseDiff acc) {
        Player thisPlayer;
        Player otherPlayer = null;
        try {
            otherPlayer = other.getCurrentPlayer();
            thisPlayer = this.getCurrentPlayer();
            if (!Objects.equals(thisPlayer, otherPlayer))
                addAttributeToDiff(acc, new JsonPrimitive(otherPlayer.getUsername()), DiffKeys.CURRENT_PLAYER);
        } catch (UnsupportedOperationException ignored) {
            if (otherPlayer != null)
                addAttributeToDiff(acc, new JsonPrimitive(otherPlayer.getUsername()), DiffKeys.CURRENT_PLAYER);
        }
    }

    /**
     * Calculates the difference between the professors and saves it inside the PhaseDiff
     *
     * @param other the Phase to compare to
     * @param acc   the PhaseDiff into which to put the difference, if there is one
     */
    private void calculateProfessorDiff(Phase other, PhaseDiff acc) {
        List<Professor> thisProfs = this.getTable().getProfessors(),
                otherProfs = other.getTable().getProfessors();
        ArrayList<Jsonable> js = new ArrayList<>();
        for (Professor otherProf : otherProfs) {
            Optional<Professor> thisProf = thisProfs.stream()
                    .filter(p -> p.getColor() == otherProf.getColor())
                    .findAny();
            if (thisProf.isEmpty() || !otherProf.equals(thisProf.get()))
                js.add(otherProf);
        }
        addEntityUpdateListToDiff(acc, js, DiffKeys.PROFESSORS);
    }

    /**
     * Calculates the difference between the Boards and saves it inside the PhaseDiff
     *
     * @param other the Phase to compare to
     * @param acc   the PhaseDiff into which to put the difference, if there is one
     */
    private void calculateBoardsDiff(Phase other, PhaseDiff acc) {
        List<Board> thisBoards = this.getTable().getBoards(),
                otherBoards = other.getTable().getBoards();
        ArrayList<Jsonable> js = new ArrayList<>();
        for (Board otherB : otherBoards) {
            Optional<Board> thisB = thisBoards.stream()
                    .filter(b -> b.getPlayer().equals(otherB.getPlayer()))
                    .findAny();
            if (thisB.isEmpty() || !otherB.equals(thisB.get()))
                js.add(otherB);
        }
        addEntityUpdateListToDiff(acc, js, DiffKeys.BOARDS);
    }

    /**
     * Calculates the difference between the Islands and relative lists and saves it inside the PhaseDiff.
     * <p>
     * If the two lists have different sizes, all the list is dumped along with an attribute specifying the id
     * distribution.
     *
     * @param other the Phase to compare to
     * @param acc   the PhaseDiff into which to put the difference, if there is one
     */
    private void calculateIslandListDiff(Phase other, PhaseDiff acc) {
        List<Island> thisIslands = this.getTable().getIslandList(),
                otherIslands = other.getTable().getIslandList();
        if (thisIslands.size() != otherIslands.size()) {
            addEntityUpdateListToDiff(acc, createIslandIdArray(otherIslands), DiffKeys.ISLAND_LIST);
            addEntityUpdateListToDiff(acc, otherIslands, DiffKeys.ISLANDS);
        } else
            calculateIslandsDiff(thisIslands, otherIslands, acc);
    }

    /**
     * Creates the array of island ids saved in the diffs from the given list of islands
     *
     * @param islandList the list of islands
     * @return a list of {@link Jsonable}
     */
    private List<Jsonable> createIslandIdArray(List<Island> islandList) {
        ArrayList<Jsonable> ids = new ArrayList<>();
        islandList.forEach(i -> ids.add(() -> {
            JsonArray idArray = new JsonArray();
            for (int id : i.getIds())
                idArray.add(id);
            return idArray;
        }));
        return ids;
    }

    /**
     * Calculates the difference between the first list of Islands and the second and saves it inside the PhaseDiff.
     *
     * @param thisIslands  first list to compare
     * @param otherIslands second list to compare
     * @param acc          the PhaseDiff into which to put the difference, if there is one
     */
    private void calculateIslandsDiff(List<Island> thisIslands, List<Island> otherIslands, PhaseDiff acc) {
        ArrayList<Jsonable> js = new ArrayList<>();
        for (Island otherI : otherIslands) {
            Optional<Island> thisI = thisIslands.stream()
                    .filter(i -> i.getIds().equals(otherI.getIds()))
                    .findAny();
            if (thisI.isEmpty() || !otherI.equals(thisI.get()))
                js.add(otherI);
        }
        addEntityUpdateListToDiff(acc, js, DiffKeys.ISLANDS);
    }

    /**
     * Calculates the difference between the Mother Natures and saves it inside the PhaseDiff
     *
     * @param other the Phase to compare to
     * @param acc   the PhaseDiff into which to put the difference, if there is one
     */
    private void calculateMotherNatureDiff(Phase other, PhaseDiff acc) {
        MotherNature thisMn = this.getTable().getMotherNature(),
                otherMn = other.getTable().getMotherNature();
        if (!Objects.equals(thisMn, otherMn))
            addAttributeToDiff(acc, otherMn.toJson(), DiffKeys.MOTHER_NATURE);
    }

    /**
     * Calculates the difference between the Characters and saves it inside the PhaseDiff
     *
     * @param other the Phase to compare to
     * @param acc   the PhaseDiff into which to put the difference, if there is one
     */
    private void calculateCharactersDiff(Phase other, PhaseDiff acc) {
        if (this.hasPlayedCharacter() ^ other.hasPlayedCharacter())
            addAttributeToDiff(acc, new JsonPrimitive(other.hasPlayedCharacter()), DiffKeys.HAS_PLAYED_CHARACTER);

        List<Character> thisCharacters = this.getTable().getCharacters(),
                otherCharacters = other.getTable().getCharacters();
        ArrayList<Jsonable> js = new ArrayList<>();
        for (Character otherC : otherCharacters) {
            Optional<Character> thisC = thisCharacters.stream()
                    .filter(c -> c.getCharacterType() == otherC.getCharacterType())
                    .findAny();
            if (thisC.isEmpty() || !otherC.equals(thisC.get()))
                js.add(otherC);
        }
        addEntityUpdateListToDiff(acc, js, DiffKeys.CHARACTERS);
    }

    /**
     * Calculates the difference between the Sacks and saves it inside the PhaseDiff
     *
     * @param other the Phase to compare to
     * @param acc   the PhaseDiff into which to put the difference, if there is one
     */
    private void calculateSackDiff(Phase other, PhaseDiff acc) {
        boolean thisEmpty = this.getTable().getSack().size() == 0,
                otherEmpty = other.getTable().getSack().size() == 0;
        if ((thisEmpty) ^ (otherEmpty))
            addAttributeToDiff(acc, new JsonPrimitive(otherEmpty), DiffKeys.IS_SACK_EMPTY);
    }

    /**
     * Calculates the difference between the Clouds and saves it inside the PhaseDiff
     *
     * @param other the Phase to compare to
     * @param acc   the PhaseDiff into which to put the difference, if there is one
     */
    private void calculateCloudsDiff(Phase other, PhaseDiff acc) {
        List<Cloud> thisClouds = this.getTable().getClouds(),
                otherClouds = other.getTable().getClouds();
        ArrayList<Jsonable> js = new ArrayList<>();
        for (int i = 0; i < otherClouds.size(); i++) {
            Cloud otherC = otherClouds.get(i);
            if (i >= thisClouds.size()) {
                addCloudToArrayList(otherC, js, i);
            } else {
                Cloud thisC = thisClouds.get(i);
                if (!Objects.equals(thisC, otherC))
                    addCloudToArrayList(otherC, js, i);
            }
        }
        if (js.size() > 0)
            addEntityUpdateListToDiff(acc, js, DiffKeys.CLOUDS);
    }

    /**
     * Adds the given cloud to the arraylist appending the index in the Jsonable
     *
     * @param c  The Cloud to add
     * @param js The ArrayList to add the Cloud into
     * @param i  The index of the Cloud
     */
    private void addCloudToArrayList(Cloud c, ArrayList<Jsonable> js, int i) {
        js.add(() -> {
            JsonElement j = c.toJson();
            JsonObject outer = new JsonObject();
            outer.addProperty("id", i);
            outer.add("students", j);
            return outer;
        });
    }
}
