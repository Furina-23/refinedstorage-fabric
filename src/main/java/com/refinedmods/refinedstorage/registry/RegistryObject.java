package com.refinedmods.refinedstorage.registry;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public final class RegistryObject<T> implements Supplier<T> {
    private final ResourceLocation id;
    private final Supplier<? extends T> factory;
    private T value;

    RegistryObject(ResourceLocation id, Supplier<? extends T> factory) {
        this.id = id;
        this.factory = factory;
    }

    void register(T value) {
        this.value = value;
    }

    public ResourceLocation getId() {
        return id;
    }

    @Override
    public T get() {
        if (value == null) {
            value = factory.get();
        }
        return value;
    }
}
