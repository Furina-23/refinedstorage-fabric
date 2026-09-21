package com.refinedmods.refinedstorage.integration.craftingtweaks;

import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.container.GridContainerMenu;
import com.refinedmods.refinedstorage.container.slot.grid.CraftingGridSlot;
import net.minecraft.nbt.CompoundTag;

import java.util.function.Function;
import java.util.function.Predicate;

public final class CraftingTweaksIntegration {
    private static final String ID = "craftingtweaks";

    private CraftingTweaksIntegration() {
    }

    public static boolean isLoaded() {
        return false;
    }

    public static boolean isCraftingTweaksClass(Class<?> clazz) {
        return clazz.getName().startsWith("net.blay09.mods.craftingtweaks");
    }

    public static void register() {
    }

    public static class ValidContainerPredicate implements Predicate<GridContainerMenu> {
        @Override
        public boolean test(GridContainerMenu containerGrid) {
            return containerGrid.getGrid().getGridType() == GridType.CRAFTING;
        }
    }

    public static class GetGridStartFunction implements Function<GridContainerMenu, Integer> {
        @Override
        public Integer apply(GridContainerMenu containerGrid) {
            for (int i = 0; i < containerGrid.slots.size(); i++) {
                if (containerGrid.slots.get(i) instanceof CraftingGridSlot) {
                    return i;
                }
            }

            return 0;
        }
    }
}
