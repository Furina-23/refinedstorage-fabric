package com.refinedmods.refinedstorage.api.storage;

import net.minecraft.world.item.ItemStack;
import com.refinedmods.refinedstorage.transfer.FluidStack;

import java.util.List;

/**
 * Represents a node that provides the network with storage.
 */
public interface IStorageProvider {
    /**
     * @param storages the item storages
     */
    void addItemStorages(List<IStorage<ItemStack>> storages);

    /**
     * @param storages the fluid storages
     */
    void addFluidStorages(List<IStorage<FluidStack>> storages);
}


