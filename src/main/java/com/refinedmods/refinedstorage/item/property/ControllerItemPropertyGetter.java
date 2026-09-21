package com.refinedmods.refinedstorage.item.property;

import com.refinedmods.refinedstorage.apiimpl.network.Network;
import com.refinedmods.refinedstorage.block.ControllerBlock;
import com.refinedmods.refinedstorage.energy.ItemEnergyStorageFactory;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import com.refinedmods.refinedstorage.transfer.energy.IEnergyStorage;

import javax.annotation.Nullable;

public class ControllerItemPropertyGetter implements ItemPropertyFunction {
    @Override
    public float call(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int p) {
        // if that stack is still clean (like in the creative mode tab, or just crafted) maintain the on state.
        if (stack.getTag() == null) {
            return ControllerBlock.EnergyType.ON.ordinal();
        }
        IEnergyStorage storage = ItemEnergyStorageFactory.get(stack);
        if (storage != null) {
            return Network.getEnergyType(storage.getEnergyStored(), storage.getMaxEnergyStored()).ordinal();
        }
        return ControllerBlock.EnergyType.ON.ordinal();
    }
}
