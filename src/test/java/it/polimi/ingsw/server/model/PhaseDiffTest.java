package it.polimi.ingsw.server.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for PhaseDiff
 */
class PhaseDiffTest {
    private PhaseDiff diff;

    /**
     * Set up a fresh environment
     */
    @BeforeEach
    void setUp() {
        diff = new PhaseDiff();
    }

    /**
     * Check that list of entities are correctly saved and converted to JSON
     */
    @Test
    void addEntityUpdate() {
        List<Jsonable> l = List.of(new MockJsonable(), new MockJsonable());
        diff.addEntityUpdate("mock", l);
        JsonObject j = diff.toJson().getAsJsonObject();

        assertTrue(j.has("mock"));
        assertEquals(new MockJsonable().toJson(), j.get("mock").getAsJsonArray().get(0));
        assertEquals(new MockJsonable().toJson(), j.get("mock").getAsJsonArray().get(1));
    }

    /**
     * Check that attributes are stored and converted to json correctly
     */
    @Test
    void addAttribute() {
        diff.addAttribute("test", new JsonPrimitive("value"));
        JsonObject j = diff.toJson().getAsJsonObject();

        assertTrue(j.has("test"));
        assertEquals("value", j.get("test").getAsString());
    }

    private static class MockJsonable implements Jsonable {
        @Override
        public JsonElement toJson() {
            JsonObject ret = new JsonObject();
            ret.addProperty("prop1", "val1");
            ret.addProperty("prop2", "val2");
            return ret;
        }
    }
}