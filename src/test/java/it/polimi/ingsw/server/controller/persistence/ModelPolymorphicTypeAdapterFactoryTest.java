package it.polimi.ingsw.server.controller.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests class for {@link ModelPolymorphicTypeAdapterFactory}
 */
class ModelPolymorphicTypeAdapterFactoryTest {
    private static Gson GSON;

    /**
     * Initializes the Gson instance
     */
    @BeforeAll
    static void setup() {
        ModelPolymorphicTypeAdapterFactory<AbstractSuperType> adapter = new ModelPolymorphicTypeAdapterFactory<>(AbstractSuperType.class);
        adapter.registerSubtype(ConcreteChild.class);
        adapter.registerSubtype(ConcreteGrandchild.class);
        GSON = new GsonBuilder()
                .registerTypeAdapterFactory(adapter)
                .create();
    }

    /**
     * Test that you cannot register abstract classes
     */
    @Test
    void register_Abstract() {
        ModelPolymorphicTypeAdapterFactory<AbstractSuperType> adapter = new ModelPolymorphicTypeAdapterFactory<>(AbstractSuperType.class);
        assertThrows(IllegalArgumentException.class, () -> adapter.registerSubtype(AbstractChild.class));
    }

    /**
     * Serializes child and then deserializes it as a AbstractSupertype checking that serialization is correct using
     * ths child's {@link AbstractSuperType#correctJson()} and that deserialization uses the correct type using
     * {@link AbstractSuperType#specific()}.
     *
     * @param child the child
     * @param <T>   the child's type
     */
    @ParameterizedTest
    @MethodSource("concreteClassSource")
    <T extends AbstractSuperType> void concreteClass_serializedThenDeserializedAsSupertype(T child) {
        JsonObject obj = GSON.toJsonTree(child).getAsJsonObject();
        assertEquals(child.correctJson(), obj);
        AbstractSuperType deserialized = GSON.fromJson(obj, AbstractSuperType.class);
        assertEquals(child.specific(), deserialized.specific());
    }

    /**
     * Cases checked:
     *
     * <ul>
     *     <li>ConcreteChild passed as a AbstractSuperType</li>
     *     <li>ConcreteChild passed as a ConcreteChild</li>
     *     <li>ConcreteGrandchild passed as a AbstractSupertype</li>
     *     <li>ConcreteGrandchild passed as a AbstractChild</li>
     *     <li>ConcreteGrandchild passed as a ConcreteGrandchild</li>
     * </ul>
     */
    static Stream<Arguments> concreteClassSource() {
        return Stream.of(
                Arguments.of(Named.of("ConcreteChild as super", (AbstractSuperType) new ConcreteChild())),
                Arguments.of(Named.of("ConcreteChild as concrete", new ConcreteChild())),
                Arguments.of(Named.of("ConcreteGrandChild as super", (AbstractSuperType) new ConcreteGrandchild())),
                Arguments.of(Named.of("ConcreteGrandchild as AbstractChild", (AbstractChild) new ConcreteGrandchild())),
                Arguments.of(Named.of("ConcreteGrandchild as concrete", new ConcreteGrandchild()))
        );
    }

    /**
     * The abstract super type
     */
    private static abstract class AbstractSuperType {
        /**
         * Return a child specific string
         *
         * @return a child specific string
         */
        public abstract String specific();

        /**
         * Return the correct JsonObject representing this instance
         *
         * @return the correct JsonObject representing this instance
         */
        public abstract JsonObject correctJson();
    }

    /**
     * Concrete implementation of AbstractSupertype
     */
    private static class ConcreteChild extends AbstractSuperType {
        private final String string = "concreteChild";

        /**
         * {@inheritDoc}
         */
        public String specific() {
            return string;
        }

        /**
         * {@inheritDoc}
         */
        public JsonObject correctJson() {
            JsonObject o = new JsonObject();
            o.addProperty(ModelPolymorphicTypeAdapterFactory.TYPE_KEY, this.getClass().getCanonicalName());
            o.addProperty("string", string);
            return o;
        }
    }

    /**
     * Abstract middle man between the super type and its grandchildren
     */
    private abstract static class AbstractChild extends AbstractSuperType {
    }

    /**
     * Concrete grandchild of AbstractSupertype
     */
    private static class ConcreteGrandchild extends AbstractChild {
        private final String string = "concreteGrandChild";

        /**
         * {@inheritDoc}
         */
        public String specific() {
            return string;
        }

        /**
         * {@inheritDoc}
         */
        public JsonObject correctJson() {
            JsonObject o = new JsonObject();
            o.addProperty(ModelPolymorphicTypeAdapterFactory.TYPE_KEY, this.getClass().getCanonicalName());
            o.addProperty("string", string);
            return o;
        }
    }
}