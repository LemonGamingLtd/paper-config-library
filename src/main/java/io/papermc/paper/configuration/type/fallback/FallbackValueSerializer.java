package io.papermc.paper.configuration.type.fallback;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import static io.leangen.geantyref.GenericTypeReflector.erase;

public class FallbackValueSerializer extends ScalarSerializer<FallbackValue> {

    private static final Map<Class<?>, FallbackCreator<?>> REGISTRY = new HashMap<>();

    static {

    }

    FallbackValueSerializer(Map<FallbackValue.ContextKey<?>, Object> contextMap) {
        super(FallbackValue.class);
        this.contextMap = contextMap;
    }

    @FunctionalInterface
    private interface FallbackCreator<T extends FallbackValue> {
        T create(Map<FallbackValue.ContextKey<?>, Object> context, Object value) throws SerializationException;
    }

    private final Map<FallbackValue.ContextKey<?>, Object> contextMap;

    @Override
    public FallbackValue deserialize(Type type, Object obj) throws SerializationException {
        final @Nullable FallbackCreator<?> creator = REGISTRY.get(erase(type));
        if (creator == null) {
            throw new SerializationException(type + " does not have a FallbackCreator registered");
        }
        return creator.create(this.contextMap, obj);
    }

    @Override
    protected Object serialize(FallbackValue item, Predicate<Class<?>> typeSupported) {
        return item.serialize();
    }
}
