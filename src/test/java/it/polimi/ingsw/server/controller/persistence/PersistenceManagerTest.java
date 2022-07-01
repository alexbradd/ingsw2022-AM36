package it.polimi.ingsw.server.controller.persistence;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.enums.DiffKeys;
import it.polimi.ingsw.server.model.MockPhase;
import it.polimi.ingsw.server.model.Phase;
import it.polimi.ingsw.server.model.PhaseDiff;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PersistenceManager}
 */
class PersistenceManagerTest {
    private HashMap<String, Function<Object[], Object>> rootHandlers;

    /**
     * Adds MockPhase to the adaptee list
     */
    @BeforeAll
    static void beforeAll() {
        PersistenceManager.getAdapter(Phase.class).registerSubtype(MockPhase.class);
    }

    /**
     * Creates new mock objects
     */
    @BeforeEach
    void setUp() {
        rootHandlers = new HashMap<>();
        rootHandlers.put("isDirectory", args -> true);
        rootHandlers.put("canRead", args -> true);
        rootHandlers.put("canWrite", args -> true);

    }

    /**
     * Check that the constructor throws if passed an invalid File
     */
    @ParameterizedTest
    @MethodSource("constructor_invalidParamSource")
    void constructor_withInvalidParam(MockFile invalid) {
        assertThrows(IllegalArgumentException.class, () -> new PersistenceManager(invalid));
    }

    /**
     * Creates invalid Files to feed to {@link #constructor_withInvalidParam(MockFile)}
     */
    static Stream<Arguments> constructor_invalidParamSource() {
        HashMap<String, Function<Object[], Object>> notDirHandle = new HashMap<>();
        notDirHandle.put("isDirectory", args -> false);

        HashMap<String, Function<Object[], Object>> notEnoughPermissions1 = new HashMap<>();
        notEnoughPermissions1.put("isDirectory", args -> true);
        notEnoughPermissions1.put("canRead", args -> false);
        notEnoughPermissions1.put("canWrite", args -> true);

        HashMap<String, Function<Object[], Object>> notEnoughPermissions2 = new HashMap<>();
        notEnoughPermissions2.put("isDirectory", args -> true);
        notEnoughPermissions2.put("canRead", args -> true);
        notEnoughPermissions2.put("canWrite", args -> false);

        return Stream.of(
                Arguments.of(Named.of("null", null)),
                Arguments.of(Named.of("not dir", new MockFile(notDirHandle))),
                Arguments.of(Named.of("cannot read", new MockFile(notEnoughPermissions1))),
                Arguments.of(Named.of("cannot write", new MockFile(notEnoughPermissions2)))
        );
    }

    /**
     * Checks that hasPending returns false if the root is empty
     */
    @Test
    void hasPending_emptyDir() {
        rootHandlers.put("list", args -> new String[]{});
        assertFalse(new PersistenceManager(new MockFile(rootHandlers)).hasPending());
    }

    /**
     * Checks that hasPending returns true if the root is not empty
     */
    @Test
    void hasPending_nonEmptyDir() {
        rootHandlers.put("list", args -> new String[]{"file"});
        assertTrue(new PersistenceManager(new MockFile(rootHandlers)).hasPending());
    }

    /**
     * Checks that trying to iterate over the pulled phases with a null consumer throws an exception
     */
    @Test
    void forEach_withNull() {
        assertThrows(IllegalArgumentException.class, () -> new PersistenceManager(new MockFile(rootHandlers)).forEach(null));
    }

    /**
     * Checks that calling forEach with a valid id feeds it all available the records.
     */
    @Test
    void forEach_withValidConsumer() {
        String json = formatObject(new MockPhase());
        var wrap = new Object() {
            int count = 0;
        };
        HashMap<String, Function<Object[], Object>> childHandles = new HashMap<>(rootHandlers);
        childHandles.put("isDirectory", args -> false);
        childHandles.put("isFile", args -> true);
        MockFile root = new MockFile(rootHandlers);
        rootHandlers.put("listFiles", args -> new File[]{
                new MockFile(root, "0.json", childHandles),
                new MockFile(root, "1.json", childHandles)});
        PersistenceManager manager = new PersistenceManager(root);
        manager.setChildFileSupplier((a, b) -> fail());
        manager.setWriterSupplier(a -> fail());
        manager.setReaderSupplier(f -> new MockReader(f, ff -> {
            String name = ff.getName();
            return name.equals("0.json") || name.equals("1.json");
        }, json));

        manager.forEach((i, p) -> {
            assertNotNull(p);
            assertInstanceOf(MockPhase.class, p);
            wrap.count++;
        });
        assertEquals(2, wrap.count);
    }

    /**
     * Utility for converting a Phase into the JSON object that will be saved to disk by PersistenceManager
     */
    private String formatObject(Phase phase) {
        JsonObject obj = new Gson().toJsonTree(phase).getAsJsonObject();
        obj.addProperty(PersistenceManager.CLASS_DISCRIMINATOR_PROP_NAME, phase.getClass().getCanonicalName());
        return obj.toString();
    }

    /**
     * Checks that committing a null Phase is not allowed
     */
    @Test
    void commit_withNull() {
        assertThrows(IllegalArgumentException.class, () -> new PersistenceManager(new MockFile(rootHandlers)).commit(0, null));
    }

    /**
     * Check that, given a valid phase, committing it to disk correctly writes it to disk to the correct file with the
     * expected format
     */
    @Test
    void commit_withPhase() {
        MockPhase phase = new MockPhase();
        String json = formatObject(phase);
        PersistenceManager manager = new PersistenceManager(new MockFile(rootHandlers));
        manager.setChildFileSupplier((f, s) -> {
            HashMap<String, Function<Object[], Object>> childHandlers = new HashMap<>(rootHandlers);
            childHandlers.put("isDirectory", args -> false);
            childHandlers.put("isFile", args -> true);
            return new MockFile(f, s, childHandlers);
        });
        manager.setWriterSupplier(f -> new MockWriter(f, ff -> ff.getName().equals("0.json"), s -> s.equals(json)));
        manager.setReaderSupplier(f -> fail());

        manager.commit(0L, phase);
    }

    /**
     * Check that trying to pull from file that doesn't exist throws an exception
     */
    @Test
    void pull_withInvalidId() {
        PersistenceManager manager = new PersistenceManager(new MockFile(rootHandlers));
        manager.setChildFileSupplier((f, s) -> {
            HashMap<String, Function<Object[], Object>> childHandlers = new HashMap<>(rootHandlers);
            childHandlers.put("isDirectory", args -> false);
            childHandlers.put("isFile", args -> false);
            return new MockFile(f, s, childHandlers);
        });
        manager.setWriterSupplier(f -> fail());
        manager.setReaderSupplier(f -> fail());

        assertThrows(IllegalArgumentException.class, () -> manager.pull(0L));
    }

    /**
     * Check that pulling a Phase from disk returns a different Phase object with the same runtime object such that
     * the difference between these two objects is empty (meaning that the difference calculated by
     * {@link Phase#compare(Phase)} has at most only the phase's name into it)
     */
    @Test
    void pull_withValidId() {
        MockPhase phase = new MockPhase();
        String json = formatObject(phase);
        PersistenceManager manager = new PersistenceManager(new MockFile(rootHandlers));
        manager.setChildFileSupplier((f, s) -> {
            HashMap<String, Function<Object[], Object>> childHandlers = new HashMap<>(rootHandlers);
            childHandlers.put("isDirectory", args -> false);
            childHandlers.put("isFile", args -> true);
            return new MockFile(f, s, childHandlers);
        });
        manager.setWriterSupplier(f -> fail());
        manager.setReaderSupplier(f -> new MockReader(f, ff -> f.getName().equals("0.json"), json));

        Phase pulled = manager.pull(0L);
        PhaseDiff diff = phase.compare(pulled);
        assertInstanceOf(MockPhase.class, pulled);
        assertTrue(diff.getEntityUpdates().isEmpty());
        assertFalse(diff.getAttributes().isEmpty());
        assertEquals(1, diff.getAttributes().size());
        assertTrue(diff.getAttributes().containsKey(DiffKeys.PHASE.toString()));
        assertNotSame(phase, pulled);
    }

    /**
     * Check that trying to drop a record that doesn't exist throws an exception
     */
    @Test
    void drop_withInvalidId() {
        PersistenceManager manager = new PersistenceManager(new MockFile(rootHandlers));
        manager.setChildFileSupplier((f, s) -> {
            HashMap<String, Function<Object[], Object>> childHandlers = new HashMap<>(rootHandlers);
            childHandlers.put("isDirectory", args -> false);
            childHandlers.put("isFile", args -> false);
            return new MockFile(f, s, childHandlers);
        });
        manager.setWriterSupplier(f -> fail());
        manager.setReaderSupplier(f -> fail());

        assertThrows(IllegalArgumentException.class, () -> manager.drop(0L));
    }

    /**
     * Check that given a valid id the correct file is deleted
     */
    @Test
    void drop_withValidId() {
        PersistenceManager manager = new PersistenceManager(new MockFile(rootHandlers));
        var token = new Object() {
            boolean invoked = false;
        };
        manager.setChildFileSupplier((f, s) -> {
            if (!s.equals("0.json")) fail();
            HashMap<String, Function<Object[], Object>> childHandlers = new HashMap<>(rootHandlers);
            childHandlers.put("isDirectory", args -> false);
            childHandlers.put("isFile", args -> true);
            childHandlers.put("delete", args -> {
                token.invoked = true;
                return true;
            });
            return new MockFile(f, s, childHandlers);
        });
        manager.setWriterSupplier(f -> fail());
        manager.setReaderSupplier(f -> fail());
        manager.drop(0L);
        assertTrue(token.invoked);
    }

    /**
     * Check that clearing the database deletes all the record from the root directory
     */
    @Test
    void clear_deletesAllFiles() {
        var wrap = new Object() {
            int count;
        };
        HashMap<String, Function<Object[], Object>> toDelHandles = new HashMap<>(rootHandlers);
        toDelHandles.put("isDirectory", args -> false);
        toDelHandles.put("isFile", args -> true);
        toDelHandles.put("delete", args -> {
            wrap.count++;
            return true;
        });
        rootHandlers.put("listFiles", args -> new File[]{new MockFile(toDelHandles), new MockFile(toDelHandles)});
        PersistenceManager manager = new PersistenceManager(new MockFile(rootHandlers));
        manager.setChildFileSupplier((a, b) -> fail());
        manager.setReaderSupplier(a -> fail());
        manager.setWriterSupplier(a -> fail());

        manager.clear();
        assertEquals(2, wrap.count);
    }
}