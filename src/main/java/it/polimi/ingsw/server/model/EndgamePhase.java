package it.polimi.ingsw.server.model;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * This phase represents the very last state of a game: the one in which a winner has been found and the game has ended.
 * This phase has no operations. It is the only phase marked as final and in which it is possible to retrieve a
 * non-empty list of winners.
 *
 * @author Leonardo Bianconi, Alexandru Gabriel Bradatan
 * @see Phase
 */
public class EndgamePhase extends Phase {
    /**
     * This EndgamePhase's table
     */
    private final Table table;

    /**
     * Creates a new EndGamePhase given the previous Phase
     *
     * @param prev the previous Phase
     * @throws IllegalArgumentException if {@code prev} is null
     */
    EndgamePhase(Phase prev) {
        super(prev);
        this.table = prev.getTable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Table getTable() {
        return table;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    boolean isFinal() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    List<Player> getWinners() {
        // if there are any players with zero towers, they are the winners. For how the game is structured, this will
        // always give 1 player.
        if (table.getBoards().stream().anyMatch(b -> b.getNumOfTowers() == 0))
            return table.getBoards().stream()
                    .filter(p -> p.getNumOfTowers() == 0)
                    .map(Board::getPlayer)
                    .toList();

        // From the players with the minimum amount of Towers, get those that have the maximum number of professors
        int minimumAmountOfTowers = table.getBoards().stream().min(Comparator.comparingInt(Board::getNumOfTowers))
                .orElseThrow(() -> new IllegalStateException("There should be at least 2 boards"))
                .getNumOfTowers();
        List<Board> withMinimumTowers = table.getBoards().stream()
                .filter(b -> b.getNumOfTowers() == minimumAmountOfTowers)
                .toList();
        int maximumAmountOfProfessors = withMinimumTowers.stream()
                .map(this::getNumOfProfessor)
                .max(Integer::compareTo)
                .orElseThrow(() -> new IllegalStateException("There should be at least 2 boards"));
        return withMinimumTowers.stream()
                .filter(b -> getNumOfProfessor(b) == maximumAmountOfProfessors)
                .map(Board::getPlayer)
                .toList();
    }

    /**
     * Returns the number of professors of the given board
     *
     * @param board the board to inspect
     * @return the number of professors of the given board
     */
    private int getNumOfProfessor(Board board) {
        return (int) table.getProfessors().stream()
                .filter(p -> Objects.equals(p.getOwner(), Optional.of(board.getPlayer())))
                .count();
    }
}
