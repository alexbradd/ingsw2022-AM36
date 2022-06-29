package it.polimi.ingsw.server.controller.persistence;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.server.model.Character;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * TypeAdapterFactory that correctly handler the model's polymorphic types
 */
public class ModelPolymorphicTypeAdapterFactory<T> implements TypeAdapterFactory {
    /**
     * The key used to reconstruct runtime type information
     */
    public static final String TYPE_KEY = "_class";
    /**
     * The class whose children the adapter will handle
     */
    private final Class<T> superclass;
    /**
     * The map between each subclass name and its Class obj
     */
    private final Map<String, Class<?>> nameToSubclass;
    /**
     * The reverse of {@link #nameToSubclass}.
     */
    private final Map<Class<?>, String> subclassToName;

    /**
     * Creates a new TypeAdapterFactory for the given superclass.
     *
     * @param superclass the superclass from which the runtime types will be subtypes of
     * @throws IllegalArgumentException if {@code superclass} is null
     */
    public ModelPolymorphicTypeAdapterFactory(Class<T> superclass) {
        if (superclass == null) throw new IllegalArgumentException("superclass shouldn't be null");
        this.superclass = superclass;
        nameToSubclass = getSubclassesInModel(superclass);
        subclassToName = new HashMap<>();
        nameToSubclass.forEach((k, v) -> subclassToName.put(v, k));
    }

    /**
     * Returns all the concrete classes that have che given class in their inheritance line.
     *
     * @param superclass the superclass
     * @return a map mapping each class to its simple name (as returned by {@link Class#getSimpleName()})
     */
    private static Map<String, Class<?>> getSubclassesInModel(Class<?> superclass) {
        String packageName = "it.polimi.ingsw.server.model";
        InputStream stream = superclass.getClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        assert stream != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, packageName))
                .filter(c -> Objects.equals(c.getSuperclass(), superclass))
                .<Class<?>>mapMulti((c, consumer) -> {
                    if (Modifier.isAbstract(c.getModifiers())) {
                        Map<String, Class<?>> subclasses = getSubclassesInModel(c);
                        subclasses.forEach((k, v) -> consumer.accept(v));
                    } else
                        consumer.accept(c);
                })
                .map(c -> new Tuple<>(c.getCanonicalName(), c.asSubclass(superclass)))
                .collect(Collectors.toMap(Tuple::getFirst, Tuple::getSecond, (v1, v2) -> v1));
    }

    /**
     * Utility method, wraps {@link Class#forName(String)} in a try-catch
     */
    private static Class<?> getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "." + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Error loading class", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> TypeAdapter<R> create(Gson gson, TypeToken<R> type) {
        if (!Objects.equals(type.getRawType(), superclass) && !subclassToName.containsKey(type.getRawType()))
            return null;

        TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
        Map<String, TypeAdapter<?>> stringToDelegate = new HashMap<>();
        Map<Class<?>, TypeAdapter<?>> subtypeToDelegate = new HashMap<>();
        nameToSubclass.forEach((s, c) -> {
            TypeAdapter<?> delegate = gson.getDelegateAdapter(this, TypeToken.get(c));
            stringToDelegate.put(s, delegate);
            subtypeToDelegate.put(c, delegate);
        });
        return new TypeAdapter<R>() {
            @Override
            public void write(JsonWriter out, R value) throws IOException {
                Class<?> srcClass = value.getClass();
                String label = subclassToName.get(srcClass);
                @SuppressWarnings("unchecked") // necessary to fix typing
                TypeAdapter<R> adapter = (TypeAdapter<R>) subtypeToDelegate.get(srcClass);
                if (adapter == null)
                    throw new JsonParseException("Unknown subclass");
                JsonObject obj = adapter.toJsonTree(value).getAsJsonObject();
                obj.add(TYPE_KEY, new JsonPrimitive(label));
                elementAdapter.write(out, obj);
            }

            @Override
            public R read(JsonReader in) throws IOException {
                JsonElement element = elementAdapter.read(in);
                JsonElement typeStringElement = element.getAsJsonObject().remove(TYPE_KEY);
                if (typeStringElement == null)
                    throw new JsonParseException("No type string present on this object");
                String typeString = typeStringElement.getAsString();
                @SuppressWarnings("unchecked") // necessary to fix typing
                TypeAdapter<R> adapter = (TypeAdapter<R>) stringToDelegate.get(typeString);
                if (adapter == null)
                    throw new JsonParseException(typeString + " is not a known subtype");
                return adapter.fromJsonTree(element);
            }
        }.nullSafe();
    }
}
