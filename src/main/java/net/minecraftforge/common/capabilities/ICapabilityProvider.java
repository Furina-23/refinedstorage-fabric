package net.minecraftforge.common.capabilities;

import net.minecraft.core.Direction;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

public interface ICapabilityProvider {
    <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction direction);
}
