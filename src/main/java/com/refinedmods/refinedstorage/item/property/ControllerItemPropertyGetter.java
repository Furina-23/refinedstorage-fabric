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
        // Fresh items, including creative-tab entries, have no energy tag and are treated as full.
        if (stack.getTag() == null) {
            return toModelValue(ControllerBlock.EnergyType.ON);
        }

        IEnergyStorage storage = ItemEnergyStorageFactory.get(stack);
        if (storage != null) {
            return toModelValue(Network.getEnergyType(storage.getEnergyStored(), storage.getMaxEnergyStored()));
        }

        return toModelValue(ControllerBlock.EnergyType.ON);
    }

    static float toModelValue(ControllerBlock.EnergyType type) {
        // ItemProperties.register clamps values to [0, 1]. Keep this scale in sync
        // with energy_type thresholds in all controller item model overrides.
        return type.ordinal() / 3.0F;
    }
}


