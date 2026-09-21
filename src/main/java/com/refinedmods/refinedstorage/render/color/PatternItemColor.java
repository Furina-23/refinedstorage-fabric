package com.refinedmods.refinedstorage.render.color;

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.item.PatternItem;
import com.refinedmods.refinedstorage.apiimpl.API;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;

public class PatternItemColor implements ItemColor {
    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        ICraftingPattern pattern = PatternItem.fromCache(Minecraft.getInstance().level, stack);

        if (pattern.isValid() && pattern.getOutputs().size() == 1
            && API.instance().getPatternRenderHandlers().stream().anyMatch(handler -> handler.canRenderOutput(stack))) {
            ItemStack output = pattern.getOutputs().get(0);
            ItemColor provider = ColorProviderRegistry.ITEM.get(output.getItem());
            int color = provider == null ? -1 : provider.getColor(output, tintIndex);

            if (color != -1) {
                return color;
            }
        }

        return 0xFFFFFF;
    }
}
