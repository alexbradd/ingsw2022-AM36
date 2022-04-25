package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;
import it.polimi.ingsw.server.model.iterators.AssistantValueIterator;

import java.util.List;
import java.util.Objects;

/**
 * Represents the part of the player's turn where he should decide how many steps
 *
 * @author Alexandru Gabriel Bradatan
 */
class MnMovePhase extends ActionPhase {
    /**
     * This phase's iterator.
     */
    private final AssistantValueIterator avi;

    /**
     * Creates a MnMovePhase from a previous actionPhase
     *
     * @param old      the ActionPhase to copy
     * @param iterator the iterator that this phase will have
     * @throws IllegalArgumentException if any parameter is null
     */
    MnMovePhase(ActionPhase old, AssistantValueIterator iterator) {
        super(old);
        if (iterator == null) throw new IllegalArgumentException("iterator should not be null");
        this.avi = new AssistantValueIterator(iterator);
    }

    /**
     * Creates a shallow copy of the given MnMovePhase
     *
     * @param old the MnMovePhase to copy
     * @throws IllegalArgumentException if {@code old} is null
     */
    private MnMovePhase(MnMovePhase old) {
        super(old);
        this.avi = old.avi;
    }

    /**
     * {@inheritDoc}
     */
    ActionPhase shallowCopy() {
        return new MnMovePhase(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Phase moveMn(Player player, int steps) throws InvalidPhaseUpdateException {
        if (player == null) throw new IllegalArgumentException("player shouldn't be null");
        Board board = getTable().getBoardOf(player);
        int maxSteps = board.getLastPlayedAssistant().orElseThrow(IllegalStateException::new).getMNSteps() + getExtraMnMoves();
        if (steps < 1 || steps > maxSteps)
            throw new InvalidPhaseUpdateException("Wrong amount of steps");

        ActionPhase newPhase = shallowCopy();
        newPhase = newPhase.updateTable(b -> {
            List<Island> oldList = b.getIslandList();
            return b.updateMotherNature(mn -> mn.move(oldList, steps));
        });
        Island newMnCurrent = newPhase.getTable().getMotherNature().getCurrentIsland();
        newPhase = newPhase.assignTower(newMnCurrent);
        return nextPhase((MnMovePhase) newPhase);
    }

    /**
     * Helper to choose the next phase to return
     *
     * @param phase the current phase
     * @return the next Phase
     */
    private Phase nextPhase(MnMovePhase phase) {
        Table t = phase.getTable();
        if (phase.checkWin())
            return new EndgamePhase(phase);
        if (t.getSack().size() == 0) {
            if (phase.avi.hasNext()) {
                Player p = phase.avi.next().getPlayer();
                return new StudentMovePhase(phase, phase.avi, p);
            }
            return new EndgamePhase(phase);
        }
        return new CloudPickPhase(phase, phase.avi);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MnMovePhase that = (MnMovePhase) o;
        return avi.equals(that.avi);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), avi);
    }
}
