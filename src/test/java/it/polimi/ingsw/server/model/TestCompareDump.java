package it.polimi.ingsw.server.model;

import it.polimi.ingsw.enums.*;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;
import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class entirely dedicated to testing {@link Phase#compare(Phase)} and {@link Phase#dump()}.
 * <p>
 * In each test a check for the presence of the phase's name is included, in addition to whatever the test is checking.
 */
public class TestCompareDump {
    /**
     * Null check
     */
    @Test
    void nullCheck() {
        assertThrows(IllegalArgumentException.class, () -> new MockPhase((Table) null).compare(null));
    }

    /**
     * Compare against two equal phases yields an empty PhaseDiff
     */
    @Test
    void equal() {
        MockPhase p = new MockPhase(new Table());
        PhaseDiff diff = p.compare(p);

        assertFalse(diff.getAttributes().isEmpty());
        assertTrue(diff.getEntityUpdates().isEmpty());
        assertEquals(1, diff.getAttributes().size());
        assertEquals(MockPhase.class.getSimpleName(), diff.getAttributes().get("phase").getAsString());
    }

    /**
     * like {@link #equal()}, however now the table has much more stuff in it, and it is not the same instance. It is
     * more of a test of the various {@code equals} methods.
     */
    @Test
    void deepEquals() {
        Player ann = new Player("ann");
        Table t1 = new Table()
                .addPlayer(ann, 5, 5, TowerColor.BLACK)
                .updateBoardOf(ann, b -> {
                    ArrayList<Assistant> d = new ArrayList<>();
                    d.add(new Assistant(AssistantType.CAT, Mage.MAGE));
                    d.add(new Assistant(AssistantType.ELEPHANT, Mage.MAGE));
                    return b.receiveDeck(Mage.MAGE, d)
                            .playAssistant(AssistantType.CAT)
                            .receiveCoin()
                            .updateEntrance(c -> c.add(new Student(PieceColor.BLUE)))
                            .updateHall(c -> c.add(new Student(PieceColor.BLUE)));
                })
                .updateProfessors(ps -> {
                    ps.replaceAll(p -> new Professor(p.getColor(), ann));
                    return ps;
                })
                .updateIslandList(is -> {
                    is.replaceAll(i -> i
                            .updateStudents(c -> c.add(new Student(PieceColor.RED)))
                            .updateTowers(ts -> {
                                ts.add(new Tower(TowerColor.BLACK, ann));
                                return ts;
                            })
                            .pushBlock(new BlockCard(CharacterType.HERBALIST)));
                    return is;
                })
                .updateCharacters(cs -> {
                    cs.add(new Herbalist());
                    cs.add(new Innkeeper());
                    return cs;
                })
                .updateClouds(cs -> {
                    cs.add(new Cloud(2).add(new Student(PieceColor.GREEN)));
                    return cs;
                })
                .updateSack(s -> s.add(new Student(PieceColor.PINK)));
        Table finalT = t1;
        t1 = t1.updateMotherNature(__ -> new MotherNature(finalT.getIslandList(), 0));
        Table t2 = new Table()
                .addPlayer(ann, 5, 5, TowerColor.BLACK)
                .updateBoardOf(ann, b -> {
                    ArrayList<Assistant> d = new ArrayList<>();
                    d.add(new Assistant(AssistantType.CAT, Mage.MAGE));
                    d.add(new Assistant(AssistantType.ELEPHANT, Mage.MAGE));
                    return b.receiveDeck(Mage.MAGE, d)
                            .playAssistant(AssistantType.CAT)
                            .receiveCoin()
                            .updateEntrance(c -> c.add(new Student(PieceColor.BLUE)))
                            .updateHall(c -> c.add(new Student(PieceColor.BLUE)));
                })
                .updateProfessors(ps -> {
                    ps.replaceAll(p -> new Professor(p.getColor(), ann));
                    return ps;
                })
                .updateIslandList(is -> {
                    is.replaceAll(i -> i
                            .updateStudents(c -> c.add(new Student(PieceColor.RED)))
                            .updateTowers(ts -> {
                                ts.add(new Tower(TowerColor.BLACK, ann));
                                return ts;
                            })
                            .pushBlock(new BlockCard(CharacterType.HERBALIST)));
                    return is;
                })
                .updateCharacters(cs -> {
                    cs.add(new Herbalist());
                    cs.add(new Innkeeper());
                    return cs;
                })
                .updateClouds(cs -> {
                    cs.add(new Cloud(2).add(new Student(PieceColor.GREEN)));
                    return cs;
                })
                .updateSack(s -> s.add(new Student(PieceColor.PINK)));
        Table finalT1 = t2;
        t2 = t2.updateMotherNature(__ -> new MotherNature(finalT1.getIslandList(), 0));
        MockPhase p1 = new MockPhase(t1);
        MockPhase p2 = new MockPhase(t2);

        PhaseDiff diff = p2.compare(p1);
        assertTrue(diff.getEntityUpdates().isEmpty());
        assertEquals(1, diff.getAttributes().size());
        assertEquals(MockPhase.class.getSimpleName(), diff.getAttributes().get("phase").getAsString());
    }

    /**
     * Comparing a phase with a current player and one without yields an attribute change and a name difference, if
     * the same table is shared.
     */
    @Test
    void currentPlayerNameChange() {
        Player ann = new Player("ann");
        Table t = new Table();
        IteratedPhase p = new IteratedPhase(new MockPhase(t), ann) {
            @Override
            Table getTable() {
                return t;
            }

            @Override
            Player getCurrentPlayer() {
                return ann;
            }
        };
        MockPhase op = new MockPhase(t);
        PhaseDiff diff = op.compare(p);

        assertTrue(diff.getEntityUpdates().isEmpty());
        assertEquals(ann.getUsername(), diff.getAttributes().get("currentPlayer").getAsString());
    }

    /**
     * Comparing a phase with one that has a different player list will return a new player list and the boards of the
     * extra players
     */
    @Test
    void differentPlayerList() {
        Player ann = new Player("ann");
        Table t = new Table();
        Table withPlayer = t.addPlayer(ann, 5, 5, TowerColor.BLACK);
        MockPhase p1 = new MockPhase(withPlayer);
        MockPhase p2 = new MockPhase(t);

        PhaseDiff diff = p2.compare(p1);

        assertFalse(diff.getAttributes().isEmpty());
        assertFalse(diff.getEntityUpdates().isEmpty());
        assertEquals(2, diff.getEntityUpdates().keySet().size());
        assertEquals(1, diff.getEntityUpdates().get("playerList").size());
        assertEquals(ann, diff.getEntityUpdates().get("playerList").get(0));
        assertEquals(1, diff.getEntityUpdates().get("boards").size());
        assertEquals(withPlayer.getBoardOf(ann), diff.getEntityUpdates().get("boards").get(0));
        assertEquals(1, diff.getAttributes().size());
        assertEquals(MockPhase.class.getSimpleName(), diff.getAttributes().get("phase").getAsString());
    }

    /**
     * Comparing a phase that has different professors will yield a diff containing the different professor
     */
    @Test
    void differentProfessors() {
        Player ann = new Player("ann");
        Professor prof = new Professor(PieceColor.BLUE, ann);
        Table t = new Table();
        Table withProfessors = t.updateProfessors(ps -> {
            ps.replaceAll(p -> {
                if (p.getColor() == prof.getColor())
                    return prof;
                return p;
            });
            return ps;
        });
        MockPhase p1 = new MockPhase(withProfessors);
        MockPhase p2 = new MockPhase(t);
        PhaseDiff diff = p2.compare(p1);

        assertFalse(diff.getAttributes().isEmpty());
        assertFalse(diff.getEntityUpdates().isEmpty());
        assertEquals(1, diff.getEntityUpdates().keySet().size());
        assertEquals(1, diff.getEntityUpdates().get("professors").size());
        assertEquals(prof, diff.getEntityUpdates().get("professors").get(0));
        assertEquals(1, diff.getAttributes().size());
        assertEquals(MockPhase.class.getSimpleName(), diff.getAttributes().get("phase").getAsString());
    }

    /**
     * Comparing two phases that have a different board will yield the updated board
     */
    @Test
    void differentBoards() {
        Player ann = new Player("ann");
        Table t = new Table().addPlayer(ann, 5, 5, TowerColor.BLACK);
        Table updatedBoard = t.updateBoardOf(ann, b -> b.updateEntrance(e -> e.add(new Student(PieceColor.RED))));
        MockPhase p1 = new MockPhase(updatedBoard);
        MockPhase p2 = new MockPhase(t);
        PhaseDiff diff = p2.compare(p1);

        assertFalse(diff.getAttributes().isEmpty());
        assertFalse(diff.getEntityUpdates().isEmpty());
        assertEquals(1, diff.getEntityUpdates().keySet().size());
        assertEquals(1, diff.getEntityUpdates().get("boards").size());
        assertEquals(updatedBoard.getBoardOf(ann), diff.getEntityUpdates().get("boards").get(0));
        assertEquals(1, diff.getAttributes().size());
        assertEquals(MockPhase.class.getSimpleName(), diff.getAttributes().get("phase").getAsString());
    }

    /**
     * Comparing two phases, both with the same list but different islands (with different number of
     * students/towers/blocks) will yield only the changed
     */
    @Test
    void updateIslandNoMerge() {
        Table t = new Table();
        Table islands = t.updateIslandList(is -> {
            Island i = is.remove(0).updateStudents(c -> c.add(new Student(PieceColor.GREEN)));
            is.add(0, i);
            return is;
        });
        MockPhase p1 = new MockPhase(t);
        MockPhase p2 = new MockPhase(islands);
        PhaseDiff diff = p2.compare(p1);

        assertFalse(diff.getAttributes().isEmpty());
        assertEquals(1, diff.getEntityUpdates().keySet().size());
        assertEquals(1, diff.getEntityUpdates().get("islands").size());
        assertEquals(1, diff.getAttributes().size());
        assertEquals(MockPhase.class.getSimpleName(), diff.getAttributes().get("phase").getAsString());
    }

    /**
     * Comparing two phases, with different island lists will dump in the phase the full list structure and islands of
     * the phase on the rhs.
     */
    @Test
    void islandMerge() {
        Player ann = new Player("ann");
        Table t = new Table();
        Table islands = t.updateIslandList(is -> {
            Island i1 = is.remove(0).updateTowers(ts -> {
                ts.add(new Tower(TowerColor.BLACK, ann));
                return ts;
            });
            Island i2 = is.remove(0).updateTowers(ts -> {
                ts.add(new Tower(TowerColor.BLACK, ann));
                return ts;
            });
            is.add(0, i1.merge(i2));
            return is;
        });
        MockPhase p1 = new MockPhase(t);
        MockPhase p2 = new MockPhase(islands);
        PhaseDiff diff = p1.compare(p2);

        assertFalse(diff.getAttributes().isEmpty());
        assertEquals(2, diff.getEntityUpdates().keySet().size());
        assertEquals(11, diff.getEntityUpdates().get("islands").size());
        assertNotNull(diff.getEntityUpdates().get("islandList"));
        assertEquals(1, diff.getAttributes().size());
        assertEquals(MockPhase.class.getSimpleName(), diff.getAttributes().get("phase").getAsString());
    }

    /**
     * Comparing two phases, with mother nature in different positions, will yield one entity update with the different
     * position
     */
    @Test
    void motherNature() {
        Table t = new Table();
        Table pos1 = t.updateMotherNature(__ -> new MotherNature(t.getIslandList(), 0));
        Table pos2 = t.updateMotherNature(__ -> new MotherNature(t.getIslandList(), 1));
        MockPhase p1 = new MockPhase(pos1);
        MockPhase p2 = new MockPhase(pos2);
        PhaseDiff diff = p2.compare(p1);

        assertFalse(diff.getAttributes().isEmpty());
        assertEquals(1, diff.getEntityUpdates().keySet().size());
        assertNotNull(diff.getEntityUpdates().get("motherNature"));
        assertEquals(1, diff.getAttributes().size());
        assertEquals(MockPhase.class.getSimpleName(), diff.getAttributes().get("phase").getAsString());
    }

    /**
     * Comparing two phases, one before using a card and the other after using a card will yield: 1. the `usedCharacter`
     * flag; 2. the character with its cost increased; 3. the board of the player with their coins scaled
     */
    @Test
    void characters() throws InvalidCharacterParameterException, InvalidPhaseUpdateException {
        Player ann = new Player("ann");
        Player bob = new Player("bob");
        Table t = new Table()
                .addPlayer(ann, 5, 5, TowerColor.BLACK)
                .addPlayer(bob, 5, 5, TowerColor.WHITE)
                .updateBoardOf(ann, b -> b.receiveCoin()
                        .receiveCoin()
                        .receiveCoin()
                        .receiveTower(new Tower(TowerColor.BLACK, ann)))
                .updateBoardOf(bob, b -> b.receiveTower(new Tower(TowerColor.WHITE, bob)))
                .updateCharacters(cs -> {
                    cs.add(new InfluenceDecoratingCharacter(InfluenceDecoratingCharacter.Behaviour.CENTAUR));
                    cs.add(new InfluenceDecoratingCharacter(InfluenceDecoratingCharacter.Behaviour.KNIGHT));
                    return cs;
                });
        MockActionPhase p1 = new MockActionPhase(t, ann);
        MockActionPhase p2 = (MockActionPhase) p1.playCharacter(ann, CharacterType.CENTAUR);

        PhaseDiff diff = p1.compare(p2);

        assertEquals(2, diff.getAttributes().keySet().size());
        assertEquals(2, diff.getEntityUpdates().keySet().size());
        assertTrue(diff.getAttributes().get("hasPlayedCharacter").getAsBoolean());
        assertEquals(MockActionPhase.class.getSimpleName(), diff.getAttributes().get("phase").getAsString());
        assertEquals(1, diff.getEntityUpdates().get("characters").size());
        assertEquals(1, diff.getEntityUpdates().get("boards").size());
    }

    /**
     * Comparing two phases, one with a non-empty sack and the other with an empty one yields a diff with one
     * attribute containing a boolean.
     */
    @Test
    void emptySack() {
        Table t = new Table();
        Table withSack = t.updateSack(s -> s.add(new Student(PieceColor.BLUE)));
        MockPhase p1 = new MockPhase(withSack);
        MockPhase p2 = new MockPhase(t);
        PhaseDiff diff = p2.compare(p1);

        assertFalse(diff.getAttributes().isEmpty());
        assertTrue(diff.getEntityUpdates().isEmpty());
        assertEquals(2, diff.getAttributes().keySet().size());
        assertFalse(diff.getAttributes().get("isSackEmpty").getAsBoolean());
        assertEquals(MockPhase.class.getSimpleName(), diff.getAttributes().get("phase").getAsString());
    }

    /**
     * Comparing two cloud list will yield only the clouds that have been drained/filled.
     */
    @Test
    void clouds() {
        Table t = new Table().updateClouds(cs -> {
            cs.add(new Cloud(1).add(new Student(PieceColor.GREEN)));
            return cs;
        });
        Table drained = t.updateClouds(cs -> {
            cs.replaceAll(c -> c.drainCloud().getFirst());
            return cs;
        });
        MockPhase p1 = new MockPhase(t);
        MockPhase p2 = new MockPhase(drained);

        PhaseDiff diff = p2.compare(p1);

        assertFalse(diff.getAttributes().isEmpty());
        assertEquals(1, diff.getEntityUpdates().keySet().size());
        assertEquals(1, diff.getEntityUpdates().get("clouds").size());
        assertEquals(1, diff.getAttributes().size());
        assertEquals(MockPhase.class.getSimpleName(), diff.getAttributes().get("phase").getAsString());
    }

    /**
     * Checks that the diff returned from dump() contains at least one element for each of the possible keys.
     */
    @Test
    void dump() {
        Player ann = new Player("ann");
        Table t1 = new Table()
                .addPlayer(ann, 5, 5, TowerColor.BLACK)
                .updateBoardOf(ann, b -> {
                    ArrayList<Assistant> d = new ArrayList<>();
                    d.add(new Assistant(AssistantType.CAT, Mage.MAGE));
                    d.add(new Assistant(AssistantType.ELEPHANT, Mage.MAGE));
                    return b.receiveDeck(Mage.MAGE, d)
                            .playAssistant(AssistantType.CAT)
                            .receiveCoin()
                            .updateEntrance(c -> c.add(new Student(PieceColor.BLUE)))
                            .updateHall(c -> c.add(new Student(PieceColor.BLUE)));
                })
                .updateProfessors(ps -> {
                    ps.replaceAll(p -> new Professor(p.getColor(), ann));
                    return ps;
                })
                .updateIslandList(is -> {
                    is.replaceAll(i -> i
                            .updateStudents(c -> c.add(new Student(PieceColor.RED)))
                            .updateTowers(ts -> {
                                ts.add(new Tower(TowerColor.BLACK, ann));
                                return ts;
                            })
                            .pushBlock(new BlockCard(CharacterType.HERBALIST)));
                    return is;
                })
                .updateCharacters(cs -> {
                    cs.add(new Herbalist());
                    cs.add(new Innkeeper());
                    return cs;
                })
                .updateClouds(cs -> {
                    cs.add(new Cloud(2).add(new Student(PieceColor.GREEN)));
                    return cs;
                })
                .updateSack(s -> s.add(new Student(PieceColor.PINK)));
        Table finalT = t1;
        t1 = t1.updateMotherNature(__ -> new MotherNature(finalT.getIslandList(), 0));
        MockActionPhase p = new MockActionPhase(t1, ann);

        PhaseDiff diff = p.dump();
        assertFalse(diff.getEntityUpdates().isEmpty());
        assertFalse(diff.getAttributes().isEmpty());
        for (DiffKeys key : DiffKeys.values())
            assertTrue(diff.getEntityUpdates().containsKey(key.toString()) || diff.getAttributes().containsKey(key.toString()));
    }
}
