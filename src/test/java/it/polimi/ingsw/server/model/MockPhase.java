package it.polimi.ingsw.server.model;

/**
 * Mocks a phase.
 */
public class MockPhase extends Phase {
    private final Table table;

    MockPhase(Table t) {
        super(GameParameters.twoPlayerGame(true));
        this.table = t;
    }

    public MockPhase() {
        this(new Table());
    }

    MockPhase(Phase old) {
        super(old);
        table = old.getTable();
    }

    @Override
    public Table getTable() {
        return table;
    }
}
