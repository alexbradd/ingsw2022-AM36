package it.polimi.ingsw.server.model;

import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.enums.AssistantType;
import it.polimi.ingsw.enums.Mage;
import it.polimi.ingsw.enums.PieceColor;
import it.polimi.ingsw.server.model.exceptions.EmptyContainerException;
import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;
import it.polimi.ingsw.server.model.exceptions.InvalidPlayerException;
import it.polimi.ingsw.server.model.iterators.ClockWiseIterator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * It represents the state of the game in which all the game instances are being populated. Before that, it also asks for
 * all players to choose their mage (assistant cards deck). It generally follows the {@link LobbyPhase}, it assumes that
 * a lobby with the correct amount of players is already connected. After this phase, the game environment should be
 * fully ready for playing.
 *
 * @author Leonardo Bianconi
 * @see Phase
 * @see LobbyPhase
 * @see PlanningPhase
 */
class PreparePhase extends IteratedPhase {
    /**
     * The table instance of the phase.
     */
    private Table table;
    /**
     * An iterator that iterates over the list of players in a cyclical way.
     */
    private final ClockWiseIterator iterator;

    /**
     * A list of {@link Mage}s already chosen by someone else.
     */
    private final List<Mage> chosenMages;

    // LobbyPhase -> PreparePhase

    /**
     * A constructor that initializes a new {@code PreparePhase} when the {@link LobbyPhase} has ended.
     *
     * @param p the {@code LobbyPhase} instance
     */
    PreparePhase(Phase p) throws IllegalArgumentException {
        super(p, p.getTable().getPlayers().get(0));

        table = p.getTable();
        iterator = new ClockWiseIterator(p.getTable().getBoards(), 0);
        iterator.next();
        chosenMages = new ArrayList<>();
    }

    // PreparePhase -> PreparePhase

    /**
     * Shallow copy constructor given the previous {@code PreparePhase}.
     *
     * @param p the previous {@code PreparePhase}
     */
    PreparePhase(PreparePhase p) throws IllegalArgumentException {
        super(p);
        table = p.table;
        iterator = p.iterator;
        chosenMages = p.chosenMages;
    }

    /**
     * Shallow copy constructor given the previous {@code PreparePhase}, allows to specify a different {@link Player} to
     * play the round.
     *
     * @param prev    the previous {@code PreparePhase} instance
     * @param current the {@code Player} to play the round
     */
    PreparePhase(PreparePhase prev, Player current) throws IllegalArgumentException {
        super(prev, current);
        this.table = prev.table;
        this.iterator = prev.iterator;
        this.chosenMages = prev.chosenMages;
    }

    /**
     * {@inheritDoc}
     */
    Table getTable() {
        return table;
    }

    /**
     * This method allows to update the Table instance of the Phase, and then returns a new PreparePhase instance with
     * the updates.
     *
     * @param update the update function
     * @return the new PreparePhase instance
     */
    PreparePhase updateTable(Function<Table, Table> update) {
        PreparePhase p = new PreparePhase(this);
        p.table = update.apply(p.table);
        return p;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Phase chooseMageDeck(Player player, Mage mage) throws InvalidPhaseUpdateException {
        if (mage == null) throw new IllegalArgumentException("mage must not be null.");
        try {
            authorizePlayer(player.getUsername());
        } catch (InvalidPlayerException e) {
            throw new UnsupportedOperationException("It is not the specified player's turn to pick a mage");
        }

        if (chosenMages.contains(mage))
            throw new InvalidPhaseUpdateException("This mage has already been chosen.");

        List<Assistant> deck = new ArrayList<>();
        for (AssistantType type : AssistantType.values()) {
            deck.add(new Assistant(type, mage));
        }
        PreparePhase p = updateTable(t -> t.updateBoardOf(getCurrentPlayer(), b -> b.receiveDeck(mage, deck)));
        p.chosenMages.add(mage);

        if (p.iterator.hasNext())
            return new PreparePhase(p, p.iterator.next().getPlayer());

        PreparePhase ready = setupTable(p);
        return new PlanningPhase(ready);
    }

    /**
     * Helper method that sets up all the uninitialized game entities for the start of a new game. The order of the
     * different steps of the initialization process follows, when possible, the real order of the preparation of the
     * game (see game rules). More in details:
     * <ul>
     *     <li>{@link GameParameters#getnStudentsInSack()} of each color are added to the {@code Sack}</li>
     *     <li>These students are distributed randomly on the {@code List<Island>}</li>
     *     <li>The remaining students are added to the {@code Sack}</li>
     *     <li>The {@code List<Cloud>} is populated</li>
     *     <li>Every {@code Board} receives {@link GameParameters#getnTowers()} towers</li>
     *     <li>Every {@code Board} receives {@link GameParameters#getnStudentsEntrance()} students</li>
     *     <li>If {@link GameParameters#isExpertMode()} {@code == true}, distribute coins pick
     *              {@link GameParameters#getnOfCharacters()} {@code Character}s and execute the PreparePhase hook</li>
     * </ul>
     *
     * @param phase the phase on which to perform the initialization operations
     * @return the new phase with an initialized {@link #table}
     */
    private PreparePhase setupTable(PreparePhase phase) {
        PreparePhase standard = phase
                .fillSack(parameters.getnStudentsInSack())
                .putStudentsOnIslands()
                .fillSack(parameters.getnStudentsOfColor() - parameters.getnStudentsInSack())
                .placeClouds()
                .distributeStudents()
                .distributeTowers();

        return parameters.isExpertMode()
                ? standard.distributeCoins().pickCharacters().executeCharacterHook()
                : standard;
    }

    /**
     * Helper method that fills the {@code Sack} with {@code nOfColor} students of each color, such that the total number
     * of students added is {@code nOfColor * number of piece colors}.
     *
     * @param nOfColor the number of students of each color to be added
     * @return the new updated {@code PreparePhase}
     */
    private PreparePhase fillSack(int nOfColor) {
        PreparePhase newPhase = new PreparePhase(this);
        for (PieceColor color : PieceColor.values()) {
            for (int i = 0; i < nOfColor; i++) {
                newPhase = newPhase.updateTable(t -> t.updateSack(s -> s.add(new Student(color))));
            }
        }

        return newPhase;
    }

    /**
     * Helper method that distributes all the students inside the sack on the islands.
     * Edge cases (for usage on different parameters than the ones specified by the game rules):
     * <ul>
     *     <li>If {@code (number of islands) - 2 > students in sack }, some islands (besides the one on which Mother
     *     Nature lies and the opposite one) will be initially empty</li>
     *     <li>If {@code (number of islands) - 2 < students in sack }, the sack won't be empty after this operation</li>
     * </ul>
     *
     * @return the new updated {@code PreparePhase}
     * @see #extractFromSack()
     */
    private PreparePhase putStudentsOnIslands() {
        PreparePhase newPhase = new PreparePhase(this);

        int mnIslandId = getTable().getMotherNature().getCurrentIslandId();
        int oppositeIslandId = (mnIslandId + parameters.getnIslands() / 2) % parameters.getnIslands();

        for (int i = 0; i < parameters.getnIslands(); i++) {
            if (i != mnIslandId && i != oppositeIslandId) {

                Tuple<PreparePhase, Student> sackUpdate = null;
                try {
                    sackUpdate = newPhase.extractFromSack();
                } catch (EmptyContainerException ignored) {
                    break;
                }

                newPhase = sackUpdate.getFirst();

                int finalI = i;
                Tuple<PreparePhase, Student> finalSackUpdate = sackUpdate;
                newPhase = newPhase.updateTable(t ->
                        t.updateIslandList(l -> {
                            l.set(finalI, l.get(finalI).updateStudents(s -> s.add(finalSackUpdate.getSecond())));
                            return l;
                        })
                );
            }
        }

        return newPhase;
    }

    /**
     * Helper method that places {@link GameParameters#getnPlayers()} empty {@code Clouds} on the {@code Table}.
     *
     * @return the new updated {@code PreparePhase}
     */
    private PreparePhase placeClouds() {
        PreparePhase newPhase = new PreparePhase(this);

        List<Cloud> newCloudList = new ArrayList<>();
        for (int i = 0; i < parameters.getnPlayers(); i++)
            newCloudList.add(new Cloud(parameters.getnStudentsMovable()));

        return newPhase.updateTable(t -> t.updateClouds(l -> newCloudList));
    }

    /**
     * Helper method that distributes {@link GameParameters#getnStudentsEntrance()} students to each player's
     * {@code Board}.
     *
     * @return the new updated {@code PreparePhase}
     * @see #extractFromSack()
     */
    private PreparePhase distributeStudents() {
        PreparePhase newPhase = new PreparePhase(this);

        for (Player p : table.getPlayers()) {

            for (int i = 0; i < parameters.getnStudentsEntrance(); i++) {
                Tuple<PreparePhase, Student> update = newPhase.extractFromSack();
                newPhase = update.getFirst();
                newPhase = newPhase.updateTable(t -> t.updateBoardOf(p, b ->
                        b.updateEntrance(sc -> sc.add(update.getSecond())
                        )));
            }
        }
        return newPhase;
    }

    /**
     * Helper method that distributes {@link GameParameters#getnTowers()} towers to each player's
     * {@code Board}.
     *
     * @return the new updated {@code PreparePhase}
     */
    private PreparePhase distributeTowers() {
        PreparePhase newPhase = new PreparePhase(this);
        for (Player p : table.getPlayers())
            for (int i = 0; i < parameters.getnTowers(); i++) {
                newPhase = newPhase.updateTable(t ->
                        t.updateBoardOf(p, b ->
                                b.receiveTower(new Tower(b.getTowersColor(), p))
                        )
                );
            }
        return newPhase;
    }

    /**
     * Helper method that distributes one coin to each player's {@code Board}.
     *
     * @return the new updated {@code PreparePhase}
     */
    private PreparePhase distributeCoins() {
        PreparePhase newPhase = new PreparePhase(this);
        for (Player p : getTable().getPlayers())
            newPhase = newPhase.updateTable(t ->
                    t.updateBoardOf(p, Board::receiveCoin)
            );

        return newPhase;
    }

    /**
     * Helper method that picks three character cards and places them on the {@link #table}.
     *
     * @return the new updated {@code PreparePhase}
     */
    private PreparePhase pickCharacters() {
        PreparePhase newPhase = new PreparePhase(this);
        List<Character> characterDeck = Arrays.stream(
                CharacterFactory.pickNRandom(parameters.getnOfCharacters())
        ).toList();

        return newPhase.updateTable(t -> t.updateCharacters(l -> characterDeck));
    }

    /**
     * Runs the PreparePhase hooks of the various Character cards
     *
     * @return the new updated {@code PreparePhase}
     */
    private PreparePhase executeCharacterHook() {
        PreparePhase p = new PreparePhase(this);
        for (Character character : table.getCharacters()) {
            p = character.doPrepare(p)
                    .map((preparePhase, updatedCharacter) -> preparePhase
                            .updateTable(t -> t
                                    .updateCharacters(cs -> {
                                        cs.replaceAll(c -> {
                                            if (c.getCharacterType() == updatedCharacter.getCharacterType())
                                                return updatedCharacter;
                                            return c;
                                        });
                                        return cs;
                                    })));
        }
        return p;
    }

    /**
     * Helper method that extracts one {@code Student} from the {@code Sack}, and returns the update, as a {@link Tuple}
     * containing the updated {@code PreparePhase} and the retrieved {@code Student}.
     *
     * @return a {@link Tuple} containing the updated {@code PreparePhase} and the retrieved {@code Student}.
     * @throws EmptyContainerException if the {@code Sack} is empty
     */
    Tuple<PreparePhase, Student> extractFromSack() throws EmptyContainerException {
        var wrapper = new Object() {
            Student drawn = null;
        };
        PreparePhase newPhase = new PreparePhase(this);

        newPhase = newPhase.updateTable(t -> t.updateSack(sack -> {
            Tuple<StudentContainer, Student> sackUpdate;
            sackUpdate = sack.remove();
            wrapper.drawn = sackUpdate.getSecond();
            return sackUpdate.getFirst();
        }));

        return new Tuple<>(newPhase, wrapper.drawn);
    }
}
