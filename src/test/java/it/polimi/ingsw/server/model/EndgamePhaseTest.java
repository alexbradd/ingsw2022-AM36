package it.polimi.ingsw.server.model;

import it.polimi.ingsw.enums.PieceColor;
import it.polimi.ingsw.enums.TowerColor;
import it.polimi.ingsw.server.model.exceptions.NoTowersException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for EndgamePhase
 */
class EndgamePhaseTest {
    private static Player ann, bob, carl;
    private static Table t;

    /**
     * Sets up common starting state.
     */
    @BeforeAll
    static void setup() {
        ann = new Player("ann");
        bob = new Player("bob");
        carl = new Player("carl");
        t = new Table()
                .addPlayer(ann, 9, 6, TowerColor.BLACK)
                .addPlayer(bob, 9, 6, TowerColor.WHITE)
                .addPlayer(carl, 9, 6, TowerColor.GRAY)
                .updateBoardOf(ann, b -> {
                    for (int i = 0; i < 6; i++)
                        b = b.receiveTower(new Tower(TowerColor.BLACK, ann));
                    return b;
                })
                .updateBoardOf(bob, b -> {
                    for (int i = 0; i < 6; i++)
                        b = b.receiveTower(new Tower(TowerColor.WHITE, bob));
                    return b;
                })
                .updateBoardOf(carl, b -> {
                    for (int i = 0; i < 6; i++)
                        b = b.receiveTower(new Tower(TowerColor.GRAY, carl));
                    return b;
                });
    }

    /**
     * Check that if a player has zero towers he is the winner. The code allows for multiple players to have zero towers,
     * however for how the game rules are, this is impossible. We can safely assume that only one player will have zero
     * towers and only check that case.
     */
    @Test
    void withZeroTowers() {
        Table withZeroTowers = t
                .updateBoardOf(ann, b -> {
                    while (true) {
                        try {
                            b = b.sendTower().getFirst();
                        } catch (NoTowersException ignored) {
                            break;
                        }
                    }
                    return b;
                })
                .updateProfessors(ps -> List.of(
                        new Professor(PieceColor.RED, bob),
                        new Professor(PieceColor.BLUE, bob),
                        new Professor(PieceColor.PINK, bob),
                        new Professor(PieceColor.GREEN, bob),
                        new Professor(PieceColor.YELLOW, bob)));
        EndgamePhase phase = new EndgamePhase(new MockPhase(withZeroTowers));
        List<Player> w = phase.getWinners();
        assertEquals(1, w.size());
        assertEquals(ann, w.get(0));
    }

    /**
     * Check that if there is no player with 0 towers, those with the most professors are chosen.
     * <p>
     * Case: two players with the same amount of towers but only one has the maximum amount of professors
     */
    @Test
    void withNonZeroTowersNoTie() {
        Table withTowers = t
                .updateBoardOf(ann, b -> {
                    for (int i = 0; i < 5; i++) b = b.sendTower().getFirst();
                    return b;
                })
                .updateBoardOf(bob, b -> {
                    for (int i = 0; i < 4; i++) b = b.sendTower().getFirst();
                    return b;
                })
                .updateBoardOf(carl, b -> {
                    for (int i = 0; i < 5; i++) b = b.sendTower().getFirst();
                    return b;
                })
                .updateProfessors(ps -> List.of(
                        new Professor(PieceColor.RED),
                        new Professor(PieceColor.BLUE, ann),
                        new Professor(PieceColor.PINK, bob),
                        new Professor(PieceColor.GREEN, bob),
                        new Professor(PieceColor.YELLOW, bob)));

        EndgamePhase phase = new EndgamePhase(new MockPhase(withTowers));
        List<Player> w = phase.getWinners();
        assertEquals(1, w.size());
        assertEquals(ann, w.get(0));
    }

    /**
     * Check that if there is no player with 0 towers, those with the most professors are chosen.
     * <p>
     * Case: two players with the same amount of towers but there is a tie on the amount of professors
     */
    @Test
    void withNonZeroTowersTie() {
        Table withTowers = t
                .updateBoardOf(ann, b -> {
                    for (int i = 0; i < 5; i++) b = b.sendTower().getFirst();
                    return b;
                })
                .updateBoardOf(bob, b -> {
                    for (int i = 0; i < 4; i++) b = b.sendTower().getFirst();
                    return b;
                })
                .updateBoardOf(carl, b -> {
                    for (int i = 0; i < 5; i++) b = b.sendTower().getFirst();
                    return b;
                })
                .updateProfessors(ps -> List.of(
                        new Professor(PieceColor.RED, carl),
                        new Professor(PieceColor.BLUE, ann),
                        new Professor(PieceColor.PINK, bob),
                        new Professor(PieceColor.GREEN, bob),
                        new Professor(PieceColor.YELLOW, bob)));

        EndgamePhase phase = new EndgamePhase(new MockPhase(withTowers));
        List<Player> w = phase.getWinners();
        assertEquals(2, w.size());
        assertTrue(w.contains(ann));
        assertTrue(w.contains(carl));
        assertFalse(w.contains(bob));
    }
}