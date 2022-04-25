package it.polimi.ingsw.server.model;

/**
 * Mocks a phase.
 */
class MockPhase extends Phase {
    private final Table table;

    MockPhase(Table t) {
        super(GameParameters.twoPlayerGame(true));
        this.table = t;
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
