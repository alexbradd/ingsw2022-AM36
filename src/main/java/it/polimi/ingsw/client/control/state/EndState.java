package it.polimi.ingsw.client.control.state;

/**
 * Client's representation of the end state of the game.
 *
 * @author Mattia Busso
 */
public class EndState {

    /**
     * The winners of the game.
     * Empty if there are no winners.
     */
    private String[] winners;

    /**
     * The reason by which the game ended.
     */
    private String reason;

    // getters

    /**
     * Returns the reason by which the game ended.
     *
     * @return the reason by which the game ended.
     */
    public String getReason() {
        return reason;
    }

    // stringify

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "** The game has ended **\n" + reason +
                (winners != null && winners.length != 0 ? "\n" + displayWinners() : "") + "\n";
    }

    /**
     * Helper method that returns a custom formatted string displaying the winners of the game.
     * Invoked if winners are actually present.
     *
     * @return custom formatted string of winners
     */
    private String displayWinners() {
        if(winners.length == 1) return winners[0] + " has won!";
        else {
            StringBuilder s = new StringBuilder();
            for(int i = 0; i < winners.length - 1; i++) {
                s.append(winners[i]).append(", ");
            }
            s.append(winners[winners.length - 1]).append(" have won!");
            return s.toString();
        }
    }

}
