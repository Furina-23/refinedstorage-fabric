package net.minecraftforge.registries;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import java.util.function.Consumer;

public final class RegisterEvent {
    public <T> void register(ResourceKey<?> registry, Consumer<RegisterHelper<T>> action) { }
    public interface RegisterHelper<T> {
        void register(ResourceLocation id, T value);
        default void register(String id, T value) { register(new ResourceLocation(id), value); }
    }
}
