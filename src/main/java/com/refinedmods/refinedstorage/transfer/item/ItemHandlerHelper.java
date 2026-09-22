package com.refinedmods.refinedstorage.transfer.item;

import net.minecraft.world.item.ItemStack;

public final class ItemHandlerHelper {
    private ItemHandlerHelper() {
    }

    public static ItemStack copyStackWithSize(ItemStack stack, int size) {
        return stack.copyWithCount(size);
    }

    public static ItemStack insertItem(IItemHandler handler, ItemStack stack, boolean simulate) {
        ItemStack remainder = stack;
        for (int i = 0; i < handler.getSlots() && !remainder.isEmpty(); ++i) {
            remainder = handler.insertItem(i, remainder, simulate);
        }
        return remainder;
    }

    public static ItemStack insertItemStacked(IItemHandler handler, ItemStack stack, boolean simulate) {
        return insertItem(handler, stack, simulate);
    }

    public static void giveItemToPlayer(net.minecraft.server.level.ServerPlayer player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }
}


