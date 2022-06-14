package it.polimi.ingsw.server.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import it.polimi.ingsw.enums.AssistantType;
import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.enums.Mage;
import it.polimi.ingsw.enums.PieceColor;
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
        throw new UnsupportedOperationException();
    }

    /**
     * Getter for this Phase's current player. Default implementation throws exception if the phase's has not got a
     * player.
     *
     * @return this phase's current player.
     */
    Player getCurrentPlayer() {
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
    }

    /**
     * Marks that the given {@link Player} has done one of his allowed movements.
     *
     * @param player the {@link Player} that has done the movements
     * @return a new Phase containing the update
     * @throws IllegalArgumentException if any parameter is null
     */
    public Phase markStudentMove(Player player) throws InvalidPhaseUpdateException {
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
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
     * Calculates a {@link PhaseDiff} from this Phase and the given one. If any differences are found, the data from
     * this instance is saved into the diff.
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

        diff.addAttribute("phase", new JsonPrimitive(this.getName()));

        calculatePlayerListDiff(other, diff);
        calculateProfessorDiff(other, diff);
        calculateBoardsDiff(other, diff);
        calculateIslandDiff(other, diff);
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
            acc.addEntityUpdate("playerList", new ArrayList<>(thisPlayers));
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
        for (Professor thisP : thisProfs) {
            Optional<Professor> otherP = otherProfs.stream()
                    .filter(p -> p.getColor() == thisP.getColor())
                    .findAny();
            if (otherP.isEmpty() || !thisP.equals(otherP.get()))
                js.add(thisP);
        }
        if (js.size() > 0)
            acc.addEntityUpdate("professors", js);
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
        for (Board thisB : thisBoards) {
            Optional<Board> otherB = otherBoards.stream()
                    .filter(b -> b.getPlayer().equals(thisB.getPlayer()))
                    .findAny();
            if (otherB.isEmpty() || !thisB.equals(otherB.get()))
                js.add(thisB);
        }
        if (js.size() > 0)
            acc.addEntityUpdate("boards", js);
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
    private void calculateIslandDiff(Phase other, PhaseDiff acc) {
        List<Island> thisIslands = this.getTable().getIslandList(),
                otherIslands = other.getTable().getIslandList();
        if (thisIslands.size() != otherIslands.size()) dumpIslandList(thisIslands, acc);
        else calculateIslandsDiff(thisIslands, otherIslands, acc);
    }

    /**
     * Dumps all islands into the given PhaseDiff together with an array of array representing the distribution of
     * ids inside the list
     *
     * @param thisIslands the island list to dump
     * @param acc         the phase diff to dump the islands into
     */
    private void dumpIslandList(List<Island> thisIslands, PhaseDiff acc) {
        ArrayList<Jsonable> ids = new ArrayList<>();
        ArrayList<Jsonable> islands = new ArrayList<>();
        for (Island island : thisIslands) {
            ids.add(() -> {
                JsonArray id = new JsonArray();
                for (int i : island.getIds())
                    id.add(i);
                return id;
            });
            islands.add(island);
        }
        acc.addEntityUpdate("islandList", ids);
        acc.addEntityUpdate("islands", islands);
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
        for (Island thisI : thisIslands) {
            Optional<Island> otherI = otherIslands.stream()
                    .filter(i -> i.getIds().equals(thisI.getIds()))
                    .findAny();
            if (otherI.isEmpty() || !thisI.equals(otherI.get()))
                js.add(thisI);
        }
        if (js.size() > 0)
            acc.addEntityUpdate("islands", js);
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
        if (!Objects.equals(thisMn, otherMn)) {
            ArrayList<Jsonable> mn = new ArrayList<>();
            mn.add(thisMn);
            acc.addEntityUpdate("motherNature", mn);
        }
    }

    /**
     * Calculates the difference between the Characters and saves it inside the PhaseDiff
     *
     * @param other the Phase to compare to
     * @param acc   the PhaseDiff into which to put the difference, if there is one
     */
    private void calculateCharactersDiff(Phase other, PhaseDiff acc) {
        if (this.hasPlayedCharacter() ^ other.hasPlayedCharacter())
            acc.addAttribute("hasPlayedCharacter", new JsonPrimitive(this.hasPlayedCharacter()));

        List<Character> thisCharacters = this.getTable().getCharacters(),
                otherCharacters = other.getTable().getCharacters();
        ArrayList<Jsonable> js = new ArrayList<>();
        for (Character thisC : thisCharacters) {
            Optional<Character> otherC = otherCharacters.stream()
                    .filter(c -> c.getCharacterType() == thisC.getCharacterType())
                    .findAny();
            if (otherC.isEmpty() || !thisC.equals(otherC.get()))
                js.add(thisC);
        }
        if (js.size() > 0)
            acc.addEntityUpdate("characters", js);
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
            acc.addAttribute("isSackEmpty", new JsonPrimitive(thisEmpty));
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
        for (int i = 0; i < thisClouds.size(); i++) {
            Cloud thisC = thisClouds.get(i);
            if (i >= otherClouds.size()) {
                addCloudToArrayList(thisC, js, i);
            } else {
                Cloud otherC = otherClouds.get(i);
                if (!Objects.equals(thisC, otherC))
                    addCloudToArrayList(thisC, js, i);
            }
        }
        if (js.size() > 0)
            acc.addEntityUpdate("clouds", js);
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
            j.getAsJsonObject().addProperty("id", i);
            return j;
        });
    }
}
