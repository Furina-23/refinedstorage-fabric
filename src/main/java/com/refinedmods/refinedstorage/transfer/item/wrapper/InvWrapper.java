package com.refinedmods.refinedstorage.transfer.item.wrapper;

import com.refinedmods.refinedstorage.transfer.item.IItemHandler;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class InvWrapper implements IItemHandler {
    protected final Container container;
    public InvWrapper(Container container) { this.container = container; }
    @Override public int getSlots() { return container.getContainerSize(); }
    @Override public ItemStack getStackInSlot(int slot) { return container.getItem(slot); }
    @Override public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) { if (!isItemValid(slot, stack) || !container.canPlaceItem(slot, stack)) return stack; ItemStack current = getStackInSlot(slot); int limit = Math.min(getSlotLimit(slot), stack.getMaxStackSize()); int amount = Math.min(limit - current.getCount(), stack.getCount()); if (amount <= 0 || (!current.isEmpty() && !ItemStack.isSameItemSameTags(current, stack))) return stack; if (!simulate) { ItemStack copy = current.isEmpty() ? stack.copy() : current.copy(); copy.grow(amount); container.setItem(slot, copy); } ItemStack rest = stack.copy(); rest.shrink(amount); return rest; }
    @Override public ItemStack extractItem(int slot, int amount, boolean simulate) { ItemStack current = getStackInSlot(slot); if (current.isEmpty()) return ItemStack.EMPTY; ItemStack result = current.copyWithCount(Math.min(amount, current.getCount())); if (!simulate) container.removeItem(slot, result.getCount()); return result; }
    @Override public int getSlotLimit(int slot) { return 64; }
    @Override public boolean isItemValid(int slot, ItemStack stack) { return container.canPlaceItem(slot, stack); }
}


