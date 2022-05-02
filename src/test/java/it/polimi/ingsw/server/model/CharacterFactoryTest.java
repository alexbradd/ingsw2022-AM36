package it.polimi.ingsw.server.model;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for CharacterFactory
 */
class CharacterFactoryTest {

    /**
     * Bound checks pickNRandom
     */
    @Test
    void boundCheck() {
        assertThrows(IllegalArgumentException.class, () -> CharacterFactory.setExtractableCards(null));
        assertThrows(IllegalArgumentException.class, () -> CharacterFactory.setExtractableCards(List.of()));
        assertThrows(IllegalArgumentException.class, () -> CharacterFactory.pickNRandom(-1));
        assertThrows(IllegalArgumentException.class, () -> CharacterFactory.pickNRandom(0));
        assertThrows(IllegalArgumentException.class, () -> CharacterFactory.pickNRandom(1000));
    }

    /**
     * Test that the cards returned 3 and different. Since the generation is random, repeat the test a few times.
     */
    @RepeatedTest(50)
    void testFactory() {
        CharacterFactory.setExtractableCards(CharacterFactory.ALL_CARDS); // in case other tests have modified it
        Character[] cs = CharacterFactory.pickNRandom(3);

        assertEquals(3, cs.length);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; i < 3; i++) {
                if (i != j)
                    assertNotEquals(cs[i], cs[j]);
            }
        }

    }
}