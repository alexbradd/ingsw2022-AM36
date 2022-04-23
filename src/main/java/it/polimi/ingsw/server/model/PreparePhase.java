package it.polimi.ingsw.server.model;

/**
 * STUB
 */
public class PreparePhase extends Phase {
    private Table t;
    public PreparePhase(LobbyPhase old) {
        super(old.parameters);
        this.t = old.getTable();
    }
    @Override
    Table getTable() {
        return new Table(t);
    }
}
