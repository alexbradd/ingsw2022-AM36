package it.polimi.ingsw.server.model;

import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.server.model.enums.CharacterType;
import it.polimi.ingsw.server.model.enums.PieceColor;
import it.polimi.ingsw.server.model.exceptions.*;

import java.util.*;
import java.util.function.Function;

/**
 * This phase represents a state of the game in which a player is currently playing. Its subclasses represent the
 * sub-phases in which a player's action turn can be subdivided. The ActionPhase class defines the common operations
 * between them.
 *
 * @author Leonardo Bianconi, Alexandru Gabriel Bradatan
 * @see StudentMovePhase
 * @see MnMovePhase
 * @see CloudPickPhase
 * @see EndgamePhase
 */
abstract class ActionPhase extends IteratedPhase {
    /**
     * This phase's current state.
     */
    private Table table;

    /**
     * A boolean indicating if the current {@link Player} has already played its assistant.
     */
    private boolean playedCharacter;
    /**
     * The {@link InfluenceCalculator} used during calculations
     */
    private InfluenceCalculator influenceCalculator;
    /**
     * The {@link MaxExtractor} used during calculations
     */
    private MaxExtractor maxExtractor;
    /**
     * {@link MotherNature} movement extension
     */
    private int extraMnMoves;

    /**
     * Creates a new ActionPhase with the given {@link Table} and {@link Player}.
     *
     * @param prev    the previous Phase that led to the creation of this one
     * @param current the current {@link Player} of this ActionPhase
     * @throws IllegalArgumentException if any parameter is null
     */
    ActionPhase(Phase prev, Player current) {
        super(prev, current);

        this.table = prev.getTable();
        playedCharacter = false;
        influenceCalculator = new StandardInfluenceCalculator();
        maxExtractor = new EqualityExclusiveMaxExtractor();
        extraMnMoves = 0;
    }

    /**
     * Creates a shallow copy of the given ActionPhase
     *
     * @param old the ActionPhase to copy
     * @throws IllegalArgumentException if {@code old} is null
     */
    ActionPhase(ActionPhase old) {
        super(old);

        table = old.table;
        playedCharacter = old.playedCharacter;
        influenceCalculator = old.influenceCalculator;
        maxExtractor = old.maxExtractor;
        extraMnMoves = old.extraMnMoves;
    }

    /**
     * Returns true if the current {@link Player} has already played a character during this phase.
     *
     * @return true if the current {@link Player} has already played a character during this phase.
     */
    boolean hasPlayedCharacter() {
        return playedCharacter;
    }

    /**
     * Returns the {@link Table} of this ActionPhase
     *
     * @return the {@link Table} of this ActionPhase
     */
    @Override
    Table getTable() {
        return table;
    }

    /**
     * Apply the given update to this object's Table
     *
     * @param update the update to apply
     * @return a new updated ActionPhase
     */
    ActionPhase updateTable(Function<Table, Table> update) {
        if (update == null) throw new IllegalArgumentException("update cannot be null");
        ActionPhase ret = this.shallowCopy();
        Table t = update.apply(ret.table);
        if (t != null)
            ret.table = t;
        return ret;
    }

    /**
     * Returns {@link MotherNature}'s movement extension of this ActionPhase
     *
     * @return {@link MotherNature}'s movement extension of this ActionPhase
     */
    int getExtraMnMoves() {
        return extraMnMoves;
    }

    /**
     * Abstract method that returns a shallow copy of the current object.
     *
     * @return returns a shallow copy of the current object.
     */
    abstract ActionPhase shallowCopy();

    /**
     * Retrieve a Student from the specified Player's entrance.
     *
     * @param player the Player of whom board to modify
     * @param color  the color to get from the entrance
     * @return a {@link Tuple} containing a Phase with the changes applied and the Student extracted
     * @throws InvalidPhaseUpdateException if the player's entrance doesn't have enough students of the specified color
     * @throws IllegalArgumentException    if any parameter is null
     */
    @Override
    public Tuple<ActionPhase, Student> getFromEntrance(Player player, PieceColor color) throws InvalidPhaseUpdateException {
        if (player == null) throw new IllegalArgumentException("player shouldn't be null");
        if (color == null) throw new IllegalArgumentException("color cannot be null");
        if (table.getBoardOf(player).getEntrance().size(color) == 0)
            throw new InvalidPhaseUpdateException("Player's entrance is empty");
        Tuple<BoundedStudentContainer, Student> update = table.getBoardOf(player).getEntrance().remove(color);
        return update.map((container, student) -> {
            ActionPhase a = this.shallowCopy();
            a.table = a.table.updateBoardOf(player, b -> b.updateEntrance(e -> container));
            return new Tuple<>(a, student);
        });
    }

    /**
     * Adds the given Student to specified student's entrance
     *
     * @param player  the Player to whose Entrance add the Student
     * @param student the Student to add
     * @return an ActionPhase with the update applied
     * @throws InvalidPhaseUpdateException if the player's entrance is full
     * @throws IllegalArgumentException    if any parameter is null
     */
    ActionPhase addToEntrance(Player player, Student student) throws InvalidPhaseUpdateException {
        if (player == null) throw new IllegalArgumentException("player shouldn't be null");
        if (student == null) throw new IllegalArgumentException("student shouldn't be null");
        if (table.getBoardOf(player).getEntrance().isFull())
            throw new InvalidPhaseUpdateException("entrance is full");
        ActionPhase a = shallowCopy();
        a.table = a.table.updateBoardOf(player, b -> b.updateEntrance(e -> e.add(student)));
        return a;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionPhase addToHall(Player player, Student student) throws InvalidPhaseUpdateException {
        if (player == null) throw new IllegalArgumentException("player shouldn't be null");
        if (student == null) throw new IllegalArgumentException("student shouldn't be null");
        Hall old = table.getBoardOf(player).getHall();
        if (old.isFull(student.getColor()))
            throw new InvalidPhaseUpdateException("cannot add student because this color is already full");
        return updateHall(player, hall -> hall.add(student))
                .reassignProfessors()
                .giveCoins(player, old);
    }

    /**
     * Like {@link #getFromEntrance(Player, PieceColor)}, return a Student of a specified color from the {@link Hall}.
     *
     * @param player the Player of whom board to modify
     * @param color  the color to get
     * @return a {@link Tuple} containing a Phase with the changes applied and the Student extracted
     * @throws IllegalArgumentException    if any argument is null
     * @throws InvalidPhaseUpdateException if there aren't enough students of the specified color in the player's hall
     */
    Tuple<ActionPhase, Student> getFromHall(Player player, PieceColor color) throws InvalidPhaseUpdateException {
        if (player == null) throw new IllegalArgumentException("player shouldn't be null");
        if (color == null) throw new IllegalArgumentException("color cannot be null");
        if (table.getBoardOf(player).getHall().size(color) == 0)
            throw new InvalidPhaseUpdateException("Player's entrance is empty");
        Hall oldHall = table.getBoardOf(player).getHall();
        Tuple<Hall, Student> update = table.getBoardOf(player).getHall().remove(color);
        return update.map((container, student) -> new Tuple<>(
                updateHall(player, h -> container).reassignProfessors().giveCoins(player, oldHall),
                student));
    }

    /**
     * Applies the given update to the Hall of the given {@link Player}.
     *
     * @param player the {@link Player} of whom the Hall will be updated
     * @param update the update to apply
     * @return a new Phase containing the update
     * @throws IllegalArgumentException if any parameter is null
     */
    private ActionPhase updateHall(Player player, Function<Hall, Hall> update) {
        if (player == null) throw new IllegalArgumentException("player shouldn't be null");
        if (update == null) throw new IllegalArgumentException("update cannot be null");
        ActionPhase a = this.shallowCopy();
        a.table = a.table.updateBoardOf(player, b -> b.updateHall(update));
        return a;
    }

    /**
     * Give one coin to the player for each stack of students that has crossed a multiple of 3 as size since the last
     * update. If the ActionPhase is not in expert mode, it does nothing.
     *
     * @param player the player to whom give coins
     * @return a new ActionPhase with the changes applied
     */
    private ActionPhase giveCoins(Player player, Hall old) {
        if (!getParameters().isExpertMode())
            return this;
        ActionPhase a = this.shallowCopy();
        for (PieceColor c : PieceColor.values()) {
            Hall h = a.table.getBoardOf(player).getHall();
            int diff = h.size(c) - old.size(c);
            if (diff > 0) {
                int numOfCoins = (h.size() / 3) - (old.size() / 3);
                a.table = a.table.updateBoardOf(player, b -> {
                    for (int i = 0; i < numOfCoins; i++)
                        b = b.receiveCoin();
                    return b;
                });
            }
        }
        return a;
    }

    /**
     * Reassign professors to the Players that own them (according to the phase's maxExtractor)
     *
     * @return a new ActionPhase with the updated professors
     */
    private ActionPhase reassignProfessors() {
        ActionPhase a = this.shallowCopy();
        HashMap<Professor, Optional<Player>> assignments = new HashMap<>();
        a.table.getProfessors().forEach(p -> {
            HashMap<Player, Integer> colorValues = new HashMap<>();
            a.table.getBoards().forEach(b -> colorValues.put(b.getPlayer(), b.getHall().size(p.getColor())));
            assignments.put(p, a.maxExtractor.apply(colorValues));
        });
        a.table = a.table.updateProfessors(ps -> {
            ps.replaceAll(p -> assignments
                    .get(p)
                    .map(o -> new Professor(p.getColor(), o))
                    .orElse(p));
            return ps;
        });
        return a;
    }

    /**
     * Apply the given mapper to each Player's board. If mapping returns null, skip the Player.
     *
     * @param update a mapper from Board to Board
     * @return an updated ActionPhase
     * @throws IllegalArgumentException if {@code update} is null
     */
    ActionPhase forEachPlayer(Function<Board, Board> update) {
        if (update == null) throw new IllegalArgumentException("update shouldn't be null");
        ActionPhase a = this.shallowCopy();
        for (Player p : a.table.getPlayers()) {
            Hall old = a.table.getBoardOf(p).getHall();
            Board b = update.apply(a.table.getBoardOf(p));
            if (b != null) {
                a.table = a.table.updateBoardOf(p, oldBoard -> b);
                a = a.giveCoins(p, old).reassignProfessors();
            }
        }
        return a;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionPhase addToIsland(Player player, int index, Student student) throws InvalidPhaseUpdateException {
        if (student == null) throw new IllegalArgumentException("student cannot be null");
        return updateIsland(player, index, i -> i.add(student));
    }

    /**
     * Applies the given update to the {@link StudentContainer} of the {@link Island} with the given index.
     *
     * @param player the {@link Player} that will execute this operation
     * @param index  the index of the {@link Island}
     * @param update the update to apply
     * @return a new Phase containing the update
     * @throws IllegalArgumentException    if any parameter is null
     * @throws InvalidPhaseUpdateException if the index is out of bounds
     */
    private ActionPhase updateIsland(Player player, int index, Function<StudentContainer, StudentContainer> update) throws InvalidPhaseUpdateException {
        if (player == null) throw new IllegalArgumentException("player shouldn't be null");
        if (update == null) throw new IllegalArgumentException("update cannot be null");
        if (!isValidIslandIndex(index))
            throw new InvalidPhaseUpdateException("invalid island index");
        ActionPhase ret = this.shallowCopy();
        ret.table = table.updateIslandList(islands -> {
            Island i = islands.remove(index).updateStudents(update);
            islands.add(index, i);
            return islands;
        });
        return ret;
    }

    /**
     * Returns true if the given index is inbounds of the ActionPhase's Island list
     *
     * @param index index to check
     * @return true if the given index is inbounds of the ActionPhase's Island list
     */
    boolean isValidIslandIndex(int index) {
        return index >= 0 && index < table.getIslandList().size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Phase playCharacter(Player player, CharacterType characterType, CharacterStep[] steps) throws InvalidPhaseUpdateException, InvalidCharacterParameterException {
        if (player == null) throw new IllegalArgumentException("player shouldn't be null");
        if (characterType == null) throw new IllegalArgumentException("characterType shouldn't be null");
        if (steps == null) throw new IllegalArgumentException("steps shouldn't be null");

        if (!getParameters().isExpertMode())
            throw new InvalidPhaseUpdateException("Action isn't allowed with these rules");
        if (playedCharacter) throw new InvalidPhaseUpdateException("cannot use 2 characters in the same turn");

        Character desired = table.getCharacters().stream()
                .filter(c -> c.getCharacterType().equals(characterType))
                .findAny()
                .orElseThrow(() -> new InvalidPhaseUpdateException("desired characterType is not present on the board"));

        ActionPhase p = spendPlayerCoins(player, desired.getCost())
                .applyCharacterEffect(desired, steps);
        if (p.checkWin())
            return new EndgamePhase(p);
        return p;
    }

    /**
     * Returns a new ActionPhase where the given {@link Player} has spent the given amount of coins
     *
     * @param player the {@link Player} that will spend coins
     * @param amount the amount of coins spent
     * @return a new updated ActionPhase
     * @throws InvalidPhaseUpdateException if {@code player}'s Board hasn't got enough coins
     */
    private ActionPhase spendPlayerCoins(Player player, int amount) throws InvalidPhaseUpdateException {
        ActionPhase ret = this.shallowCopy();
        var wrapper = new Object() {
            boolean failed = false;
        };
        ret.table = ret.table.updateBoardOf(player, (b) -> {
            try {
                return b.spendCoins(amount);
            } catch (NotEnoughCoinsException ignored) {
                wrapper.failed = true;
            }
            return b;
        });
        if (wrapper.failed)
            throw new InvalidPhaseUpdateException("player cannot buy this character");
        return ret;
    }

    /**
     * Returns a new ActionPhase where the given {@link Character} has executed its effect.
     *
     * @param desired the {@link Character} that will apply its effect
     * @param steps   the parameters to be passed to said {@link Character}
     * @return a new updated ActionPhase
     */
    private ActionPhase applyCharacterEffect(Character desired, CharacterStep[] steps) throws InvalidCharacterParameterException {
        Tuple<ActionPhase, Character> update = desired.doEffect(this, steps);
        ActionPhase ret = update.getFirst();
        ret.playedCharacter = true;
        ret.table = ret.table.updateCharacters(characters -> {
            Character current = update.getSecond();
            return characters.stream()
                    .map(c -> {
                        if (Objects.equals(c.getCharacterType(), current.getCharacterType()))
                            return current;
                        return c;
                    }).toList();
        });
        return ret;
    }

    /**
     * Assigns the given {@link Island} to the {@link Player} that has the highest influence.
     *
     * @param island {@link Island} calculate influence on
     * @return a new updated ActionPhase
     * @throws IllegalArgumentException if {@code island} is null
     */
    ActionPhase assignTower(Island island) {
        if (island == null) throw new IllegalArgumentException("island shouldn't be null");
        return influenceCalculator
                .calculateInfluences(island, table.getProfessors())
                .flatMap(maxExtractor)
                .map(p -> receiveTowersFromPlayer(p, island.getMaxNumOfTowers())
                        .map(t -> t.getFirst().replaceTowersOnIsland(island, t.getSecond()))
                        .map(t -> t.getFirst().sendTowersToOwner(t.getSecond())))
                .orElseGet(() -> this.shallowCopy().popBlockFromIsland(island));
    }

    /**
     * Convenience overload of {@link #assignTower(Island)}.
     *
     * @param index the index of the Island to assign towers to
     * @return a new updated ActionPhase
     */
    ActionPhase assignTower(int index) {
        if (index < 0 || index > table.getIslandList().size())
            throw new IllegalArgumentException("island index out of bounds");
        return assignTower(table.getIslandList().get(index));
    }

    /**
     * If blocked, pops one block from the island and returns it to the corresponding character.
     *
     * @param island the Island from which to pop the block
     * @return a new updated ActionPhase
     */
    private ActionPhase popBlockFromIsland(Island island) {
        if (!island.isBlocked()) return this;

        ActionPhase a = this.shallowCopy();
        return island.popBlock().map(t -> {
            a.table = a.table
                    .updateCharacters(characters -> characters.stream()
                            .map(c -> {
                                BlockCard block = t.getSecond();
                                if (Objects.equals(c.getCharacterType(), block.getOwner()))
                                    return c.pushBlock(block);
                                return c;
                            })
                            .toList())
                    .updateIslandList(islands -> islands.stream()
                            .map(i -> {
                                Island newIsland = t.getFirst();
                                if (Objects.equals(i.getIds(), newIsland.getIds()))
                                    return newIsland;
                                return i;
                            })
                            .toList());
            return a;
        });
    }

    /**
     * Tries to get as many Towers as the given amount of towers from the given {@link Player}.
     *
     * @param player the {@link Player} that will send the Towers
     * @param amount the amount of towers to send
     * @return a {@link Tuple} containing a new updated ActionPhase and the list of Tower received.
     */
    private Tuple<ActionPhase, List<Tower>> receiveTowersFromPlayer(Player player, int amount) {
        ActionPhase ret = this.shallowCopy();
        List<Tower> newTowers = new ArrayList<>();
        ret.table = ret.table.updateBoardOf(player, b -> {
            for (int i = 0; i < amount; i++) {
                try {
                    b = b.sendTower().map(t -> {
                        newTowers.add(t.getSecond());
                        return t.getFirst();
                    });
                } catch (NoTowersException ignored) {
                    break;
                }
            }
            return b;
        });
        return new Tuple<>(ret, newTowers);
    }

    /**
     * Sends the given list of Tower to their owner
     *
     * @param towers the list of Tower to send
     * @return a new updated ActionPhase
     */
    private ActionPhase sendTowersToOwner(List<Tower> towers) {
        ActionPhase ret = this.shallowCopy();
        if (towers.isEmpty())
            return ret;
        Player owner = towers.get(0).getOwner();
        ret.table = ret.table.updateBoardOf(owner, b -> {
            for (Tower t : towers)
                b = b.receiveTower(t);
            return b;
        });
        return ret;
    }

    /**
     * Replaces the currently placed towers on the given island with the given one and scrubs the list merging every
     * island it can.
     *
     * @param island    the Island on which to put the towers
     * @param newTowers the list of new Towers
     * @return a {@link Tuple} containing a new updated ActionPhase and the previous list of Tower.
     */
    private Tuple<ActionPhase, List<Tower>> replaceTowersOnIsland(Island island, List<Tower> newTowers) {
        ActionPhase ret = this.shallowCopy();
        List<Tower> oldTowers = new ArrayList<>();
        ret.table = ret.table.updateIslandList(islands -> scrub(
                islands.stream()
                        .map(replacement -> {
                            if (Objects.equals(replacement.getIds(), island.getIds()))
                                replacement = replacement.updateTowers(t -> {
                                    oldTowers.addAll(t);
                                    return newTowers;
                                });
                            return replacement;
                        })
                        .toList()));
        return new Tuple<>(ret, oldTowers);
    }

    /**
     * Scrubs the list merging {@link Island} where it can, keeping the correct index order.
     *
     * @param list the list to scrub
     * @return a new scrubbed list
     */
    private List<Island> scrub(List<Island> list) {
        List<Island> newList = new ArrayList<>();

        newList.add(list.get(0));
        for (Island i : list.subList(1, list.size())) {
            Island last = newList.get(newList.size() - 1);
            if (last.canBeMergedWith(i)) {
                newList.remove(last);
                newList.add(last.merge(i));
            } else {
                newList.add(i);
            }
        }
        Island first = newList.get(0);
        Island last = newList.get(newList.size() - 1);
        if (first.canBeMergedWith(last)) {
            newList.remove(first);
            newList.remove(last);
            newList.add(0, last.merge(first));
        }

        return newList;
    }

    /**
     * Returns true if conditions for immediate endgame are met.
     *
     * @return true if conditions for immediate endgame are met
     */
    boolean checkWin() {
        return table.getBoards().stream().anyMatch(b -> b.getNumOfTowers() == 0) ||
                table.getIslandList().size() == 3;
    }

    /**
     * Returns a new ActionPhase with the given {@link MotherNature} movement extension.
     *
     * @param steps the number of steps the movement will be extended
     * @return a new ActionPhase with the given {@link MotherNature} movement extension.
     * @throws IllegalArgumentException if {@code steps} is negative
     */
    ActionPhase requestExtraMnMovement(int steps) {
        if (steps < 0) throw new IllegalArgumentException("steps should be >= 0");
        ActionPhase ret = shallowCopy();
        ret.extraMnMoves = steps;
        return ret;
    }

    /**
     * Returns the {@link MaxExtractor} currently in use.
     *
     * @return the {@link MaxExtractor} currently in use.
     */
    public MaxExtractor getMaxExtractor() {
        return maxExtractor;
    }

    /**
     * Sets the MaxExtractor of this ActionPhase to the given one.
     *
     * @param extractor the new MaxExtractor
     * @return a new updated ActionPhase
     * @throws IllegalArgumentException if {@code extractor} is null
     */
    ActionPhase setMaxExtractor(MaxExtractor extractor) {
        if (extractor == null) throw new IllegalArgumentException("extractor shouldn't be null");
        ActionPhase ret = shallowCopy();
        ret.maxExtractor = extractor;
        return ret;
    }

    /**
     * Getter for this ActionPhase's {@link InfluenceCalculator}.
     *
     * @return this ActionPhase's {@link InfluenceCalculator}.
     */
    public InfluenceCalculator getInfluenceCalculator() {
        return this.influenceCalculator;
    }

    /**
     * Sets the InfluenceCalculator of this ActionPhase to the given one.
     *
     * @param influenceCalculator the new InfluenceCalculator
     * @return a new updated ActionPhase
     * @throws IllegalArgumentException if {@code influenceCalculator} is null
     */
    ActionPhase setInfluenceCalculator(InfluenceCalculator influenceCalculator) {
        if (influenceCalculator == null) throw new IllegalArgumentException("influenceCalculator shouldn't be null");
        ActionPhase ret = shallowCopy();
        ret.influenceCalculator = influenceCalculator;
        return ret;
    }

    /**
     * Block the Island with the given index with the give BlockCard
     *
     * @param index the index of the Island
     * @param block the BlockCard to use
     * @return a new updated ActionPhase
     * @throws IllegalArgumentException if {@code block} is null or {@code index} does not correspond to any island
     */
    ActionPhase blockIsland(int index, BlockCard block) {
        if (block == null) throw new IllegalArgumentException("block cannot be null");
        if (!isValidIslandIndex(index))
            throw new IllegalArgumentException("index is out of bounds");
        ActionPhase ret = shallowCopy();
        ret.table = ret.table.updateIslandList(islands -> {
            Island i = islands.remove(index).pushBlock(block);
            islands.add(index, i);
            return islands;
        });
        return ret;
    }

    /**
     * Draws a student from the board's Sack and returns it, if it can.
     *
     * @return an Optional containing the student drawn from the Sack
     */
    Tuple<ActionPhase, Optional<Student>> drawStudent() {
        ActionPhase ret = shallowCopy();
        var wrapper = new Object() {
            Student drawn = null;
        };
        ret.table = table.updateSack(sack -> {
            try {
                return sack.remove().map(t -> {
                    wrapper.drawn = t.getSecond();
                    return t.getFirst();
                });
            } catch (EmptyContainerException ignored) {
            }
            return sack;
        });
        return new Tuple<>(ret, Optional.ofNullable(wrapper.drawn));
    }

    /**
     * Puts the given list of Students in the sack
     *
     * @param students a list of Students
     * @return a new ActionPhase with the updates
     */
    ActionPhase putInSack(List<Student> students) {
        ActionPhase ret = shallowCopy();
        ret.table = ret.table.updateSack(sack -> {
            for (Student s : students)
                sack = sack.add(s);
            return sack;
        });
        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActionPhase that = (ActionPhase) o;
        return playedCharacter == that.playedCharacter &&
                extraMnMoves == that.extraMnMoves &&
                table.equals(that.table) &&
                influenceCalculator.equals(that.influenceCalculator) &&
                maxExtractor.equals(that.maxExtractor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(table, playedCharacter, influenceCalculator, maxExtractor, extraMnMoves);
    }
}
