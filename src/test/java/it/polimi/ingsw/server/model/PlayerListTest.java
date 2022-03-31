package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.TowerColor;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for the {@link PlayerList} class.
 *
 * @author Mattia Busso
 * @see PlayerList
 */
public class PlayerListTest {

    /**
     * The {@link PlayerList} to test.
     */
    private PlayerList list;

    /**
     * A vanilla list of players used to check correctness of {@code list}.
     */
    private List<Player> players;

    /**
     * Initializes the {@code list} and the {@code players}.
     * Tests the PlayerList's {@code add()} method.
     */
    @BeforeEach
    @Test
    @DisplayName("add() method test and list/players initializer")
    void addTest() {
        list = new PlayerList(4);
        players = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            Player player = new Player("p" + i, 5, 5, TowerColor.WHITE);
            list.add(player);
            players.add(player);
            if(i == 0) {
                assertThrows(IllegalArgumentException.class, () -> list.add(new Player("p0", 5, 5, TowerColor.WHITE)));
            }
        }
        assertThrows(IllegalStateException.class, () -> list.add(new Player("p", 5, 5, TowerColor.WHITE)));
        assertThrows(IllegalArgumentException.class, () -> list.add(null));
    }

    /**
     * Test for the PlayerList's constructor.
     */
    @Test
    @DisplayName("Constructor test")
    void constructorTest() {
        assertThrows(IllegalArgumentException.class, () -> new PlayerList(-1));
        assertThrows(IllegalArgumentException.class, () -> new PlayerList(5));
    }

    /**
     * Test for {@code contains()} method.
     */
    @Test
    @DisplayName("contains() method test")
    void containsTest() {
        for(Player p : players) {
            assertTrue(list.contains(p));
        }
        assertFalse(list.contains(new Player("p", 5, 5, TowerColor.WHITE)));
        assertThrows(IllegalArgumentException.class, () -> list.contains(null));
    }

    /**
     * Test for {@code get(index)} method.
     */
    @Test
    @DisplayName("get(index) method test")
    void getIndexTest() {
        for(int i = 0; i < 4; i++) {
            assertEquals(list.get(i), players.get(i));
        }
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(5));
    }

    /**
     * Test for {@code get(username)} method.
     */
    @Test
    @DisplayName("get(username) methods test")
    void getUsernameTest() {
        for(int i = 0; i < 4; i++) {
            assertEquals(list.get("p"+i), players.get(i));
        }
        assertThrows(IllegalArgumentException.class, () -> list.get("p99"));
    }

    /**
     * Test for {@code remove(index)} method.
     */
    @Test
    @DisplayName("remove(index) method test")
    void removeIndexTest() {
        for(int i = 0; i < 4; i++) {
            assertEquals(players.get(i), list.remove(0));
        }
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(0));
    }

    /**
     * Test for {@code remove(username)} method.
     */
    @Test
    @DisplayName("remove(username) method test")
    void removeTest() {
        assertEquals(players.get(0), list.remove("p0"));
        assertEquals(players.get(1), list.remove("p1"));
        assertEquals(players.get(2), list.remove("p2"));
        assertEquals(players.get(3), list.remove("p3"));
        assertThrows(IllegalArgumentException.class, () -> list.remove("p0"));
    }

}
