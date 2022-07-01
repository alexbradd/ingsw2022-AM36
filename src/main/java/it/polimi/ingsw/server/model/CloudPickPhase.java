package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;
import it.polimi.ingsw.server.model.iterators.AssistantValueIterator;

import java.util.HashSet;
import java.util.Set;

/**
 * This phase represents the last action a player must perform during his turn: picking a cloud full of students from the
 * table and moving all of these students to its Entrance. After that, the game evolves to a new phase, depending on whether
 * the current player is the last of this turn or not.
 *
 * @author Leonardo Bianconi
 * @see MnMovePhase
 * @see ActionPhase
 */
public class CloudPickPhase extends ActionPhase {
    /**
     * This phase's AssistantValueIterator
     */
    private final AssistantValueIterator avi;

    /**
     * Creates a new ActionPhase with the given {@link Table} and {@link Player}.
     *
     * @param prev the previous Phase that led to the creation of this one
     * @param avi  an {@link AssistantValueIterator}
     * @throws IllegalArgumentException if any parameter is null
     */
    CloudPickPhase(ActionPhase prev, AssistantValueIterator avi) {
        super(prev);
        if (avi == null) throw new IllegalArgumentException("iterator shouldn't be null");
        this.avi = new AssistantValueIterator(avi);
    }

    /**
     * Creates a shallow copy of the given CloudPickPhase
     *
     * @param old the CloudPickPhase to copy
     * @throws IllegalArgumentException if {@code old} is null
     */
    private CloudPickPhase(CloudPickPhase old) {
        super(old);
        this.avi = old.avi;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Phase drainCloud(Player player, int id) throws InvalidPhaseUpdateException {
        if (player == null) throw new IllegalArgumentException("player shouldn't be null");
        if (id < 0 || id >= getTable().getClouds().size())
            throw new InvalidPhaseUpdateException("Index out of bounds for clouds");
        if (getTable().getClouds().get(id).size() == 0)
            throw new InvalidPhaseUpdateException("Cloud with given index has already been drained");
        CloudPickPhase cloudPickPhase = this.shallowCopy();
        cloudPickPhase = (CloudPickPhase) cloudPickPhase.updateTable(t -> {
            Set<Student> drained = new HashSet<>();
            return t.updateClouds(cs -> {
                        Cloud cloud = cs.remove(id);
                        cs.add(id, cloud.drainCloud().map((c, s) -> {
                            drained.addAll(s);
                            return c;
                        }));
                        return cs;
                    })
                    .updateBoardOf(player, b -> b.updateEntrance(e -> {
                        for (Student s : drained)
                            e = e.add(s);
                        return e;
                    }));
        });
        return nextPhase(cloudPickPhase);
    }

    /**
     * Helper to choose the next phase to return
     *
     * @param cloudPickPhase the current phase
     * @return the next Phase
     */
    private Phase nextPhase(CloudPickPhase cloudPickPhase) {
        Table t = cloudPickPhase.getTable();
        if (checkWin())
            return new EndgamePhase(cloudPickPhase);
        if (cloudPickPhase.avi.hasNext()) {
            Player next = cloudPickPhase.avi.next().getPlayer();
            return new StudentMovePhase(cloudPickPhase, cloudPickPhase.avi, next);
        }
        if (t.getSack().size() == 0 || t.getBoards().stream().anyMatch(b -> b.getAssistants().size() == 0))
            return new EndgamePhase(cloudPickPhase);
        return new PlanningPhase(cloudPickPhase, cloudPickPhase.avi.getFirstPlayedIndex());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    CloudPickPhase shallowCopy() {
        return new CloudPickPhase(this);
    }
}
