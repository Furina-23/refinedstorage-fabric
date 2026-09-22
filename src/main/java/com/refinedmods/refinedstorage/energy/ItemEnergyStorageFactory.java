package com.refinedmods.refinedstorage.energy;

import com.refinedmods.refinedstorage.item.EnergyItem;
import com.refinedmods.refinedstorage.item.blockitem.EnergyBlockItem;
import com.refinedmods.refinedstorage.transfer.energy.IEnergyStorage;
import net.minecraft.world.item.ItemStack;

public final class ItemEnergyStorageFactory {
    private ItemEnergyStorageFactory() { }
    public static IEnergyStorage get(ItemStack stack) {
        if (stack.getItem() instanceof EnergyItem item) return new ItemEnergyStorage(stack, item.getEnergyCapacity());
        if (stack.getItem() instanceof EnergyBlockItem item) return new ItemEnergyStorage(stack, item.getEnergyCapacity());
        return null;
    }
}


