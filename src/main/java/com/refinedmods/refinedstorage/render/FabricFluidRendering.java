package com.refinedmods.refinedstorage.render;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public final class FabricFluidRendering {
    private FabricFluidRendering() {
    }

    public static TextureAtlasSprite getStillSprite(Fluid fluid) {
        return getHandler(fluid).getFluidSprites(null, null, fluid.defaultFluidState())[0];
    }

    public static int getColor(Fluid fluid) {
        return getHandler(fluid).getFluidColor(null, null, fluid.defaultFluidState());
    }

    private static FluidRenderHandler getHandler(Fluid fluid) {
        FluidRenderHandler handler = FluidRenderHandlerRegistry.INSTANCE.get(fluid);
        if (handler == null) {
            handler = FluidRenderHandlerRegistry.INSTANCE.get(Fluids.WATER);
        }
        if (handler == null) {
            throw new IllegalStateException("No Fabric fluid render handler is registered for " + fluid);
        }
        return handler;
    }
}


