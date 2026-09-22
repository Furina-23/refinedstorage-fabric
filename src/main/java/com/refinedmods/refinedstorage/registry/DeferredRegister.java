package com.refinedmods.refinedstorage.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public final class DeferredRegister<T> {
    private final Registry<T> registry;
    private final String namespace;
    private final Map<String, RegistryObject<? extends T>> entries = new LinkedHashMap<>();
    private boolean registered;

    private DeferredRegister(Registry<T> registry, String namespace) {
        this.registry = registry;
        this.namespace = namespace;
    }

    public static <T> DeferredRegister<T> create(Registry<T> registry, String namespace) {
        return new DeferredRegister<>(registry, namespace);
    }

    public <S extends T> RegistryObject<S> register(String name, Supplier<S> factory) {
        if (registered) {
            throw new IllegalStateException("Cannot add " + name + " after registry " + namespace + " has been registered");
        }
        RegistryObject<S> object = new RegistryObject<>(
            new ResourceLocation(namespace, name),
            Objects.requireNonNull(factory, "factory")
        );
        if (entries.putIfAbsent(name, object) != null) {
            throw new IllegalArgumentException("Duplicate registry entry " + namespace + ":" + name);
        }
        return object;
    }

    @SuppressWarnings("unchecked")
    public void register() {
        if (registered) {
            throw new IllegalStateException("Registry " + namespace + " has already been registered");
        }
        registered = true;
        entries.forEach((name, object) -> {
            T value = object.get();
            Registry.register(registry, object.getId(), value);
            ((RegistryObject<T>) object).register(value);
        });
    }
}


