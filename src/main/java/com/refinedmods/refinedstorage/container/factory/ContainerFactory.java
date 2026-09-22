package com.refinedmods.refinedstorage.container.factory;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

@FunctionalInterface
public interface ContainerFactory<T extends AbstractContainerMenu> {
    T create(int windowId, Inventory inventory, FriendlyByteBuf data);
}


