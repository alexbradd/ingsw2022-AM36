package it.polimi.ingsw.server.model;

import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.enums.PieceColor;
import it.polimi.ingsw.enums.TowerColor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Table
 */
class TableTest {
    private static final Table t = new Table();

    /**
     * Check that updateSack updates the internal sack if update function doesn't return null
     */
    @Test
    void updateSack() {
        Table updatedSack = t.updateSack(c -> null);
        assertEquals(t.getSack(), updatedSack.getSack());

        updatedSack = updatedSack.updateSack(c -> c.add(new Student(PieceColor.RED)));
        assertEquals(1, updatedSack.getSack().size());
        assertEquals(PieceColor.RED, updatedSack.getSack().remove().getSecond().getColor());
        assertNotSame(t.getSack(), updatedSack.getSack());
    }

    /**
     * Check that updateClouds updates the internal list of clouds if update function doesn't return null
     */
    @Test
    void updateClouds() {
        Table updatedClouds = t.updateClouds(cs -> null);
        assertEquals(t.getClouds(), updatedClouds.getClouds());

        Cloud c = new Cloud(3);
        updatedClouds = updatedClouds.updateClouds(cs -> {
            cs.add(c);
            return cs;
        });
        assertEquals(1, updatedClouds.getClouds().size());
        assertEquals(c, updatedClouds.getClouds().get(0));
        assertNotSame(t.getClouds(), updatedClouds.getClouds());
    }

    /**
     * Check that updateMotherNature updates the internal motherNature if update function doesn't return null
     */
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11})
    void updateMotherNature(int pos) {
        Table fixedMn = t.updateMotherNature(m -> new MotherNature(t.getIslandList(), pos));
        Table updateMn = fixedMn.updateMotherNature(m -> null);
        assertEquals(fixedMn.getMotherNature(), updateMn.getMotherNature());

        MotherNature old = updateMn.getMotherNature();
        updateMn = updateMn.updateMotherNature(m -> m.move(t.getIslandList(), 1));
        assertNotEquals(old, updateMn.getMotherNature());
        assertNotSame(t.getMotherNature(), updateMn.getMotherNature());
    }

    /**
     * Check that updateIslandList updates the internal island list if update function doesn't return null
     */
    @Test
    void updateIslandList() {
        Table updateIslands = t.updateIslandList(l -> null);
        assertEquals(t.getIslandList(), updateIslands.getIslandList());

        updateIslands = updateIslands.updateIslandList(l -> {
            l.remove(0);
            return l;
        });
        assertEquals(11, updateIslands.getIslandList().size());
        assertNotSame(t.getIslandList(), updateIslands.getIslandList());
    }

    /**
     * Check that updateProfessors updates the internal professor list if update function doesn't return null
     */
    @Test
    void updateProfessors() {
        Table updateProfessors = t.updateProfessors(l -> null);
        assertEquals(t.getProfessors(), updateProfessors.getProfessors());

        updateProfessors = updateProfessors.updateProfessors(l -> {
            l.remove(0);
            return l;
        });
        assertEquals(4, updateProfessors.getProfessors().size());
        assertNotSame(t.getProfessors(), updateProfessors.getProfessors());
    }

    /**
     * Check that updateCharacters updates the internal professor list if update function doesn't return null
     */
    @Test
    void updateCharacters() {
        Table updateCharacters = t.updateCharacters(l -> null);
        assertEquals(t.getCharacters(), updateCharacters.getCharacters());

        updateCharacters = updateCharacters.updateCharacters(l -> {
            l.add(new MockCharacter(CharacterType.HERBALIST));
            return l;
        });
        assertEquals(1, updateCharacters.getCharacters().size());
        assertNotSame(t.getCharacters(), updateCharacters.getCharacters());
    }

    /**
     * Check that a player with the give username and its board are added and that you cannot add a user with a username
     * already present in the table
     */
    @Test
    void addPlayer() {
        Player ann = new Player("ann");
        Table updatePlayers = t.addPlayer(ann, 8, 8, TowerColor.BLACK);

        assertThrows(IllegalArgumentException.class,
                () -> updatePlayers.addPlayer(ann, 8, 8, TowerColor.BLACK));
        assertEquals(1, updatePlayers.getPlayers().size());
        assertEquals(1, updatePlayers.getBoards().size());
        assertAll(() -> updatePlayers.getBoardOf(ann));
        assertEquals(ann, updatePlayers.getBoardOf(ann).getPlayer());
        assertNotSame(t.getPlayers(), updatePlayers.getPlayers());
        assertNotSame(t.getBoards(), updatePlayers.getBoards());
    }

    /**
     * Test that removePlayer (both its variants) removes the player if it can find it.
     */
    @Test
    void removePlayer() {
        Player ann = new Player("ann");
        Player bob = new Player("bob");

        Table updatePlayers = t.removePlayer("ann");
        assertEquals(t, updatePlayers);

        updatePlayers = t.removePlayer(ann);
        assertEquals(t, updatePlayers);

        Table preUpdate = t.addPlayer(ann, 8, 8, TowerColor.BLACK)
                .addPlayer(bob, 8, 8, TowerColor.WHITE);
        updatePlayers = preUpdate.removePlayer(ann);
        assertEquals(1, updatePlayers.getPlayers().size());
        assertEquals(1, updatePlayers.getBoards().size());
        assertEquals(bob, updatePlayers.getPlayers().get(0));
        assertEquals(bob, updatePlayers.getBoards().get(0).getPlayer());
        assertNotSame(preUpdate.getPlayers(), updatePlayers.getPlayers());
        assertNotSame(preUpdate.getBoards(), updatePlayers.getBoards());

        preUpdate = updatePlayers.addPlayer(ann, 8, 8, TowerColor.BLACK);
        updatePlayers = preUpdate.removePlayer("ann");
        assertEquals(1, updatePlayers.getPlayers().size());
        assertEquals(1, updatePlayers.getBoards().size());
        assertEquals(bob, updatePlayers.getPlayers().get(0));
        assertEquals(bob, updatePlayers.getBoards().get(0).getPlayer());
        assertNotSame(preUpdate.getPlayers(), updatePlayers.getPlayers());
        assertNotSame(preUpdate.getBoards(), updatePlayers.getBoards());
    }

    /**
     * Check that getBoardOf explodes if the given Player is not in the table
     */
    @Test
    void getBoardOf() {
        Player ann = new Player("ann");
        assertThrows(IllegalArgumentException.class, () -> t.getBoardOf(ann));
    }

    /**
     * Check that updateBoardOf updates the board of the given player if the update function doesn't return null;
     */
    @Test
    void updateBoardOf() {
        Player ann = new Player("ann");
        Table preUpdate = t.addPlayer(ann, 8, 8, TowerColor.BLACK);
        Table update = preUpdate.updateBoardOf(ann, b -> null);
        assertEquals(update, preUpdate);

        update = update.updateBoardOf(ann, b -> b.receiveTower(new Tower(TowerColor.BLACK, ann)));
        assertNotEquals(preUpdate.getBoardOf(ann), update.getBoardOf(ann));
        assertNotSame(preUpdate.getBoardOf(ann), update.getBoardOf(ann));
        assertEquals(1, update.getBoardOf(ann).getNumOfTowers());
    }

    /**
     * Mock Character card. It does nothing.
     */
    private static class MockCharacter extends Character {
        public CharacterType type;

        /**
         * {@inheritDoc}
         */
        MockCharacter(CharacterType type) {
            super(type);
            this.type = type;
        }

        /**
         * Abstract method that returns a shallow copy of the current object.
         *
         * @return returns a shallow copy of the current object.
         */
        @Override
        Character shallowCopy() {
            return new MockCharacter(type);
        }
    }
}