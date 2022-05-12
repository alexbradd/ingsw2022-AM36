package it.polimi.ingsw.server.model;

import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.server.model.enums.AssistantType;
import it.polimi.ingsw.server.model.exceptions.EmptyContainerException;
import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;
import it.polimi.ingsw.server.model.exceptions.InvalidPlayerException;
import it.polimi.ingsw.server.model.iterators.AssistantValueIterator;
import it.polimi.ingsw.server.model.iterators.ClockWiseIterator;

import java.util.*;
import java.util.function.Function;

/**
 * This class represents the game state in which a player must play an assistant card. It uses a {@link ClockWiseIterator}
 * to cycle through all the players of the game, and keeps track of the already played assistants through the
 * {@link #alreadyPicked} attribute. After the current player plays an assistant card, another phase instance is created
 * and the game evolves to a new state (another {@code PlanningPhase} or a {@link StudentMovePhase}).
 *
 * @author Leonardo Bianconi
 * @see PreparePhase
 * @see StudentMovePhase
 * @see IteratedPhase
 * @see ClockWiseIterator
 */
class PlanningPhase extends IteratedPhase {
    /**
     * The table instance of the phase.
     */
    private Table table;
    /**
     * An iterator that iterates over the list of players in a cyclical way.
     */
    private final ClockWiseIterator iterator;

    /**
     * A list of {@link Assistant}s already chosen by someone else during this turn.
     */
    private final List<AssistantType> alreadyPicked;

    /**
     * Constructor that creates a new PlanningPhase given an IteratedPhase and an index representing the index inside the
     * {@code List<Player>} of the current player to play this turn. It is used from a {@code CloudPickPhase}, after a
     * game round has ended.
     *
     * @param prev the IteratedPhase to (partially) copy
     * @param currentIndex the index inside the list of players to choose an assistant
     */
    // CloudPickPhase -> PlanningPhase
    PlanningPhase(IteratedPhase prev, int currentIndex) {
        super(prev, prev.getTable().getPlayers().get(currentIndex));
        table = prev.getTable();

        iterator = new ClockWiseIterator(getTable().getBoards(), currentIndex);
        iterator.next();
        alreadyPicked = new ArrayList<>();
        refillClouds();
    }

    // for PreparePhase -> PlanningPhase, when instantiating a new AVI is not possible
    /**
     * Constructor that creates a new PlanningPhase given a {@link PreparePhase}, used only for the first round.
     * Because it is the first round and no assistants have been played yet, this constructor picks the starting player
     * with a {@link ClockWiseIterator} instance.
     *
     * @param prev the previous PreparePhase instance
     */
    PlanningPhase(PreparePhase prev) {
        super(prev, new ClockWiseIterator(prev.getTable().getBoards(), 0).next().getPlayer());
        table = prev.getTable();

        Player firstPlayer = new ClockWiseIterator(getTable().getBoards(), 0).next().getPlayer();
        int firstPlayerIndex = getTable().getPlayers().indexOf(firstPlayer);

        iterator = new ClockWiseIterator(getTable().getBoards(), firstPlayerIndex);
        iterator.next();
        alreadyPicked = new ArrayList<>();

        refillClouds();
    }

    /**
     * Shallow copy constructor given the previous {@code PlanningPhase}.
     *
     * @param prev the previous {@code PlanningPhase}
     */
    // shallow copy
    PlanningPhase(PlanningPhase prev) {
        super(prev);
        table = prev.table;
        iterator = prev.iterator;
        alreadyPicked = prev.alreadyPicked;
    }

    /**
     * Constructor that returns a new {@code PlanningPhase} instance, given the one to (partially) copy and the current
     * {@code Player}.
     *
     * @param prev    the previous {@code PlanningPhase} instance
     * @param current the current {@link Player} to choose an assistant
     */
    // PlanningPhase -> PlanningPhase
    PlanningPhase(PlanningPhase prev, Player current) {
        super(prev, current);
        table = prev.table;
        iterator = prev.iterator;
        alreadyPicked = prev.alreadyPicked;
    }

    /**
     * {@inheritDoc}
     */
    Table getTable() {
        return table;
    }

    /**
     * This method allows to update the Table instance of the Phase, and then returns a new PlanningPhase instance with
     * the updates.
     *
     * @param update the update function
     * @return the new PlanningPhase instance
     */
    PlanningPhase updateTable(Function<Table, Table> update) {
        PlanningPhase p = new PlanningPhase(this);
        p.table = update.apply(p.table);
        return p;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Phase playAssistant(Player player, AssistantType assistant) throws InvalidPhaseUpdateException {
        if (assistant == null) throw new IllegalArgumentException("assistant must not be null.");
        try {
            authorizePlayer(player.getUsername());
        } catch (InvalidPlayerException e) {
            throw new UnsupportedOperationException("It is not the specified player's turn to play an assistant.");
        }

        if (alreadyPicked.contains(assistant) && canPlayOther(player, assistant))
            throw new InvalidPhaseUpdateException("This assistant has already been played by another player in this round.");

        PlanningPhase p = new PlanningPhase(this);

        try {
            p = p.updateTable(t ->
                    t.updateBoardOf(player, b ->
                            b.playAssistant(assistant)
                    )
            );
        } catch (NoSuchElementException e) {
            throw new InvalidPhaseUpdateException("This assistant has already been played by the player.");
        }

        p.alreadyPicked.add(assistant);

        if (p.iterator.hasNext())
            return new PlanningPhase(p, p.iterator.next().getPlayer());

        AssistantValueIterator avi = new AssistantValueIterator(p.getTable().getBoards(), 0);
        Player firstToPlay = avi.next().getPlayer();
        return new StudentMovePhase(p, avi, firstToPlay);
    }

    /**
     * Helper method that populates all the {@code clouds} of the {@link Table} instance with students extracted from
     * the {@code Sack}. If the {@code Sack} is empty, then the {@code clouds} are only partially filled for this turn.
     */
    private void refillClouds() {
        for (Cloud c : table.getClouds()) {

            Set<Student> toCloud = new HashSet<>();
            for (int i = 0; i < parameters.getnStudentsMovable(); i++) {
                try {
                    table = table.updateSack(s -> {
                        Tuple<StudentContainer, Student> sackUpdate;
                        sackUpdate = s.remove();
                        toCloud.add(sackUpdate.getSecond());
                        return sackUpdate.getFirst();
                    });
                } catch (EmptyContainerException e) {
                    break;
                }
            }

            table = table.updateClouds(l -> {
                l.set(l.indexOf(c), c.refillCloud(toCloud));
                return l;
            });

        }
    }

    /**
     * Helper method that returns true if the specified {@code Player} cannot play an assistant of a different type of the
     * specified {@code AssistantType}.
     *
     * @param player the {@link Player} to play an assistant
     * @param type   the specified {@link AssistantType}
     * @return {@code true} if and only if the player has only that assistant type in his deck
     */
    private boolean canPlayOther(Player player, AssistantType type) {
        List<Assistant> playersDeck = getTable().getBoardOf(player).getAssistants();
        for (Assistant a : playersDeck)
            if (!a.getAssistantType().equals(type))
                return true;

        return false;
    }
}
