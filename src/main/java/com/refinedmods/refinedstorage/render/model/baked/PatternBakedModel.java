package com.refinedmods.refinedstorage.render.model.baked;

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternRenderHandler;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.item.PatternItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.util.RandomSource;
import net.minecraftforge.client.model.BakedModelWrapper;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class PatternBakedModel extends BakedModelWrapper<BakedModel> implements FabricBakedModel {
    public PatternBakedModel(BakedModel base) {
        super(base);
    }

    public static boolean canDisplayOutput(ItemStack patternStack, ICraftingPattern pattern) {
        if (pattern.isValid() && pattern.getOutputs().size() == 1) {
            for (ICraftingPatternRenderHandler renderHandler : API.instance().getPatternRenderHandlers()) {
                if (renderHandler.canRenderOutput(patternStack)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public ItemOverrides getOverrides() {
        return originalModel.getOverrides();
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null) {
            ICraftingPattern pattern = PatternItem.fromCache(minecraft.level, stack);
            if (canDisplayOutput(stack, pattern)) {
                ItemStack output = pattern.getOutputs().get(0);
                BakedModel outputModel = minecraft.getItemRenderer().getModel(output, minecraft.level, minecraft.player, 0);
                if (outputModel != this) {
                    ((FabricBakedModel) outputModel).emitItemQuads(output, randomSupplier, context);
                    return;
                }
            }
        }
        ((FabricBakedModel) originalModel).emitItemQuads(stack, randomSupplier, context);
    }
}
