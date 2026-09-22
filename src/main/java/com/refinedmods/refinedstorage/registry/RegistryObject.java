package com.refinedmods.refinedstorage.registry;

import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.function.Supplier;

public final class RegistryObject<T> implements Supplier<T> {
    private final ResourceLocation id;
    private final Supplier<? extends T> factory;
    private T value;

    RegistryObject(ResourceLocation id, Supplier<? extends T> factory) {
        this.id = Objects.requireNonNull(id, "id");
        this.factory = Objects.requireNonNull(factory, "factory");
    }

    void register(T value) {
        T registeredValue = Objects.requireNonNull(value, "value");
        if (this.value != null && this.value != registeredValue) {
            throw new IllegalStateException("Registry entry " + id + " was created more than once");
        }
        this.value = registeredValue;
    }

    public ResourceLocation getId() {
        return id;
    }

    @Override
    public T get() {
        if (value == null) {
            value = Objects.requireNonNull(factory.get(), () -> "Factory for " + id + " returned null");
        }
        return value;
    }
}


