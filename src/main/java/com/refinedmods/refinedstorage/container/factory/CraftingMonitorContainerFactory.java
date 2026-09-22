package com.refinedmods.refinedstorage.container.factory;

import com.refinedmods.refinedstorage.RSContainerMenus;
import com.refinedmods.refinedstorage.container.CraftingMonitorContainerMenu;
import com.refinedmods.refinedstorage.blockentity.craftingmonitor.CraftingMonitorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import com.refinedmods.refinedstorage.container.factory.ContainerFactory;

public class CraftingMonitorContainerFactory implements ContainerFactory<CraftingMonitorContainerMenu> {
    @Override
    public CraftingMonitorContainerMenu create(int windowId, Inventory inv, FriendlyByteBuf data) {
        BlockPos pos = data.readBlockPos();

        CraftingMonitorBlockEntity blockEntity = (CraftingMonitorBlockEntity) inv.player.level().getBlockEntity(pos);

        return new CraftingMonitorContainerMenu(RSContainerMenus.CRAFTING_MONITOR.get(), blockEntity.getNode(), blockEntity, inv.player, windowId);
    }
}



