package net.minecraftforge.client.extensions.common;

import com.refinedmods.refinedstorage.transfer.FluidStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

public interface IClientFluidTypeExtensions {
    ResourceLocation getStillTexture(FluidStack stack);
    int getTintColor(FluidStack stack);

    static IClientFluidTypeExtensions of(Fluid fluid) {
        return new IClientFluidTypeExtensions() {
            public ResourceLocation getStillTexture(FluidStack stack) {
                return new ResourceLocation("minecraft", "block/water_still");
            }
            public int getTintColor(FluidStack stack) { return 0xFFFFFFFF; }
        };
    }
}
