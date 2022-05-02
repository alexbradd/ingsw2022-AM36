package it.polimi.ingsw.server.model;


/**
 * Mocks an actionPhase implementation
 */
class MockActionPhase extends ActionPhase {
    private final Table table;

    /**
     * Creates a new MockActionPhase with the given {@link Table} and {@link Player}.
     *
     * @param t       the Table this mock should have
     * @param current the current {@link Player} of this ActionPhase
     * @throws IllegalArgumentException if any parameter is null
     */
    MockActionPhase(Table t, Player current) {
        super(new MockPhase(t), current);
        this.table = t;
    }

    private MockActionPhase(MockActionPhase old) {
        super(old);
        this.table = old.table;
    }

    /**
     * Abstract method that returns a shallow copy of the current object.
     *
     * @return returns a shallow copy of the current object.
     */
    @Override
    ActionPhase shallowCopy() {
        return new MockActionPhase(this);
    }

    public Phase mockPhaseChangeOperation() {
        return new MockActionPhase(this);
    }
}
