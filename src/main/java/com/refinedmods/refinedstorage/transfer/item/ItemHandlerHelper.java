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
        ItemStack remainder = stack;

        // Match Forge's stacked insertion behavior: fill compatible stacks first,
        // then use empty slots for anything left over.
        for (int i = 0; i < handler.getSlots() && !remainder.isEmpty(); ++i) {
            ItemStack existing = handler.getStackInSlot(i);
            if (!existing.isEmpty() && ItemStack.isSameItemSameTags(existing, remainder)) {
                remainder = handler.insertItem(i, remainder, simulate);
            }
        }

        for (int i = 0; i < handler.getSlots() && !remainder.isEmpty(); ++i) {
            if (handler.getStackInSlot(i).isEmpty()) {
                remainder = handler.insertItem(i, remainder, simulate);
            }
        }

        return remainder;
    }

    public static void giveItemToPlayer(net.minecraft.server.level.ServerPlayer player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }
}


