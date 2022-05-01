package it.polimi.ingsw.server.model;

/**
 * STUB
 */
public class StudentMovePhase extends ActionPhase {
    private Table table;

    StudentMovePhase(IteratedPhase prev, Player current) {
        super(prev, current);
        table = prev.getTable();
    }

    StudentMovePhase(ActionPhase prev) {
        super(prev);
    }

    @Override
    Table getTable() {
        return table;
    }
}
