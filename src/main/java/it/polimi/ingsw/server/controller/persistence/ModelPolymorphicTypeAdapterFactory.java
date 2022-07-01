package it.polimi.ingsw.server.controller.persistence;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.server.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * TypeAdapterFactory that correctly handler the model's polymorphic types.
 * <p>
 * Each subtype should be manually registered with {@link #registerSubtype(Class)}.
 * <p>
 * The polymorphism is handled by adding to the root object a key with the name {@link #TYPE_KEY} containing the class's
 * canonical name. During deserialization the class whose name is in the property {@link #TYPE_KEY} will be effectively
 * instantiated.
 *
 * @param <T> The type that is handled by this adapter
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
        nameToSubclass = new HashMap<>();
        subclassToName = new HashMap<>();
    }

    /**
     * Registers a new subtype
     *
     * @param subtype the subtype
     * @throws IllegalArgumentException if {@code subtype} is null, abstract or already registered
     */
    public void registerSubtype(Class<? extends T> subtype) {
        if (subtype == null) throw new IllegalArgumentException("subtype shouldn't be null");
        if (subclassToName.containsKey(subtype))
            throw new IllegalArgumentException("subtype already registered");
        if (Modifier.isAbstract(subtype.getModifiers()))
            throw new IllegalArgumentException("only concrete subtypes please");
        nameToSubclass.put(subtype.getCanonicalName(), subtype);
        subclassToName.put(subtype, subtype.getCanonicalName());
        Logger.log("Registered classes for supertype " + superclass + ": " + subtype);
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
                    throw new JsonParseException("Unknown subclass " + srcClass);
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
